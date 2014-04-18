/** Joe Pelz, Set A, A00893517 */
package core;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;

/**
 * <p>Holds useful static functions for manipulating images 
 * and pixel arrays.</p>
 * @author Joe Pelz, Set A, A00893517
 * @version 1.0
 */
public class Util {
    /** The index of the alpha channel. */
    public static final byte A = 0;
    /** The index of the blue channel. */
    public static final byte B = 1;
    /** The index of the green channel. */
    public static final byte G = 2;
    /** The index of the red channel. */
    public static final byte R = 3;
    /** The number of channels to include. */
    public static final byte CHANNELS = 4;
    /** The highest value to store in a byte. */
    public static final short BYTE = 256;

    /**
     * <p>Originally from <a href="http://stackoverflow.com/questions/6524196/
     * java-get-pixel-array-from-image">Stack Overflow</a>, this method 
     * converts an image buffer to a pixel array.</p>
     * <p>The original image has 0,0 in the top left, 
     * but the output array has 0,0 in the bottom left.</p> 
     * @param image The image to convert to pixels.
     * @param result The output arg array to put data into.
     */
    public static void imageToPixels(BufferedImage image, 
                                             double[][][] result) {
        final double maxByte = 255.0;
        final byte[] pixels = ((DataBufferByte) 
                               image.getRaster()
                               .getDataBuffer()).getData();
        final int width = image.getWidth();
        final int height = image.getHeight();
        final boolean hasAlphaChannel = image.getAlphaRaster() != null;
        int row = height - 1;
        int col = 0;

        if (hasAlphaChannel) {
            final int pixelLength = 4;
            for (int pixel = 0; 
                    pixel < pixels.length; 
                    pixel += pixelLength) {
                //must convert from signed byte (-128:127) to unsigned byte, 
                //therefore add 256 to a negative value.
                result[col][row][A] = ((pixels[pixel    ] >= 0) 
                        ? pixels[pixel    ] 
                        : pixels[pixel    ] + BYTE) / maxByte; // alpha
                result[col][row][B] = ((pixels[pixel + 1] >= 0) 
                        ? pixels[pixel + 1] 
                        : pixels[pixel + 1] + BYTE) / maxByte; // blue
                result[col][row][G] = ((pixels[pixel + 2] >= 0) 
                        ? pixels[pixel + 2] 
                        : pixels[pixel + 2] + BYTE) / maxByte; // green
                result[col][row][R] = ((pixels[pixel + 3] >= 0) 
                        ? pixels[pixel + 3] 
                        : pixels[pixel + 3] + BYTE) / maxByte; // red
                col++;
                if (col == width) {
                    col = 0;
                    row--;
                }
            }
        } else {
            final int pixelLength = 3;
            for (int pixel = 0; pixel < pixels.length; pixel += pixelLength) {
                result[col][row][A] = 1.0; // alpha
                result[col][row][B] = (pixels[pixel] >= 0) 
                        ? pixels[pixel] 
                        : pixels[pixel] + BYTE; // blue
                result[col][row][G] = (pixels[pixel + 1] >= 0) 
                        ? pixels[pixel + 1] 
                        : pixels[pixel + 1] + BYTE; // green
                result[col][row][R] = (pixels[pixel + 2] >= 0) 
                        ? pixels[pixel + 2] 
                        : pixels[pixel + 2] + BYTE; // red
                col++;
                if (col == width) {
                    col = 0;
                    row--;
                }
            }
        }
//        System.out.println("at 0,0: A=" + result[0][0][A] 
//                        + ", R=" + result[0][0][R] 
//                        + ", G=" + result[0][0][G] 
//                        + ", B=" + result[0][0][B]);
        //return result;
    }
    
    /**
     * Convert a pixel array to an image.
     * @param pixels The pixels to convert.
     * @param image The image to receive the converted pixels.
     */
    public static void pixelsToImage(double[][][] pixels, BufferedImage image) {
        WritableRaster r = image.getRaster();
        //TODO: write body...
        /* the double array is a 1-dimensional array that lists 
         * r, g, b, a, r, g, b, a, for each row (or column?) in sequence.
         */
        //r.setPixels(x, y, w, h, dArray);
        
        image.setData(r);
    }
    
    /**
     * Convert an Image into a BufferedImage, so that it's editable.
     * @param img the image to convert
     * @return the image as a BufferedImage
     */
    public static BufferedImage toBufferedImage(Image img) {
        if (img instanceof BufferedImage) {
            return (BufferedImage) img;
        }

        // Create a buffered image with transparency
        BufferedImage bimage = new BufferedImage(img.getWidth(null), 
                                                 img.getHeight(null), 
                                                 BufferedImage.TYPE_4BYTE_ABGR);

        // Draw the image on to the buffered image
        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();

        // Return the buffered image
        return bimage;
    }
}
