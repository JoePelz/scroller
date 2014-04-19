/** Joe Pelz, Set A, A00893517 */
package core;

import java.awt.Rectangle;

/**
 * <p>This interface enforces an object meets all the requirements 
 * for physics simulations.</p>
 * @author Joe Pelz, Set A, A00893517
 * @version 1.0
 */
public interface Dynamic {
    /**
     * Apply forces to the object's velocity.
     * @param seconds delta time (amount of time that has elapsed)
     */
    void applyForces(double seconds);
    /**
     * Add a force to the object.
     * @param force The force to push the object with. (400 is reasonable)
     */
    void addForce(Vector2D force);
    /**
     * Get the object's velocity.
     * @return A vector of the object's velocity.
     */
    Vector2D getVel();
    /**
     * Set the object's velocity.
     * @param vel The replacement velocity for this object.
     */
    void setVel(Vector2D vel);
    /**
     * Apply drag to the object's velocity.
     * @param seconds delta time (amount of time that has elapsed)
     */
    void applyDrag(double seconds);
    /**
     * Get the collision box the given object.
     * @return the object's collision rectangle
     */
    Rectangle getCollisionBox();
    /**
     * Directly move the object by an amount.
     * @param x How much to move in the x direction.
     * @param y How much to move in the y direction.
     */
    void move(int x, int y);
}
