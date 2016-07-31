package sim;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import physics.*;
import sim.Wall.WallType;

// Testing strategy!
// ...
// Wall class(es):
//  1. test collision handling
//  2. test drawing behavior w/ multiple walls, gadgets in play
public class WallTests {
    private char[][] charGrid;
    
    private void clearGrid(){
        charGrid = new char[22][22];
        for(int jj = 0; jj < charGrid.length; jj++){
            for(int ii = 0; ii < charGrid[0].length; ii ++){
                charGrid[jj][ii] = ' ';
            }
        }
    }
    
    //Tests for Wall class!
    @Test public void testWallTimeUntilCollision(){
        Wall wall = new Wall(WallType.BOTTOM);
        Ball ball = new Ball(15, 18, 2, 10);
        assertTrue(wall.timeUntilCollision(ball) < 1);
        wall.collideWith(ball);
        assertTrue(wall.timeUntilCollision(ball) == Double.POSITIVE_INFINITY);
    }
    
    @Test public void testWallCollideWith(){
        Wall wall = new Wall(WallType.BOTTOM);
        Ball ball = new Ball(15, 19, 2, 10);
        Vect pos = ball.getCenter();
        wall.collideWith(ball);
        //checks that collision is perfectly elastic,
        //ball's position is not affected
        assertTrue(ball.vel().y() + 10 == 0);
        assertTrue(ball.vel().x() == 2);
        assertTrue(ball.getCenter().equals(pos));
    }
    
    @Test public void testWallCollisionCornerCase(){
        Wall wallR = new Wall(WallType.RIGHT);
        Wall wallB = new Wall(WallType.BOTTOM);
        Ball ball = new Ball(18, 18, 1, 1);
        assertTrue(wallR.timeUntilCollision(ball) == wallB.timeUntilCollision(ball));
    }
    
    @Test public void testDrawOnlyOneWall(){
        Wall wall = new Wall(WallType.RIGHT);
        clearGrid();
        wall.draw(charGrid);
        for(char[] row : charGrid){
            assertTrue(new String(row).equals("          "+"           ."));
        }
    }
    
    @Test public void testDrawMultipleWallsAndBall(){
        Wall wallR = new Wall(WallType.RIGHT);
        Wall wallB = new Wall(WallType.BOTTOM);
        Wall wallL = new Wall(WallType.LEFT);
        Wall wallT = new Wall(WallType.TOP);
        Ball ball = new Ball(10, 10);
        Collidable[] things = {ball, wallL, wallT, wallB, wallR};
        clearGrid();
        for(Collidable thing : things){
            thing.draw(charGrid);
        }
        String str = "";
        for(char[] row : charGrid){
            str += new String(row) + "\n";
        }
        assertTrue(str.equals("......................\n" + 
                              ".                    .\n" + 
                              ".                    .\n" + 
                              ".                    .\n" + 
                              ".                    .\n" + 
                              ".                    .\n" + 
                              ".                    .\n" + 
                              ".                    .\n" + 
                              ".                    .\n" + 
                              ".                    .\n" + 
                              ".                    .\n" + 
                              ".          *         .\n" + 
                              ".                    .\n" + 
                              ".                    .\n" + 
                              ".                    .\n" + 
                              ".                    .\n" + 
                              ".                    .\n" + 
                              ".                    .\n" + 
                              ".                    .\n" + 
                              ".                    .\n" + 
                              ".                    .\n" + 
                              "......................\n"));
    }
    
    @Test public void testWallDrawLinkToBoard(){
        Wall wallR = new Wall(WallType.RIGHT);
        Wall wallB = new Wall(WallType.BOTTOM);
        Wall wallL = new Wall(WallType.LEFT);
        Wall wallT = new Wall(WallType.TOP);
        Collidable[] things = {wallR, wallB, wallL, wallT};
        wallR.connectBoard("France");
        clearGrid();
        for(Collidable thing : things){
            thing.draw(charGrid);
        }
        String str = "";
        for(char[] row : charGrid){
            str += new String(row) + "\n";
        }
        assertTrue(str.equals(
                "......................\n" + 
                ".                    .\n" + 
                ".                    .\n" + 
                ".                    .\n" + 
                ".                    .\n" + 
                ".                    .\n" + 
                ".                    .\n" + 
                ".                    .\n" + 
                ".                    F\n" + 
                ".                    r\n" + 
                ".                    a\n" + 
                ".                    n\n" + 
                ".                    c\n" + 
                ".                    e\n" + 
                ".                    .\n" + 
                ".                    .\n" + 
                ".                    .\n" + 
                ".                    .\n" + 
                ".                    .\n" + 
                ".                    .\n" + 
                ".                    .\n" + 
                "......................\n"));
        assertTrue(wallR.linkName().equals("France"));
    }
    
    @Test public void testWallLinkAlreadyLinked(){
        Wall wallR = new Wall(WallType.RIGHT);
        Wall wallB = new Wall(WallType.BOTTOM);
        Wall wallL = new Wall(WallType.LEFT);
        Wall wallT = new Wall(WallType.TOP);
        assertFalse(wallL.isTransparent());
        wallL.connectBoard("Guatemala");
        assertTrue(wallL.isTransparent());
        assertTrue(wallL.linkName().equals("Guatemala"));
        wallL.connectBoard("SuperCaliFragiListicExpialadocious");//i know i spelled it wrong
        assertTrue(wallL.linkName().equals("SuperCaliFragiListicExpialadocious"));
        assertTrue(wallL.isTransparent());
        Collidable[] things = {wallR, wallB, wallL, wallT};
        clearGrid();
        for(Collidable thing : things){
            thing.draw(charGrid);
        }
        String str = "";
        for(char[] row : charGrid){
            str += new String(row) + "\n";
        }
        assertTrue(str.equals(
                "......................\n" + 
                "S                    .\n" + 
                "u                    .\n" + 
                "p                    .\n" + 
                "e                    .\n" + 
                "r                    .\n" + 
                "C                    .\n" + 
                "a                    .\n" + 
                "l                    .\n" + 
                "i                    .\n" + 
                "F                    .\n" + 
                "r                    .\n" + 
                "a                    .\n" + 
                "g                    .\n" + 
                "i                    .\n" + 
                "L                    .\n" + 
                "i                    .\n" + 
                "s                    .\n" + 
                "t                    .\n" + 
                "i                    .\n" + 
                "c                    .\n" + 
                "......................\n"));
    }
    
    @Test public void testWallDelink(){
        Wall wallT = new Wall(WallType.TOP);
        Ball ball = new Ball(10,10,0,-50);
        wallT.connectBoard("Guatemala");
        double t = wallT.timeUntilCollision(ball);
        assertTrue(wallT.linkName().equals("Guatemala"));
        assertTrue(wallT.isTransparent());
        wallT.disconnect();
        assertTrue(wallT.timeUntilCollision(ball) < t);
        assertTrue(wallT.linkName().isEmpty());
        assertFalse(wallT.isTransparent());
    }
    
    @Test public void testWallInvisibleCollision(){
        Wall wallR = new Wall(WallType.RIGHT);
        Ball ball = new Ball(10,10,50,0);
        double t = wallR.timeUntilCollision(ball);
        wallR.connectBoard("Frabjous");
        assertTrue(wallR.timeUntilCollision(ball) > t);
        ball.displace(wallR.timeUntilCollision(ball));
        assertTrue(wallR.shouldTeleport(ball));
        wallR.collideWith(ball);
        ball.becomeReleased();
        assertTrue(ball.vel().equals(new Vect(50,0)));
    }
    
    @Test public void testWallInvisibleCornerCollision(){
        Wall wallR = new Wall(WallType.RIGHT);
        Ball ball = new Ball(19,1.5, 5,-5);
        wallR.connectBoard("HyperionIV");
        assertTrue(wallR.isTransparent());
        ball.displace(wallR.timeUntilCollision(ball));
        wallR.collideWith(ball);
        ball.becomeReleased();
        assertTrue(Math.abs(ball.vel().x()) - 5 < 0.000001);
        assertTrue(Math.abs(ball.vel().y()) - 5 < 0.000001);
        assertTrue(ball.vel().y() > 0);
    }
    
    @Test public void testWallInvisibleCornerCollisionTwoWalls(){
        Wall wallR = new Wall(WallType.RIGHT);
        Wall wallT = new Wall(WallType.TOP);
        Ball ball = new Ball(19,1,5,-5);
        wallR.connectBoard("HyperionIV");
        wallT.connectBoard("HyperionV");
        assertTrue(wallR.isTransparent());
        assertTrue(wallR.timeUntilCollision(ball) < Double.POSITIVE_INFINITY);
        ball.displace(wallR.timeUntilCollision(ball));
        wallR.collideWith(ball);
        assertFalse(wallR.shouldTeleport(ball));
        assertTrue(Math.abs(ball.vel().x()) - 5 < 0.000001);
        assertTrue(Math.abs(ball.vel().y()) - 5 < 0.000001);
        assertTrue(ball.vel().y() > 0);
        assertTrue(ball.vel().x() < 0);
    }
    
    @Test public void testWallTransportCheck(){
        Wall wallB = new Wall(WallType.BOTTOM);
        Ball ball = new Ball(2,12,1,25);
        ball.displace(wallB.timeUntilCollision(ball));
        assertFalse(wallB.shouldTeleport(ball));
        wallB.connectBoard("FRAB");
        ball.displace(wallB.timeUntilCollision(ball));
        assertTrue(wallB.shouldTeleport(ball));
        wallB.collideWith(ball);
        ball = new Ball(0.25, 20.24, -1, 5);
        assertTrue(wallB.timeUntilCollision(ball) == 0);
        assertFalse(wallB.shouldTeleport(ball));
    }
    
    @Test public void testWallClippingCheck(){
        Wall wallB = new Wall(WallType.BOTTOM);
        Wall wallL = new Wall(WallType.LEFT);
        Ball ball1 = new Ball(0, 0, 0, -4);
        Ball ball2 = new Ball(-0.6, 2, 0, 3);
        Ball ball3 = new Ball(5, 19.8, 0, 0);
        Ball ball4 = new Ball(5, 20.25, 4, 0);
        assertTrue(wallL.isClipping(ball1) && wallL.isClipping(ball2));
        assertTrue(wallB.isClipping(ball3) && wallB.isClipping(ball4));
        wallB.connectBoard("barthen");
        wallL.connectBoard("brew");
        assertTrue(wallL.isClipping(ball2));
        assertFalse(wallL.isClipping(ball1));
        assertFalse(wallB.isClipping(ball3));
        assertFalse(wallB.isClipping(ball4));
    }
}
