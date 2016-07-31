package sim;

import graphics.CollidableGraphic;
import graphics.RectangleGraphic;

import java.awt.Color;
import java.util.LinkedList;
import java.util.Queue;

import physics.Geometry;
import physics.Vect;
import physics.LineSegment;
import sim.Ball;

/**
 * Absorber class - can be built at an x,y root position with a specified
 * width and height -- absorbs balls that collide with it.  Balls are released
 * when the Absorber becomes triggered
 */
public class Absorber extends Triggerable{
    
    private static final Vect LAUNCH_VECTOR = new Vect(0,-50);
    //this is the velocity that is imparted to launched balls
    public static final double LATENCY_PERIOD = 0.0; //TODO: CHECK LATENCY PERIOD - IF > 0 LOST BALL ERROR!
    //this is the number of seconds that must elapse between launches
    
    private LineSegment[] edges;
    //these correspond to the four edges of the absorber
    private Vect ballSlot;
    //rep invariant -- represents the position 0.25Lx0.25L from the 
    //                 bottom right corner(inside the absorber)
    private Vect ballRelease;
    //rep invariant -- represents the position 0.25L above the top edge
    //                 and 0.25L inside the right edge of the absorber
    private Queue<Ball> capturedBalls;
    //rep invariant -- contains those balls which are currently held 
    //                 by the absorber
    private double actionTimer = LATENCY_PERIOD;
    //keeps a count from the last time a ball was launched
    
    @Deprecated 
    public Absorber(int x, int y, int w, int h){
        this(x,y,w,h,"ABSORBER");
    }
    
    /**
     * construct a new absorber with an origin at the specified x, y position,
     * with specified width and height
     * 
     * @param x the x position of the absorber's origin
     * @param y the y position of the absorber's origin
     *          x,y must be >= 0 and < 20
     * @param width the width of the absorber
     *          x + w <= 20, w > 0
     * @param height the height of the absorber
     *          y + h <= 20, h > 0
     */
    public Absorber(int x, int y, int w, int h, String name) {
        super(x, y, w, h, 'X', name);
        capturedBalls = new LinkedList<Ball>();
        ballSlot = new Vect(x + w - Ball.DEFAULT_RADIUS, y + h - Ball.DEFAULT_RADIUS);
        ballRelease = new Vect(x + w - Ball.DEFAULT_RADIUS, y - Ball.DEFAULT_RADIUS);
        
        LineSegment[] edgeArray = {
                new LineSegment(x,y,x+w,y),
                new LineSegment(x+w,y,x+w,y+h),
                new LineSegment(x+w,y+h,x,y+h),
                new LineSegment(x,y+h,x,y)
        };
        edges = edgeArray;
    }
    
    /** 
     * @return the number of balls currently being held by this absorber
     */
    public int capturedBalls(){
        return capturedBalls.size();
    }
    
    /**
     * advance the absorber's trigger timer - the trigger timer has to
     * advance at least Absorber.LATENCY_PERIOD seconds between launches
     */
    @Override public void displace(double deltaT){
        actionTimer += deltaT;
    }
    
    /**
     * empty and reset the absorber
     */
    @Override public void reset(){
        super.reset();
        while(!capturedBalls.isEmpty()){
            capturedBalls.remove();
        }
    }
    
    /**
     * launch a captured ball, if this absorber is holding a ball
     * and the last ball was launched more than 0.05 seconds ago
     */
    @Override public void triggerAction() {
        if(actionTimer >= LATENCY_PERIOD){
            actionTimer = 0;
            if(!capturedBalls.isEmpty()){
                Ball toRelease = capturedBalls.remove();
                toRelease.becomeReleased();
                toRelease.moveTo(ballRelease);
                toRelease.impart(LAUNCH_VECTOR);
            }
        }
    }

    @Override public double timeUntilCollision(Ball ball) {
        double lowestTime = Double.POSITIVE_INFINITY;
        for(int ii = 0; ii < edges.length; ii++){
            double t = Geometry.timeUntilWallCollision(edges[ii], ball.toCircle(), ball.vel());
            lowestTime = t < lowestTime ? t : lowestTime;
        }
        return lowestTime;
    }

    @Override public void collideWith(Ball ball) {
        ball.becomeAbsorbed();
        ball.moveTo(ballSlot);
        capturedBalls.add(ball);
        becomeTriggered();
    }
    
    @Override public void draw(char[][] grid){
        for(int jj = 0; jj < grid.length; jj ++){
            for(int ii = 0; ii < grid[0].length; ii ++){
                if(this.isLocatedAtPosition(ii - 1, jj - 1)){
                    grid[jj][ii] = '=';
                }
            }
        }
    }
    
    /**
     * @return a string with the position and dimensions of the absorber
     */
    @Override public String toString(){
        return "absorber " + w + "x" + h + "(" +
                    x + "," + y + ")";
    }

    @Override
    public CollidableGraphic getGraphic() {
        //System.out.println(capturedBalls.size()); //TODO
        Color color = Color.green;
        if(triggerTimer() < 0.5){
            color = color.brighter();
        }
        return new RectangleGraphic(x,y,w,h, color);
    }

}
