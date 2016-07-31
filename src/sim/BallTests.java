package sim;

import static org.junit.Assert.assertTrue;
import org.junit.Test;
import physics.*;

// Testing strategy!
// ...
// Ball class:
//  1. test with one ball in a vacuum
//      test with ball moving or stationary. 
//      test with small and large time step.
//      test with physics and without. 
//      also test boundary cases such as terminal velocity, zero radius, etc.
//  2. test with two or more balls
//      collision detection and response
//
public class BallTests {
    
    //tests stationary ball
    @Test public void testBallStationary(){
        Ball ball = new Ball(0, 0);
        Vect iniPos = ball.getCenter();
        ball.displace(0.05);
        assertTrue(ball.getCenter().equals(iniPos));
        ball.displace(2.5);
        assertTrue(ball.getCenter().equals(iniPos));
    }
    
    //Tests ball displacement, where ball moves
    @Test public void testBallDisplacement(){
        Ball ball = new Ball(0,0,3,4);
        Vect iniPos = ball.getCenter();
        Vect iniVel = ball.vel();
        ball.displace(0.05);
        Vect newPos = ball.getCenter();
        assertTrue(newPos.equals(iniPos.plus(iniVel.times(0.05))));
        ball.displace(1.5);
        assertTrue(ball.getCenter().equals(newPos.plus(iniVel.times(1.5))));
    }
    
    //Tests moving a ball wiwth friction
    @Test public void testBallFriction(){
        Ball ball = new Ball(0, 0, -1, 1, 0.5);
        Vect iniVel = ball.vel();
        Angle iniTheta = ball.vel().angle();
        ball.applyPhysics(0.05, 0);
        //if no gravity, there should be no change in direction
        assertTrue(ball.vel().angle().equals(iniTheta));
        assertTrue(ball.vel().length() < iniVel.length());
    }
    
    //tests applying gravity to balls going up and down
    @Test public void testBallGravityVertical(){
        Ball ballGoingUp = new Ball(0, 0, 0, -5);
        Ball ballGoingDown = new Ball(0, 0, 0, 5);
        //adjust w/ zero friction
        ballGoingUp.applyPhysics(0.05, 25, 0, 0);
        ballGoingDown.applyPhysics(0.05, 25, 0, 0);
        assertTrue(5 > ballGoingUp.vel().length());
        assertTrue(5 < ballGoingDown.vel().length());
        assertTrue(3.75 == ballGoingUp.vel().length());
        assertTrue(0. == ballGoingDown.vel().x());
    }
    
    //Tests applying gravity to balls going sideways
    @Test public void testBallGravityParabolic(){
        Ball ballGoingSide = new Ball(0, 0, 3, 0);
        //adjust w/ zero friction
        ballGoingSide.applyPhysics(0.15, 25, 0, 0);
        assertTrue(3.0 == ballGoingSide.vel().x());
        assertTrue(0. < ballGoingSide.vel().y());
    }
    
    //Tests to make sure ball can't go faster than terminal velocity
    @Test public void testBallTerminalVelocity(){
        double vel = Ball.TERMINAL_VEL;
        Ball ballVert = new Ball(0, 0, 0, vel);
        Ball ballDiag = new Ball(0, 0, vel+100, vel+100);
        Angle iniTheta = ballDiag.vel().angle();
        //test w/ gravity and no friction
        ballVert.applyPhysics(0.002, 25, 0, 0);
        //turn gravity off for this ball to avoid angular deflection
        ballDiag.applyPhysics(0.002, 0, 0, 0);
        assertTrue(Ball.TERMINAL_VEL >= ballVert.vel().length());
        assertTrue(Ball.TERMINAL_VEL >= ballDiag.vel().length());
        assertTrue(iniTheta.radians() == ballDiag.vel().angle().radians());
        assertTrue(Angle.DEG_90.radians() == ballVert.vel().angle().radians());
    }
    
    //Tests to make sure ball radius cannot be zero
    @Test public void testBallZeroRadius(){
        try {
            Ball ball = new Ball(0, 0, 0, 0, 0);
            ball.displace(0.1);
            assertTrue(false);
        }catch(IllegalArgumentException e){
            assertTrue(true);
        }
    }
    
    //tests for Ball-Ball collisions
    //Two balls collide.
    @Test public void testTimeToCollisionWillCollide(){
        Ball ball1 = new Ball(0, -1, 0, 4);
        Ball ball2 = new Ball(0, 1, 0, 0);
        assertTrue(ball1.timeUntilCollision(ball2) < 1);
        assertTrue(ball2.timeUntilCollision(ball1) < 1);
    }
    
    //Tests that two balls don't collide.
    @Test public void testTimeToCollisionWontCollide(){
        Ball ball1 = new Ball(0, -1, 4, 0);
        Ball ball2 = new Ball(0, 1, 0, 2);
        assertTrue(ball1.timeUntilCollision(ball2) == Double.POSITIVE_INFINITY);
        assertTrue(ball2.timeUntilCollision(ball1) == Double.POSITIVE_INFINITY);
    }
    
    //Tests ball collisions
    //Two balls collide, and then do not collide again.
    @Test public void testCollideWith(){
        Ball ball1 = new Ball(0, -1, 0, 4);
        Ball ball2 = new Ball(0.15, 1, 0, 1);
        double deltaT = ball1.timeUntilCollision(ball2);
        assertTrue(deltaT != Double.POSITIVE_INFINITY);
        ball1.displace(deltaT);
        ball2.displace(deltaT);
        ball1.collideWith(ball2);
        assertTrue(ball1.timeUntilCollision(ball2) == Double.POSITIVE_INFINITY);
        assertTrue(ball1.vel().x() < 0);
        assertTrue(ball2.vel().x() > 0);
    }
    
    //Tests ball behavior in stasis
    @Test public void testStasisBehavior(){
        Ball ball = new Ball(0, 0, -1, -1);
        ball.impart(new Vect(0,0));
        ball.becomeAbsorbed();
        assertTrue(ball.vel().length() == 0);
        Vect pos = ball.getCenter();
        ball.applyPhysics(0.20);
        ball.applyPhysics(0.15, 50000);
        ball.displace(0.30);
        assertTrue(ball.vel().length() == 0);
        assertTrue(ball.getCenter().equals(pos));
    }
    
}
