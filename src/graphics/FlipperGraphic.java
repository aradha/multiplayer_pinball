package graphics;

import java.awt.Color;
import java.awt.Graphics2D;

/**
 * FlipperGraphic class
 * 
 * for animating the shape and rotation of Flipper objects
 */
public class FlipperGraphic extends CollidableGraphic{
    private final int x1, y1, x2, y2, r;
    //(x1,y1) and (x2,y2) are the center points of the two circles that define the
    //flipper's end points.  r is the radius of both circles
    private int dx, dy;
    //these are displacement parameters used to compute the edge points for the flipper's
    //length
    private final Color color;
    
    /**
     * create a FlipperGraphic based on the provided dimensions
     * 
     * @param cx1 x position of circle 1
     * @param cy1 y position of circle 1
     * @param cx2 x position of circle 2
     * @param cy2 y position of circle 2
     * @param r the radius of the circles
     * @param sin the sin() of the Flipper's angle
     * @param cos the cos() of the Flipper's angle
     * @param color the desired color
     */
    public FlipperGraphic( double cx1, double cy1, double cx2, double cy2, 
                           double r, double sin, double cos, Color color){
        
        dx = convertToPixels(sin*r,0);
        dy = convertToPixels(cos*r,0);
        
        this.x1 = convertToPixels(cx1, ORIG_X);
        this.y1 = convertToPixels(cy1, ORIG_Y);
        
        this.x2 = convertToPixels(cx2, ORIG_X);
        this.y2 = convertToPixels(cy2, ORIG_Y);
        
        this.r = convertToPixels(r, 0);

        this.color = color;
    }
    
    @Override public void draw(Graphics2D g) {
        g.setColor(this.color);
        g.fillOval(x1-r, y1-r, 2*r, 2*r);
        g.fillOval(x2-r, y2-r, 2*r, 2*r);
        int[] x = {
                x1 + dx, x2 + dx, x2-dx, x1-dx
        };
        int[] y = {
                y1-dy, y2-dy, y2 + dy, y1 + dy
        };
        g.fillPolygon(x, y, 4);
    }


}
