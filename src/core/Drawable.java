/** Joe Pelz, Set A, A00893517 */
package core;

import java.awt.Rectangle;

/**
 * <p>Indicated that something can be drawn to screen.</p>
 * @author Joe Pelz, Set A, A00893517
 * @version 1.0
 */
public interface Drawable {
    /**
     * Get a 2D array of unaltered pixels (double[4]) 
     * for drawing to the screen.  This function 
     * should not do calculations, but merely return a handle
     * to a pre-calculated array.
     * @return The pixel array (double[width][height][channel])
     */
    double[][][] getPixels();
    /**
     * Get the abgr pixels of the entity. 
     * @return A byte array of {a,b,g,r,a,b,g,r...}. 
     */
    byte[] getData();
    /**
     * Get the bounds of the Drawable in pixel coordinates. 
     * x is the left edge,
     * y is the BOTTOM edge.
     *   
     * @return A Rectangle storing the drawable's bounding box.
     */
    Rectangle getBounds();
    /**
     * Get a short that represents how to combine this drawable array. 
     * Add, or Merge so far.
     * TODO: replace with enum. 
     * @return A draw type code.
     */
    short getDrawType();
    
    /**
     * Check if the entity should be drawn to screen.
     * @return true if the entity should be drawn.
     */
    boolean isDrawn();
}
