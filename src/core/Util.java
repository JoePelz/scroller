/** Joe Pelz, Set A, A00893517 */
package core;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

/**
 * <p></p>
 * @author Joe Pelz, Set A, A00893517
 * @version 1.0
 */
public class Util {
    public static final byte A = 0;
    public static final byte B = 1;
    public static final byte G = 2;
    public static final byte R = 3;
    public static final byte CHANNELS = 4;

    /**
     * <p>Originally from <a href="http://stackoverflow.com/questions/6524196/
     * java-get-pixel-array-from-image">Stack Overflow</a>, this method 
     * converts an image buffer to a pixel array.</p>
     * <p>The original image has 0,0 in the top left, 
     * but the output array has 0,0 in the bottom left.</p> 
     * @param image The image to convert to pixels
     * @return An array of pixels [width][height][r/g/b/a]
     */
    public static double[][][] imageToPixels(BufferedImage image) {

        final byte[] pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        final int width = image.getWidth();
        final int height = image.getHeight();
        final boolean hasAlphaChannel = image.getAlphaRaster() != null;

        double[][][] result = new double[height][width][CHANNELS];
        if (hasAlphaChannel) {
            final int pixelLength = 4;
            for (int pixel = 0, row = height - 1, col = 0; 
                    pixel < pixels.length; 
                    pixel += pixelLength) {
                //must convert from signed byte (-128:127) to unsigned byte, 
                //therefore add 256 to a negative value.
                result[col][row][A] = ((pixels[pixel    ] >= 0) ? pixels[pixel    ] : pixels[pixel    ] + 256) / 255.0; // alpha
                result[col][row][B] = ((pixels[pixel + 1] >= 0) ? pixels[pixel + 1] : pixels[pixel + 1] + 256) / 255.0; // blue
                result[col][row][G] = ((pixels[pixel + 2] >= 0) ? pixels[pixel + 2] : pixels[pixel + 2] + 256) / 255.0; // green
                result[col][row][R] = ((pixels[pixel + 3] >= 0) ? pixels[pixel + 3] : pixels[pixel + 3] + 256) / 255.0; // red
                col++;
                if (col == width) {
                    col = 0;
                    row--;
                }
            }
        } else {
            final int pixelLength = 3;
            for (int pixel = 0, row = height - 1, col = 0; pixel < pixels.length; pixel += pixelLength) {
                result[col][row][A] = 1.0; // alpha
                result[col][row][B] = (pixels[pixel] >= 0) ? pixels[pixel] : pixels[pixel] + 256; // blue
                result[col][row][G] = (pixels[pixel + 1] >= 0) ? pixels[pixel + 1] : pixels[pixel + 1] + 256; // green
                result[col][row][R] = (pixels[pixel + 2] >= 0) ? pixels[pixel + 2] : pixels[pixel + 2] + 256; // red
                col++;
                if (col == width) {
                    col = 0;
                    row--;
                }
            }
        }
//        System.out.println("at 0,0: A=" + result[0][0][A] + ", R=" + result[0][0][R] + ", G=" + result[0][0][G] + ", B=" + result[0][0][B]);
        return result;
    }
    
    public static BufferedImage toBufferedImage(Image img)
    {
        if (img instanceof BufferedImage)
        {
            return (BufferedImage) img;
        }

        // Create a buffered image with transparency
        BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_4BYTE_ABGR);

        // Draw the image on to the buffered image
        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();

        // Return the buffered image
        return bimage;
    }
}
