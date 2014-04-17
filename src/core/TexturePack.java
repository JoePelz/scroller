/** Joe Pelz, Set A, A00893517 */
package core;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.net.URL;

import javax.swing.ImageIcon;

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
    /** Stores the pixels of each image. */
    private double[][][][] pixels = new double[Texture.values().length][0][0][0];
    
    /**
     * Constructor, that requires the user to supply a path to the images.
     * @param basePath The relative (to class) path the images 
     * are stored in (e.g. "/images/")
     */
    public TexturePack(String basePath) {
        String path;
        URL res;
        Toolkit defToolkit = Toolkit.getDefaultToolkit();
        BufferedImage swap;
        for (int i = 0; i < Texture.values().length; i++) {
            path = basePath + Texture.values()[i].getName();
            /* Don't ask me why this next line is important. Just trust me.
             * It has something to do with image observers, and without it
             * a call to image.getWidth(null) fails. 
             */
            images[i] = new ImageIcon(getClass().getResource(path));
            res = getClass().getResource(path);
            imgs[i] = defToolkit.getImage(res);
            swap = Util.toBufferedImage(imgs[i]);
            pixels[i] = new double[imgs[i].getWidth(null)][imgs[i].getHeight(null)][Util.CHANNELS];
            Util.imageToPixels(swap, pixels[i]);
        }
    }

    /**
     * Convert a Texture enum into a Image.
     * @param t The Texture to source
     * @return The Image to draw.
     */
    public Image get(Texture t) {
        return imgs[t.ordinal()];
    }
    
    /**
     * Convert a Texture enum into a pixel array.
     * @param t The Texture to source
     * @return The pixel array to draw.
     */
    public double[][][] getP(Texture t) {
        return pixels[t.ordinal()];
    }
}
