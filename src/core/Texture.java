/** Joe Pelz, Set A, A00893517 */
package core;

/**
 * <p>This class tracks all the textures in use by the program.</p>
 * @author Joe Pelz, Set A, A00893517
 * @version 1.0
 */
public enum Texture {
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
