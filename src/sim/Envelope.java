package sim;

/**
 * this is a wrapper class that packages a Ball object with two Strings,
 * representing the destination address of the Ball.  This is for use with
 * teleportation features
 */
public class Envelope {
    
    private Ball ball;
    //this is the ball to be transported
    private String primaryAddress;
    //this is the name of the board to which the ball should be transported
    private String secondaryAddress;
    //optional! this this the name of the gadget (if applicable) to which the
    //ball should be transported
    
    /**
     * generate a new Envelope containing a ball, with only a primary address
     * 
     * @param ball the ball to be transported
     * @param primaryAddress the destination board
     */
    public Envelope(Ball ball, String primaryAddress){
        this(ball,primaryAddress,"");
    }
    
    /**
     * generate a new Envelope containing a ball, with both the primary and secondary
     * addresses specified
     * 
     * @param ball the ball to be transported
     * @param primaryAddress the destination board
     * @param secondaryAddress the destination gadget
     */
    public Envelope(Ball ball, String primaryAddress, String secondaryAddress){
        this.ball = ball;
        this.primaryAddress = primaryAddress;
        this.secondaryAddress = secondaryAddress;
    }
    
    /**
     * @return the ball in the envelope
     */
    public Ball contents(){
        return ball;
    }
    
    /**
     * @return the envelope's primary address
     */
    public String getPrimaryAddress(){
        return primaryAddress;
    }
    
    /**
     * @return the envelope's secondary address
     */
    public String getSecondaryAddress(){
        return secondaryAddress;
    }
    
}
