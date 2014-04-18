/** Joe Pelz, Set A, A00893517 */
package core;

import java.awt.Rectangle;

/**
 * <p>Interface for things that can be triggered 
 * (usually by player contact).</p>
 * @author Joe Pelz, Set A, A00893517
 * @version 1.0
 */
public interface Trigger {
    /**
     * Test if the trigger should perform it's action.
     * @param bounds the bounding box of whatever might trigger this action.
     * @return true if the action should trigger
     */
    boolean isTriggered(Rectangle bounds);
    /**
     * The action performed when a particular trigger is activated.
     */
    void triggerAction();
}
