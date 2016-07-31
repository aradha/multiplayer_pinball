package sim;

import graphics.CollidableGraphic;
import graphics.WallGraphic;

import java.awt.Color;
import java.util.LinkedList;
import java.util.Queue;

import client.Board;
import physics.Circle;
import physics.Geometry;
import physics.LineSegment;
import physics.Vect;

public class Wall extends Collidable{
    
    public enum WallType {TOP,BOTTOM,LEFT,RIGHT}
    
    private Queue<Envelope> transportQueue = new LinkedList<Envelope>();
    
    private Vect facing;
    //rep invariant -- left wall:   (-1,0)
    //                 right wall:  (+1,0)
    //                 top wall:    (0,-1)
    //                 bottom wall: (0,+1)
    //this member is genuinely a bit magical, but it helps to transform a lot
    //of boolean control logic into plain arithmetic
    private WallType type;
    //rep invariant -- represents the type of the wall. closely related to facing
    private final Vect p1, p2;
    //these are the base endpoints for the wall's long edge. when the wall
    //moves, these points remain fixed.
    private LineSegment edge;
    //this is "the wall"
    private LineSegment[] corners;
    //these are perpendicular caps to prevent gaps in invisible walls
    private Circle[] ends;
    //these help to resolve the joints between wall corners
    //rep invariant -- when solid, the wall is one of the boundary edges of the
    //                 (0,20)x(0,20) playing space
    //                 when permeable, the wall edge is 0.5L outside of the board
    //                 boundary (to prevent clipping issues after transport)
    
    private boolean opacity = true;
    //rep invariant -- represents the wall's permeability. true unless the board
    //                 is connected to another across this wall
    private String linkName = "";
    //rep invariant -- the wall has a name iff it is invisible
    private char[] selfImage;
    //rep invariant -- if the wall is visible, this array contains only '.'
    //                 if it is not visible, it contains some readable
    //                 representation of the wall's name, bracketed by '.' characters
    
    /**
     * construct a new wall of the specified type, to fit on a 20 x 20 board
     * 
     * @param type the type of wall, as one of left, top, right, bottom
     */
    public Wall(WallType type){
        this(type, Board.DEFAULT_SIZE);
    }
    
    public Wall(WallType type, int dimension){
        this.type = type;
        switch(this.type){
        case LEFT: 
            facing = new Vect(-1,0);
            break;
        case RIGHT: 
            facing = new Vect(+1,0);
            break;
        case TOP: 
            facing = new Vect(0,-1);
            break;
        case BOTTOM: 
            facing = new Vect(0,+1);
            break;
        default:
            throw new IllegalArgumentException("unreachable block");
        }
        resetImage();
        //what follows is a little bit magical..
        //you can think about it as a transformation of a coordinate system 
        //spanning (-1,1)x(-1,1) to (0,20)x(0,20)
        double factor = dimension/2.;
        double x1 = factor*(1 + facing.dot(new Vect(+1,+1)));
        double x2 = factor*(1 + facing.dot(new Vect(+1,-1)));
        double y1 = factor*(1 + facing.dot(new Vect(+1,+1)));
        double y2 = factor*(1 + facing.dot(new Vect(-1,+1)));
        p1 = new Vect(x1,y1);
        p2 = new Vect(x2,y2);
        edge = new LineSegment(p1,p2);
        corners = new LineSegment[2];
        corners[0] = new LineSegment(p1, p1.plus(facing));
        corners[1] = new LineSegment(p2, p2.plus(facing));
        ends = new Circle[2];
        ends[0] = new Circle(p1, 0);
        ends[1] = new Circle(p2, 0);
    }
    
    public Wall(WallType type, int dimension, Queue<Envelope> transportQueue){
        this(type, dimension);
        this.transportQueue = transportQueue;
    }
    
    //checkRep()
    @SuppressWarnings("unused")
    private boolean checkRep(){
        //TODO:
        return true;
    }
    
    //reset this wall's image to a series of '.' characters
    private void resetImage(){
        selfImage = new char[22];
        for(int ii = 0; ii < selfImage.length; ii ++){
            selfImage[ii] = '.';
        }
    }
    
    /**
     * @return the boolean value of this wall's transparency
     */
    public boolean isTransparent(){
        return !opacity;
    }
    
    /**
     * @return the name of the board to which this wall is linked
     */
    public String linkName(){
        return linkName;
    }
    
    /**
     * @return the type of this wall
     */
    public WallType type(){
        return this.type;
    }
    
    /**
     * make a connection accross this wall to otherBoard -- extends the wall's position
     * out to one half unit beyond the boundary to avoid clipping issues with
     * transported balls
     * 
     * @param otherBoard the name of the board to which this wall will be linked
     */
    public void connectBoard(String otherBoard){
        linkName = otherBoard;
        opacity = false;
        //here we move the edge out to one half unit beyond the boundary of the board, 
        //in the appropriate direction for each type of wall
        edge = new LineSegment(
                p1.plus(facing.times(0.5)),
                p2.plus(facing.times(0.5))
                );
        //this is where we adjust the wall's visual appearance
        resetImage();
        if(linkName.length() >= 20){
            for(int ii = 0; ii < 20; ii ++){
                selfImage[ii+1] = linkName.charAt(ii);
            }
        }else{
            int d = 1 + (20 - linkName.length())/2;
            for(int ii = 0; ii < linkName.length(); ii ++){
                selfImage[ii+d] = linkName.charAt(ii);
            }
        }
    }
    
    /**
     * remove any board links across this wall and reset the wall to its opaque
     * state
     */
    public void disconnect(){
        linkName = "";
        opacity = true;
        edge = new LineSegment(p1, p2);
        resetImage();
    }
    
    /**
     * check if a ball should be transported through this wall - the ball should
     * be transported if the wall is invisible and the ball is colliding with the
     * wall's long edge
     * 
     * @param ball the ball to be assessed for transportation
     * @return true iff the ball in question should be teleported through this wall
     */
    public boolean shouldTeleport(Ball ball){
        if(opacity){
            return false;
        }
        return Geometry.timeUntilWallCollision(edge, ball.toCircle(), ball.vel()) == 0;
    }
    
    /**
     * check whether a ball is positioned illegally on top of this wall
     * illegal positions are those which lie outside of the board boundary defined by
     * this wall, or which place the ball over top of the wall's collision profile
     * 
     * @param ball the ball to be checked
     * @return true iff this ball's position is clipping with the wall
     */
    public boolean isClipping(Ball ball){
        Vect perpendicularPoint = Geometry.perpendicularPointWholeLine(edge, ball.getCenter());
        boolean overlap = 
                Geometry.distanceSquared(perpendicularPoint, ball.getCenter()) 
                < ball.getRadius()*ball.getRadius();
        boolean outOfBounds = edge.p1().dot(facing) <= ball.getCenter().dot(facing);
        return overlap || outOfBounds;
    }
    
    /**
     * determine the time in seconds until a collision will occur between
     * this wall and the specified ball, assuming constant velocity
     * 
     * @param ball the ball whose collision time is to be checked
     * @return the number of seconds before a collision will take place
     *          or positive infinity if no collision will take place
     */
    @Override public double timeUntilCollision(Ball ball){
        double timeToCollision = Geometry.timeUntilWallCollision(edge, ball.toCircle(), ball.vel());
        if(!opacity){
            for(LineSegment edge : corners){
                double t = Geometry.timeUntilWallCollision(edge, ball.toCircle(), ball.vel());
                timeToCollision = t < timeToCollision ? t : timeToCollision;
            } for(Circle end : ends){
                double t = Geometry.timeUntilCircleCollision(end, ball.toCircle(), ball.vel());
                timeToCollision = t < timeToCollision ? t : timeToCollision;
            }
        }
        return timeToCollision;
    }
    
    /**
     * if is wall is solid, reflect the ball, assuming an instantaneous 
     * collision
     * 
     * if the wall is invisible, the ball's velocity is unaffected, and the ball
     * is teleported across the wall
     * 
     * @param ball the ball to be reflected
     */
    @Override public void collideWith(Ball ball) {
        double timeToCollision = Geometry.timeUntilWallCollision(edge, ball.toCircle(), ball.vel());
        Vect reflection = ball.vel();
        if(opacity){
            reflection = Geometry.reflectWall(edge,ball.vel());
        }
        else{
            for(LineSegment corner : corners){
                double t = Geometry.timeUntilWallCollision(corner, ball.toCircle(), ball.vel());
                if(t <= timeToCollision){
                    timeToCollision = t;
                    reflection = Geometry.reflectWall(corner,ball.vel());
                }
            }for(Circle end : ends){
                double t = Geometry.timeUntilCircleCollision(end, ball.toCircle(), ball.vel());
                if(t <= timeToCollision){
                    timeToCollision = t;
                    reflection = Geometry.reflectCircle(end.getCenter(),ball.getCenter(),ball.vel());
                }
            }
        }
        ball.impart(reflection);
        
        if(shouldTeleport(ball)){
            reflectAcross(ball);
            ball.becomeAbsorbed();
            transportQueue.add(new Envelope(ball, linkName));
        }
    }
    
    //private helper method reflects the ball across the board from this wall
    private void reflectAcross(Ball ball){
        ball.moveTo(ball.getCenter().plus(facing.times(-20.5)));
    }
    
    /**
     * add a line of '.' (or letter characters in the case of client-server play)
     * at the indices corresponding to this wall's position on the board
     * 
     * @param grid the array into which the wall will draw itself
     */
    @Override public void draw(char[][] grid){
        if(facing.x() != 0){
            int xIndex = facing.x() == -1 ? 0 : 21; //either left side or right side
            for(int jj = 0; jj < grid.length; jj ++){
                for(int ii = 0; ii < grid[0].length; ii++){
                    if(ii == xIndex){
                        grid[jj][ii] = selfImage[jj];
                    }
                }
            }
        } else {
            int yIndex = facing.y() == -1 ? 0 : 21; //either top or bottom
            for(int jj = 0; jj < grid.length; jj ++){
                for(int ii = 0; ii < grid[0].length; ii++){
                    if(jj == yIndex){
                        grid[jj][ii] = selfImage[ii];
                    }
                }
            }
        }
    }
    
    public static WallType stringToWallType(String name){
        for(WallType type: WallType.values()){
            if(name.equals(type + "")){
                return type;
            }
        }
        return null;
    }
    
    public static WallType oppositeType(WallType type){
        switch(type){
        case LEFT:
            return WallType.RIGHT;
        case RIGHT:
            return WallType.LEFT;
        case TOP:
            return WallType.BOTTOM;
        case BOTTOM:
            return WallType.TOP;
        default:
            return null;
        }
    }
    /**
     * return a string describing the type of wall and its current visibility:
     *      either "solid" if the wall is visible,
     *      or "portal ---> [name]" if the wall is invisible, where [name]
     *      represents the name of the connected board
     */
    @Override public String toString(){
        String facing = "";
        if(this.facing.x() != 0){
            facing = this.facing.x() == -1 ? "left" : "right";
        }else{
            facing = this.facing.y() == -1 ? "top" : "bottom";
        }
        if(opacity){
            return facing + " wall:" + " solid";
        }else{
            return facing + " wall:" + " portal --> " + linkName;
        }
    }
    
    /**
     * draw a line depicting the board boundary, with the name of the
     * board this wall links to, if applicable
     */
    @Override public CollidableGraphic getGraphic() {
        Color color = Color.white;
        return new WallGraphic(p1.x(), p1.y(), p2.x(), p2.y(), selfImage, facing, color);
    }
    
}
