package sim;

import java.awt.Color;

import graphics.CollidableGraphic;
import graphics.TriangleGraphic;
import physics.Angle;
import physics.Circle;
import physics.Geometry;
import physics.LineSegment;
import physics.Vect;

public class TriangleBumper extends Triggerable {
    
    private final LineSegment[] sides = new LineSegment[3];
    private final Circle[] corners = new Circle[3];
    
    @Deprecated
    public TriangleBumper(int x, int y, int orientation) {
        this(x,y,orientation,"TRIANGLE");
    }
    
    public TriangleBumper(int x, int y, int orientation, String name){
        super(x,y, orientation/90%2 == 0 ? '/' : '\\' ,name); //Regex to get the right char for orientation
        if(orientation % 90 != 0){
            throw new IllegalArgumentException("Orientation input must be one of {0, 90, 180, 270}");
        }
        //Init for orientation 0
        LineSegment diagonal = new LineSegment(x,y+1,x+1,y); // Diagonal up as /
        LineSegment base1 = new LineSegment(x,y, x+1,y); //Top base in 0 orientation --
        LineSegment base2 = new LineSegment(x,y,x,y+1); //Left base in 0 orientation |
        
        Circle db1 = new Circle(x+1,y,0); //North east corner padding
        Circle b1b2 = new Circle(x,y,0); //North west corner padding
        Circle db2 = new Circle(x,y+1,0); //South west corner padding
        
        sides[0] = diagonal;
        sides[1] = base1;
        sides[2] = base2;
        
        corners[0] = db1;
        corners[1] = b1b2;
        corners[2] = db2;
        
        Vect center = new Vect(x+.5, y+.5);
        Angle rotationAngle = new Angle(Math.toRadians(orientation));
        //Code to rotate the triangle bumper to the right orientation
        for(int i = 0; i < 3; i++){
            sides[i] = Geometry.rotateAround(sides[i], center, rotationAngle);
            corners[i] = Geometry.rotateAround(corners[i], center, rotationAngle);
        }
                
    }
    
    /**
     * triangle bumpers have no triggerable action
     */
    @Override public void triggerAction() {;}

    @Override public double timeUntilCollision(Ball ball) {
        double minTime = Double.POSITIVE_INFINITY;
        for(int i = 0; i < 3; i++){
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
        for(int i = 0; i < 3; i++){
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
        return "triangle bumper(" + x +"," + y + ")";
    }

    @Override
    public CollidableGraphic getGraphic() {
        Vect v0 = corners[0].getCenter();
        Vect v1 = corners[1].getCenter();
        Vect v2 = corners[2].getCenter();
        float colorValue = (float)(1/(1 + 2*triggerTimer()));
        Color color = new Color(1,colorValue,colorValue);
        return new TriangleGraphic(v0.x(), v0.y(), v1.x(), v1.y(), v2.x(), v2.y(), color);
    }

}
