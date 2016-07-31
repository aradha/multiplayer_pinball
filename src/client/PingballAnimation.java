package client;

import graphics.CollidableGraphic;

import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

/**
 * PingballAnimation class
 * 
 * Used for generating the graphical animation of the game and managing the
 * board state as the game is played
 *
 */
public class PingballAnimation extends JPanel{
    
    private static final long serialVersionUID = 1L;
    
    private CollidableGraphic viewModel;
    //the immutable graphical object that will be drawn when updateComponent
    //is called
    
    /**
     * generate a new PingballAnimation component with a blank board
     */
    public PingballAnimation(CollidableGraphic view){
        this.viewModel = view;
    }
    
    /**
     * repaint the animation with the new graphical model
     * 
     * @param view the graphics model for the next frame of animation
     */
    public void newFrame(CollidableGraphic view){
        this.viewModel = view;
        repaint();
    }
    
    /**
     * update the pingball animation on the gui
     */
    @Override public void paintComponent(Graphics g){
        Graphics2D g2D = (Graphics2D)g;
        g2D.fillRect(0, 0, getWidth(), getHeight());
        viewModel.draw(g2D);
    }
    
}
