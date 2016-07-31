package sim;

import java.util.HashSet;
import java.util.Set;

/**
 * abstract class for objects exhibiting triggering behavior
 */
public abstract class Triggerable extends Collidable {
    
    private double triggerTimer = 10;
    
    private Set<Triggerable> triggers;
    //the set of objects which are tied to this object's trigger
    public final int x, y;
    //these represent the origin of the gadget on the board
    public final int w, h;
    //rep invariant -- w and h are both > 0
    private final char pictoral;
    //this is the character representation of the gadget in the text-based
    //animation
    private final String name;
    //this is the identifying name of the gadget
    
    /**
     * create a triggerable object without specifying its name or character
     * identifier
     * 
     * @param x the x position
     * @param y the y position
     * @param w the width
     * @param h the height
     */
    public Triggerable(int x, int y, int w, int h){
        this(x,y,w,h,'X',"UNSPECIFIED");
    }
    
    /**
     * create a 1x1 triggerable object at position x,y. it will be represented
     * by the char 'pic' in the text-based pingball animation
     * 
     * @param x the x position
     * @param y the y position
     * @param pic the character image
     * @param name the name of the gadget
     */
    public Triggerable(int x, int y, char pic, String name){
        this(x,y,1,1,pic, name);
    }
    
    /**
     * create a triggerable object at position x,y with specified width and height.
     * it will be represented by the char 'pic' in the text-based pingball animation
     * 
     * @param x the x position
     * @param y the y position
     * @param w the width
     * @param h the height
     * @param pic the character image
     * @param name the name of the gadget
     */
    public Triggerable(int x, int y, int w, int h, char pic, String name){
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.pictoral = pic;
        this.name = name;
        this.triggers = new HashSet<Triggerable>();
    }
    
    @Override public void displace(double deltaT){
        triggerTimer += deltaT;
    }
    
    public void reset(){
        triggerTimer = 10;
    }
    
    /**
     * perform the triggerable action of this object
     */
    //override this
    public abstract void triggerAction();
    
    //common trigger-related functions
    /**
     * connect another gadget on this gadget's trigger. any time
     * that this gadget collides with a ball, the specified gadget
     * will perform its triggerable action
     * 
     * @param drain the gadget to be connected
     */
    //if triangle triggers absorber, do triangle.addTrigger(absorber)
    public void addTrigger(Triggerable toAdd){
        triggers.add(toAdd);
    }
    
    /**
     * disconnect another gadget from this gadget's trigger. the
     * disconnected gadget will no longer have its action triggered
     * by this gadget
     * 
     * @param drain the gadget to be disconnected
     */
    public void removeTrigger(Triggerable toRemove){
        triggers.remove(toRemove);
    }
    
    /**
     * @return the number of gadgets whose actions are triggered by this object
     */
    public int numberOfTriggerLinks(){
        return triggers.size();
    }
    
    /**
     * trigger the actions of any objects tied to this object's trigger
     */
    public void becomeTriggered(){
        triggerTimer = 0;
        for(Triggerable gadget : triggers){
            gadget.triggerAction();
        }
    }
    
    public double triggerTimer(){
        return triggerTimer;
    }

    /**
     * determine whether a particular point on the board is occupied by this
     * object
     * 
     * @param x the x position to be checked
     * @param y the y position to be checked
     * @return true iff this object is located at positon (x,y) of the board
     */
    public boolean isLocatedAtPosition(int x, int y){
        return (x >= this.x && x < this.x + this.w) && (y >= this.y && y < this.y + this.h);
    }
    
    //classes representing objects that occupy more than one grid square
    //should override this method
    @Override
    public void draw(char[][] grid){
        if((x < 0 || y < 0) || (y+1 >= grid.length || x+1 >= grid[0].length)){
            return;
        }
        grid[y+1][x+1] = pictoral;
    }
    
    /**
     * @return the name of this gadget
     */
    public String name(){
        return name;
    }
    
    /**
     * @return a string listing the other gadgets tied to this
     *          gadget's trigger
     */
    public String triggerListString(){
        return triggers.toString();
    }

}
