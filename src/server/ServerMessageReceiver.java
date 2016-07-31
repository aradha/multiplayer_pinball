package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.Map;
import java.util.Set;

import sim.Wall;
import message.Messages;
import message.Messages.MessageType;

/**
 * class for handling of incoming traffic from clients
 * 
 * message types that can be handled here are:
 *          ball transport -- when a ball leaves a board through an invisible wall
 *          delink message -- when a player has to overwrite a previously connected wall,
 *                            this message is sent to the severed board
 *          disconnection  -- not really a message, but when the client disconnects,
 *                            that information has to propagate to other users.
 */
public class ServerMessageReceiver implements Runnable{
    
    private final String clientName;
    private final Map<String, BlockingQueue<String>> threadMap;
    private final BufferedReader in;
 
    /**
     * construct a message receiver for parsing and routing messages received
     * from the client
     * 
     * @param clientName -- the name of this thread's client ('s board)
     * @param inputReader -- the inputStream for this thread's client
     * @param threadMap -- a mapping from client names to sender queues, for 
     *                      routing messages
     */
    public ServerMessageReceiver(String clientName, BufferedReader inputReader, 
            Map<String, BlockingQueue<String>> map){
        this.clientName = clientName;
        this.threadMap = map;
        this.in = inputReader;
    }
    
    /**
     * loop while monitoring the input stream - checks that messages conform to
     * messaging grammar and searches for the input queue of each message's 
     * listed recipient. If found, the message will be added to the recipient's sender
     * queue. Otherwise, the message is discarded.
     * 
     * if this thread's client disconnects, the thread will compose disconnect
     * notifications and send them to all clients. The sending queue associated with
     * this thread's client will be removed from the shared map, and the thread will
     * terminate.
     */
    public void run(){
        try{
            greet();
            try{
                for(String message = in.readLine(); message != null; message = in.readLine()){
                    message = message.trim();
                    //System.out.println("(message received) " + message); //debug
                    //thread simply forwards all messages to addressed recipient
                    if(Messages.isValidMessage(message)){
                        String recipient = Messages.parseReceiver(message);
                        if(recipient.equals("Server")){
                            
                            //if this is a message for the server, compose and fwd a response
                            if(Messages.parseType(message) == MessageType.HELLO){
                                greet();
                            } else if(Messages.parseType(message) == MessageType.LINK){
                                String sender = Messages.parseSender(message); //Guy who sent the message
                                String wallType = Messages.parseArguments(message)[1]; //To whom sender wants to connect
                                String receiver = Messages.parseArguments(message)[0];
                                //TODO
                                //TODO
                                //TODO CHECK IF RECEIVER, SENDER NAMES ARE VALID!
                                String forward = Messages.composeLinkMessage(sender, receiver, 
                                        sender, Wall.oppositeType(Wall.stringToWallType(wallType)));
                                forwardMessage(receiver, forward);
                                
                                String back = Messages.composeLinkMessage(receiver, sender, 
                                        receiver, Wall.stringToWallType(wallType));
                                forwardMessage(sender, back);                                
                            }                                                        
                        } else {
                            //otherwise forward the message to the appropriate client
                            forwardMessage(recipient, message);
                        }
                    }
                }
            }finally{
                synchronized(threadMap){
                    //inform other clients of the disconnect and then remove the appropriate
                    //sender queue from the common threadMap
                    for(String client : threadMap.keySet()){
                        threadMap.get(client).add(
                                Messages.composeDisconnectMessage("Server", client, this.clientName));
                    }
                    threadMap.remove(this.clientName);
                }
                //don't bother closing the input stream; the sender thread will close the socket
                //in.close();
                System.out.println("receiver for client " + this.clientName + " has stopped");
            }
        }catch(IOException e){
        }
    }
    
    //compose and forward a Server greeting to this thread's client
    private void greet(){
        forwardMessage(clientName, composeHelloReply());
    }
    
    //compose a server greeting for this thread's client
    private String composeHelloReply(){
        synchronized(threadMap){
            Set<String> clients = threadMap.keySet();
            return Messages.composeServerHello("Server", clientName, clients);
        }
    }
    
    //forward a message to the sender thread of the specified client
    private void forwardMessage(String clientRecipient, String message){
        //forward the message to the appropriate client
        synchronized(threadMap){
        
            if(threadMap.get(clientRecipient) != null){
                //forward to sender
                threadMap.get(clientRecipient).add(message);
            }
        }
    }
}
