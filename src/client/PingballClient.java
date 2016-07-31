package client;

import graphics.CollidableGraphic;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import message.Messages;
import message.Messages.MessageType;
import physics.Geometry.VectPair;
import sim.Wall;
import BoardGrammar.OutputProcessor;

/**
 * 
 * A note about names:
 *      While in principle "Server" is a legal name for a Pingball board, due to the way
 *      our messaging system is structured, "Server" is a name reserved by the Pingball
 *      Server.  Because of this, if a board with the name "Server" is used, it could very
 *      well cause errors in the network features.
 * 
 *                           ***Client Thread safety argument***
 *                           
 * THE SOCKET
 * The socket is shared between the main thread (and its background threads) and two messaging 
 * threads. Of the two messaging threads, the ClientMessageSender uses the socket exclusively 
 * for sending, and the ClientMessageReceiver uses the socket exclusively for listening. and 
 * The main thread only accesses the socket to open and close the connection. To avoid locking
 * the UI, these tasks may be delegated to background threads.  All such threads will synchronize
 * on a serverLock object, to avoid concurrent modifications to the network connection. 
 * 
 * Care has been taken to ensure that any time the socket is modified, there are ABSOLUTELY NOT any
 * network threads running concurrently.  This is accomplished through calls of join() on running
 * threads prior to changing the socket.
 * 
 * 
 * THE BOARD
 * The board ('model') is not a thread-safe type, so its concurrency argument is one of confinement*.  
 * The main thread does not modify or reference the board while the simulation background thread is 
 * running, except through calls of name() (the board's name is a final String, so this kind of 
 * access is safe).
 * 
 * As with the network threads, we can guarantee that no background tasks that modify the model are
 * running simultaneously by synchronizing on the model. Note that while Swing background threads 
 * syncrhonize on the model, the simulation thread does not. hence....  as with the network 
 * threads, calls of join() are used (in stopSimulation()) to ensure that no simulation thread is 
 * running when the board has to be modified by another thread.  While multiple threads have
 * references to the board object, we can be sure that no threads modify it concurrently.
 * 
 * For tasks that must be handled while the simulation is running, such as key press events or 
 * server messages, thread-safe message queues are used under the producer-consumer paradigm.
 * 
 * 
 * THE UI
 * Background tasks that modify UI components will do so through SwingUtilities.invokeLater(),
 * to avoid concurrent modifications.
 * 
 * The animation component requires a somewhat more delicate treatment; it is modified directly
 * by the simulation thread, which passes a CollidableGraphic concurrently, and calls repaint().
 * CollidableGraphics are immutable, so this is a safe modification
 * 
 * 
 * DEADLOCK
 * there are two locks being used in this client. To avoid deadlock, any thread that needs to
 * acquire both locks will do so in the same order: model { socket { } } 
 * 
 * This will prevent deadlock scenarios
 */
public class PingballClient extends JFrame{
    
    private static final long serialVersionUID = 1L;

    private static final int DEFAULT_PORT = 10987;
    
    private Socket server;
    private String hostname;
    private int portNumber;
    //rep invariant --- if server is not null, then hostname and portNumber
    //                  represent the address of that socket.
    private Object serverLock = new Object();
    //lock object for access to network-related variables
    
    private BlockingQueue<String> serverOutgoing = new LinkedBlockingQueue<String>();
    private BlockingQueue<String> serverIncoming = new LinkedBlockingQueue<String>();
    //message passing queues for network threads
    //rep invariant -- any time that network threads are running, these queues must
    //                 be the queues for those threads
    
    private volatile boolean togglePause = false; 
    //Need volatile to act like memory barrier for pause/resume
    
    private Thread sender;
    //thread responsible for sending messages to the server
    private Thread receiver;
    //thread responsible for listening for messages from the server
    //rep invariant -- the name broadcasted by these threads MUST be the name of the 
    //                 board model, any time that the simulation is running
    class KillSwitch {
        private boolean kill = false;
        //kill
        private void kill(){ kill = true; }
        /**
         * return true if it's time to die
         */
        public boolean check(){ return kill; }
    }
    private KillSwitch kill = new KillSwitch();
    //for killing network threads
    
    private Board model = new Board("");
    //the board to be used for the animation
    private BlockingQueue<String> userInputQueue = model.getUserInputQueue();
    //message passing queue for key-press events
    //rep invariant -- userInputQueue must be the userInputQueue of model
    //                 so this must be reassigned any time a new model is used
    
    private Thread simThread;
    //the simulation thread that will mutate the board
    private boolean cancelled = true;
    //flag used to terminate the simulation thread when it is time to reset/restart
    //the animation
    private double simulationTimeStep = 0.050;
    //rep invariant -- must be > 0
    private static final long ANIMATION_TIME_STEP = 50;
    
    private JLabel nameLabel = new JLabel("Name: ");
    private JLabel serverLabel = new JLabel("Server: not connected");
    //board state labels -- display name and connection state
    
    private JTextField hostnameTextField = new JTextField("hostname");
    private JTextField portTextField = new JTextField("port");
    private JTextField fileTextField = new JTextField("file");
    //for specifying board file and connection parameters
    
    private JButton connectButton = new JButton("Connect");
    private JButton disconnectButton = new JButton("Disconnect");
    private JButton fileButton = new JButton("Load File");
    //for changing the board file or altering the network connection
    
    private JButton pause = new JButton("Pause"); 
    private JButton restart = new JButton("Restart"); 
    private JButton speedup = new JButton("Speed Up");
    private JButton slowDown = new JButton("Slow Down");
    //for altering the simulation/animation
    
    private JList<String> clientList;
    private DefaultListModel<String> clientListModel;
    private JLabel clientListLabel = new JLabel("Select a Client");
    private JList<String> wallChoice;
    private JButton linkChoices = new JButton("Link!");
    private JLabel wallChoicesLabel = new JLabel("Select a Wall");
    //for user-specified board links
    
    private PingballAnimation animation = new PingballAnimation(model.getGraphic());
    //primary animation panel
    
    /**
     * Create a new Pingball Client UI
     * 
     * @param initFilepath the relative path of the file to be used on startup
     *          if an empty string is provided, the game will start with a blank
     *          board
     *          this must be specified in order to start with a connection to a
     *          server
     * @param initHostname the hostname for a server connection to be used on startup
     *          if an empty string is provided, no server connection will be attempted
     * @param initPort the port for the server connection
     */
    public PingballClient(String initFilepath, String initHostname, int initPort){
        
        setTitle("Pingball");
        
        //if a hostname has been specified, display in the appropriate text fields
        if(!initHostname.isEmpty()){
            hostnameTextField.setText(initHostname);
            portTextField.setText("" + initPort);
        }
        
        //if a filepath has been specified
        if(!initFilepath.isEmpty()){
            fileTextField.setText(initFilepath);
            try{
                model = OutputProcessor.parse(new File(getResourcePath(initFilepath)));
                userInputQueue = model.getUserInputQueue();
            }catch(Exception e){
                System.err.println("Error retrieving specified file:" + initFilepath);
            }
            //if a server connection is desired
            if(!initHostname.isEmpty()){
                openNewServerConnection(initHostname, initPort, model.name());
            }
        }
        
        hostnameTextField.setName("hostnameTextField");
        JLabel hostnameLabel = new JLabel("hostname:");
        hostnameLabel.setName("hostnameLabel");
        
        portTextField.setName("portTextField");
        JLabel portLabel = new JLabel("port number:");
        portLabel.setName("portLabel");

        nameLabel.setName("nameLabel");
        serverLabel.setName("serverLabel");
        
        animation.setFocusable(true);
        //mouse input listener
        MouseAdapter myListener = new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent arg0){
                userInputQueue.add("mouse:release");
            }
            
            @Override
            public void mousePressed(MouseEvent arg0){
                animation.requestFocusInWindow();
                sendMousePosition(arg0);
            }
            
            @Override
            public void mouseDragged(MouseEvent arg0){
                sendMousePosition(arg0);
            }
            
            private void sendMousePosition(MouseEvent arg0){
                double x = arg0.getX();
                double y = arg0.getY();
                x = (x-CollidableGraphic.ORIG_X)/CollidableGraphic.PIXELS_PER_UNIT;
                y = (y-CollidableGraphic.ORIG_Y)/CollidableGraphic.PIXELS_PER_UNIT;
                userInputQueue.add("mouse:" + x + ":" + y);
            }
        };
        animation.addMouseListener(myListener);
        animation.addMouseMotionListener(myListener);
        
        //keyboard input listener
        animation.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                String keyName = KeyEvent.getKeyText(e.getKeyCode()).toLowerCase().replaceAll(" ", "");
                keyName = "keydown:" + keyName;
                if(!togglePause){
                    userInputQueue.add(keyName);
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                String keyName = KeyEvent.getKeyText(e.getKeyCode()).toLowerCase().replaceAll(" ", "");
                keyName = "keyup:" + keyName;
                if(!togglePause){
                    userInputQueue.add(keyName);
                }
            }
        });

        pause.setName("pause");
        pause.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent ae){
                togglePause = !togglePause; //flip the pause switch
                if(togglePause){
                    pause.setText("Resume");
                }
                else{
                    pause.setText("Pause");
                }
            }
        });
        
        restart.setName("restart");
        restart.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent ae){
                
                Thread backgroundThread = new Thread(new Runnable(){
                    public void run(){
                        synchronized(model){
                            
                            stopSimulation();
                            model.restartSimulation();
                            startSimulation();
                        }
                    }
                });
                backgroundThread.start();
            }
        });
        
        speedup.setName("Speed Up");
        speedup.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent ae){
                simulationTimeStep = Math.min(simulationTimeStep*2, .2);
            }
        });
        
        slowDown.setName("Slow Down");
        slowDown.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent ae){
                simulationTimeStep = Math.max(simulationTimeStep/2, .0025);
            }
        });
        
        connectButton.setName("connectButton");
        connectButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent ae){
                
                final String host = resolveHostname();
                final int port =  resolvePortNumber();
                
                if(host.equals(hostname) && port == portNumber){
                    System.err.println("Already connected to that host");
                    return;
                }
                
                Thread backgroundThread = new Thread(new Runnable(){
                    public void run(){
                        synchronized(serverLock){
                            openNewServerConnection(host, port, model.name());
                        }
                    }
                });
                backgroundThread.start();
            }
        });
        
        disconnectButton.setName("disconnectButton");
        disconnectButton.addActionListener(new ActionListener(){
           public void actionPerformed(ActionEvent ae){
               Thread backgroundThread = new Thread(new Runnable(){
                   public void run(){
                       synchronized(serverLock){
                           closeServerConnection();
                       }
                   }
               });
               backgroundThread.start();
           }
        });
        
        fileTextField.setName("fileTextField");
        fileTextField.addActionListener(new FileInputListener());
        
        fileButton.setName("fileButton");
        fileButton.addActionListener(new FileInputListener());
        
        clientListModel = new DefaultListModel<String>();
        clientList = new JList<String>(clientListModel);
        clientList.setName("clientList");
        
        String[] data = {"Top", "Bottom", "Left", "Right"}; 
        wallChoice = new JList<String>(data);
        wallChoice.setName("wallChoiceList");
        
        linkChoices.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                String otherBoard = clientList.getSelectedValue();
                String newWall = wallChoice.getSelectedValue();
                Wall.WallType connectAcross;
                if(otherBoard == null || otherBoard.isEmpty() ){
                    JOptionPane.showMessageDialog(null, "Select a Client Please", "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
                else if(newWall == null || newWall.isEmpty()){
                    JOptionPane.showMessageDialog(null, "Select a Wall Please", "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
                else{
                    newWall = newWall.toUpperCase();
                    connectAcross = Wall.stringToWallType(newWall);
                    serverOutgoing.add(Messages.composeLinkMessage(model.name(), "Server", otherBoard, connectAcross));
                }
            }
        });
        
        writeNameLabel(model.name());
        startSimulation();
        
        //for sizing the panel - fits the 20x20 (or whatever) board + 1 unit of border space
        int panelSize = (2 + Board.DEFAULT_SIZE)*CollidableGraphic.PIXELS_PER_UNIT;
        
        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
        ParallelGroup animationHGroup =             
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                    .addComponent(nameLabel)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(hostnameLabel)
                                        .addComponent(hostnameTextField, 72, 72, 1000)))
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                    .addComponent(serverLabel)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(portLabel)
                                        .addComponent(portTextField))))
                            .addComponent(fileTextField,72,144,1000))
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(connectButton)
                                .addComponent(disconnectButton))
                            .addComponent(fileButton)))
                    .addComponent(animation,panelSize,panelSize,panelSize)
                          .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                    .addComponent(restart)
                                    .addComponent(pause)
                                    .addComponent(speedup)
                                    .addComponent(slowDown)));

        
        ParallelGroup clientListHGroup = 
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(clientListLabel)
                .addComponent(clientList, 100, 100, 100)
                .addComponent(wallChoicesLabel)
                .addComponent(wallChoice, 100, 100, 100)
                .addComponent(linkChoices);

        layout.setHorizontalGroup(layout.createSequentialGroup()
                .addGroup(animationHGroup)
                .addGroup(clientListHGroup));
        
        SequentialGroup animationVGroup =             
                layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(nameLabel)
                        .addComponent(serverLabel))
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(hostnameLabel)
                        .addComponent(hostnameTextField,18,18,18)
                        .addComponent(portLabel)
                        .addComponent(portTextField,18,18,18)
                        .addComponent(connectButton)
                        .addComponent(disconnectButton))
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(fileTextField,18,18,18)
                        .addComponent(fileButton))
                    .addComponent(animation,panelSize,panelSize,panelSize)
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                            .addComponent(restart)
                            .addComponent(pause)
                            .addComponent(speedup)
                            .addComponent(slowDown));
        
        SequentialGroup clientListVGroup = 
                layout.createSequentialGroup()
                    
                    .addComponent(wallChoicesLabel)
                    .addComponent(wallChoice)
                    .addComponent(clientListLabel)
                    .addComponent(clientList, 144, panelSize/2, 1000)
                    .addComponent(linkChoices);
        
        
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(animationVGroup)
                .addGroup(clientListVGroup));
    }
    
    //input listener for file loading UI
    private class FileInputListener implements ActionListener{
        public void actionPerformed(ActionEvent ae){
            
            final String newFile = fileTextField.getText().trim();
            Thread backgroundThread = new Thread(new Runnable(){
                public void run(){
                    synchronized(model){
                        try{
                            model = OutputProcessor.parse(new File(getResourcePath(newFile)));
                        } catch(Exception e){
                            e.printStackTrace();
                            JOptionPane.showMessageDialog(null, "File not found", "Error",
                                    JOptionPane.ERROR_MESSAGE);
                            System.err.println("error retrieving file: " + newFile);
                            return;
                        }
    
                        synchronized(serverLock){
                            stopSimulation();
                            String newName = model.name();
                            writeNameLabel(newName);
                            if(server != null){
                                if(!openNewServerConnection(hostname,portNumber,newName)){
                                    writeServerLabel("not connected");
                                }
                            }
                            startSimulation();
                        }
                    }
                }
            });
            backgroundThread.start();
        }
    }
    
    //resolve a hostname from the hostname text field
    private String resolveHostname(){
        return hostnameTextField.getText().trim();
    }
    
    // extract a port number from the port text field, or use default if a port number
    // cannot be resolved
    private int resolvePortNumber(){
        int portNumber = DEFAULT_PORT;
        try{
            portNumber = Integer.parseInt(portTextField.getText());
        }catch(NumberFormatException nfe){
            System.err.println("using default port: " + DEFAULT_PORT);
        }
        return portNumber;
    }
    
    //NOTE: these invokeLater methods are described in the Thread Safety Argument
    //in the Class header
    
    //Add a new name to our table of clients
    public void addNameToTable(final String clientName){
        SwingUtilities.invokeLater(new Runnable(){
            public void run(){
                clientListModel.addElement(clientName);
            }
        });
    }
    
    //Remove a client name from our table of clients
    public void removeNameFromTable(final String clientName){
        SwingUtilities.invokeLater(new Runnable(){
            public void run(){
                clientListModel.removeElement(clientName);
            }
        });
    }
    
    //Remove all client names from table
    public void removeAllNamesFromTable(){
        SwingUtilities.invokeLater(new Runnable(){
            public void run(){
                clientListModel.removeAllElements();
            }
        });
    }
    
    //set the displayed name on nameLabel using SwingUtilities.invokeLater
    private void writeNameLabel(final String name){
        SwingUtilities.invokeLater(new Runnable(){
            public void run(){
                nameLabel.setText("Name:  " + name);
            }
        });
    }
    
    //set the displayed server connection using SwingUtilities.invokeLater
    private void writeServerLabel(final String server){
        SwingUtilities.invokeLater(new Runnable(){
            public void run(){
                serverLabel.setText("Server:  " + server);
            }
        });
    }
    
    //private helper method sets the cancelled flag and waits for the simulation
    //thread to die
    private void stopSimulation(){
        if(simThread == null || cancelled == true){
            return;
        }
        //set cancelled flag - should kill simThread
        cancelled = true;
        try {
            //wait for thread to die
            simThread.join();
            System.out.println("Simulation halted");
        } catch (InterruptedException e) {
            //call recursively until the thread is definitely dead...
            stopSimulation();
        }
    }
    
    /**
     * start a new simulation from the specified board.
     * this call will block while it waits for the ongoing simulation
     * thread to die.
     * 
     * @param board the board to be used for the new animation.
     */
    public void startSimulation(){
        if(cancelled == false){
            return;
        }
        cancelled = false;
        model.saveConfiguration();
        animation.newFrame(model.getGraphic());
        userInputQueue = model.getUserInputQueue();
        simThread = new Thread(new Runnable(){

            public void run() {
                //cancelled flag is used to kill the thread
                while(!cancelled){
                    if(togglePause){
                        //allow the simulation to be stopped while paused
                        if(cancelled){
                            break;
                        }
                        continue; //Spin while paused
                    }

                    //advance the physics simulation
                    long nextFrame = System.currentTimeMillis() + ANIMATION_TIME_STEP;
                    model.advanceSimulation(simulationTimeStep);
                    //model.printGrid();
                    //"publish" frame
                    animation.newFrame(model.getGraphic());
                    
                    //wait for next frame
                    while(System.currentTimeMillis() < nextFrame){
                        //Handle Networking tasks...
                        
                        //First check if any messages need to be sent to the server,
                        //and handle one
                        if(model.hasPendingMessage()){
                            serverOutgoing.add(model.grabMessage());
                        }
                        
                        //Then check if any messages have been received from the server,
                        //and handle one
                        if(!serverIncoming.isEmpty()){
                            handleIncomingMessage();
                        }
                    }
                }
            }
        });
        simThread.start();
        System.out.println("New simulation started");
    }
    
    //private helper method -- resolves the server message parameters and
    //appropriately modifies the board connections and contents
    private void handleIncomingMessage(){
        
        String message = serverIncoming.remove();
        //System.out.println("received new server message: " + message); //debug
        String sender = Messages.parseSender(message);
        MessageType type = Messages.parseType(message);
        String[] tokens = Messages.parseArguments(message);
        
        switch(type){
        case WALL_TELEPORT:
            //spawn a ball that has entered our board through a wall
            VectPair pair = Messages.parseWallTeleportArguments(message);
            model.spawnBall(pair);
            break;
            
        case PORTAL_TELEPORT:
            //spawn a ball that has entered our board through a portal
            String portalName = tokens[0];
            double xVel = Double.parseDouble(tokens[1]);
            double yVel = Double.parseDouble(tokens[2]);
            model.spawnBall(portalName, xVel, yVel);
            break;
            
        case PORTAL_QUERY:
            //if we receive a query, we compose a reply
            List<String> portalNames = model.getPortalNames();
            String toSend = Messages.composePortalResponseMessage(model.name(), sender, portalNames);
            serverOutgoing.add(toSend);
            break;
            
        case PORTAL_REPLY:
            //when we receive a reply, we can open portals as necessary
            model.openPortals(sender, Arrays.asList(tokens));
            break;
            
        case LINK:
            //connect a wall
            model.connectWall(Wall.stringToWallType(tokens[1]), tokens[0]);
            break;
            
        case DELINK:
            //disconnect a wall
            model.disconnectWall(Wall.stringToWallType(tokens[1]), tokens[0]);
            break;

        case CONNECTION:
            //when we become aware of a new client, we have to send a portal query to them
            //and add them to our UI client list
            serverOutgoing.add(Messages.composePortalQueryMessage(model.name(), tokens[0]));
            addNameToTable(tokens[0]);
            break;
            
        case DISCONNECT:
            //when another client disconnects, we have to remove them from our client list
            //and disconnect any elements that are linked to them through the server
            //if we have disconnected from the server, instead disconnect ALL such links
            //and clear the client list completely
            if(tokens[0].equals(model.name())){
                model.disconnectAll();
                removeAllNamesFromTable();
            }else{
                removeNameFromTable(tokens[0]);
            }
            model.disconnectBoard(Messages.parseArguments(message)[0]);
            break;
            
        default:
            //ignore
        }
    }
    
    //if connected to a server, close the connection and wait for the messaging
    //threads to die
    private void closeServerConnection(){
        if(server == null){
            return;
        } else {
            try{
                //kill the network threads
                kill.kill();
                kill = new KillSwitch();
                
                //wait for messaging threads to stop
                receiver.join();
                sender.join();
                
                //close the socket
                server.close();
                
                //discard old queues
                serverOutgoing = new LinkedBlockingQueue<String>();
                serverIncoming = new LinkedBlockingQueue<String>();
                
                System.out.println("Server connection stopped");
                
            } catch(IOException ioe) {
                System.err.println("Error closing socket: " + ioe.getMessage());
            } catch(InterruptedException e) {
                System.err.println("Interrupted while waiting for threads to join");
            }
            server = null;
            hostname = "";
            writeServerLabel("not connected");
            removeAllNamesFromTable();
        }
    }
    
    //open a new network connection to the server at host:port
    //returns true if the new connection resolves properly
    //otherwise returns false
    private boolean openNewServerConnection(String host, int port, String name){
        
        //first close existing connection
        closeServerConnection();
        
        if(name.trim().isEmpty()){
            JOptionPane.showMessageDialog(null, "Please make sure your board has a name", "Error",
                    JOptionPane.ERROR_MESSAGE);
            System.err.println("You must have a name in order to connect to a server");
            return false;
        }
        
        //then attempt to open the socket
        server = null;
        try{
            server = new Socket(host, port);
            this.hostname = host;
            this.portNumber = port;
            writeServerLabel(hostname + ":" + port);
        }catch(IOException ioe){
            JOptionPane.showMessageDialog(null, "Connection refused: " + host + " : " + port, "Error",
                    JOptionPane.ERROR_MESSAGE);
            System.err.println("Connection refused: " + host + ":" + port);
            return false;
        }
        
        //if the connection was successful, then start the communication threads
        System.out.println("Connection resolved: " + host + ":" + port);
        
        sender = new Thread(new ClientMessageSender(name, server, serverOutgoing, kill));
        sender.start();
        
        receiver = new Thread(new ClientMessageReceiver(name, server, serverIncoming, kill));
        receiver.start();
        addNameToTable(name);
        return true;
    }
    
    /**
     * taken from ps3 Return the absolute path of the specified file resource on
     * the classpath.
     * 
     * @throws IOException
     *             if a valid path to an existing file cannot be returned
     */
    private String getResourcePath(String fileName) throws IOException {
        System.out.println("Running File:" + fileName);
        URL url = Thread.currentThread().getContextClassLoader()
                .getResource(fileName);
        if (url == null) {
            throw new IOException("Failed to locate resource " + fileName);
        }
        File file;
        try {
            file = new File(url.toURI());
        } catch (URISyntaxException urise) {
            throw new IOException("Invalid URL: " + urise);
        }
        String path = file.getAbsolutePath();
        if (!file.exists()) {
            throw new IOException("File " + path + " does not exist");
        }
        return path;
    }
    
    /**
     * parses command line arguments and begins the board simulation
     * 
     * USAGE: PingballClient [--host HOST] [--port PORT] FILE
     * 
     * HOST is an optional hostname or IP address of the server to connect to.
     * If no HOST is provided, then the client starts in single-machine play
     * mode, as described above.
     * 
     * PORT is an optional integer in the range 0 to 65535 inclusive, specifying
     * the port where the server is listening for incoming connections. The
     * default port is 10987.
     * 
     * FILE is an optional argument specifying a file pathname of the Pingball
     * board that this client should run on startup.
     * 
     * @param args
     *            command-line arguments to be parsed
     */
    public static void main(String[] args){
        Queue<String> arguments = new LinkedList<String>(Arrays.asList(args));
        int port = DEFAULT_PORT;//default port
        String hostname = "";//if not specified, no server connection will be attempted
        String filepath = "";
        //parse arguments
        try{
            String flag = "";
            try{
                while(!arguments.isEmpty()){
                    flag = arguments.remove();
                    if(flag.equals("--port")){
                        port = Integer.parseInt(arguments.remove());
                        if(port < 0 || port > 65535){
                            throw new IllegalArgumentException("port " + port + " out of range");
                        }
                    }else if(flag.equals("--host")){
                        hostname = arguments.remove();
                    }else{
                        filepath = flag;
                    }
                }
            } catch (NoSuchElementException e) {
                throw new IllegalArgumentException("missing argument for " + flag);
            } catch(NumberFormatException e) {
                System.err.println("unable to parse number for \"" + flag + "\"");
            }
        } catch(IllegalArgumentException e) {
            System.err.println(e.getMessage());
            System.err.println("usage: PingballClient [--host HOST] [--port PORT] FILE");
        }
        
        final int initPort = port;
        final String initHostname = hostname;
        final String initFilepath = filepath;
        
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                PingballClient main = new PingballClient(initFilepath, initHostname, initPort);
                main.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                main.pack();
                main.setVisible(true);
            }
        });
    }
    
}
