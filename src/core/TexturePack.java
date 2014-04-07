/** Joe Pelz, Set A, A00893517 */
package core;

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
    hero("heroShort.png"),
    /** Alternative image to use for the hero. */
    heroNoise("heroNoiseShort.png"),
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
    /** This is the actual folder to look in for images. */
    private final String basePath;
    
    /**
     * Constructor, that requires the user to supply a path to the images.
     * @param path The relative (to class) path the images 
     * are stored in (e.g. "/images/")
     */
    public TexturePack(String path) {
        basePath = path;
    }
    
    /**
     * Convert a Texture enum into a ImageIcon.
     * @param t The Texture to source
     * @return The ImageIcon to draw.
     */
    public ImageIcon get(Texture t) {
        String path = basePath + t.getName();
        return new ImageIcon(getClass().getResource(path));
    }
}
