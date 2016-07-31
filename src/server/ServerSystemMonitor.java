package server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.regex.Pattern;
import java.util.Map;

import sim.Wall.WallType;
import message.Messages;

/**
 * class for handling of commands entered at the server via System.in
 *
 * messages are of type:
 *          link/(delink?) message - link two boards together by board name
 *          
 * System Monitor Grammar
 * link ::= (v|h)\s+name\s+name\s*
 * name ::= [A-Za-z_][A-Za-z0-9_]*
 */
public class ServerSystemMonitor implements Runnable {
    
    private Pattern commandPattern = Pattern.compile(
            "(v|h)\\s+[A-Za-z_]\\w*\\s+[A-Za-z_]\\w*"
            );
    
    private Map<String, BlockingQueue<String>> threadMap;
    
    /**
     * create a new monitor for the System input stream - used for executing user
     * commands to the server
     */
    public ServerSystemMonitor(Map<String, BlockingQueue<String>> map){
        this.threadMap = map;
    }
    
    /**
     * monitor System.in for executable commands. if an invalid command is received,
     * or if the names specified by the command are not in play, print a helpful message
     * to System.err. if the command is valid, compose messages and give them to the
     * appropriate clientSender threads by searching the threadMap.
     */
    public void run(){
        try{
            monitor();
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    
    //decompose input commands and, in the case of invalid or impossible commands,
    //inform the user. for valid commands, compose messages and give them to the 
    //appropriate message sending threads
    private void monitor() throws IOException{
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        
        try{
            for (String line = in.readLine().trim(); line != null; line = in.readLine()) {
                if(validCommand(line)){//if command is executable; otherwise discard and try again
                    String[] tokens = line.split("\\s+");
                    String commandWord = tokens[0];
                    String name1 = tokens[1];
                    String name2 = tokens[2];//this array access is safe for valid commands
                    WallType type1 = WallType.LEFT;
                    WallType type2 = WallType.RIGHT;
                    if(commandWord.equals("v")){//commandWord is either 'v' or 'h'
                        type1 = WallType.TOP;
                        type2 = WallType.BOTTOM;
                    }
                    synchronized(threadMap){
                        if(threadMap.get(name1) == null){
                            System.err.println("ERROR: no player by name " + name1);
                        } if(threadMap.get(name2) == null){
                            System.err.println("ERROR: no player by name " + name2);
                        } if(threadMap.get(name1) != null && threadMap.get(name2) != null){
                            threadMap.get(name1).add(Messages.composeLinkMessage(
                                    "Server", name1, name2, type1));
                            threadMap.get(name2).add(Messages.composeLinkMessage(
                                    "Server", name2, name1, type2));
                        }
                    }
                }else{
                    System.err.println("bad command");
                }
            }
        }finally{
            in.close();
        }
    }
    
    //confirms that an input string conforms to the server command grammar
    private boolean validCommand(String command){
        return commandPattern.matcher(command).matches();
    }
}
