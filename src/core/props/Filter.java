package core.props;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;

/**
 * <p>This indicates the implementing object 
 * can influence the drawing of the world around it.</p>
 * @author Joe Pelz, Set A, A00893517
 * @version 1.0
 */
public interface Filter {
    /**
     * Takes the source image, does something to it, and 
     * pastes the results into the destination image.
     * @param source The buffer to draw from.
     * @param destination The buffer to draw to.
     */
    void filter(BufferedImage source, BufferedImage destination);
    
    boolean isInRange(Rectangle r);
    
    boolean isActive();
}
