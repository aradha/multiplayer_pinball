package client;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import client.PingballClient.KillSwitch;
import message.Messages;
import message.Messages.MessageType;

/**
 * monitors a message queue which is fed by the main client thread
 * and sends those messages to the server.
 * 
 * The types of messages that can be sent from here are:
 *          client hello   -- send a poll to the server
 *          wall transport -- when a ball enters the board through an invisible wall
 *          portal query   -- when we need a list of portals on another board
 *          portal reply   -- when a list of portals from our board has been requested
 *          portal teleport-- when a ball is leaving our board through a portal
 *          delink         -- when this board is being joined to or disconnected from 
 *                            another across a wall.       
 */
public class ClientMessageSender implements Runnable{
    
    private final BlockingQueue<String> messagesToSend;
    private final Socket server;
    private final String name;
    private final KillSwitch killSwitch;
    
    
    /**
     * create a new message sender to send messages from a PingballClient to
     * a PingballServer
     * 
     * @param name the name of the client's board
     * @param socket the socket of the server
     * @param input the queue through which the primary thread will feed
     *          messages for the server
     */
    @Deprecated
    public ClientMessageSender(String name, Socket socket, BlockingQueue<String> input){
        this(name,socket,input,null);
    }
    
    /**
     * create a new message sender to send messages from a PingballClient to
     * a PingballServer
     * 
     * @param name the name of the client's board
     * @param socket the socket of the server
     * @param input the queue through which the primary thread will feed
     *          messages for the server
     * @param killSwitch the switch that kills this thread
     */
    public ClientMessageSender(String name, Socket socket, BlockingQueue<String> input, KillSwitch killSwitch){
        this.name = name;
        this.server = socket;
        this.messagesToSend = input;
        this.killSwitch = killSwitch;
    }
    
    /**
     * sends a hello message to the server to confirm the connection, and then
     * loops while taking messages from this thread's input queue to send them
     * to the server
     * 
     * loops until it receives a poison pill, in the form of a self-referential
     * disconnect message
     */
    public void run(){
        try{
            PrintWriter out = new PrintWriter(server.getOutputStream(),true);
            try{
                //loop while sending messages from the client to the server
                //periodically send poll messages to the server to update
                //the client list
                while(true){
                    greet(out);
                    if(!send(out, 1000)){
                        break;
                    }
                }
            }finally{
                System.err.println("Sender stopped");
            }
        }catch(IOException e){
            System.err.println(e.getMessage());
            //handle it
        }
    }
    
    //send a hello message through the specified output stream
    private void greet(PrintWriter out) throws IOException{
        String hello = Messages.composeClientHello(name, "Server");
        //System.out.println(hello);
        out.println(hello);
    }
    
    //loop for some length of time and send messages from the input queue through the 
    //specified output stream
    //returns false if and only if it finds a poison pill message -- if the thread should die
    private boolean send(PrintWriter out, long pollTime) throws IOException{
        try{
            long stopTime = System.currentTimeMillis() + pollTime;
            while(System.currentTimeMillis() < stopTime){
                if(killSwitch.check()){
                    System.err.println("Sender kill switch tripped");
                    return false;
                }
                String message = messagesToSend.poll(100, TimeUnit.MILLISECONDS);
                if(message != null){
                    //System.err.println(message); //debug
                    if(Messages.parseType(message) == MessageType.DISCONNECT){
                        if(Messages.parseArguments(message)[0].equals(name)){
                            //poison pill; just exit the loop.
                            return false;
                        }
                    }
                    out.println(message);
                }
            }
            return true;
        } catch(InterruptedException e){
            e.printStackTrace();
            return true;
        }
    }
    
}
