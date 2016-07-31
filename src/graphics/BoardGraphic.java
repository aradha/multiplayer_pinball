package graphics;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.LinkedList;
import java.util.List;

import client.Board;
import physics.Vect;
import sim.Ball;
import sim.Triggerable;
import sim.Wall;

/**
 * this is the CollidableGraphic that represents an entire
 * board, which is the cumulative object used for animation
 */
public class BoardGraphic extends CollidableGraphic {
    
    public static final int WIDTH = 20*PIXELS_PER_UNIT;
    public static final int HEIGHT = 20*PIXELS_PER_UNIT;
    
    private Vect vortex;
    
    private List<CollidableGraphic> balls;
    private List<CollidableGraphic> walls;
    private List<CollidableGraphic> gadgets;
    //the reason that I made three lists here is just so that, if desired,
    //the BoardGraphic can designate different colors for each class of objects
    //though this behavior has been delegated to lower level objects
    
    //this still serves a purpose though, because it allows the board to specify,
    //in a sense, the order in which things are drawn
    
    /**
     * construct a BoardGraphic containing a CollidableGraphic for every
     * specified Collidable object
     * 
     * @param balls balls to be included
     * @param walls walls to be included
     * @param gadgets Triggerable objects to be included
     */
    public BoardGraphic(List<Ball> balls, List<Wall> walls, List<Triggerable> gadgets){
        this.balls = new LinkedList<CollidableGraphic>();
        this.walls = new LinkedList<CollidableGraphic>();
        this.gadgets = new LinkedList<CollidableGraphic>();
        for(Ball ball : balls){
            this.balls.add(ball.getGraphic());
        }
        for(Wall wall : walls){
            this.walls.add(wall.getGraphic());
        }
        for(Triggerable gadget: gadgets){
            this.gadgets.add(gadget.getGraphic());
        }
    }
    
    /**
     * construct a BoardGraphic containing a CollidableGraphic for every
     * specified Collidable object, and also draws a representation of a
     * gravity vortex at the specified position
     * 
     * @param balls balls to be included
     * @param walls walls to be included
     * @param gadgets Triggerable objects to be included
     * @param vortex the Vect position of the center of a gravity vortex
     */
    public BoardGraphic(List<Ball> balls, List<Wall> walls, List<Triggerable> gadgets, Vect vortex){
        this(balls,walls,gadgets);
        this.vortex = vortex;
    }
    
    /**
     * draw a graphical representation of each of the objects contained
     * by the BoardGraphic using the specified Graphics2D object
     */
    @Override public void draw(Graphics2D g){
        Font font = new Font(g.getFont().getFontName(),PIXELS_PER_UNIT/2,PIXELS_PER_UNIT/2);
        g.setFont(font);
        g.setColor(Color.BLACK);
        g.fillRect(ORIG_X,ORIG_Y,WIDTH,HEIGHT);
        
        if(vortex != null){
            float color = 1; color*=0.25;
            g.setColor(new Color(color,color,color));
            int radius = convertToPixels(Board.DEFAULT_DISTORTION_RADIUS,0);
            int x = convertToPixels(vortex.x(),ORIG_X);
            int y = convertToPixels(vortex.y(),ORIG_Y);
            g.drawOval(x-radius, y-radius, 2*radius, 2*radius);
        }
        for(CollidableGraphic ball : balls){
            ball.draw(g);
        }
        for(CollidableGraphic wall : walls){
            wall.draw(g);
        }
        for(CollidableGraphic gadget : gadgets){
            gadget.draw(g);
        }
    }
}
