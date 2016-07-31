package graphics;

import java.awt.Color;
import java.awt.Graphics2D;

/**
 * RectangleGraphic
 * 
 * for drawing square and rectangular Board objects
 */
public class RectangleGraphic extends CollidableGraphic {
    
    private final int x,y,w,h;
    //x and y are the origin of the rectangle
    //w and h are the width and height dimensions
    private final Color color;
    
    /**
     * create a new RectangleGraphic at position x,y with width w and height h
     * 
     * @param x the x origin
     * @param y the y origin
     * @param w the width
     * @param h the height
     * @param color the disired color
     */
    public RectangleGraphic(int x, int y, int w, int h, Color color){
        this.x = convertToPixels(x,ORIG_X);
        this.y = convertToPixels(y,ORIG_Y);
        this.w = convertToPixels(w,0);
        this.h = convertToPixels(h,0);
        this.color = color;
    }

    @Override
    public void draw(Graphics2D g) {
        g.setColor(color);
        g.fillRect(x,y,w,h);
    }

}
