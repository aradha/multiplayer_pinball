package client;

import graphics.BoardGraphic;
import graphics.CollidableGraphic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import physics.Geometry.VectPair;
import physics.Vect;
import message.Messages;
import sim.Ball;
import sim.Collidable;
import sim.Envelope;
import sim.Portal;
import sim.Triggerable;
import sim.Wall;
import sim.Wall.WallType;

/**
 * Board Class
 * 
 * Data structure for storing and organizing pingball objects. Supports methods
 * for construction of boards from base objects, and has an internal simulation
 * that can be advanced by a specified time step.
 */
public class Board {
    
    public static final double DEFAULT_GRAVITY = 25;
    public static final double DEFAULT_DISTORTION_RADIUS = 5;
    public static final double DEFAULT_FRICTION1 = 0.025;
    public static final double DEFAULT_FRICTION2 = 0.025;
    //default physics parameters
    public static final int DEFAULT_SIZE = 20;
    //default size

    private final Simulation simulation;
    //internal simulation instance
    //rep invariant -- simulation must contain all of the same balls, collidables
    //                 &c. as the board. This is realistically guaranteed by the
    //                 constructor
    private final String name;
    //the name of the board
    private final double g, mu, mu2;
    //physics parameters
    private Vect vortex;
    //gravity vortex
    private char[][] grid = new char[DEFAULT_SIZE + 2][DEFAULT_SIZE + 2];
    //character grid for text based rendering
    // +2 because we must add extra space for walls
    private List<Wall> wallList;
    //contains the four walls of the board
    //rep invariant -- all walls in this list must have references to this
    //                 board's transport queue
    private List<Ball> ballList = new ArrayList<Ball>();
    //contains all balls currently on the board
    //rep invariant -- all balls are positioned within the board's (-0.5,20.5)^2
    //                 board space. This is guaranteed through calls of handleClipping()
    private List<Triggerable> triggerableList = new ArrayList<Triggerable>();
    //contains all triggerable gadgets on the board
    private List<Portal> portalList = new ArrayList<Portal>();
    //contains specific references to portals, for resolving teleport events
    //rep invariant -- all portals in portalList must have references to this
    //                 board's transport queue
    private List<VectPair> saveList = new ArrayList<VectPair>();
    //stores the states of balls for reset
    //rep invariant -- contains the <position,velocity> vect pairs for the balls'
    //                 states at the last time saveConfiguration() was called
    
    private Queue<Envelope> transportQueue = new LinkedList<Envelope>();
    //this Queue is used to transport envelopes from low level pingballObjects
    //up to the board, so that messages can be sent to the server. It must be
    //shared with any gadget or object that involves network transport of balls
    private Queue<String> messageQueue = new LinkedList<String>();
    //this Queue is used to store messages that have been translated from Envelopes
    //taken from the transportQueue. Higher level classes can access this queue by
    //calling hasPendingMessage() and grabMessage()
    private BlockingQueue<String> userInputQueue = new LinkedBlockingQueue<String>();
    //this Queue is used to communicate key press events from the client to the board
    private Map<String, Set<String>> keyBindings = new HashMap<String, Set<String>>();
    //this map is used to map keypress events to board actions
    //rep invariant -- A's name is in keyBindings.get(B) iff user input B should trigger
    //                 gadget A's action
    private Map<String, Triggerable> nameLookup = new HashMap<String, Triggerable>();
    //for looking up gadgets by name
    //rep invariant -- the set of keys consists of exactly the names of every gadget
    //                 in triggerableList.  Each key maps to the gadget with that name
    
    /**
     * construct an otherwise empty board with the specified name and default
     * physics parameters
     * 
     * @param name the name to be used by this board
     */
    public Board(String name){
        this(name, 0.025, 0.025, 25);
    }
    
    /**
     * construct a board with the specified name and specified physics parameters
     * 
     * @param name the name to be used by this board
     * @param g the value of gravity
     * @param mu the first value of friction
     * @param mu2 the second value of friction
     */
    public Board(String name, double g, double mu, double mu2){
        Wall[] walls = {new Wall(WallType.TOP, DEFAULT_SIZE, transportQueue),
                new Wall(WallType.BOTTOM, DEFAULT_SIZE, transportQueue),
                new Wall(WallType.LEFT, DEFAULT_SIZE, transportQueue),
                new Wall(WallType.RIGHT, DEFAULT_SIZE, transportQueue)};

        this.wallList = Arrays.asList(walls);
        this.name = name;
        this.g = g; this.mu = mu; this.mu2 = mu2;
        this.simulation = new Simulation(wallList, ballList, triggerableList, 
                g, mu, mu2);
    }
    
    
    
    //Generic Observer methods!
    
    /**
     * @return the name of the board
     */
    public String name(){
        return name;
    }
    /**
     * @return the gravity value used by this board
     */
    public double gravity(){
        return g;
    }
    /**
     * @return the first friction value used by this board
     */
    public double friction1(){
        return mu;
    }
    /**
     * @return the second friction value used by this board
     */
    public double friction2(){
        return mu2;
    }
    /**
     * @return the number of balls in play
     */
    public int numberOfBalls(){
        return ballList.size();
    }
    /**
     * 
     * @return the list of portals on the board
     */
    public List<Portal> getPortalList(){
        return portalList;
    }
    /**
     * @return the number of triggerable gadgets on the board
     */
    public int numberOfGadgets(){
        return triggerableList.size();
    }
    /**
     * @return the number of triggering links between gadgets on the board
     */
    public int numberOfTriggerLinks(){
        int count = 0;
        for(Triggerable gadget : triggerableList){
            count += gadget.numberOfTriggerLinks();
        }
        return count;
    }
    
    /**
     * get a list of Portal objects on the board
     * 
     * @return a list containing the names of every Portal on this board
     */
    public List<String> getPortalNames(){
        List<String> names = new LinkedList<String>();
        for(Portal p : portalList){
            names.add(p.name());
        }
        return names;
    }
    
    /**
     * Higher level classes can call this method to get a reference to the
     * blocking queue used to pipe user inputs into the board simulation
     * 
     * @return the user input queue by which to send user inputs to the board
     */
    public BlockingQueue<String> getUserInputQueue(){
        return userInputQueue;
    }
    
    //private helper method -- checks the availability of the specified gadget name
    private boolean nameIsTaken(String name){
        return nameLookup.get(name) != null;
    }
    
    
    
    
    //Simulation control methods!
    
    /**
     * Advance the simulation forward by the time step provided.
     * @param deltaT: a double representing the duration for which
     *                the simulation should be advanced.                
     */
    public void advanceSimulation(double deltaT){
        simulation.advance(deltaT,0);
        handleUserInput();
        processTransportQueue();
    }
    
    /**
     * reset all gadgets on the board to their initial state, and,
     * reset the balls on the board to the state they were in when
     * saveConfiguration() was called
     */
    public void restartSimulation(){
        for(Triggerable gadget : triggerableList){
            gadget.reset();
        }
        //do we need to empty out the transport queue?
        //doesn't hurt
        while(!transportQueue.isEmpty()){
            transportQueue.remove();
        }
        //get rid of all balls on the board.
        while(!ballList.isEmpty()){
            ballList.remove(0);
        }
        //spawn new balls from saved state
        for(VectPair pair : saveList){
            spawnBall(pair);
        }
        //clear the vortex
        vortex = null;
        //change sim gravity back to normal
        simulation.resetGravity();
    }
    
    //private helper method executes keypresses and mouse actions from the
    //user input queue
    private void handleUserInput(){
        String mouseDrag = "";
        while(!userInputQueue.isEmpty()){
            String input = userInputQueue.remove();
            if(input.startsWith("key")){
                if(keyBindings.get(input) != null){
                    for(String gadgetName : keyBindings.get(input)){
                        triggerGadget(gadgetName);
                    }
                }
            }else if(input.contains("mouse:")){
                if(input.equals("mouse:release")){
                    simulation.resetGravity();
                    vortex = null;
                }else{
                    mouseDrag = input;
                }
            }
        }
        //if the mouse is being dragged, make a gravity vortex using the
        //most recent mouse position from the queue
        if(!mouseDrag.isEmpty()){
            String[] mouseInputTokens = mouseDrag.split(":");
            double x = Double.parseDouble(mouseInputTokens[1]);
            double y = Double.parseDouble(mouseInputTokens[2]);
            vortex = new Vect(x,y);
            simulation.distortGravity(x,y);
        }
    }
    
    //trigger the action of the gadget with name gadgetName
    private void triggerGadget(String gadgetName){
        nameLookup.get(gadgetName).triggerAction();
    }
    

    
    
    
    
    //Board construction and layout methods!
    
    /**
     * add a ball to the board with position and velocity specified by
     * the passed VectPair
     * 
     * @param pair the VectPair (<x,y>,<vx,vy>) describing the new ball
     */
    public void spawnBall(VectPair pair){
        ballList.add(new Ball(pair.v1.x(), pair.v1.y(), pair.v2));
    }
    
    /**
     * Add a ball to the simulation from a Portal (or other spawner-enabled object?)
     * the ball will automatically be added to the board's ball list
     * 
     * @param spawnerName the name of the Portal which will spawn the ball
     * @param xVel the x component of the ball velocity
     * @param yVel the y component of the ball velocity
     */
    public void spawnBall(String spawnerName, double xVel, double yVel){
        Ball ball = new Ball(0, 0, new Vect(xVel, yVel));
        spawnBall(spawnerName, ball);
        ballList.add(ball);
    }
    
    /**
     * spawn an existing ball from a portal
     * 
     * @param spawnerName the name of the portal
     * @param ball the ball to be spawned
     */
    public void spawnBall(String spawnerName, Ball ball){
        for(Portal p : portalList){
            if(p.name().equals(spawnerName)){
                p.spawnBall(ball);
            }
        }
    }
    
    /**
     * add the specified ball to the board
     * 
     * @param ball the ball to be added
     */
    public void add(Ball ball){
        ballList.add(ball);
    }
    
    /**
     * add the specified gadget to the board
     * 
     * @param gadget the gadget to be added
     * @throws IllegalArgumentException if the provided gadget has the same
     *          name as a gadget already on the board
     */
    public void add(Triggerable gadget){
        if(!nameIsTaken(gadget.name())){
            triggerableList.add(gadget);
            nameLookup.put(gadget.name(), gadget);
        }else{
            throw new IllegalArgumentException(
                    "Error: A gadget with that name already exists: " + gadget.name());
        }
    }
    
    /**
     * add a portal to the board at position x,y with the specified parameters
     * 
     * @param x the x position
     * @param y the y position
     * @param name the unique name of this gadget (on the board)
     * @param board the board to which this portal connects.  If the portal should
     *          be local (that is, if the portal connects to the board it is on), then
     *          this field should be an empty string
     * @param otherPortal the portal to which this portal connects.  This is the
     *          name of the exit portal to which balls will be transported
     */
    //has to be constructed in this way so that queue reference can be properly passed
    public void addPortal(int x, int y, String portalName, String exitBoard, String otherPortal){
        Portal portal = new Portal(x,y,portalName,exitBoard,otherPortal,transportQueue);
        for(Portal p : portalList){
            if(portal.exitBoard().isEmpty() && p.name().equals(otherPortal)){
                portal.open();
            }if(p.exitBoard().isEmpty() && portalName.equals(p.exitPortal())){
                p.open();
            }
        }
        add(portal);//add to collision handling list
        portalList.add(portal);//add to list of class-specific references
    }
    
    /**
     * create a trigger link from the first named gadget to the second. any time
     * that a ball collides with the first gadget, the second gadget will perform
     * some action (specific to its object class)
     * 
     * @param nameFrom the source of the trigger signal
     * @param nameTo the gadget whose action should be triggered
     */
    public void createTriggerLink(String nameFrom, String nameTo){
        if(nameLookup.get(nameFrom) != null && nameLookup.get(nameTo) != null){
            nameLookup.get(nameFrom).addTrigger(nameLookup.get(nameTo));
        }
    }
    
    /**
     * bind key event keyName to gadget gadgetName's action. key event syntax
     * consists of "(keyup | keydown)':'key", where key is the lower case, spaces
     * removed version of some key's name
     * 
     * @param keyName the key event string
     * @param gadgetName the name of the gadget to be bound to that key
     */
    public void bindKey(String keyName, String gadgetName){
        if(keyBindings.get(keyName) == null){
            keyBindings.put(keyName, new HashSet<String>());
        }
        keyBindings.get(keyName).add(gadgetName);
    }
    
    /**
     * save the states of all free balls currently on the board.
     * does not save the states of balls that have been absorbed.
     * 
     * This method is not intended to save the game state at an arbitrary
     * point in time. Rather, it should be called after the board is 
     * fully initialized, to create a restore point for reset features
     */
    public void saveConfiguration(){
        saveList = new ArrayList<VectPair>();
        for(Ball ball : ballList){
            if(!ball.isInStasis()){
                saveList.add(new VectPair(ball.getCenter(), ball.vel()));
            }
        }
    }
    
    
    
    //Board Link control methods!
    
    /**
     * removes all connections to the named board. called when another board
     * becomes disconnected from the server
     * 
     * @param boardName the name of the board to be disconnected
     */
    public void disconnectBoard(String boardName){
        for(Wall wall : wallList){
            disconnectWall(wall, boardName);
        }
        closePortals(boardName);
    }
    
    /**
     * removes all connections to external boards. called when this board becomes
     * disconnected from the server
     */
    public void disconnectAll(){
        for(Wall wall : wallList){
            wall.disconnect();
            handleClipping(wall);
        }
        closeNonLocalPortals();
    }
    
    /**
     * connect the specified wall to another board. disconnects any pre-existing
     * connection through that wall, and notifies the disconnected board.
     * 
     * @param type the wall which is to be connected
     * @param otherBoard the name of the board which is being connected
     */
    public void connectWall(WallType type, String otherBoard){
        for(Wall wall : wallList){
            if(wall.type() == type){
                if(!wall.isTransparent()){
                    wall.connectBoard(otherBoard);
                    return;
                }
                if(!wall.linkName().equals(otherBoard)){
                    WallType opposite = Wall.oppositeType(type);
                    String message = Messages.composeDelinkMessage(
                            name, wall.linkName(), name, opposite);
                    messageQueue.add(message);
                    wall.connectBoard(otherBoard);
                }
                return;
            }
        }
    }
    
    /**
     * Given a specified board and a list of portal names from that board,
     * open all portals that can be linked to that board
     * 
     * @param boardName the name of the board
     * @param portalNames a list of portals on that board
     */
    public void openPortals(String boardName, List<String> portalNames){
        for(Portal p : portalList){
            if(p.exitBoard().equals(boardName)){
                for(String exit : portalNames){
                    if(p.exitPortal().equals(exit)){
                        p.open();
                    }
                }
            }
        }
    }
    
    /**
     * close all portals that link to boardName
     * 
     * @param boardName the name of the board to be disconnected
     */
    public void closePortals(String boardName){
        for(Portal p : portalList){
            if(p.exitBoard().equals(boardName)){
                p.close();
            }
        }
    }
    
    /**
     * close all portals that connect to other boards
     */
    public void closeNonLocalPortals(){
        for(Portal p : portalList){
            if(!p.exitBoard().isEmpty()){
                p.close();
            }
        }
    }
    
    //private method using internal rep - disconnects a referenced wall
    private void disconnectWall(Wall wall, String boardName){
        if(wall.linkName().equals(boardName)){
            wall.disconnect();
            handleClipping(wall);
        }
    }
    
    /**
     * disconnect the wall of the specified type, if it is connected to the
     * named board
     * 
     * @param type the type of the wall that is to be disconnected
     * @param boardName the name of the board which is to be disconnected
     */
    public void disconnectWall(WallType type, String boardName){
        for(Wall wall : wallList){
            if(wall.type() == type){
                this.disconnectWall(wall, boardName);
            }
        }
    }
    
    //private method removes balls that are placed into illegal states by the 
    //materialization of solid walls
    private void handleClipping(Wall wall){
        if(ballList.isEmpty()){
            return;
        }
        for(Iterator<Ball> it = ballList.iterator(); it.hasNext();){
            Ball ball = it.next();
            if(wall.isClipping(ball)){
                it.remove();
            }
        }
    }
    
    
    
    
    //Teleport resolution methods
    
    /**
     * resolve all pending (outgoing) transport events by composing appropriate server
     * messages or directly resolving local teleportation
     */
    private void processTransportQueue(){
        while(!transportQueue.isEmpty()){
            Envelope envelope = transportQueue.remove();
            Ball contents = envelope.contents();
            ballList.remove(contents);
            contents.becomeReleased();
            String boardAddress = envelope.getPrimaryAddress();
            String gadgetAddress = envelope.getSecondaryAddress();
            
            if(boardAddress.isEmpty()){
                //then it is a local event
                Vect vel = contents.vel();
                spawnBall(gadgetAddress, vel.x(), vel.y());
            } else {
                //otherwise the ball needs to be sent over the network
                String message = "";
                if(gadgetAddress.isEmpty()){
                    //if gadget is not specified, then the ball is being teleported
                    //by position
                    message = Messages.composeWallTeleportMessage(name,boardAddress,contents);
                }else{
                    //otherwise we address the message to the destination gadget
                    message = Messages.composePortalTeleportMessage(name,boardAddress,gadgetAddress,contents);
                }
                messageQueue.add(message);
            }
        }
    }
    
    /**
     * poll the message queue of this board
     * 
     * @return true if the board has messages that need to be sent over the
     *          network
     */
    public boolean hasPendingMessage(){
        return !messageQueue.isEmpty();
    }
    
    /**
     * please check hasPendingMessage() before calling! :D
     * grab a message from the message queue
     * 
     * @return the next message which should be send to the server
     */
    public String grabMessage(){
        return messageQueue.remove();
    }
    
    
    
    //Rendering methods!
    
    /**
     * Redraw the character grid with all objects
     */
    public void initGrid(){
        for(int ii = 0; ii < grid.length; ii++){
            for(int jj = 0; jj < grid[0].length; jj++){
                grid[ii][jj] = ' ';
            }
        }
        for(Wall wall: wallList){
            wall.draw(grid); //Draw walls on grid
        }
        for(Ball ball: ballList){
            ball.draw(grid);//Draw balls on grid
        }
        for(Collidable gadget: triggerableList){
            gadget.draw(grid); //Draw gadgets on grid
        }
    }
    
    /**
     * Print the character grid containing all board objects
     * to console
     */
    public void printGrid(){
        initGrid();
        String out = "";
        for(int ii= 0; ii < grid.length; ii++){
            for(int jj = 0; jj < grid[0].length; jj++){
                out += grid[ii][jj];
            }
            out += "\r\n";
        }
        System.out.println(out);
    }
    
    /**
     * @return a CollidableGraphic representation of this board, with all objects
     */
    public CollidableGraphic getGraphic(){
        if(vortex == null){
            return new BoardGraphic(ballList, wallList, triggerableList);
        }else{
            return new BoardGraphic(ballList, wallList, triggerableList, vortex);
        }
    }
    
    /**
     * generate a string describing the parameters and components of the board
     */
    @Override public String toString(){
        String boardParams = "Board:\n";
        boardParams += "name = " + name + "\n";
        boardParams += "g = " + g + ", mu = " + mu + ", mu2 = " + mu2 + "\n\n";
        
        String balls = "Balls:\n";
        for(Ball ball : ballList){
            balls += ball.toString() + "\n";
        }
        if(ballList.isEmpty()){
            balls+= "none\n";
        }
        balls += "\n";
        
        String walls = "Walls:\n";
        for(Wall wall : wallList){
            walls += wall.toString() + "\n";
        }
        walls += "\n";
        
        String gadgets = "Gadgets:\n";
        for(Triggerable gadget : triggerableList){
            gadgets += gadget.toString() + "\n";
        }
        if(triggerableList.isEmpty()){
            gadgets += "none\n";
        }
        gadgets += "\n";
        
        String triggers = "Triggers:\n";
        if(numberOfTriggerLinks() == 0){
            triggers += "none\n";
        } else {
            for(Triggerable gadget : triggerableList){
                if(gadget.numberOfTriggerLinks() > 0){
                    triggers += gadget.toString() + " ---> " + gadget.triggerListString() + "\n";
                }
            }
        }
        
        return boardParams + balls + walls + gadgets + triggers;
    }

}
