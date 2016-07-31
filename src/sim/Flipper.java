package sim;

import graphics.CollidableGraphic;
import graphics.FlipperGraphic;

import java.awt.Color;

import physics.Angle;
import physics.Circle;
import physics.Geometry;
import physics.LineSegment;
import physics.Vect;

/**
 * Flipper
 * 
 * 2 x 2 board gadget that can be triggered to rotate from vertical to horizontal,
 * and then triggered again to return to its starting position.  While transitioning,
 * it rotates at a speed of 1080 degrees per second, to a total of 90 degrees.  this
 * rotational velocity can impart energy into any balls that collide with it.
 * 
 * comes in two varieties : left and right
 * left flippers' initial rotation is counter-clockwise, whereas right flippers' is
 * clockwise (down -> up)
 */
public class Flipper extends Triggerable {
    
    public enum FlipperType {LEFT, RIGHT};
    private enum FlipperState {UP, DOWN, FLIPPINGUP, FLIPPINGDOWN};

    private static final int FLIPPER_WIDTH = 2, FLIPPER_HEIGHT = 2;
    private static final double ROTATION_SPEED = Math.toRadians(1080);
    private static final double FLIP_TIME = Math.PI/2/ROTATION_SPEED;
    private static final double R_COEFF = 0.95;
    private static final double RADIUS = 0.25;
    
    private final int chirality;
    //rep invariant -- equals +1 if left flipper (ccw)
    //                        -1 if right flipper (cw)
    private final Vect orientationVector;
    //rep invariant -- this vector corresponds to the angular
    //                 orientation argument used to construct the flipper
    //                 it is the unit vector corresponding to the input angle
    //                 of 0, 90, 80, or 270
    private final Vect pivot;
    //center of rotation
    private Circle[] ends;
    //rep invariant -- ends is an array of length 2. ends[0] is centered on pivot,
    //                 and ends[1] is the free end of the flipper
    private LineSegment[] edges;
    //rep invariant -- must remain inside of the flipper's 2 x 2 bounding box
    //                 if state == up, then the edges, ends are horizontally aligned
    //                 if state == down, then the edges, ends are vertically aligned
    private FlipperState state;
    //rep invariant -- if up, angle == 0 (left) or pi(right)
    //                 if down, angle == pi/2
    //                 the only acceptable state transitions are
    //                 down -> flipping up -> up -> flipping down -> down
    private double actionTimer;
    //rep invariant -- 0 <= triggerTimer <= pi/2/ROTATION_SPEED
    //                 used to synchronize with the simulation clock
    
    @Deprecated
    public Flipper(int x, int y, FlipperType type) {
        this(x,y,type,0);
    }
    
    @Deprecated
    public Flipper(int x, int y, FlipperType type, int orientation){
        this(x,y,type,orientation,type + " FLIPPER");
    }
    
    /**
     * create a new flipper at the specified location, with specified
     * orientation
     * 
     * @param x the x position
     * @param y the y position
     * @param type the facing direction of the flipper - left or right
     * @param orientation the angle by which this flipper is rotated
     *          0|90|180|270 are valid inputs
     * @param name the name of this gadget
     */
    //this is the constructor which we should be in the habit of using
    //before we start to implement phase 2 specs
    public Flipper(int x, int y, FlipperType type, int orientation, String name) {
        super(x, y, FLIPPER_WIDTH, FLIPPER_HEIGHT, 'X', name);
        chirality = type == FlipperType.LEFT ? +1 : -1;
        //what follows is highly magical
        switch(orientation){
        case 0:
            orientationVector = new Vect(0,+1);
            break;
        case 90:
            orientationVector = new Vect(-1,0);
            break;
        case 180:
            orientationVector = new Vect(0,-1);
            break;
        case 270:
            orientationVector = new Vect(+1,0);
            break;
        default:
            orientationVector = new Vect(0,+1);//default is 0 degrees
        }
        //MAAAAAAAGIC
        //TODO: remove magic
        double x1 = x + 1 - orientationVector.dot(new Vect(+1,chirality))*(1-RADIUS);
        double y1 = y + 1 - orientationVector.dot(new Vect(-chirality,+1))*(1-RADIUS);
        pivot = new Vect(x1,y1);
        //initialize flipper geometry to its resting down state
        fixedPosition(FlipperState.DOWN);
        state = FlipperState.DOWN;
    }
    
    //private constructor using internal representations
    private Flipper(int x, int y, int facingVector, int orientation) {
        this(x, y, facingVector == -1 ? FlipperType.LEFT : FlipperType.RIGHT, orientation);
    }
    
    //checkRep
    private boolean checkRep(){
        if(!isMoving() && getAngularVelocity() != 0){
            System.err.println("state error");
            return false;
        } if(isMoving() && getAngularVelocity() == 0){
            System.err.println("state error");
            return false;
        }
        for(LineSegment edge : edges){
            //check that the edges are in bounds -- indirectly checks end points
            if(outOfBounds(edge.p1()) || outOfBounds(edge.p2())){
                System.err.println("boundary error: " + edge.p1() + ", " + edge.p2());
                return false;
            }
        }
        return true;
    }
    
    //private helper method for checkRep -- returns true if the point is
    //inside of this flipper's 2x2 bounding box
    private boolean outOfBounds(Vect point) {
        if(point.x() < x || point.x() > x + 2){
            return true;
        }
        if(point.y() < y || point.y() > y + 2){
            return true;
        }
        return false;
    }
    
    
    //some observer methods -- largely for debugging and testing purposes
    
    /**
     * @return true iff the flipper is currently rotating
     */
    public boolean isMoving() {
        return state == FlipperState.FLIPPINGUP || state == FlipperState.FLIPPINGDOWN;
    }
    /**
     * @return true iff the flipper is resting in the vertical orientation
     */
    public boolean isDown() {
        return this.state == FlipperState.DOWN;
    }
    /**
     * @return true iff the flipper is resting in the horizontal orientation
     */
    public boolean isUp() {
        return this.state == FlipperState.UP;
    }
    
    public Vect getPivot() {
        return pivot;
    }
    
    //returns the current angular deflection of the flipper 
    private Angle getAngle() {
        Vect vector = ends[1].getCenter().minus(pivot);
        return new Angle(vector.x(),vector.y());
    }
    
    //return the angular velocity in radians per second of the flipper
    private double getAngularVelocity() {
        double vel = 0;
        if(!isMoving()){
            return vel;
        }else{
            vel = chirality * ROTATION_SPEED;
            if(state == FlipperState.FLIPPINGDOWN){
                return vel;
            }else{
                return -1 * vel;
            }
        }
    }
    
    
    //Mutator Methods
    
    /**
     * rotate the flipper as necessary to reflect a time step of deltaT in seconds
     * 
     * @param deltaT the time step in seconds
     *          must be > 0
     */
    @Override public void displace(double deltaT) {
        if(isMoving()){
            if(actionTimer + deltaT >= FLIP_TIME){
                if(state == FlipperState.FLIPPINGUP){
                    fixedPosition(FlipperState.UP);
                    state = FlipperState.UP;
                }else{
                    fixedPosition(FlipperState.DOWN);
                    state = FlipperState.DOWN;
                }
            }else{
                actionTimer += deltaT;
                Angle rotation = new Angle(actionTimer * ROTATION_SPEED);
                if(state == FlipperState.FLIPPINGUP){
                    moveToAngle(rotation);
                }else{
                    moveToAngle(Angle.DEG_90.minus(rotation));
                }
            }
        }
        assert checkRep();
    }
    //helper method for displace() -- rotates the flipper by the rotation angle
    private void rotate(Angle rotation){
        for(int ii = 0; ii < ends.length; ii ++) {
            Circle rotatedEnd = Geometry.rotateAround(ends[ii], pivot, rotation);
            ends[ii] = rotatedEnd;
        } for(int jj = 0; jj < edges.length; jj ++){
            LineSegment rotatedEdge = Geometry.rotateAround(edges[jj], pivot, rotation);
            edges[jj] = rotatedEdge;
        }
    }
    //helper method for displace() -- rotates the flipper to the argument angle.
    //angle must be between 0 and pi/2 degrees (0 being down and pi/2 being up)
    //automatically accounts for the flipper's type and rotates to an angle inside of
    //the flipper's range.
    private void moveToAngle(Angle angle) {
        fixedPosition(FlipperState.DOWN);
        Angle rotation = new Angle(angle.radians()*-chirality);
        rotate(rotation);
    }
    //reconstruct the flipper's geometry to reflect either UP or DOWN position. 
    //if a state other than UP or DOWN is provided, the flipper will not be changed.
    //the STATE of the flipper is IN NO WAY affected by this method.
    private void fixedPosition(FlipperState position) {
        if(position != FlipperState.UP && position != FlipperState.DOWN){
            return;
        }
        double dx = 0, dy = 0;
        //MAAAAAAAAAAAAAAAAGIC
        if(position == FlipperState.UP){
            dx = -chirality*orientationVector.x();
            dy = chirality*orientationVector.y();
        }else{
            dx = orientationVector.y();
            dy = orientationVector.x();
        }
        Vect side1 = pivot.plus(new Vect(dx,dy).times(RADIUS));
        Vect side2 = pivot.minus(new Vect(dx,dy).times(RADIUS));
        Vect length = new Vect(dy, dx).times(2*(1-RADIUS));
        Circle[] endArray = {
                new Circle(pivot,RADIUS),
                new Circle(pivot.plus(length),RADIUS)
        };
        this.ends = endArray;
        LineSegment[] edgeArray = {
                new LineSegment(side1, side1.plus(length)),
                new LineSegment(side2, side2.plus(length))
        };
        this.edges = edgeArray;
        assert checkRep();
    }
    
    /**
     * return the flipper to the default position
     */
    @Override public void reset(){
        super.reset();
        fixedPosition(FlipperState.DOWN);
        state = FlipperState.DOWN;
    }
    
    /**
     * When a flipper becomes triggered, if it is currently at rest, it will 
     * flip, rotating 90 degrees at 1080 degrees per second. Any collisions 
     * that happen during this time must account for changing angle and 
     * rotational speed of the flipper
     * 
     * if the flipper is moving when it is triggered, the trigger will be ignored
     */
    @Override public void triggerAction() {
        if(state == FlipperState.UP){
            state = FlipperState.FLIPPINGDOWN;
            actionTimer = 0;
        }else if(state == FlipperState.DOWN){
            state = FlipperState.FLIPPINGUP;
            actionTimer = 0;
        }
        assert checkRep();
    }

    @Override public double timeUntilCollision(Ball ball) {
        double timeToCollision = Double.POSITIVE_INFINITY;
        double t = 0;
        double angularV = getAngularVelocity();
        for(Circle end : ends){
            t = Geometry.timeUntilRotatingCircleCollision(
                    end, pivot, angularV, ball.toCircle(), ball.vel());
            timeToCollision = t < timeToCollision ? t : timeToCollision;
        }for(LineSegment edge : edges){
            t = Geometry.timeUntilRotatingWallCollision(
                    edge, pivot, angularV, ball.toCircle(), ball.vel());
            timeToCollision = t < timeToCollision ? t : timeToCollision;
        }
        //if no collision predicted, return right away
        if(timeToCollision == Double.POSITIVE_INFINITY){
            return timeToCollision;
        }
        //this next loop attempts to account for the fact that the flipper may
        //stop moving before colliding with the ball
        if(isMoving() && actionTimer + timeToCollision >= FLIP_TIME){
            Flipper hypothetical = new Flipper(x,y,chirality,0);
            if(state == FlipperState.FLIPPINGUP){
                hypothetical.fixedPosition(FlipperState.UP);
                hypothetical.state = FlipperState.UP;
            }//otherwise the flipper will be created already in the down state
            timeToCollision = FLIP_TIME + hypothetical.timeUntilCollision(ball);
        }
        return timeToCollision;
    }

    @Override public void collideWith(Ball ball) {
        double timeToCollision = Double.POSITIVE_INFINITY;
        double t = 0;
        Vect reflection = null;
        double angularV = getAngularVelocity();
        for(Circle end : ends){
            t = Geometry.timeUntilRotatingCircleCollision(
                    end, pivot, angularV, ball.toCircle(), ball.vel());
            if(t < timeToCollision){
                timeToCollision = t;
                reflection = Geometry.reflectRotatingCircle(
                        end, pivot, angularV, ball.toCircle(), ball.vel(), R_COEFF);
            }
        }
        for(LineSegment edge : edges){
            t = Geometry.timeUntilRotatingWallCollision(
                    edge, pivot, angularV, ball.toCircle(), ball.vel());
            if(t < timeToCollision){
                timeToCollision = t;
                reflection = Geometry.reflectRotatingWall(
                        edge, pivot, angularV, ball.toCircle(), ball.vel(), R_COEFF);
            }
        }
        assert checkRep();
        if(reflection != null){
            ball.impart(reflection);
        }
        
        becomeTriggered();
    }
    
    @Override public void draw(char[][] grid) {
        Angle angle = getAngle();
        for(int jj = 0; jj < grid.length; jj ++){
            for(int ii = 0; ii < grid[0].length; ii ++){
                if((int)pivot.x() == ii - 1 && (int)pivot.y() == jj - 1){
                    if(Math.abs(angle.sin()) >= Math.abs(angle.cos())){
                        int offset = (int)orientationVector.dot(new Vect(-chirality,+1));
                        grid[jj][ii] = '|';
                        grid[jj+offset][ii] = '|';
                    }else{
                        int offset = (int)orientationVector.dot(new Vect(+1,chirality));
                        grid[jj][ii] = '-';
                        grid[jj][ii + offset] = '-';
                    }
                }
            }
        }
    }
    
    /**
     * @return a string containing the position of this flipper
     */
    @Override public String toString(){
        String type = chirality == +1 ? "left" : "right";
        return type + " flipper(" + x + "," + y + ")";
    }

    @Override
    public CollidableGraphic getGraphic() {
        Vect c1 = ends[0].getCenter();
        Vect c2 = ends[1].getCenter();
        Angle theta = getAngle();
        //TODO : this fix with the angle sin and cos is waaaaaay magical; we can probably do better
        return new FlipperGraphic(c1.x(), c1.y(), c2.x(), c2.y(), RADIUS, theta.sin(), theta.cos(), Color.pink);
    }

}
