package sim;

import graphics.CollidableGraphic;

/**
 * abstract class for objects involved in physics collisions in the
 * pingball game
 */
public abstract class Collidable {
    
    /**
     * displace the object through a time step of deltaT in seconds
     * 
     * @param deltaT the time step
     *          must be > 0
     */
    //classes representing motile objects should override this
    public void displace(double deltaT){;}
    
    /**
     * determine the time in seconds until a collision will occur between
     * this object and the specified ball, assuming constant velocity
     * 
     * @param ball the ball whose collision time is to be checked
     * @return the number of seconds before a collision will take place
     *          or positive infinity if no collision will take place
     */
    public abstract double timeUntilCollision(Ball ball);
    
    /**
     * Change the state of the ball to reflect a collision with
     * this object.
     * 
     * @param ball the ball which has collided with this object
     */
    public abstract void collideWith(Ball ball);
    
    /**
     * place characters into the passed array at indices corresponding
     * to this object's position on the board
     * 
     * @param grid the array into which this object is to insert its
     *          pictorial representation
     */
    public abstract void draw(char[][] grid);
    
    public abstract CollidableGraphic getGraphic();

}
