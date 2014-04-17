/** Joe Pelz, Set A, A00893517 */
package core;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import core.world.Level;

/**
 * <p></p>
 * @author Joe Pelz, Set A, A00893517
 * @version 1.0
 */
public class Renderer {
    private static final byte G_OFF = 8;
    private static final byte R_OFF = 16;
    private static final byte A_OFF = 24;
    
    private boolean stale = true;
    
    //store the background
//    private Level background;
    //store the level
    private Level world;
    //store the hero
//    private Hero hero;
    //store the creatures
    //private ArrayList<Creature> creatures;
    //store the props
    //private ArrayList<Prop> prop;
    //store the lights
    private ArrayList<Drawable> props = new ArrayList<Drawable>();
    //store the effects
//    private ArrayList<Effect> effects;
    
    /**all static layers mixed. 0,0 is bottom left. */
    private double[][][] precomp;
    /** final output image. 0,0 is top left. */
    private BufferedImage finComp;
    /**
     * Constructor for an empty renderer.
     */
    public Renderer() {
    }

    public void invalidate() {
        stale = true;
    }
    
    public void invalidate(Rectangle r) {
        updateComp(r);
        updateImage(r);
    }
    
    public void setWorld(Level w) {
        world = w;
        finComp = new BufferedImage(w.getBounds().width, w.getBounds().height, BufferedImage.TYPE_4BYTE_ABGR);
        precomp = new double[w.getBounds().width][w.getBounds().height][Util.CHANNELS];
        stale = true;
    }
    public void addProp(Drawable prop) {
        props.add(prop);
        stale = true;
    }

    private void updateComp() {
//        System.out.println("Updating everything.");
        double[][][] worldP = world.getPixels();
        //convert array from double[] to int
        for (int x = 0; x < worldP.length; x++) {
            for (int y = 0; y < worldP[0].length; y++) {
                for (int ch = 0; ch < Util.CHANNELS; ch++) {
                    precomp[x][y][ch] = worldP[x][y][ch];
                }
            }
        }

        for(Drawable d : props) {
//            System.out.println("adding light at " + ((Light) d).getPos());
            add(d.getPixels(), d.getBounds());
        }
        stale = false;
    }
    
    private void updateComp(Rectangle bounds) {
//        System.out.println("Updating region.");
        double[][][] worldP = world.getPixels();
        //convert array from double[] to int
        int left   = Math.max(0, bounds.x);
        int bottom = Math.max(0, bounds.y);
        int right  = Math.min(worldP.length,    bounds.x + bounds.width);
        int top    = Math.min(worldP[0].length, bounds.y + bounds.height);
        
        for (int x = left; x < right; x++) {
            for (int y = bottom; y < top; y++) {
                for (int ch = 0; ch < Util.CHANNELS; ch++) {
                    precomp[x][y][ch] = worldP[x][y][ch];
                }
            }
        }
        
        for(Drawable d : props) {
//            System.out.println("adding light at " + ((Light) d).getPos());
            add(d.getPixels(), d.getBounds(), bounds);
        }
        stale = false;
    }

    private void add(double[][][] pixels, Rectangle region) {
        Rectangle worldBounds = world.getBounds();
        int left   = Math.max(region.x, worldBounds.x);
        int bottom = Math.max(region.y, worldBounds.y);
        int right  = (Math.min(region.x + region.width,  worldBounds.x + worldBounds.width));
        int top    = (Math.min(region.y + region.height, worldBounds.y + worldBounds.height));
//        System.out.println("Bounds: Left=" + left + ", Right=" + right + ", Top=" + top + ", Bottom=" + bottom);
//        System.out.println("World Bounds: Left=" + worldBounds.x
//                + ", Right=" + (worldBounds.x + worldBounds.width)
//                + ", Top=" + (worldBounds.y + worldBounds.height)
//                + ", Bottom=" + worldBounds.y);
        for (int x = left; x < right; x++) {
            for (int y = bottom; y < top; y++) {
                precomp[x][y][Util.R] += pixels[x - region.x][y - region.y][Util.R];
                precomp[x][y][Util.G] += pixels[x - region.x][y - region.y][Util.G];
                precomp[x][y][Util.B] += pixels[x - region.x][y - region.y][Util.B];
            }
        }
    }
    private void add(double[][][] pixels, Rectangle bounds, Rectangle region) {
        Rectangle worldBounds = world.getBounds();
        int left   = Math.max(bounds.x, worldBounds.x);
        int bottom = Math.max(bounds.y, worldBounds.y);
        int right  = (Math.min(bounds.x + bounds.width,  worldBounds.x + worldBounds.width));
        int top    = (Math.min(bounds.y + bounds.height, worldBounds.y + worldBounds.height));

        //only update the relevant region
        left   = Math.max(left,   region.x);
        right  = Math.min(right,  region.x + region.width);
        bottom = Math.max(bottom, region.y);
        top    = Math.min(top,    region.y + region.height);
        
        for (int x = left; x < right; x++) {
            for (int y = bottom; y < top; y++) {
                precomp[x][y][Util.R] += pixels[x - bounds.x][y - bounds.y][Util.R];
                precomp[x][y][Util.G] += pixels[x - bounds.x][y - bounds.y][Util.G];
                precomp[x][y][Util.B] += pixels[x - bounds.x][y - bounds.y][Util.B];
            }
        }
    }
    
    /**
     * Convert a double[argb] to a single int color.
     * @param argb the double[argb] that is storing a color
     * @return A color-encoded integer.
     */
    private int toIntColor(double[] argb) {
        
        int channel = (int) (argb[Util.B] * 255);
        channel = Math.max(0, Math.min(channel, 255));
        int color = channel;
        
        channel = (int) (argb[Util.G] * 255);
        channel = Math.max(0, Math.min(channel, 255));
        color += channel << G_OFF;
        
        channel = (int) (argb[Util.R] * 255);
        channel = Math.max(0, Math.min(channel, 255));
        color += channel << R_OFF;
        
        channel = (int) (argb[Util.A] * 255);
        channel = Math.max(0, Math.min(channel, 255));
        color += channel << A_OFF;
        
        return color;
    }
    
    
    /**
     * Transfer the final colors from the array to the buffered image.  
     * This is the point where we switch coordinate systems.
     * Up until now 0,0 has been the bottom left, with +Y going up.
     * After this (in finComp) 0,0 will be the top left, with +Y going down.
     */
    private void updateImage() {
        for (int x = 0; x < precomp.length; x++) {
            for (int y = 0; y < precomp[0].length; y++) {
                finComp.setRGB(x, 
                        precomp[0].length - 1 - y, 
                        toIntColor(precomp[x][y]));
            }
        }
    }
    /**
     * Transfer the final colors from the array to the buffered image.  
     * This is the point where we switch coordinate systems.
     * Up until now 0,0 has been the bottom left, with +Y going up.
     * After this (in finComp) 0,0 will be the top left, with +Y going down.
     */
    private void updateImage(Rectangle region) {
        int left   = Math.max(0, region.x);
        int bottom = Math.max(0, region.y);
        int right  = Math.min(precomp.length,    region.x + region.width);
        int top    = Math.min(precomp[0].length, region.y + region.height);
        
        for (int x = left; x < right; x++) {
            for (int y = bottom; y < top; y++) {
                finComp.setRGB(x, 
                        precomp[0].length - 1 - y, 
                        toIntColor(precomp[x][y]));
            }
        }
    }
    
    /**
     * Actually draw the image to screen.
     * @param g The graphics buffer to draw into
     * @param comp The component for sizing and scaling purposes
     * @param offsetX The camera position, or world offset, in X
     * @param offsetY The camera position, or world offset, in Y
     */
    public void draw(Graphics g, Component comp, int offsetX, int offsetY) {
        if (stale) {
            updateComp();
            updateImage();
        }
        Rectangle bounds = world.getBounds();
        g.drawImage(finComp, 
                bounds.x - offsetX, 
                bounds.y - bounds.height + offsetY + comp.getHeight(),
                null);
    }
}
