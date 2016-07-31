package sim;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

import org.junit.Test;

import client.Board;
import physics.Vect;
import sim.Flipper.FlipperType;

/** Testing strategy!
 *...
 * Gadget classes:
 *  1. test collidable methods between the gadget and one or more balls
 *  2. test interaction between multiple gadgets with triggering
 *  3. check that all gadgets render themselves properly in text format
 *  4. special considerations per gadget
 *...
 * Special considerations:
 *  1. check absorber behavior - ball is unaffected by physical accelerations
 *      while absorbed
 *  2. triggerable behaviors - verify that calling the trigger method on absorbers and
 *      flippers causes the appropriate events to occur
 *  3. triggerable behaviors - verify that a flipper is not responsive to triggers
 *      while it is moving, etc.
 *  4. verify that flippers do not predict collisions that would occur outside of
 *      the flipper's bounding box, or which would not occur due to the flipper
 *      stopping before the collision could take place
 *  5. for flippers and triangle bumpers, ensure that arbitrary rotations of
 *      the geometry have the same behavior
 *  6. general triggering behaviors - verify that triggering occurs automatically
 *      with all gadgets when a collision takes place
 *  7. general collision behaviors - calling collideWith() in nonsense situations
 *      (such as with a stationary ball) should not throw errors
 *  8. Test portals to make sure can link with other portals, open, and close 
 *  9. Test to make sure board can open and close portals 
 *...
 *  
 *
 */
@SuppressWarnings("deprecation")
public class GadgetTests {
    
    //
    //=== Absorber ===
    //
    
    //Tests that absorbers can absorb balls
    @Test public void testAbsorberAbsorbs(){
        Ball ball = new Ball(0, 0, 5, 25);
        Absorber absorber = new Absorber(5,5, 4, 3);
        absorber.collideWith(ball);
        assertTrue(ball.vel().length() == 0);
        assertTrue(ball.getCenter().x() == 8.75);
        assertTrue(ball.getCenter().y() == 7.75);
        assertTrue(absorber.capturedBalls() == 1);
    }
    
    //Tests that balls inside absorbers aren't affected by gravity
    @Test public void testAbsorberBallIsNotMovedByGravity(){
        Ball ball = new Ball(0, 0, 5, 25);
        Absorber absorber = new Absorber(5,5, 4, 3);
        absorber.collideWith(ball);
        Vect pos1 = ball.getCenter();
        ball.applyPhysics(0.005);
        ball.displace(0.005);
        assertTrue(pos1.equals(ball.getCenter()));
    }
    
    //Tests that balls are released when the absorber is triggered.
    @Test public void testAbsorberTrigger(){
        Ball ball = new Ball(0, 0, 5, 25);
        Absorber absorber = new Absorber(0,19, 20, 1);
        absorber.collideWith(ball);
        assertTrue(absorber.capturedBalls() == 1);
        absorber.triggerAction();
        assertTrue(ball.vel().equals(new Vect(0,-50)));
        assertTrue(ball.getCenter().equals(new Vect(19.75, 18.75)));
    }
    
    //Makes sure the absorber is located at the correct place
    @Test public void testAbsorberIsLocatedAtPosition(){
        Absorber absorber = new Absorber(0, 15, 5, 4);
        assertTrue(absorber.isLocatedAtPosition(3, 17));
        assertFalse(absorber.isLocatedAtPosition(0, 0));
        assertFalse(absorber.isLocatedAtPosition(5, 15));
        assertFalse(absorber.isLocatedAtPosition(4,  19));
        assertTrue(absorber.isLocatedAtPosition(4, 18));
    }
    
    //Tests that the absorber can properly absorb and trigger multiple balls at once.
    @SuppressWarnings("unused")
    @Test public void testAbsorberMultipleBalls(){
        Ball ball1 = new Ball(0, 0, 5, 25);
        Ball ball2 = new Ball(2, 2, 25, -12);
        Absorber absorber = new Absorber(0, 19, 20, 1);
        absorber.collideWith(ball1);
        absorber.collideWith(ball2);
        assertTrue(absorber.capturedBalls() == 2);
        absorber.triggerAction();
        absorber.triggerAction();//should not release twice in quick succession
        if(Absorber.LATENCY_PERIOD > 0){
            assertTrue(absorber.capturedBalls() == 1);
        }
    }
    
    //tests the timing of collisions between balls and absorbers.  Makes sure ball is 
    //one radius away from absorber at time of collision.
    @Test public void testAbsorberTimeUntilCollision(){
        Ball ball = new Ball(0,0,1,-13);
        Absorber absorber = new Absorber(0,19, 20, 1);
        assertTrue(absorber.timeUntilCollision(ball) == Double.POSITIVE_INFINITY);
        ball.impart(0, 25);
        assertTrue(absorber.timeUntilCollision(ball) < 1);
        ball.displace(absorber.timeUntilCollision(ball));
        //at time of collision, the ball should be one radius away from absorber edge
        assertTrue(ball.getCenter().y() - (19 + ball.getRadius()) < 0.01);
    }
    
    //Tests drawing an absorber
    @Test public void testAbsorberDraw(){
        Absorber absorber = new Absorber(0,9, 10, 1);
        char[][] charGrid = new char[22][22];
        for(int jj = 0; jj < charGrid.length; jj++){
            for(int ii = 0; ii < charGrid[0].length; ii ++){
                charGrid[jj][ii] = ' ';
            }
        }
        absorber.draw(charGrid);
        //the tiles directly above and below the absorber should not contain an absorber
        assertTrue(new String(charGrid[9]).equals("                      "));
        //but the tiles containing the absorber certainly should
        assertTrue(new String(charGrid[10]).equals(" ==========           "));
        assertTrue(new String(charGrid[11]).equals("                      "));
    }
    
    //
    //\\\ TriangleBumper ///
    //
    
    //Tests collision with triangle bumper on the middle of the edge
    @Test public void testTriangleCollisionMiddleOfEdge(){
        TriangleBumper tri = new TriangleBumper(6,0,0);
        Ball ball = new Ball(0.5, 0.5, 50, 0);
        assertTrue(Math.abs(tri.timeUntilCollision(ball) - 0.105) < 0.00000001);
        ball.displace(tri.timeUntilCollision(ball));
        assertTrue(tri.timeUntilCollision(ball) == 0);
        tri.collideWith(ball);
        assertTrue(ball.vel().equals(new Vect(-50,0)));
        assertTrue(tri.timeUntilCollision(ball) == Double.POSITIVE_INFINITY);
    }
    
    //tests triangle bumper collision on the corner of the bumper
    @Test public void testTriangleCollisionOnCorner(){
        TriangleBumper tri = new TriangleBumper(6,0,270);
        Ball ball = new Ball(0, 0, 10, 0);
        assertTrue(tri.timeUntilCollision(ball) == 0.575);
        ball.displace(tri.timeUntilCollision(ball));
        tri.collideWith(ball);
        assertTrue(tri.timeUntilCollision(ball) == Double.POSITIVE_INFINITY);
    }
    
    //Tests to make sure triangle is located at correct position
    @Test public void testTriangleIsLocatedAtPosition(){
        TriangleBumper tri = new TriangleBumper(6, 15, 180);
        for(int ii = 0; ii < 20; ii ++){
            for(int jj = 0; jj < 20; jj ++){
                if(ii == 6 && jj == 15){
                    assertTrue(tri.isLocatedAtPosition(ii, jj));
                }else{
                    assertFalse(tri.isLocatedAtPosition(ii, jj));
                }
            }
        }
    }
    
    //Tests drawing a triangle graphic
    @Test public void testTriangleDraw(){
        TriangleBumper tri1 = new TriangleBumper(0,9,0);
        TriangleBumper tri2 = new TriangleBumper(1,9,90);
        TriangleBumper tri3 = new TriangleBumper(2,9,180);
        TriangleBumper tri4 = new TriangleBumper(3,9,270);
        char[][] charGrid = new char[22][22];
        for(int jj = 0; jj < charGrid.length; jj++){
            for(int ii = 0; ii < charGrid[0].length; ii ++){
                charGrid[jj][ii] = ' ';
            }
        }
        tri1.draw(charGrid);
        tri2.draw(charGrid);
        tri3.draw(charGrid);
        tri4.draw(charGrid);
        assertTrue(new String(charGrid[10]).equals(" /\\/\\                 "));
    }
    
    //
    //OOO CircleBumper OOO
    //
    
    //Tests collision with circle bumper
    @Test public void testCircleCollisionNormal(){
        CircleBumper circle = new CircleBumper(6,0);
        Ball ball = new Ball(0.5,0.5,50,0);
        assertTrue(circle.timeUntilCollision(ball) == 0.105);
        ball.displace(circle.timeUntilCollision(ball));
        assertTrue(circle.timeUntilCollision(ball) == 0);
        circle.collideWith(ball);
        assertTrue(ball.vel().equals(new Vect(-50,0)));
        assertTrue(circle.timeUntilCollision(ball) == Double.POSITIVE_INFINITY);
    }
    
    //Tests glancing collision with circle bumper
    @Test public void testCircleCollisionGlancing(){
        CircleBumper circle = new CircleBumper(6,0);
        Ball ball = new Ball(0,0,50,0);
        ball.moveTo(0.5, 0);
        assertTrue(circle.timeUntilCollision(ball) > 0.105);
        assertTrue(circle.timeUntilCollision(ball) < 0.110);
        ball.displace(circle.timeUntilCollision(ball));
        assertTrue(circle.timeUntilCollision(ball) == 0);
        circle.collideWith(ball);
        assertTrue(ball.vel().y() < 0);
        assertTrue(circle.timeUntilCollision(ball) == Double.POSITIVE_INFINITY);
    }
    
    //tests to make sure circle bumper is located in correct spot
    @Test public void testCircleIsLocatedAtPosition(){
        CircleBumper circle = new CircleBumper(4, 5);
        for(int ii = 0; ii < 20; ii ++){
            for(int jj = 0; jj < 20; jj ++){
                if(ii == 4 && jj == 5){
                    assertTrue(circle.isLocatedAtPosition(ii, jj));
                }else{
                    assertFalse(circle.isLocatedAtPosition(ii, jj));
                }
            }
        }
    }
    
    //Tests to make sure circle bumper is drawn correctly
    @Test public void testCircleDraw(){
        CircleBumper circle = new CircleBumper(4, 5);
        char[][] grid = new char[22][22];
        circle.draw(grid);
        assertTrue(grid[6][5] == 'O');
    }
    
    //
    //### SquareBumper ###
    //
    
    //Tests colliding with a square bumper on the edge of the bumper
    @Test public void testSquareCollisionOnEdge(){
        SquareBumper square = new SquareBumper(0,0);
        Ball ball = new Ball(0.5, 6.5, 0, -50);
        assertTrue(square.timeUntilCollision(ball) == 0.105);
        ball.displace(square.timeUntilCollision(ball));
        assertTrue(square.timeUntilCollision(ball) == 0);
        square.collideWith(ball);
        assertTrue(ball.vel().equals(new Vect(0, 50)));
        assertTrue(square.timeUntilCollision(ball) == Double.POSITIVE_INFINITY);
    }
    
    //Tests colliding with the corner of the bumper
    @Test public void testSquareCollisionOnCorner(){
        SquareBumper square = new SquareBumper(0,0);
        Ball ball = new Ball(6.5, 6.5, -50, -50);
        assertTrue(square.timeUntilCollision(ball) > 0.105);
        assertTrue(square.timeUntilCollision(ball) < 0.110);
        ball.displace(square.timeUntilCollision(ball));
        assertTrue(square.timeUntilCollision(ball) == 0);
        square.collideWith(ball);
        assertTrue(ball.vel().x() - 50 < 0.00001);
        assertTrue(ball.vel().y() - 50 < 0.00001);
        assertTrue(square.timeUntilCollision(ball) == Double.POSITIVE_INFINITY);
    }
    
    //Tests to make sure the bumper is located at the right spot.
    @Test public void testSquareIsLocatedAtPosition(){
        SquareBumper square = new SquareBumper(18,16);
        for(int ii = 0; ii < 20; ii ++){
            for(int jj = 0; jj < 20; jj ++){
                if(ii == 18 && jj == 16){
                    assertTrue(square.isLocatedAtPosition(ii, jj));
                }else{
                    assertFalse(square.isLocatedAtPosition(ii, jj));
                }
            }
        }
    }
    
    //Tests drawing a square bumper
    @Test public void testSquareDraw(){
        SquareBumper square = new SquareBumper(11,3);
        char[][] grid = new char[22][22];
        square.draw(grid);
        assertTrue(grid[4][12] == '#');
    }
    
    //
    //--- Flipper |||
    //
    
    //Tests colliding with a stationary flipper
    @Test public void testFlipperStationaryCollision(){
        Flipper flip = new Flipper(6, -1, FlipperType.LEFT);
        Ball ball = new Ball(0.5, 0, 50, 0);
        assertTrue(flip.timeUntilCollision(ball) == 0.105);
        flip.collideWith(ball);
        assertTrue(ball.vel().x() == -50 * 0.95);
    }
    
    //Tests trying to trigger a flipper when the ball will not collide
    @Test public void testFlipperTimeUntilCollisionFalseResult(){
        Flipper flip = new Flipper(5,5,FlipperType.RIGHT);
        Ball ball = new Ball(6,4.74,0,0);
        flip.triggerAction();
        assertTrue(flip.timeUntilCollision(ball) == Double.POSITIVE_INFINITY);
    }
    
    //Tests rotating flippers to collide them with stationary balls.
    @Test public void testFlipperMovingFlipperStationaryBall(){
        Flipper flipL = new Flipper(0, 0, FlipperType.LEFT);
        Flipper flipR = new Flipper(1, 0, FlipperType.RIGHT);
        Ball ball = new Ball(1.5,0.5,0,0);
        flipL.triggerAction();
        flipR.triggerAction();
        assertTrue(Math.abs(flipL.timeUntilCollision(ball) - flipR.timeUntilCollision(ball)) < 0.000001);
        assertTrue(flipL.timeUntilCollision(ball) < 1);
        flipL.displace(flipL.timeUntilCollision(ball));
        assertTrue(flipL.timeUntilCollision(ball) == 0);
        flipL.collideWith(ball);
        assertTrue(flipL.timeUntilCollision(ball) == Double.POSITIVE_INFINITY);
        //run same test with right flipper
        ball = new Ball(1.5, 0.5, 0, 0);
        flipR.displace(flipR.timeUntilCollision(ball));
        assertTrue(flipR.timeUntilCollision(ball) == 0);
        flipR.collideWith(ball);
        assertTrue(flipR.timeUntilCollision(ball) == Double.POSITIVE_INFINITY);
    }
    
    //Tests displacing a flipper with a long time step.  Flipper should be up and 
    //not moving.
    @Test public void testFlipperTriggerLongDisplacement(){
        Flipper flip = new Flipper(0, 0, FlipperType.RIGHT);
        flip.triggerAction();
        flip.displace(0.34);
        assertTrue(flip.isUp());
        assertFalse(flip.isMoving());
    }
    
    //Tests moving a flipper and returning it to starting position
    @Test public void testFlipperReturnsToStartingPosition(){
        Flipper flip = new Flipper(0, 0, FlipperType.RIGHT);
        flip.triggerAction();
        assertTrue(flip.isMoving());
        flip.displace(0.085);
        assertTrue(flip.isUp());
        flip.triggerAction();
        assertTrue(flip.isMoving());
        flip.displace(0.085);
        assertTrue(flip.isDown());
    }
    
    //Tests triggering a left flipper
    @Test public void testFlipperTriggerLeft(){
        Flipper flip = new Flipper(0,0,FlipperType.LEFT);
        flip.triggerAction();
        assertTrue(flip.isMoving());
        flip.displace(0.085);
        assertTrue(flip.isUp());
        flip.triggerAction();
        flip.displace(0.085);
        assertTrue(flip.isDown());
    }
    
    //Tests triggering a flipper twice in a row.  Should ignore the second trigger.
    @Test public void testFlipperDoubleTrigger(){
        Flipper flip = new Flipper(0, 0, FlipperType.RIGHT);
        flip.triggerAction();
        assertTrue(flip.isMoving());
        flip.triggerAction(); //should be ignored
        flip.displace(0.085);
        assertTrue(flip.isUp());
    }
    
    //Tests to make sure flipper is located in correct location
    @Test public void testFlipperIsLocatedAtPosition(){
        Flipper flip = new Flipper(3, 16, FlipperType.LEFT);
        for(int ii = 0; ii < 20; ii ++){
            for(int jj = 0; jj < 20; jj++){
                if((ii == 3 || ii == 4)&&(jj == 16 || jj == 17)){
                    assertTrue(flip.isLocatedAtPosition(ii,jj));
                }else{
                    assertFalse(flip.isLocatedAtPosition(ii, jj));
                }
            }
        }
    }
    
    //Checks to make sure flipper pivots are correct.
    @Test public void testFlipperCOR(){
        Flipper[] flips = {
                new Flipper(0, 0, FlipperType.LEFT, 0),
                new Flipper(0, 0, FlipperType.LEFT, 90),
                new Flipper(0, 0, FlipperType.LEFT, 180),
                new Flipper(0, 0, FlipperType.LEFT, 270),
                new Flipper(0, 0, FlipperType.RIGHT, 0),
                new Flipper(0, 0, FlipperType.RIGHT, 90),
                new Flipper(0, 0, FlipperType.RIGHT, 180),
                new Flipper(0, 0, FlipperType.RIGHT, 270)
        };
        assertTrue(flips[0].getPivot().equals(new Vect(0.25,0.25)));
        assertTrue(flips[1].getPivot().equals(new Vect(1.75,0.25)));
        assertTrue(flips[2].getPivot().equals(new Vect(1.75,1.75)));
        assertTrue(flips[3].getPivot().equals(new Vect(0.25,1.75)));
        
        assertTrue(flips[4].getPivot().equals(new Vect(1.75,0.25)));
        assertTrue(flips[5].getPivot().equals(new Vect(1.75,1.75)));
        assertTrue(flips[6].getPivot().equals(new Vect(0.25,1.75)));
        assertTrue(flips[7].getPivot().equals(new Vect(0.25,0.25)));
    }
    
    //Tests drawing a right and left flipper
    @Test public void testFlipperDraw(){
        Flipper flip1 = new Flipper(2, 5, FlipperType.LEFT);
        Flipper flip2 = new Flipper(10, 10, FlipperType.RIGHT);
        flip2.triggerAction();
        flip2.displace(0.085);
        char[][] grid = new char[22][22];
        flip2.draw(grid);
        flip1.draw(grid);
        for(int ii = 0; ii < grid[0].length; ii ++){
            for(int jj = 0; jj < grid.length; jj ++){
                if((ii == 3 && jj == 6)||(ii == 3 && jj == 7)){
                    assertTrue(grid[jj][ii] == '|');
                }else if((ii == 11 && jj == 11)||(ii == 12 && jj == 11)){
                    assertTrue(grid[jj][ii] == '-');
                }
            }
        }
        flip1.triggerAction();
        flip1.displace(0.05);
        flip1.draw(grid);
        assertTrue(grid[6][3] == '-' && grid[6][4] == '-');
    }
    
    //
    //*** General Tests ***
    //
    
    //Tests auto-triggering flippers
    @Test public void testAutoTriggering(){
        Flipper flips[] = {
                new Flipper(0,0,FlipperType.LEFT,180),
                new Flipper(0,0,FlipperType.RIGHT,90),
                new Flipper(0,0,FlipperType.RIGHT,0),
                new Flipper(0,0,FlipperType.LEFT,90),
                new Flipper(3,3,FlipperType.RIGHT)
        };
        Triggerable[] sources = {
                new SquareBumper(5,5),
                new TriangleBumper(5,5,180),
                new CircleBumper(5,5),
                new Absorber(0,0,1,1),
                new Flipper(4,2,FlipperType.RIGHT)
        };
        Ball ball = new Ball(10,10);
        for(int ii = 0; ii < flips.length; ii ++){
            assertFalse(flips[ii].isMoving());
            sources[ii].addTrigger(flips[ii]);
            sources[ii].collideWith(ball);
            assertTrue(flips[ii].isMoving());
        }
    }
    
    
    @Test public void testWhiff(){
        Triggerable[] trigs = {
                new SquareBumper(0,0),
                new TriangleBumper(0,0,0),
                new CircleBumper(0,0),
                new Flipper(0,0,FlipperType.LEFT)
        };
        Ball ball = new Ball(10,10,5,0);
        for(Triggerable gadget : trigs){
            assertTrue(gadget.timeUntilCollision(ball) == Double.POSITIVE_INFINITY);
            gadget.collideWith(ball);//this should not be allowed to break the program
        }
    }
    
    //Portal testing!
    
    //tests instantiating a portal
    @Test public void testPortalExits(){
        Portal first = new Portal(0, 1, "first", "A", "second", new LinkedList<Envelope>());
        assertTrue(first.exitBoard()=="A");
        assertTrue(first.exitPortal() == "second");
    }
    //tests opening a portal
    @Test public void testPortalOpening(){
        Portal first = new Portal(0, 1, "first", "A", "second", new LinkedList<Envelope>());
        first.open();
        assertTrue(first.isOpen());
        first.close();
        assertFalse(first.isOpen());
    }
    //tests colliding a ball with an open portal
    @Test public void testPortalOpenCollision(){
        Queue<Envelope> queue = new LinkedList<Envelope>();
        Portal p = new Portal(0, 0, "first", "A", "second", queue);
        Ball ball = new Ball(5,5, -5, -5);
        p.open();
        assertTrue(p.timeUntilCollision(ball) > 0 && p.timeUntilCollision(ball) < Double.POSITIVE_INFINITY);
        p.collideWith(ball);
        assertTrue(ball.isInStasis());
        assertTrue(queue.remove().contents() == ball);
        ball.becomeReleased();
        assertTrue(ball.vel().equals(new Vect(-5,-5)));
    }
    //tests colliding a ball with a closed portal
    @Test public void testPortalClosedCollision(){
        Queue<Envelope> queue = new LinkedList<Envelope>();
        Portal p = new Portal(0, 0, "first", "", "second", queue);
        Ball ball = new Ball(5,5, -5, -5);
        p.close();
        assertTrue(p.timeUntilCollision(ball) == Double.POSITIVE_INFINITY);
        p.collideWith(ball);
        assertTrue(queue.isEmpty());
        assertFalse(ball.isInStasis());
    }
    //tests spawning a ball in a portal
    @Test public void testPortalSpawnBall(){
        Queue<Envelope> queue = new LinkedList<Envelope>();
        Portal p = new Portal(0, 0, "first", "A", "second", queue);
        Ball ball1 = new Ball(5,5, -5, -5);
        p.spawnBall(ball1);
        Ball ball2 = p.spawnBall(new Vect(-5,-5));
        assertTrue(ball1.getCenter().equals(ball2.getCenter()));
        assertTrue(ball1.vel().equals(ball2.vel()));
        Ball ball3 = p.spawnBall(new Vect(0,0));
        assertFalse(ball3.vel().equals(new Vect(0,0)));
    }
    //tests adding a portal to the board
    @Test
    public void addPortalToBoard(){
        Board A = new Board("A", 20.0, 0.2, 0.3);
        A.addPortal(4, 4, "chester", "A", "nelson");
        assertTrue(A.getPortalNames().size()==1);
    }
    //tests closing portals on disconnect from a server
    @Test
    public void closePortalsOnDisconnect() throws IOException{
        Board testBoard1 = new Board("testBoard1", 20.0, 0.2, 0.3);
        testBoard1.addPortal(4, 4, "chester", "testBoard2", "nelson");
        Board testBoard2 = new Board("testBoard2", 20.0, 0.2, 0.3);
        testBoard2.addPortal(5, 5, "nelson", "testBoard1", "chester");
        testBoard1.openPortals("testBoard2", testBoard2.getPortalNames());
        testBoard2.openPortals("testBoard1", testBoard1.getPortalNames());
        for (Portal portal: testBoard1.getPortalList()){
            assertTrue(portal.isOpen());
        }for (Portal portal: testBoard2.getPortalList()){
            assertTrue(portal.isOpen());
        }
        testBoard1.disconnectBoard("testBoard2");
        for (Portal portal: testBoard1.getPortalList()){
            assertFalse(portal.isOpen());
        }
    }
}
