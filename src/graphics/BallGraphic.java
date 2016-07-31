package graphics;

import java.awt.Color;
import java.awt.Graphics2D;

/**
 * graphical representation of a Ball object
 */
public class BallGraphic extends CollidableGraphic{
    
    private final int x,y,r;
    //pixel coordinates and dimensions
    private final Color color;
    //color to be used
    
    /**
     * create a new BallGraphic corresponding to the given double coordinate and
     * dimension values
     * 
     * @param x the double value x coordinate
     * @param y the double value y coordinate
     * @param r the double value radius
     * @param color the desired color for this graphic
     */
    public BallGraphic(double x, double y, double r, Color color){
        this.x = convertToPixels(x, ORIG_X);
        this.y = convertToPixels(y, ORIG_Y);
        this.r = convertToPixels(r,0);
        this.color = color;
    }
    
    /**
     * draw a Ball using the specified Graphics2D object
     */
    public void draw(Graphics2D g){
        g.setColor(this.color);
        g.fillOval(x-r,y-r,2*r,2*r);
    }
    
}
