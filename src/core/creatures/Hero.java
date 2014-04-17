/** Joe Pelz, Set A, A00893517 */
package core.creatures;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;

import core.Entity;
import core.Dynamic;
import core.Vector2D;



/**
 * <p>Everyone's favorite green box, our hero.</p>
 * @author Joe Pelz, Set A, A00893517
 * @version 1.0
 */
public class Hero extends Entity implements Dynamic {
    /** How much drag to apply to the hero's motion. */
    private static final double DRAG_FACTOR = 5;
    /** How strong vertical drag is relative to horizontal. */
    private static final double DRAG_FACTOR_Y = 0.2;
    /** How much smaller the collision box is than the graphic. */
    private static final double CBOX_SCALE = 0.65;
    /** The hero's current velocity. */
    private Vector2D vel;
    /** The forces currently acting on the hero. */
    private Vector2D force;
    /** True if hero is on the ground. */
    private boolean onGround;

    /**
     * Hero constructor, default with 0 velocity and force.
     */
    public Hero() {
        super();
        vel = new Vector2D(0, 0);
        force = new Vector2D(0, 0);
    }
    
    @Override
    public void applyForces(double seconds) {
        vel.x += force.x * seconds;
        vel.y += force.y * seconds;
        force.set(0, 0);
    }

    @Override
    public void addForce(Vector2D f) {
        force.offset(f);
    }

    @Override
    public Vector2D getVel() {
        return vel;
    }

    @Override
    public void setVel(Vector2D v) {
        vel = new Vector2D(v);
    }
    
    @Override
    public void applyDrag(double seconds) {
        double factorX = 1 - DRAG_FACTOR * seconds;
        double factorY = 1 - (DRAG_FACTOR * DRAG_FACTOR_Y) * seconds;
        vel = new Vector2D(vel.x * factorX, vel.y * factorY);
    }
    
    @Override
    /**
     * Get the width and height of the entity.
     * @return A new dimension object holding the width and height.
     */
    public Dimension getSize() {
        Dimension dim = super.getSize();
        Dimension result = new Dimension((int) (dim.height * CBOX_SCALE),
                                         (int) (dim.width  * CBOX_SCALE));
        return result;
    }
    
    @Override
    public void draw(Component comp, Graphics page, int offsetX, int offsetY) {
        // Get the actual size, not the shrunken (overridden) size.
        Dimension dim = super.getSize();
        
        //If the collision is scaled down, we want 
        //  the image drawn centered on the collision box.
        //This offsets the drawing of the image  
        //  to be centered on the collision box.
        double tweakX = dim.width  * (1.0 - CBOX_SCALE) / 2.0;
        double tweakY = dim.height * (1.0 - CBOX_SCALE) / 2.0;
        
        super.draw(comp, 
                page, 
                offsetX + (int) tweakX, 
                offsetY + (int) tweakY);
    }
    
    /**
     * True if the hero is on the ground.
     * @return the onGround
     */
    public boolean isOnGround() {
        return onGround;
    }

    /**
     * Whether or not the hero is on the ground. 
     * @param onGround the onGround to set
     */
    public void setOnGround(boolean onGround) {
        this.onGround = onGround;
    }
    
    @Override
    public Rectangle getBounds() {
        Rectangle bounds = super.getBounds();
//        bounds.x -= (bounds.width  * (1 - CBOX_SCALE) / 2);
//        bounds.y -= (bounds.height * (1 - CBOX_SCALE) / 2);
//        bounds.width  *= CBOX_SCALE;
//        bounds.height *= CBOX_SCALE;
        
        return bounds;
    }
}
