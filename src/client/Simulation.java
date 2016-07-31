package client;

import java.util.List;

import physics.Vect;
import sim.Ball;
import sim.Collidable;
import sim.Triggerable;
import sim.Wall;

/**
 * Simulation Class
 * 
 * supports methods for physical simulation of pingball game
 */
public class Simulation {
    
    private static final int MAX_CALL_STACK_SIZE = 100;
    //stack limit to avoid stack overflow when clipping glitches occur
    private static final int G_FIELD_RESOLUTION = 2;
    private static final int G_FIELD_SIZE = G_FIELD_RESOLUTION*(Board.DEFAULT_SIZE+1);
    private static final double WARP_INTENSITY = -30;
    private static final double WARP_RADIUS = Board.DEFAULT_DISTORTION_RADIUS*2;
    //parameters for gravity distortions
    
    private List<Wall> walls;
    //the walls of the board
    private List<Ball> balls;
    //the balls in play
    private List<Triggerable> gadgets;
    //the gadgets on the board
    
    private final Vect g;
    //the gravity value to be used
    private final double mu;
    //the first friction value to be used
    private final double mu2;
    //the second friction value to be used
    
    private VectField gravity;
    //this is the VectField used to compute physical accelerations during
    //simulation
    
    /**
     * construct a simulation using the provided lists of walls, balls, and gadgets,
     * and the specified physics parameters.  This class will violently mutate objects
     * in the balls, gadgets list.
     * 
     * @param walls the walls of the board
     * @param balls the balls in play
     * @param gadgets the gadgets on the board
     * @param g the value of gravity
     * @param mu the first friction value
     * @param mu2 the second friction value
     */
    public Simulation(List<Wall> walls, List<Ball> balls, List<Triggerable> gadgets,
            double g, double mu, double mu2){
        this.walls = walls;
        this.balls = balls;
        this.gadgets = gadgets;
        this.g = new Vect(0,g);
        this.mu = mu;
        this.mu2 = mu2;
        gravity = VectField.uniformVectField(42, this.g);
    }
    
    /**
     * advance the simulation through a period of time, accounting for all collisions,
     * physical accelerations, and other state changes that occur during that time
     * 
     * @param timeStepSeconds the length of the time step in seconds
     */
    public void advance(double timeStepSeconds, int n){
        double lowestTime = Double.POSITIVE_INFINITY;
        double t;
        Ball collidingBall = null;
        Collidable collidingWith = null;
        //iterate over all balls, finding earliest collision
        for(int ii = 0; ii < balls.size(); ii ++){
            //first check for ball-ball collisions
            for(int jj = 0; jj < balls.size(); jj ++){
                if(ii != jj){
                   t = balls.get(jj).timeUntilCollision(balls.get(ii));
                   if(t < lowestTime){
                       lowestTime = t;
                       collidingBall = balls.get(ii);
                       collidingWith = balls.get(jj);
                   }
                }
            } if(lowestTime == 0)break;
            
            //second, check for ball-wall collisions
            for(int kk = 0; kk < walls.size(); kk ++){
                t = walls.get(kk).timeUntilCollision(balls.get(ii));
                if(t < lowestTime){
                    lowestTime = t;
                    collidingBall = balls.get(ii);
                    collidingWith = walls.get(kk);
                }
            } if(lowestTime == 0)break;
            
            //finally, check for ball-gadget collisions
            for(int hh = 0; hh < gadgets.size(); hh++){
                t = gadgets.get(hh).timeUntilCollision(balls.get(ii));
                if(t < lowestTime){
                    lowestTime = t;
                    collidingBall = balls.get(ii);
                    collidingWith = gadgets.get(hh);
                }
            }
        }
        
        if(lowestTime <= timeStepSeconds && n < MAX_CALL_STACK_SIZE){
            //if a collision will occur within this time step, then call
            //advance() recursively until the time step is completed.
            displaceAll(lowestTime);
            collidingWith.collideWith(collidingBall);
            applyPhysics(lowestTime);
            advance(timeStepSeconds - lowestTime, n+1);
        } else{
            //base case; no collisions remaining. displace objects and return.
            displaceAll(timeStepSeconds);
            applyPhysics(timeStepSeconds);
        }
    }
    
    //private helper method. displaces all objects on the board through
    //the specified time step, assuming that no collisions or physical accelerations
    //will take place
    private void displaceAll(double timeStepSeconds){
        for(Ball ball : balls){
            if(timeStepSeconds != 0){
                ball.displace(timeStepSeconds);
            }
        }
        for(Collidable gadget: gadgets){
            gadget.displace(timeStepSeconds);
        }
    }
    
    //private helper method. apply physical accelerations to all pertinent
    //objects on the board, for the given time step
    private void applyPhysics(double timeStepSeconds){
        if(timeStepSeconds > 0){
            for(Ball ball : balls){
                //have to transform ball position from (-0.5,20.5) to (0, G_FIELD_SIZE-1)
                Vect g = gravity.valueAt(ball.getCenter()
                        .plus(new Vect(0.5,0.5)).times(G_FIELD_RESOLUTION));
                ball.applyPhysics(timeStepSeconds, g, mu, mu2);
            }
        }
    }
    
    /**
     * create a local gravity distortion around position x,y
     * the gravity distortion will attract nearby objects to position x,y
     * 
     * @param x the x position
     * @param y the y position
     */
    public void distortGravity(double x, double y){
        //have to transform x,y position from (-0.5,20.5) to (0, G_FIELD_SIZE-1)
        gravity = VectField.createLocalDistortion(
                G_FIELD_SIZE, WARP_RADIUS, g, WARP_INTENSITY,
                G_FIELD_RESOLUTION*(x+0.5), G_FIELD_RESOLUTION*(y+0.5));
    }
    
    /**
     * return simulation gravity to a uniform field
     */
    public void resetGravity(){
        gravity = VectField.uniformVectField(G_FIELD_SIZE, g);
    }
}
