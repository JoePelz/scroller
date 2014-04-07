/** Joe Pelz, Set A, A00893517 */
package core;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;

/**
 * <p>Draws a burst of 8 sparks to the screen on creation. </p>
 * @author Joe Pelz, Set A, A00893517
 * @version 1.0
 */
public class Burst extends Drawable {
    /** to make a circle instead of a square. */
    private static final double DIAG_FACTOR = 0.707;
    /** Nanoseconds per second. */
    private static final double NS_PER_SEC = 1.0E9;
    /** The max size of the spark field. */
    private static final int MAX_SIZE = 50;
    /** How fast for the sparks to move. */
    private static final int SPEED = 120;
    
    /** How large the spark currently is. */
    private int radius;
    /** The timestamp when the spark began. */
    private long birth;
    /** Whether or not to delete the particle next pass. */
    private boolean expired;
    
    /**
     * Constructor to generate sparks and set the texture.
     * @param tp The texture pack to pull from
     */
    public Burst(TexturePack tp) {
        super();
        super.setImage(tp.getSpark());
        birth = System.nanoTime();
    }
    
    
    @Override
    /**
     * Draw the entity to the screen, at the entity's current position, 
     * offset by the camera's position.  8 sparks are drawn at the 
     * radius' distance from the center.  The coordinates correspond 
     * to the center of the spark burst.
     * 
     * @param comp The component used as observer
     * @param page The graphics context
     * @param offsetX The camera's x position
     * @param offsetY The camera's y position
     */
    public void draw(Component comp, Graphics page, int offsetX, int offsetY) {
        if (expired) {
            return;
        }
        radius = (int) ((System.nanoTime() - birth) / NS_PER_SEC * SPEED);
        
        //Draw from the center, instead of the bottom left
        Dimension dim = getSize();
        offsetX += dim.width / 2;
        offsetY += dim.height / 2;

        if (radius >= MAX_SIZE) {
            expired = true;
        }
        super.draw(comp, page, 
                offsetX + (int) (radius * DIAG_FACTOR), 
                offsetY - (int) (radius * DIAG_FACTOR));
        super.draw(comp, page, 
                offsetX + radius, 
                offsetY);
        super.draw(comp, page, 
                offsetX + (int) (radius * DIAG_FACTOR), 
                offsetY + (int) (radius * DIAG_FACTOR));
        super.draw(comp, page, 
                offsetX, 
                offsetY + radius);
        super.draw(comp, page, 
                offsetX - (int) (radius * DIAG_FACTOR), 
                offsetY + (int) (radius * DIAG_FACTOR));
        super.draw(comp, page, 
                offsetX - radius, 
                offsetY);
        super.draw(comp, page, 
                offsetX - (int) (radius * DIAG_FACTOR), 
                offsetY - (int) (radius * DIAG_FACTOR));
        super.draw(comp, page, 
                offsetX, 
                offsetY - radius);
    }
    
    /**
     * Check if the particle is dead, to queue for deletion.
     * @return True if the effect can be deleted. 
     */
    public boolean isDead() {
        return expired;
    }
}
