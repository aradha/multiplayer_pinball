package graphics;

import java.awt.Color;
import java.awt.Graphics2D;

import client.Board;
import physics.Vect;

/**
 * WallGraphic class
 * 
 * for drawing the boundaries of the board
 */
public class WallGraphic extends CollidableGraphic {
    
    private final int x1,y1,x2,y2;
    //these are the end points of the line
    private final int dx,dy;
    //these are the spacing increment for drawing the wall's link name
    private final int nameX, nameY;
    //these are the offset parameter (from the wall edge) for drawing the
    //wall's link name
    private final char[] name;
    //this is a character array of length [the wall's length] which corresponds
    //to the way the wall's link name should appear on the graphic. period characters
    //in the array are not drawn
    private final Color color;
    
    /**
     * create a new WallGraphic based on the provided dimensions and parameters
     * 
     * @param x1 the x position of the first end point
     * @param y1 the y position of the first end point
     * @param x2 the x position of the second end point
     * @param y2 the y position of the second end point
     * @param charArray the array containing the wall's displayed name
     *          walls with no name to be displayed can pass an array of '.' or ' '
     *          characters
     * @param facing the facing vector of the wall (indicates the direction in
     *          which the name will be offset from the wall)
     * @param color the desired color
     */
    public WallGraphic(double x1, double y1, double x2, double y2, 
            char[] charArray, Vect facing, Color color){
        //first transcribe the end points
        this.x1 = convertToPixels(x1,ORIG_X);
        this.y1 = convertToPixels(y1,ORIG_Y);
        this.x2 = convertToPixels(x2,ORIG_X);
        this.y2 = convertToPixels(y2,ORIG_Y);
        
        //the name offset can be derived from the facing vector
        //because the names are drawn via a baseline, the offset is different
        //for x and y
        this.nameX = convertToPixels(facing.x(),0)/2 - PIXELS_PER_UNIT/4;
        this.nameY = convertToPixels(facing.y(),0)/2 + PIXELS_PER_UNIT/4;
        
        //this is the spacing increment for individual characters
        this.dx = Math.abs(this.x2 - this.x1)/Board.DEFAULT_SIZE;
        this.dy = Math.abs(this.y2 - this.y1)/Board.DEFAULT_SIZE;
        
        //then we can just make a copy of the passed character array
        this.name = new char[charArray.length];
        for(int ii = 0; ii < charArray.length; ii ++){
            name[ii] = charArray[ii];
        }
        this.color = color;
    }

    @Override public void draw(Graphics2D g) {
        g.setColor(this.color);
        g.drawLine(x1, y1, x2, y2);
        int charX,charY;
        charX = Math.min(x1, x2) + nameX; charY = Math.min(y1, y2) + nameY;
        for(int ii = 0; ii < name.length; ii ++){
            if(name[ii] != '.'){
                g.drawChars(name, ii, 1, charX, charY);
            }
            charX += dx;
            charY += dy;
        }
    }

}
