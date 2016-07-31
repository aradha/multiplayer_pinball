package message;

import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import physics.Vect;
import physics.Geometry.VectPair;
import sim.Ball;
import sim.Wall.WallType;

/**
 * this class is used to compose and decompose messages passed between pingball 
 * clients and their servers
 * 
 * Message grammar:
 *      name      ::= [a-zA-Z_][a-zA-Z_0-9]* 
 *      arguments ::= \:([a-zA-Z_0-9.-]+' ')*([a-zA-Z_0-9.-]+)?
 *      routing   ::= name--->name
 *      message   ::= routing type arguments
 *      wall      ::= left|right|top|bottom
 *      double    ::= -?[0-9]+(\.[0-9]+)?(E-?[0-9]+)?
 *  
 *  client hello arguments:
 *      arguments ::= ''          //none
 *      
 *  server hello arguments:
 *      arguments ::= (name\s+)* name  //there must be at least one person connected...
 *  
 *  wall teleport arguments:
 *      arguments ::= xpos ypos xvel yvel
 *                ::= (double){4}\s*
 *                
 *  portal teleport arguments:
 *      arguments ::= name xvel yvel
 *                ::= name (double){2}\s*
 *                
 *  portal poll arguments:
 *      arguments ::= ''          //none
 *      
 *  portal response arguments:
 *      arguments ::= (name\s+)* name?
 *                
 *  link/delink request arguments:
 *      arguments ::= name\s+ wall
 *      
 *  disconnection notice arguments:
 *      arguments ::= name
 *      
 *  connection notice arguments:
 *      arguments ::= name
 *      
 */
public class Messages {
    
    public enum MessageType {
        HELLO, WALL_TELEPORT, PORTAL_TELEPORT, PORTAL_QUERY, 
        PORTAL_REPLY, LINK, DELINK, DISCONNECT, CONNECTION, INVALID
        };
    
    /*
     * message grammar
     * name      ::= [a-zA-Z_][a-zA-Z_0-9]*
     * routing   ::= name--->name
     * type      ::= \:[A-Z]+
     * arguments ::= \:([a-zA-Z_0-9.-]+ ' ')*([a-zA-Z_0-9.-]+)?
     * message   ::= routing type arguments
     *           ::= [a-zA-Z_]\w*--->[a-zA-Z_]\w*\:[A-Z]+\:([a-zA-Z_0-9.-]+\s+)*([a-zA-Z_0-9.-]+)?
     */
    private static Pattern namePattern = Pattern.compile(
            "[a-zA-Z_]\\w*"
            );
    private static Pattern messagePattern =Pattern.compile(
            "[a-zA-Z_]\\w*--->[a-zA-Z_]\\w*\\:[A-Z_]+\\:([a-zA-Z_0-9\\.\\-]+\\s+)*([a-zA-Z_0-9\\.-]+)?"
            );
    /*
     * ball transport argument grammar
     * double    ::= -?[0-9]+(\.[0-9]+)?(E-?[0-9]+)?
     * arguments ::= xpos ypos xvel yvel
     *           ::= (double\\s+){3}double\s*
     *           ::= (-?[0-9]+(\.[0-9]+)?(E-?[0-9]+)?\s+){3}-?[0-9]+(\.[0-9]+)?(E-?[0-9]+)?\s*
     */
    private static Pattern wallTeleportArgumentPattern = Pattern.compile(
            "(-?[0-9]+(\\.[0-9]+)?(E-?[0-9]+)?\\s+){3}-?[0-9]+(\\.[0-9]+)?(E-?[0-9]+)?\\s*"
            );
    
    private static String nameSeparator = "--->";
    
    /**
     * don't use this constructor
     */
    public Messages(){
        throw new IllegalArgumentException("no one should be making instances");
    }
    
    //helper method -- checks that names are valid and composes the routing prefix
    //for a new message
    private static String composeBlankMessage(String sender, String receiver){
        if(!isValidName(sender)){
            throw new IllegalArgumentException(sender + " is not a legal name");
        }
        if(!isValidName(receiver)){
            throw new IllegalArgumentException(receiver + " is not a legal name");
        }
        return sender + nameSeparator + receiver;
    }
    
    /**
     * compose a new client hello message
     * 
     * @param sender the name of the greeter
     * @param receiver the name of the greeted
     * @return a properly formatted hello message from [sender] to [receiver]
     * @throws IllegalArgumentException if the passed names do not match the
     *          message formatting rules
     */
    public static String composeClientHello(String sender, String receiver){
        return composeBlankMessage(sender,receiver) + ":HELLO:";
    }
    
    /**
     * Compose a Server hello message, which includes a list of clients
     * connected to the server
     * 
     * @param server the name of the server
     * @param client the recipient for this message
     * @param clientList a Set containing the names of every client currently connected
     *          to the server
     * @return a properly formatted Server Hello message to the specified client,
     *          using the specified client list
     */
    public static String composeServerHello(String server, String client, Set<String> clients){
        String message = composeBlankMessage(server,client) + ":HELLO:";
        for(String name : clients){
            message += name + " ";
        }
        return message;
    }
    
    /**
     * compose a new Wall teleport message with the specified ball
     * 
     * @param sender the source of the ball
     * @param receiver the destination of the ball
     * @param ball the ball which is being transported
     * @return a ball message containing the ball's physical parameters
     */
    public static String composeWallTeleportMessage(String sender, String receiver, Ball ball){
        Vect pos = ball.toCircle().getCenter();
        Vect vel = ball.vel();
        String arguments = pos.x() + " " + pos.y() + " " + vel.x() + " " + vel.y();
        return composeBlankMessage(sender,receiver) + ":WALL_TELEPORT:" + arguments;
    }
    
    /**
     * Compose a Portal Teleport message to transport a ball to the named portal on the
     * named client's board
     * 
     * @param sender the sender of the ball
     * @param receiver the board to which the ball is being teleported
     * @param portal the name of the portal from which the ball should exit
     * @param ball the ball to be teleported
     * @return a network message to teleport the ball
     */
    public static String composePortalTeleportMessage(String sender, String receiver, 
            String portal, Ball ball){
        Vect vel = ball.vel();
        String arguments = portal + " " + vel.x() + " " + vel.y();
        return composeBlankMessage(sender,receiver) + ":PORTAL_TELEPORT:" + arguments;
    }
    
    /**
     * Compose a message to poll the portal names on another user's board
     * 
     * @param sender the sender of the message
     * @param receiver the receiver of the message
     * @return a portal query message
     */
    public static String composePortalQueryMessage(String sender, String receiver){
        return composeBlankMessage(sender,receiver) + ":PORTAL_QUERY:";
    }
    
    /**
     * Compose a message listing the portals of a user's board
     * 
     * @param sender the user of whose board the portals are to be listed
     * @param receiver the recipient for the message
     * @param portals the list of portal names from sender's board
     * @return a network message string describing the portals of sender's board
     */
    public static String composePortalResponseMessage(String sender, String receiver, List<String> portals){
        String arguments = "";
        for(String portalName : portals){
            arguments += portalName + " ";
        }
        return composeBlankMessage(sender,receiver) + ":PORTAL_REPLY:" + arguments;
    }
    
    /**
     * compose a new link request message
     * 
     * @param sender the source of the message -- 'server'
     * @param receiver the recipient of the message
     * @param otherBoard the player with which to link boards
     * @return a formatted message requesting a board link
     */
    public static String composeLinkMessage(String sender, String receiver, 
            String otherBoard, WallType connectAcross){
        return composeBlankMessage(sender,receiver) + ":LINK:" + otherBoard + " " + connectAcross;
    }
    
    // "" delink ""....
    public static String composeDelinkMessage(String sender, String receiver, 
            String otherBoard, WallType toDisconnect){
        return composeBlankMessage(sender,receiver) + ":DELINK:" + otherBoard + " " + toDisconnect;
    }
    
    /**
     * compose a new disconnect notification message
     * 
     * @param sender the source of the message -- should almost always be 'server'
     * @param receiver the recipient of the message
     * @param disconnectedUser the user that has disconnected
     * @return a formatted message from sender to receiver, informing the receiver
     *          of disconnectedUser's disconnection
     */
    public static String composeDisconnectMessage(String sender, String receiver, String disconnectedUser){
        return composeBlankMessage(sender,receiver) + ":DISCONNECT:" + disconnectedUser;
    }
    
    //"" connection ""......
    public static String composeConnectionMessage(String sender, String receiver, String newlyConnectedUser){
        return composeBlankMessage(sender,receiver) + ":CONNECTION:" + newlyConnectedUser;
    }
    
    /**
     * check that a name meets the message sending criteria
     * 
     * @param name the name to be checked
     * @return true iff [name] matches the format of a valid name
     */
    public static boolean isValidName(String name){
        return namePattern.matcher(name).matches();
    }
    
    /**
     * check that a message has the correct formatting for the pingball
     * client/server protocol
     * 
     * @param message the message to be checked
     * @return true if the message meets the formatting rules for client/server
     *          messages
     */
    public static boolean isValidMessage(String message){
        if(messagePattern.matcher(message).matches()){
            String type = message.split(":")[1];
            return isValidType(type);
        }
        return false;
    }
    
    //private helper method -- checks that a message's type is valid
    private static boolean isValidType(String type){
        for(MessageType mType : MessageType.values()){
            if(type.equals("" + mType)){
                return !type.equals("INVALID");
            }
        }
        return false;
    }
    
    /**
     * extract the type information from a message string
     * 
     * @param message the message
     * @return the type of the message, if it is a valid type,
     *          or INVALID if this message's type is not valid
     */
    public static MessageType parseType(String message){
        if(isValidMessage(message)){
            String[] split = message.split(":");
            String token = split.length > 1 ? split[1]:"INVALID";
            for(MessageType type : MessageType.values()){
                if(token.equals("" + type)){
                    return type;
                }
            }
        }
        return MessageType.INVALID;
    }
    
    /**
     * parse the name of a message's sender
     * 
     * @param message a String message of the format composed by Messages' static
     *          methods
     * @return the name of the sender of this message
     *          IllegalArgumentException will be thrown if this message does not
     *          meet the Pingball network protocol
     */
    public static String parseSender(String message){
        if(!isValidMessage(message)){
            throw new IllegalArgumentException("invalid message format");
        }
        String name = message.split(nameSeparator)[0];
        if(isValidName(name)){
            return name;
        }else{
            throw new IllegalArgumentException("invalid message");
        }
    }
    
    /**
     * parse the intended recipient of a message
     * 
     * @param message the message to be parsed
     * @return the name of the recipient of the message
     *          IllegalArgumentException will be thrown if this message does not
     *          meet the Pingball network protocol
     */
    public static String parseReceiver(String message){
        if(isValidMessage(message)){
            String substring = message.split(nameSeparator)[1];
            return substring.split(":")[0];
        }else{
            throw new IllegalArgumentException("invalid message");
        }
    }
    
    /**
     * extract the tokenized arguments from a message
     * 
     * @param message the message whose arguments are to be extracted
     * @return the tokenized arguments of this message
     *          IllegalArgumentException will be thrown if this message does not
     *          meet the Pingball network protocol
     */
    public static String[] parseArguments(String message){
        if(isValidMessage(message)){
            if(message.split(":").length > 2){
                String arguments = message.split(":")[2];
                return arguments.split("\\s+");
            } else {
                return new String[0];
            }
        }else{
            throw new IllegalArgumentException("invalid message");
        }
    }
    
    /**
     * extract position and velocity vectors from a wall teleport message
     * 
     * @param wallTeleportMessage the WALL_TELEPORT type message that contains the position
     *          and velocity vectors
     * @return a vectpair (p,v) where p is the position vector and v is the 
     *          velocity vector
     */
    public static VectPair parseWallTeleportArguments(String wallTeleportMessage){
        if(parseType(wallTeleportMessage) != MessageType.WALL_TELEPORT){
            throw new IllegalArgumentException("invalid message");
        }
        if(!hasValidBallArguments(wallTeleportMessage)){
            throw new IllegalArgumentException("invalid message");
        }
        
        String[] args = parseArguments(wallTeleportMessage);
        Vect position = new Vect(Double.parseDouble(args[0]), Double.parseDouble(args[1]));
        Vect velocity = new Vect(Double.parseDouble(args[2]), Double.parseDouble(args[3]));
        return new VectPair(position, velocity);
    }
    
    //checks that the given string message has valid routing prefix, arguments, and type
    //for a ball message
    private static boolean hasValidBallArguments(String ballMessage){
        if(!isValidMessage(ballMessage)){
            return false;
        }else if(ballMessage.split(":").length < 2){
            return false;
        }
        String args = ballMessage.split(":")[2];
        return wallTeleportArgumentPattern.matcher(args).matches();
        
    }

}
