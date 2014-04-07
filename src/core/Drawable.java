/** Joe Pelz, Set A, A00893517 */
package core;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;

import javax.swing.ImageIcon;

/**
 * <p>Base class for drawable entities.  Stores position and image 
 * information and draws to the screen.</p>
 * @author Joe Pelz, Set A, A00893517
 * @version 1.0
 */
public class Drawable {
    /** The position of the entity. */
    private Point pos;
    /** The image that represents this entity. */
    private ImageIcon brush;
    
    /**
     * Constructor: initialized the entity at the origin, 
     * and sets the draw image to a brick.
     */
    public Drawable() {
        pos = new Point(0, 0);
    }
    
    /**
     * Sets the image to draw for this entity.
     * @param image The image to draw for this entity
     */
    public void setImage(ImageIcon image) {
        brush = image;
    }
    /**
     * Get the width and height of the entity.
     * @return A new dimension object holding the width and height.
     */
    public Dimension getSize() {
        return new Dimension(brush.getIconWidth(), brush.getIconHeight());
    }
    /**
     * Set an absolute position for the entity.
     * @param x X axis position (right)
     * @param y Y axis position (up)
     */
    public void setPos(int x, int y) {
        pos.x = x;
        pos.y = y;
    }
    /**
     * Set an absolute position for the entity.
     * @param p The point where this entity should reside.
     */
    public void setPos(Point p) {
        pos = new Point(p);
    }
    /**
     * Adjust the entity's current position.
     * @param x The adjustment in the X direction (right)
     * @param y The adjustment in the Y direction (up)
     */
    public void move(int x, int y) {
        pos.x += x;
        pos.y += y;
    }
    /**
     * Get the position of the object.
     * @return the object's position
     */
    public Point getPos() {
        return pos;
    }
    
    /**
     * Draw the entity to the screen, at the entity's current position, 
     * offset by the camera's position.  THe coordinates correspond 
     * to the bottom left of the image.
     * @param comp The component used as observer
     * @param page The graphics context
     * @param offsetX The camera's x position
     * @param offsetY The camera's y position
     */
    public void draw(Component comp, Graphics page, int offsetX, int offsetY) {
        
        //set x, y to the bottom left corner
        int x = 0;
        int y = comp.getHeight() - brush.getIconHeight();
        
        //move right and up by the x and y values.
        x += pos.x;
        y -= pos.y; //this should be a subtraction
        
        //offset by the camera
        x -= offsetX;
        y += offsetY;
        
        brush.paintIcon(comp, page, x, y);
    }
}
