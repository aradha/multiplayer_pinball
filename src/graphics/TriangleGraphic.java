package graphics;

import java.awt.Color;
import java.awt.Graphics2D;

/**
 * TriangleGraphic class
 * 
 * for drawing pointy triangles
 */
public class TriangleGraphic extends CollidableGraphic{

    private final int x1,y1, x2,y2, x3,y3;
    //the three vertices of the triangle
    private final Color color;
    
    /**
     * create a new TriangleGraphic based on the given vertices
     * 
     * @param x1 the x position of the first vertex
     * @param y1 the y position of the first vertex
     * @param x2 the x position of the second vertex
     * @param y2 the y position of the second vertex
     * @param x3 the x position of the third vertex
     * @param y3 the y position of the third vertex
     * @param color the desired color
     */
    public TriangleGraphic(double x1, double y1,
                           double x2, double y2,
                           double x3, double y3,
                           Color color){

        this.x1 = convertToPixels(x1, ORIG_X);
        this.y1 = convertToPixels(y1, ORIG_Y);
        
        this.x2 = convertToPixels(x2, ORIG_X);
        this.y2 = convertToPixels(y2, ORIG_Y);

        this.x3 = convertToPixels(x3, ORIG_X);
        this.y3 = convertToPixels(y3, ORIG_Y);
        
        this.color = color;
    }
    
    @Override public void draw(Graphics2D g) {
        g.setColor(this.color);
        int [] x = {x1, x2, x3};
        int [] y = {y1, y2, y3};
        g.fillPolygon(x, y, 3);
    }

}
