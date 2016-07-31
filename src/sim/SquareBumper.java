package sim;

import java.awt.Color;

import graphics.CollidableGraphic;
import graphics.RectangleGraphic;
import physics.Circle;
import physics.Geometry;
import physics.LineSegment;
import physics.Vect;

public class SquareBumper extends Triggerable {
    
    private final LineSegment[] sides = new LineSegment[4]; //The 4 sides of the square 
    private final Circle[] corners = new Circle[4]; //The 4 corners of the square
   
    //Rep invariant: square bumper has 4 sides, 4 corners, and side length L
    //TODO: implement checkRep()
    
    @Deprecated
    public SquareBumper(int x, int y){
        this(x,y,"SQUARE");
    }
    
    public SquareBumper(int x, int y, String name) {
        super(x, y, '#', name);
                
        //Create sides
        LineSegment upper = new LineSegment(x ,y, x+1 ,y);
        LineSegment lower = new LineSegment(x, y+1, x+1, y+1);
        LineSegment left = new LineSegment(x, y, x, y+1);
        LineSegment right = new LineSegment(x+1, y, x+1, y+1);
        
        //Create corners
        Circle upLeft = new Circle(x, y, 0.0);
        Circle upRight = new Circle(x+1, y, 0.0);
        Circle downLeft = new Circle(x, y+1, 0.0);
        Circle downRight = new Circle(x+1, y+1, 0.0);
        
        //Add sides
        sides[0] = upper;
        sides[1] = lower;
        sides[2] = left;
        sides[3]  = right;
        
        //Add corners
        corners[0] = upLeft;
        corners[1] = upRight;
        corners[2] = downRight;
        corners[3] = downLeft;
    }
    
    /**
     * square bumpers have no triggerable behavior
     */
    @Override public void triggerAction() {;}

    @Override public double timeUntilCollision(Ball ball) {
        double minTime = Double.POSITIVE_INFINITY;
        for(int i = 0; i < 4; i++){
            double wallCollisionTime = Geometry.timeUntilWallCollision(sides[i], ball.toCircle(), ball.vel());
            double cornerCollisionTime = Geometry.timeUntilCircleCollision(corners[i], ball.toCircle(), ball.vel());
            if(wallCollisionTime < minTime){
                minTime = wallCollisionTime;
            }
            if(cornerCollisionTime < minTime){
                minTime = cornerCollisionTime;
            }
        }
        return minTime;
    }

    @Override public void collideWith(Ball ball) {
        Vect newVelocity = null;
        double minTime = Double.POSITIVE_INFINITY;
        //Loop again to find the exact side or corner that the ball collides with
        for(int i = 0; i < 4; i++){
            double wallCollisionTime = Geometry.timeUntilWallCollision(sides[i], ball.toCircle(), ball.vel());
            double cornerCollisionTime = Geometry.timeUntilCircleCollision(corners[i], ball.toCircle(), ball.vel());
            if(wallCollisionTime < minTime){
                minTime = wallCollisionTime;
                newVelocity = Geometry.reflectWall(sides[i], ball.vel());
            }
            if(cornerCollisionTime < minTime){
                minTime = cornerCollisionTime;
                newVelocity = Geometry.reflectCircle(corners[i].getCenter(), ball.toCircle().getCenter(), ball.vel());
            }
        }
        if(newVelocity != null){
            ball.impart(newVelocity);
        }
        
        becomeTriggered();
    }
    
    /**
     * @return a string describing the position of this bumper
     */
    @Override public String toString(){
        return "square bumper(" + x +"," + y + ")";
    }

    @Override
    public CollidableGraphic getGraphic() {
        float colorValue = (float)(1/(1 + 2*triggerTimer()));
        Color color = new Color(1,colorValue,colorValue);
        return new RectangleGraphic(x,y,w,h,color);
    }

}
