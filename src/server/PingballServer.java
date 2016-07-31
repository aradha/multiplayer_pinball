package server;

import java.io.*;
import java.net.*;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import message.Messages;
import message.Messages.MessageType;

/**
 * Class for running a server to route messages and handle network features
 * of the pingball program
 * 
 * 
 *                       ***Server Thread safety argument***
 *  each client socket is shared between two threads -- of the two, one will
 *  have exclusive responsibility for sending messages, and the other will have
 *  exclusive responsibility for receiving messages.
 *      side note: the main server loop does open an input stream to the client's socket
 *                 before spinning new threads. It drops this reference once the send/
 *                 receive threads are spun
 *  
 *  Each ServerMessageSender has an input queue which is accessible to all 
 *  ServerMessageReceivers and the ServerSystemMonitor. these queues use a thread-safe
 *  data type.
 *  
 *  The input queues will be kept in a map that is shared between multiple threads
 *  all threads will synchronize on this map before using it.
 *  
 *  The messages themselves will be strings, a thread-safe type
 */
public class PingballServer {
    
    private static final int DEFAULT_PORT = 10987;
    
    private final ServerSocket serverSocket;
    private final Map<String, BlockingQueue<String>> threadMap;
    //this Map links a board name to the input queue of the thread
    //that is responsible for sending messages to that client
    
    /**
     * construct a new server instance on the specified port
     * 
     * @param port the port number to be used -- 0 <= port <= 65535
     * @throws IOException
     */
    public PingballServer(int port) throws IOException{
        serverSocket = new ServerSocket(port);
        threadMap = new HashMap<String, BlockingQueue<String>>();
        Thread inputMonitor = new Thread(new ServerSystemMonitor(threadMap));
        inputMonitor.start();
    }
    
    /**
     * sits in a loop accepting new connections. Spins new sender/receiver threads 
     * to handle communication with each accepted client. Accepted connections are 
     * expected to provide a valid "Hello" message; otherwise they will be ignored. 
     * 
     * @throws IOException if the server socket is broken
     */
    private void serve() throws IOException{
        while(true){
            Socket clientSocket = serverSocket.accept();
            clientSocket.setSoTimeout(2000);//enable timeout for main server loop
            try{
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                String clientHello = in.readLine().trim();
                if(Messages.parseType(clientHello) != MessageType.HELLO){
                    //if this is not a good hello message, close the stream
                    //and throw an exception(returning to beginning of while() loop)
                    clientSocket.close();
                    throw new IllegalArgumentException();
                }
                String clientName = Messages.parseSender(clientHello);
                synchronized(threadMap){
                    if(threadMap.containsKey(clientName)){
                        clientSocket.close();
                        throw new IllegalArgumentException("a client with that name is already on the server");
                    }
                }
                System.out.println("\na client has connected: " + clientName);
                clientSocket.setSoTimeout(0);//reset timeout value
                //make an sending queue for this client
                BlockingQueue<String> senderQueue = new LinkedBlockingQueue<String>();
                //add queue to shared map
                synchronized(threadMap){
                    threadMap.put(clientName, senderQueue);
                }
                //spin handler threads
                Thread sender = new Thread(new ServerMessageSender(clientName, clientSocket, senderQueue));
                sender.start();
                Thread receiver = new Thread(new ServerMessageReceiver(clientName, in, threadMap));
                receiver.start();
            } catch(SocketTimeoutException e){
                System.err.println("a client timed out while attempting to connect");
            } catch(IllegalArgumentException e){
                e.printStackTrace();
                System.err.println("bad hello");
            }
        }
    }
    
    /**
     * make a server on the specified port (default 4444) and begin serving
     * 
     * to open the server on a specific port, the optional argument
     * [--port PORT] can be used, where 0 <= port <= 65535
     * 
     * @param args
     */
    public static void main(String[] args){
        Queue<String> arguments = new LinkedList<String>(Arrays.asList(args));
        int port = DEFAULT_PORT;//default port
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
                    }else{
                        throw new IllegalArgumentException("--port flag expected");
                    }
                }
            }catch (NoSuchElementException e) {
                throw new IllegalArgumentException("missing argument for " + flag);
            } catch(NumberFormatException e) {
                System.err.println("unable to parse number for \"" + flag + "\"");
            }
        }catch (IllegalArgumentException e){
            System.err.println(e.getMessage());
            System.err.println("usage: PingballServer [--port PORT]");
        }
        try{
            PingballServer server = new PingballServer(port);
            server.serve();
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}
