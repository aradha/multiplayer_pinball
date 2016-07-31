package sim;

import java.awt.Color;

import graphics.BallGraphic;
import graphics.CollidableGraphic;
import physics.Circle;
import physics.Geometry;

/**
 * Circle Bumper class
 * 
 * very bumpy!
 */
public class CircleBumper extends Triggerable{
    
    public static final double RADIUS = 0.5;
    
    private final Circle circle;
    //rep invariant -- circle of radius RADIUS
    //                 probably doesn't warrant a checkRep()
    
    @Deprecated
    public CircleBumper(int x, int y){
        this(x,y,"CIRCLE");
    }
    
    /**
     * create a new circle bumper at position x,y with the specified name
     * 
     * @param x the x position
     * @param y the y position
     * @param name the name for this gadget
     */
    public CircleBumper(int x, int y, String name){
        super(x,y,'O',name);
        circle = new Circle(x + 0.5, y + 0.5, RADIUS);
    }

    /**
     * Circle Bumpers have no triggerable behavior
     */
    @Override public void triggerAction() {;}

    @Override public double timeUntilCollision(Ball ball) {
        return Geometry.timeUntilCircleCollision(circle, ball.toCircle(), ball.vel());
    }

    @Override public void collideWith(Ball ball) {
        ball.impart(Geometry.reflectCircle(circle.getCenter(), ball.getCenter(), ball.vel()));
        
        becomeTriggered();
    }
    
    /**
     * @return a string describing the position of this bumper
     */
    @Override public String toString(){
        return "circle bumper(" + x +"," + y + ")";
    }

    @Override
    public CollidableGraphic getGraphic() {
        float colorValue = (float)(1/(1 + 2*triggerTimer()));
        Color color = new Color(1,colorValue,colorValue);
        return new BallGraphic(
                circle.getCenter().x(), circle.getCenter().y(), circle.getRadius(), color);
    }
}
