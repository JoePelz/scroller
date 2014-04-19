/** Joe Pelz, Set A, A00893517 */
package core;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
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
    /** The number of bits per channel in an integer color. */
    public static final byte BITS_PER_CHANNEL = 8;
    /** The size of a byte. */
    public static final short BYTE = 256;
    /** The highest value to store in a byte. */
    public static final short B_MAX = 255;

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
                result[col][row][A] = ((pixels[pixel + A] >= 0) 
                        ? pixels[pixel + A] 
                        : pixels[pixel + A] + BYTE) / maxByte; // alpha
                result[col][row][B] = ((pixels[pixel + B] >= 0) 
                        ? pixels[pixel + B] 
                        : pixels[pixel + B] + BYTE) / maxByte; // blue
                result[col][row][G] = ((pixels[pixel + G] >= 0) 
                        ? pixels[pixel + G] 
                        : pixels[pixel + G] + BYTE) / maxByte; // green
                result[col][row][R] = ((pixels[pixel + R] >= 0) 
                        ? pixels[pixel + R] 
                        : pixels[pixel + R] + BYTE) / maxByte; // red
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
     * Convert a pixel array to an image. Raises IllegalArgumentException
     * if the sizes don't match.
     * @param pixels The pixels to convert.
     * @param image The image to receive the converted pixels.
     */
    public static void pixelsToImage(double[][][] pixels, BufferedImage image) {
        if (image.getWidth() != pixels.length 
                || image.getHeight() != pixels[0].length) {
            throw new IllegalArgumentException("Image size "
                    + "does not match pixel array.");
        }
        //boolean hasAlpha = image.getAlphaRaster() != null;
        
        int y = pixels[0].length - 1; 
        int x = 0; 
        int width = pixels.length;
        int height = pixels[0].length;
        
        int[] dbPacked = new int[width * height];
        
        for (int i = 0; i < dbPacked.length; i++) {
            dbPacked[i] = toIntRGBA(pixels[x][y]);

            x++;
            if (x == width) {
                x = 0;
                y--;
            }
        }
        WritableRaster raster = Raster.createPackedRaster(DataBuffer.TYPE_INT, 
                pixels.length, 
                pixels[0].length, 
                CHANNELS, 
                BITS_PER_CHANNEL, 
                null);
        raster.setDataElements(0, 0, pixels.length, pixels[0].length, dbPacked);
        
        image.setData(raster);
    }
    
    /**
     * Convert a pixel array to an image. Raises IllegalArgumentException
     * if the sizes don't match.
     * @param pixels The pixels to convert.
     * @param image The image to receive the converted pixels.
     * @param region The region to update in the image.
     */
    public static void pixelsToImage(double[][][] pixels, 
                                     BufferedImage image, 
                                     Rectangle region) {
        if (image.getWidth() != pixels.length 
                || image.getHeight() != pixels[0].length) {
            throw new IllegalArgumentException("Image size "
                    + "does not match pixel array.");
        }
        //boolean hasAlpha = image.getAlphaRaster() != null;
        
        //Identify the boundary of the box (cropped to the image)
        int left   = Math.max(0, region.x);
        int bottom = Math.max(0, region.y);
        int right  = Math.min(image.getWidth(),  region.x + region.width);
        int top    = Math.min(image.getHeight(), region.y + region.height);
        
        //Set width, height and starting position.
        int width = right - left;
        int height = top - bottom;
        int x = left;
        int y = top - 1; 
        
        int[] dbPacked = new int[width * height];
        
        for (int i = 0; i < dbPacked.length; i++) {
            dbPacked[i] = toIntRGBA(pixels[x][y]);

            x++;
            if (x == right) { 
                x = left;
                y--;
            }
        }
        
        int rTop = image.getHeight() - top;
        WritableRaster r2 = Raster.createPackedRaster(
                DataBuffer.TYPE_INT, 
                width, 
                height, 
                CHANNELS, 
                BITS_PER_CHANNEL, 
                new Point(left, rTop));
        r2.setDataElements(left, rTop, width, height, dbPacked);
        
        image.setData(r2);
    }
    
    /**
     * Duplicate a pixel array (x,y,channel).
     * @param source The array to duplicate.
     * @param dest The array to copy into.
     */
    public static void copyPixelArray(double[][][] source, double[][][] dest) {
        for (int x = 0; x < source.length; x++) {
            for (int y = 0; y < source[0].length; y++) {
                System.arraycopy(
                        source[x][y], 0, 
                        dest[x][y], 0, 
                        source[0].length);
            }
        }
        
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

    /**
     * Convert a double[argb] to a single int color.
     * @param argb the double[argb] that is storing a color
     * @return A color-encoded integer.
     */
    public static int toIntRGBA(double[] argb) {
        
        int channel = (int) (argb[Util.A] * B_MAX);
        channel = Math.max(0, Math.min(channel, B_MAX));
        int color = channel << 0;
        
        channel = (int) (argb[Util.B] * B_MAX);
        channel = Math.max(0, Math.min(channel, B_MAX));
        color    += channel << (B * BITS_PER_CHANNEL);
        
        channel = (int) (argb[Util.G] * B_MAX);
        channel = Math.max(0, Math.min(channel, B_MAX));
        color    += channel << (G * BITS_PER_CHANNEL);
        
        channel = (int) (argb[Util.R] * B_MAX);
        channel = Math.max(0, Math.min(channel, B_MAX));
        color    += channel << (R * BITS_PER_CHANNEL);
        
        return color;
    }
}
