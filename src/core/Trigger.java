/** Joe Pelz, Set A, A00893517 */
package core;

import java.awt.Rectangle;

/**
 * <p>Interface for things that can be triggered (usually by player contact.)</p>
 * @author Joe Pelz, Set A, A00893517
 * @version 1.0
 */
public interface Trigger {
    boolean isTriggered(Rectangle bounds);
    void triggerAction();
}
