/** Joe Pelz, Set A, A00893517 */
package core;

import javax.swing.ImageIcon;

/**
 * <p>This class tracks all the textures in use by the program.</p>
 * @author Joe Pelz, Set A, A00893517
 * @version 1.0
 */
public class TexturePack {
    /** The path images are stored in. */
    public static final String IMAGE_PATH = "/images/";
    /** The image to use for background tiles. */
    private ImageIcon bg;
    /** A light in the background. */
    private ImageIcon bgLight;
    /** A broken light in the background. */
    private ImageIcon bgLightDead;
    /** The image to use for the collision blocks. */
    private ImageIcon wall;
    /** The image to use for our hero. */
    private ImageIcon hero;
    /** Alternative image to use for the hero. */
    private ImageIcon heroWhite;
    
    /** Spark texture. */
    private ImageIcon spark;
    
    /**
     * Construct a new texture pack using hardcoded default images.
     */
    public TexturePack() {
        bg = new ImageIcon(
                getClass().getResource("/images/bg.png"));
        bgLight = new ImageIcon(
                getClass().getResource("/images/bgLight.png"));
        bgLightDead = new ImageIcon(
                getClass().getResource("/images/bgLightDead.png"));
        wall = new ImageIcon(
                getClass().getResource("/images/brick.png"));
        //      hero = new ImageIcon(
        //      getClass().getResource("/images/hero.png"));
        //      heroWhite = new ImageIcon(
        //      getClass().getResource("/images/heroNoise.png"));
        hero = new ImageIcon(
                getClass().getResource("/images/heroShort.png"));
        heroWhite = new ImageIcon(
                getClass().getResource("/images/heroNoiseShort.png"));
        
        spark = new ImageIcon(
                getClass().getResource("/images/spark.png"));
    }

    /**
     * Get the texture to use for background tiles. 
     * @return The bg tile texture.
     */
    public ImageIcon getBG() {
        return bg;
    }

    /**
     * Get the texture to use for background variation. 
     * @return The bg light tile texture.
     */
    public ImageIcon getBGLight() {
        return bgLight;
    }
    /**
     * Get the texture to use for background variation. 
     * @return The bg light tile texture.
     */
    public ImageIcon getBGLightDead() {
        return bgLightDead;
    }

    /**
     * Get the texture to use for solid brick. 
     * @return The solid brick texture.
     */
    public ImageIcon getWall() {
        return wall;
    }

    /**
     * Get the texture to use for the hero. 
     * @return The hero texture.
     */    
    public ImageIcon getHero() {
        return hero;
    }

    /**
     * Get an alternate hero texture. 
     * @return The alt hero texture.
     */
    public ImageIcon getHeroWhite() {
        return heroWhite;
    }

    /**
     * Get the Spark image. 
     * @return The spark texture.
     */
    public ImageIcon getSpark() {
        return spark;
    }
}
