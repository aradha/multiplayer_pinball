package sim;

import graphics.BallGraphic;
import graphics.CollidableGraphic;

import java.awt.Color;
import java.util.Random;

import physics.Circle;
import physics.Geometry;
import physics.Geometry.VectPair;
import physics.Vect;

/**
 * Ball Class!
 * 
 * these are the things that bounce around the board space colliding with other things
 * has a position, radius, and velocity.
 * 
 * has methods to move the ball some distance in time, and to adjust the velocity of the
 * ball based on physical parameters of the simulation
 * 
 * has methods for detecting and performing collisions with other ball objects
 */
public class Ball extends Collidable{
    
    public static final double TERMINAL_VEL = 200;
    public static final double DEFAULT_RADIUS = 0.25;
    
    private Circle circle;
    //rep invariant -- has nonzero radius
    private Vect vel;
    //rep invariant -- magnitude is less than or equal to TERMINAL_VEL
    private double mass = 1.0;
    //rep invariant -- mass > 0
    private double r;
    //rep invariant -- positive, not zero
    private boolean stasis;
    //rep invariant -- if stasis is true, the ball is unaffected by displacements
    private Color color;
    
    /**
     * create a stationary ball at the specified position, with default
     * radius (r = 0.25)
     * 
     * @param x the x position
     * @param y the y position
     */
    public Ball(double x, double y){
        this(new Circle(x,y,DEFAULT_RADIUS), new Vect(0,0));
    }
    
    /**
     * create a ball at the specified position, with the provided velocity
     * and default radius (r = 0.25)
     * 
     * @param x the x position
     * @param y the y position
     * @param xVel the x component of the velocity
     * @param yVel the y component of the velocity
     */
    public Ball(double x, double y, double xVel, double yVel){
        this(new Circle(x,y,DEFAULT_RADIUS), new Vect(xVel,yVel));
    }
    
    public Ball(double x, double y, Vect velocity){
        this(new Circle(x, y, DEFAULT_RADIUS), velocity);
    }
    
    /**
     * create a ball at the specified position, with the provided velocity
     * and radius
     * 
     * @param x the x position
     * @param y the y position
     * @param xVel the x component of the velocity
     * @param yVel the y component of the velocity
     * @param radius the radius of the ball
     */
    public Ball(double x, double y, double xVel, double yVel, double radius){
        this(new Circle(x, y, radius), new Vect(xVel, yVel));
        if(radius <= 0){
            throw new IllegalArgumentException("positive radius required");
        }
    }
    
    //private constructor using Circle and Vect
    private Ball(Circle circle, Vect velocity){
        this.circle = circle;
        this.vel = velocity;
        this.r = circle.getRadius();
        Random gen = new Random();
        float[] colors = {
                0.05f,0.05f,0.05f
                
        };
        while(colors[0] + colors[1] + colors[2] < 1 || (colors[0] < .5 && colors[1] < .5 && colors[2] < .5)){
            int change = gen.nextInt(3);
            colors[change] *= 2.5;
            colors[change] = Math.min(1,colors[change]);
        }
        this.color = new Color(colors[0], colors[1], colors[2]);
    }
    
    //checkRep
    private boolean checkRep(){
        boolean preserved = r > 0;
        preserved = preserved && r == circle.getRadius();
        preserved = preserved && vel.length() <= TERMINAL_VEL;
        return preserved && mass > 0;
    }
    
    
    //Observer Methods
    
    /**
     * @return the velocity of the ball
     */
    public Vect vel(){
        if(stasis){
            return new Vect(0,0);
        }
        return vel;
    }
    /**
     * @return the vect representing the center of the ball
     */
    public Vect getCenter(){
        return circle.getCenter();
    }
    /**
     * @return the circle representing the geometry of the ball
     */
    public Circle toCircle(){
        return circle;
    }
    
    public double x(){
        return circle.getCenter().x();
    }
    
    public double y(){
        return circle.getCenter().y();
    }
    /**
     * @return the radius of the ball
     */
    public double getRadius(){
        return r;
    }
    /**
     * @return the mass of the ball;
     */
    public double getMass(){
        return mass;
    }
    
    public boolean isInStasis(){
        return stasis;
    }
    
    
    //General Mutators
    
    /**
     * if the ball is not in stasis, change its position to reflect the passage 
     * of time deltaT, assuming no collisions or accelerations
     * 
     * if the ball is in stasis, it is not changed
     * 
     * @param deltaT the time increment in seconds
     *          must be > 0
     */
    @Override public void displace(double deltaT){
        if(deltaT <= 0){
            throw new IllegalArgumentException("delta_t must be positive: " + deltaT);
        }if(stasis){
            return;
        }
        Vect newPos = circle.getCenter().plus(vel.times(deltaT));
        circle = new Circle(newPos, this.r);
        assert checkRep();
    }
    
    /**
     * move the ball so that its center is in the specified position
     * 
     * @param position the position to which the ball should be moved
     */
    public void moveTo(Vect position){
        circle = new Circle(position, this.r);
    }
    
    /**
     * move the ball so that its center is at the specified x,y position
     * 
     * @param x the x position
     * @param y the y position
     */
    public void moveTo(double x, double y){
        moveTo(new Vect(x, y));
    }
    
    /**
     * if the ball is not in stasis, adjust its velocity vector for a time step of
     * deltaT, using the specified values for mu, mu2, and g.
     * The new velocity will be given by the equation:
     *      v' = v * (1 - mu * deltaT - mu2 * |v| * deltaT) + g * deltaT * yHat
     *  
     * if the ball is in stasis, it is not changed
     * 
     * ball velocity will be scaled to Ball.TERMINAL_VEL
     * 
     * @param deltaT the time increment over which the velocity is to be adjusted
     *          must be > 0
     * @param g the gravitational vector that should be used for this adjustment
     * @param mu the first friction coefficient
     * @param mu2 the second friction coefficient
     */
    public void applyPhysics(double deltaT, Vect g, double mu, double mu2){
        if(deltaT <= 0){
            throw new IllegalArgumentException("delta_t must be positive");
        }if(stasis){
            return;
        }
        vel = acceleratedVelocity(deltaT, g, mu, mu2);
        scaleVelocityToTerminal();
        assert checkRep();
    }
    
    /**
     * if the ball is not in stasis, adjust its velocity vector for a time step of 
     * deltaT, assuming the default physical parameters:
     *          g = 25 [L/s^2], mu = 0.025 [L/s^2], mu2 = 0.025 [1/s]
     * 
     * @param deltaT the time increment over which the velocity is to be adjusted
     *          must be > 0
     */
    public void applyPhysics(double deltaT){
        applyPhysics(deltaT, 25, 0.025, 0.025);
    }
    
    /**
     * if the ball is not in stasis, adjust its velocity vector for a time step of 
     * deltaT, assuming the default friction parameters and using the specified
     * gravitational value
     *          mu = 0.025 [L/s^2], mu2 = 0.025 [1/s]. 
     * 
     * @param deltaT the time increment over which the velocity is to be adjusted
     *          must be > 0
     * @param g the gravitational constant that should be used for this adjustment
     */
    public void applyPhysics(double deltaT, double g){
        applyPhysics(deltaT, g, 0.025, 0.025);
    }
    
    /**
     * if the ball is not in stasis, adjust its velocity vector for a time step of
     * deltaT, using the specified values for mu, mu2, and g.
     * 
     * @param deltaT the time increment over which the velocity is to be adjusted
     *          must be > 0
     * @param g the gravitational constant that should be used for this adjustment
     * @param mu the first friction coefficient
     * @param mu2 the second friction coefficient
     */
    public void applyPhysics(double deltaT, double g, double mu, double mu2){
        applyPhysics(deltaT, Vect.Y_HAT.times(g), mu, mu2);
    }
    
    //helper method for adjustVelocity methods - applies the physical acceleration
    //caused by the specified parameters and returns a vector corresponding to the
    //accelerated velocity of the ball
    private Vect acceleratedVelocity(double deltaT, Vect g, double mu, double mu2){
        double frictLoss = 1 - mu * deltaT - mu2 * vel.length() * deltaT;
        Vect newVel = vel.times(frictLoss).plus(g.times(deltaT));
        return newVel;
    }
    
    /**
     * give the ball a new velocity vector
     * 
     * @param velocity the new velocity vector for the ball
     */
    public void impart(Vect velocity){
        this.vel = velocity;
        scaleVelocityToTerminal();
    }
    
    /**
     * if this ball is not in stasis, change its velocity vector to the
     * specified velocity. ball velocity will be scaled to Ball.TERMINAL_VEL
     * 
     * if this ball is in stasis, it will not be changed
     * 
     * @param x the x component of the new velocity
     * @param y the y component of the new velocity
     */
    public void impart(double x, double y){
        if(stasis){
            return;
        }
        this.vel = new Vect(x, y);
        scaleVelocityToTerminal();
    }
    
    //this is a helper method which scales the ball's velocity vector (if necessary)
    //to ensure it is less than TERMINAL_VEL
    private void scaleVelocityToTerminal(){
        if(vel.length() <= TERMINAL_VEL){
            return;
        } else{
            Vect newVel = vel.times(TERMINAL_VEL/vel.length());
            vel = newVel;
        }
    }
    
    /**
     * stop this ball and place it in stasis. balls in stasis have
     * zero velocity and are not affected by physical accelerations
     */
    public void becomeAbsorbed(){
        stasis = true;
    }
    
    /**
     * release this ball from stasis, allowing it to be affected by
     * accelerations
     */
    public void becomeReleased(){
        stasis = false;
    }
    
    
    //Collision Handling!
    
    /**
     * compute the time until another ball (argument) collides with this one
     * 
     * @param ball the ball which might collide with this
     * @return the time in seconds until the collision will take place,
     *          assuming constant velocity
     *          if no collision will occur, returns positive infinity
     */
    @Override public double timeUntilCollision(Ball ball){
        return Geometry.timeUntilBallBallCollision(ball.circle, ball.vel, this.circle, this.vel);
    }
    
    /**
     * change the velocities of both balls to reflect an instantaneous
     * collision
     * 
     * if this ball is in stasis, the collision is instead treated as a
     * reflection from a stationary circle. this ball will not be changed
     * 
     * @param ball the ball which has collided with this one
     */
    @Override public void collideWith(Ball ball){
        if(stasis){
            Vect reflection = 
                    Geometry.reflectCircle(circle.getCenter(), ball.getCenter(), ball.vel);
            ball.impart(reflection);
            return;
        }
        VectPair pair = Geometry.reflectBalls(
                ball.getCenter(), ball.mass, ball.vel, this.getCenter(), this.mass, this.vel);
        ball.impart(pair.v1);
        this.impart(pair.v2);
    }
    
    
    //String handling!
    
    @Override public void draw(char[][] grid) {
        Vect center = circle.getCenter();
        if((center.x() < 0 || center.y() < 0) || 
                (center.y()+1 >= grid.length || center.x()+1 >= grid[0].length)){
            return;
        }
        grid[(int)center.y() + 1][(int)center.x() + 1] = '*';
    }
    
    /**
     * @return a string containing the ball's position and velocity information
     */
    @Override public String toString(){
        return "ball(" + getCenter().x() + "," + getCenter().y() + ")" + vel;
    }

    @Override public CollidableGraphic getGraphic() {
        Vect center = circle.getCenter();
        return new BallGraphic(center.x(), center.y(), r, this.color);
    }
}
