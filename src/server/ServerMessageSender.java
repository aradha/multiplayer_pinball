package server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;

import message.Messages;
import message.Messages.MessageType;

/**
 * class for handling of outgoing messages to clients
 * 
 * message types that can be handled here are:
 *          ball transport -- when a ball enters a board through an invisible wall
 *          link request   -- when a server command joins two boards, this message is
 *                            sent to both clients
 *          delink request -- when a player has to overwrite a previously connected wall,
 *                            this message is sent to the previously connected board               
 *          disconnection  -- messages are sent to all clients when a user disconnects
 *                            from the server
 *                            when this thread's client disconnects, the thread has to 
 *                            close its output and shut down
 */
public class ServerMessageSender implements Runnable{
    
    private final BlockingQueue<String> messagesToSend;
    private final Socket client;
    private final String clientName;
    
    /**
     * create a new message sender for communicating with the client
     * 
     * @param socket the socket of the client
     * @param input the queue through which other server threads will
     *          feed messages for the client
     */
    public ServerMessageSender(String clientName, Socket socket, BlockingQueue<String> input){
        this.clientName = clientName;
        this.client = socket;
        this.messagesToSend = input;
    }
    
    /**
     * loop while sending messages to the client - take messages from the input
     * queue and send them to the user. If the message indicates that this thread's
     * client has disconnected, the thread will instead terminate
     */
    public void run(){
        try{
            send(client);
        }catch(IOException e){
            //do something
        }
    }
    
    //method runs in a loop, checking its message queue and sending any messages
    //it finds to its client. if it receives a disconnect notification for this
    //thread's client, the thread will terminate
    private void send(Socket socket) throws IOException{
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        
        try{
            while(true){
                String message = messagesToSend.take().trim();
                String[] args = Messages.parseArguments(message);
                if(Messages.parseType(message) == MessageType.DISCONNECT){
                    if(args.length > 0 && args[0].equals(clientName)){
                        System.out.println(clientName + " has disconnected; closing output stream");
                        break;
                    }else {                   
                        //System.out.println("(message sent) " + message);
                        out.println(message);
                    }
                } else {
                    //System.out.println("(message sent) " + message); //debug
                    out.println(message);
                }
            }
        } catch(InterruptedException e){
            System.err.println("sender thread interrupted");
        } finally {
            //close output, socket
            out.close();
        }
        
    }
}
