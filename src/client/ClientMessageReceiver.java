package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;

import client.PingballClient.KillSwitch;
import message.Messages;
import message.Messages.MessageType;

/**
 * class monitors a socket for incoming messages and feeds a
 * message queue consumed by the main client thread
 * 
 * types of messages that can be received:
 *          server hello   -- when we have polled the server for a list of clients
 *          wall transport -- when a ball enters the board through an invisible wall
 *          portal query   -- when another board is requesting a list of portals from this board
 *          portal reply   -- when we've requested a portal list from another board
 *          portal teleport-- when a ball has teleported to a portal on our board
 *          link/delink    -- when this board is being joined to or disconnected from 
 *                            another across a wall.
 *          disconnect     -- when another pingball board disconnects from the server
 */
public class ClientMessageReceiver implements Runnable{
    
    private BlockingQueue<String> messagesReceived;
    private Socket server;
    private String name;
    private KillSwitch killSwitch;
    
    private Set<String> boardList = new HashSet<String>();;
    
    /**
     * construct a new message receiver to monitor messages from the server
     * 
     * @param socket the server socket
     * @param output the queue through which to communicate with the primary thread
     */
    @Deprecated
    public ClientMessageReceiver(String name, Socket socket, BlockingQueue<String> output){
        this(name,socket,output,null);
    }
    
    /**
     * construct a new message receiver to monitor messages from the server
     * 
     * @param name the name of this client
     * @param socket the server socket
     * @param output the queue through which to communicate with the primary thread
     * @param killSwitch the switch that kills this thread
     */
    public ClientMessageReceiver(String name, Socket socket, BlockingQueue<String> output, KillSwitch killSwitch){
        this.name = name;
        this.server = socket;
        this.messagesReceived = output;
        this.killSwitch = killSwitch;
    }
    
    /**
     * loop while monitoring the socket -- forwards messages to a companion thread
     * through a message queue
     */
    public void run(){
        try{
            BufferedReader in = new BufferedReader(new InputStreamReader(server.getInputStream()));
            while(true){
                try{
                    server.setSoTimeout(100);
                    String line = in.readLine();
                    //System.err.println(line); //debug
                    if(Messages.parseType(line) == MessageType.HELLO){
                        updateBoardList(line);
                    } else {
                        
                        if(Messages.parseType(line) == MessageType.DISCONNECT){
                            boardList.remove(Messages.parseArguments(line)[0]);
                        }
                        messagesReceived.add(line);
                    }
                } catch(SocketTimeoutException ste) {
                    if(killSwitch.check()){
                        System.err.println("Receiver kill switch tripped");
                        String poisonPill = Messages.composeDisconnectMessage(name, name, name);
                        messagesReceived.add(poisonPill);//inform the client of the disconnect
                        return;
                    }
                }
            }
        }catch(IOException e){
            //handle it
            e.printStackTrace();
        }finally{
            String poisonPill = Messages.composeDisconnectMessage(name, name, name);
            messagesReceived.add(poisonPill);//inform the client of the disconnect
            System.err.println("Receiver stopped unexpectedly");
        }
    }
    
    //based on a server hello message, update the list of other users and notify
    //the client when new connections appear
    private void updateBoardList(String serverHelloMessage){
        String[] boards = Messages.parseArguments(serverHelloMessage);
        for(int ii = 0; ii < boards.length; ii ++){
            if(!boardList.contains(boards[ii]) && !boards[ii].equals(name)){
                messagesReceived.add(Messages.composeConnectionMessage(name, name, boards[ii]));
                boardList.add(boards[ii]);
            }
        }
    }
}
