package graphics;

import java.awt.Graphics2D;

/**
 * immutable data type for depicting Collidable objects via a Graphics2D
 * 
 * every Collidable object has a getGraphic() method that returns a CollidableGraphic
 */
public abstract class CollidableGraphic {
    
    public static final int PIXELS_PER_UNIT = 20; //Each square of size L maps to some number of pixels
    //scaling factor to convert 20x20 double coordinates to a pixel map
    public static final int ORIG_X = PIXELS_PER_UNIT;
    //the offset of the board space's x origin, on the JPanel
    public static final int ORIG_Y = PIXELS_PER_UNIT;
    //the offset of the board space's y origin, on the JPanel
    
    /**
     * draw this object with the provided Graphics2D object
     * 
     * @param g: an instance of a graphics 2D object 
     *           (In this program it is the board graphic in 
     *           which the object should draw itself)
     */
    public abstract void draw(Graphics2D g);
    
    /**
     * convert the given double value to pixels
     * 
     * @param loc the double coordinate value to be converted
     * @param offset the pixel offset to be applied to the result
     * @return the pixel ordinate corresponding to the provided double, + offset
     */
    public static int convertToPixels(double loc, int offset){
        return (int)(loc*PIXELS_PER_UNIT) + offset;
    }
    
}
