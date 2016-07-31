package sim;

import graphics.BallGraphic;

import graphics.CollidableGraphic;

import java.awt.Color;
import java.util.Queue;

import physics.Vect;

/**
 * Portal class
 * 
 * can be connected to other portals; even portals on other boards.
 * balls that collide with open portals will be transported and will exit
 * from a different portal.
 * 
 * closed portals will not collide with balls
 */
public class Portal extends CircleBumper {
    
    private Queue<Envelope> transportQueue;
    //this is the queue used to communicate transport events to the Board
    
    private boolean open;
    //this describes the state of the portal. Open portals will teleport
    //balls, and closed portals can only act as exits
    
    private final String board;
    //the name of the board to which this portal links. If this parameter
    //is left empty, the portal is local (the exit is on the same board)
    private final String otherPortal;
    //the name of the exit portal for this portal. This is the portal from
    //which balls that enter this portal will emerge
    
    /**
     * create a portal at position x,y with the specified parameters
     * 
     * @param x the x position
     * @param y the y position
     * @param name the name of this gadget
     * @param board the board to which this portal connects.  If the portal should
     *          be local (that is, if the exit portal is on the same board), then
     *          this field should be an empty string
     * @param otherPortal the portal to which this portal connects.  This is the
     *          name of the exit portal to which balls will be transported
     * @param transportQueue the Envelope queue for communicating transport events
     */
    public Portal(int x, int y, String name, String board, String otherPortal, Queue<Envelope> transportQueue) {
        super(x, y, name);
        this.open = false;
        this.board = board;
        this.otherPortal = otherPortal;
        this.transportQueue = transportQueue;
    }
    
    /**
     * open the portal
     * 
     * an open portal will transport balls that collide with it
     */
    public void open(){
        open = true;
    }
    
    /**
     * close the portal
     * 
     * a closed portal will not collide with balls and can only act
     * as an exit
     */
    public void close(){
        open = false;
    }
    
    /**
     * used for testing.  Returns status of portal (open/closed)
     * @return boolean, true if open, false if closed.
     */
    public boolean isOpen(){
        return open;
    }
    
    /**
     * @return the name of the board to which this portal links, or "" if this
     *          is a local portal
     */
    public String exitBoard(){
        return board;
    }
    
    /**
     * @return the name of the exit portal for this portal
     */
    public String exitPortal(){
        return otherPortal;
    }
    
    /**
     * create and return a new ball given the specified velocity. the resultant 
     * velocity may differ if a zero length velocity is provided
     * 
     * @param velocity the velocity of the ball to be generated
     * @return a ball which will be launched from this portal
     */
    public Ball spawnBall(Vect velocity){
        Vect vel = velocity;
        if(vel.length() == 0){
            vel = new Vect(0, 5);
        }
        Vect disp = vel.times(0.25/vel.length());
        Vect loc = new Vect(x+0.5,y+0.5).plus(disp);
        return new Ball(loc.x(), loc.y(), vel);
    }
    
    /**
     * move the argument ball so that it will spawn from the portal.
     * the ball's velocity should not be changed unless the ball has zero velocity
     * 
     * @param ball the ball to be spawned
     * @return the ball
     */
    public Ball spawnBall(Ball ball){
        Vect vel = ball.vel();
        if(vel.length() == 0){
            ball.impart(new Vect(0, 5));
        }
        Vect disp = vel.times(0.25/vel.length());
        Vect loc = new Vect(x+0.5,y+0.5).plus(disp);
        ball.moveTo(loc);
        return ball;
    }
    
    /**
     * @return the time until a ball will collide with this portal, if the
     *          portal is open.  if the portal is closed, returns Double.POSITIVE_INFINITY
     */
    @Override public double timeUntilCollision(Ball ball){
        if(open){
            return super.timeUntilCollision(ball);
        }else{
            return Double.POSITIVE_INFINITY;
        }
    }
    
    /**
     * if the portal is open, teleport it
     */
    @Override public void collideWith(Ball ball){
        if(open){
            ball.moveTo(x+0.5,y+0.5);
            ball.becomeAbsorbed();
            transportQueue.add(new Envelope(ball, board, otherPortal));
            this.becomeTriggered();
        }
    }
    
    /**
     * @return a color-coded CollidableGraphic that represents this Portal's
     *          position and state on the board
     */
    @Override public CollidableGraphic getGraphic(){
        Color color = open ? Color.orange : Color.blue;
        return new BallGraphic(x+0.5,y+0.5,RADIUS,color);
    }

}
