/** Joe Pelz, Set A, A00893517 */
package core;

import java.awt.Image;
import java.awt.Toolkit;
import java.net.URL;

import javax.swing.ImageIcon;

/**
 * <p>This class tracks all the textures in use by the program.</p>
 * @author Joe Pelz, Set A, A00893517
 * @version 1.0
 */
enum Texture {
    /** The image to use for background tiles. */
    bg("bg.png"),
    /** A light in the background. */
    bgLight("bgLight.png"),
    /** A broken light in the background. */
    bgLightDead("bgLightDead.png"),
    /** The image to use for the collision blocks. */
    brick("brick.png"),
    /** The image to use for our hero. */
    hero("fuzzy.png"),
    /** Alternative image to use for the hero. */
    heroGround("fuzzyFlat.png"),
    /** Spark texture. */
    spark("spark.png");
    
    /** The path to the file for that texture. */
    private final String path;
    
    /**
     * Initializer to save the image path.
     * @param path The path to the image from the /src/ directory
     */
    private Texture(String path) {
        this.path = path;
    }
    
    /**
     * Get the file name for the current brick.
     * @return The filename for the texture.
     */
    public String getName() {
        return path;
    }
}

/**
 * <p>This holds the base path to look in for images. 
 * It starts in the src directory.</p>
 * @author Joe Pelz, Set A, A00893517
 * @version 1.0
 */
public class TexturePack {
    /** Stores the actual images. */
    private ImageIcon[] images = new ImageIcon[Texture.values().length];
    /** Stores the actual images. */
    private Image[] imgs = new Image[Texture.values().length];
    
    /**
     * Constructor, that requires the user to supply a path to the images.
     * @param basePath The relative (to class) path the images 
     * are stored in (e.g. "/images/")
     */
    public TexturePack(String basePath) {
        String path;
        URL res;
        for (int i = 0; i < Texture.values().length; i++) {
            path = basePath + Texture.values()[i].getName();
            images[i] = new ImageIcon(getClass().getResource(path));
            res = getClass().getResource(path);
            imgs[i] = Toolkit.getDefaultToolkit().getImage(res);
        }
    }
    
    /**
     * Convert a Texture enum into a ImageIcon.
     * @param t The Texture to source
     * @return The ImageIcon to draw.
     */
    public Image get(Texture t) {
        return imgs[t.ordinal()];
    }
}
