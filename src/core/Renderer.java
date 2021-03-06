/** Joe Pelz, Set A, A00893517 */
package core;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.ArrayList;

import core.world.Level;

/**
 * <p>This class handles rendering and translates pixel arrays
 * into images.</p>
 * @author Joe Pelz, Set A, A00893517
 * @version 1.0
 */
public class Renderer {
    /** If stale = true, the image needs to be redrawn
     * (because something has changed). */
    private boolean stale = true;
    
    /** Store the level. */
    private Level world;
    /** Store the lights and other static props. */
    private ArrayList<Drawable> staticProps = new ArrayList<Drawable>();
    /** Store dynamic props. */
    private ArrayList<Drawable> dynProps = new ArrayList<Drawable>();

    /** All static layers mixed. 0,0 is bottom left. */
    private double[][][] precomp;
    /** Final output image. 0,0 is top left. */
    private BufferedImage finComp;
    /** Screen size image. */
    private BufferedImage screen;
    /** Graphics handle for the screen. */
    private Graphics2D gScreen;
    /**
     * Constructor for an empty renderer.
     */
    public Renderer() {
    }

    /**
     * Tells the renderer that it's image needs recalculating.
     */
    public void invalidate() {
        stale = true;
    }
    
    /**
     * Tells the renderer that a region of the image needs recalculating.
     * @param r The region that needs to be redrawn.
     */
    public void invalidate(Rectangle r) {
        updateComp(r);
        updateImage(r);
    }
    
    /**
     * Give the renderer a world to base it's drawing around.
     * @param w the world to draw (and draw on)
     */
    public void setWorld(Level w) {
        world = w;
        finComp = new BufferedImage(
                w.getBounds().width, 
                w.getBounds().height, 
                BufferedImage.TYPE_4BYTE_ABGR);
        precomp = new double[w.getBounds().width]
                            [w.getBounds().height]
                            [Util.CHANNELS];
        stale = true;
    }
    /**
     * Add a static prop to the world renderer.
     * @param prop The prop to add to the world.
     */
    public void addStaticProp(Drawable prop) {
        staticProps.add(prop);
    }
    /**
     * Add a static prop to the world renderer.
     * @param prop The prop to add to the world.
     */
    public void addDynProp(Drawable prop) {
        dynProps.add(prop);
    }

    /**
     * Update the final pixel array comp of the background.
     * This includes the level and static props.
     */
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

        for (Drawable d : staticProps) {
//            System.out.println("adding light at " + ((Light) d).getPos());
            if (d.isDrawn()) {
                add(d.getPixels(), d.getBounds());
            }
        }
        stale = false;
    }
    
    /**
     * Update a specific region in the final pixel comp.
     * @param bounds The region to update.
     */
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
        
        for (Drawable d : staticProps) {
//            System.out.println("adding light at " + ((Light) d).getPos());
            if (d.isDrawn()) {
                add(d.getPixels(), d.getBounds(), bounds);
            }
        }
    }

    /**
     * Merge a layer into the background by adding it. 
     * @param pixels The pixel array to merge
     * @param bound The global coordinates of those pixels.
     */
    private void add(double[][][] pixels, Rectangle bound) {
        Rectangle worldBounds = world.getBounds();
        int left   = Math.max(bound.x, worldBounds.x);
        int bottom = Math.max(bound.y, worldBounds.y);
        int right  = (Math.min(bound.x + bound.width,  
                               worldBounds.x + worldBounds.width));
        int top    = (Math.min(bound.y + bound.height, 
                               worldBounds.y + worldBounds.height));
        for (int x = left; x < right; x++) {
            for (int y = bottom; y < top; y++) {
                precomp[x][y][Util.R] += pixels[x - bound.x]
                                               [y - bound.y]
                                               [Util.R];
                precomp[x][y][Util.G] += pixels[x - bound.x]
                                               [y - bound.y]
                                               [Util.G];
                precomp[x][y][Util.B] += pixels[x - bound.x]
                                               [y - bound.y]
                                               [Util.B];
            }
        }
    }
    
    /**
     * Merge a layer into the background by adding it, 
     * but only over a specific region. 
     * @param pixels The pixel array to merge
     * @param bound  The global coordinates of those pixels.
     * @param region The specific region to exclusively draw on. 
     */
    private void add(double[][][] pixels, Rectangle bound, Rectangle region) {
        Rectangle worldBounds = world.getBounds();
        int left   = Math.max(bound.x, worldBounds.x);
        int bottom = Math.max(bound.y, worldBounds.y);
        int right  = (Math.min(bound.x + bound.width,  
                               worldBounds.x + worldBounds.width));
        int top    = (Math.min(bound.y + bound.height,
                               worldBounds.y + worldBounds.height));

        //only update the relevant region
        left   = Math.max(left,   region.x);
        right  = Math.min(right,  region.x + region.width);
        bottom = Math.max(bottom, region.y);
        top    = Math.min(top,    region.y + region.height);
        
        for (int x = left; x < right; x++) {
            for (int y = bottom; y < top; y++) {
                precomp[x][y][Util.R] += pixels[x - bound.x]
                                               [y - bound.y]
                                               [Util.R];
                precomp[x][y][Util.G] += pixels[x - bound.x]
                                               [y - bound.y]
                                               [Util.G];
                precomp[x][y][Util.B] += pixels[x - bound.x]
                                               [y - bound.y]
                                               [Util.B];
            }
        }
    }
    

    /**
     * Transfer the final colors from the array to the buffered image.  
     * This is the point where we switch coordinate systems.
     * Up until now 0,0 has been the bottom left, with +Y going up.
     * After this (in finComp) 0,0 will be the top left, with +Y going down.
     */
    private void updateImage() {
        Util.pixelsToImage(precomp, finComp);
    }
    
    /**
     * Transfer the final colors from the pixel array to the buffered image,  
     * but only update a specific subset rectangle.
     * This is the point where we switch coordinate systems.
     * Up until now 0,0 has been the bottom left, with +Y going up.
     * After this (in finComp) 0,0 will be the top left, with +Y going down.
     * @param region The particular region of the image to update. 
     */
    private void updateImage(Rectangle region) {
        Util.pixelsToImage(precomp, finComp, region);
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
        
        if (screen == null 
                || screen.getWidth() != comp.getWidth() 
                || screen.getHeight() != comp.getHeight()) {
            screen = new BufferedImage(
                    comp.getWidth(), 
                    comp.getHeight(), 
                    BufferedImage.TYPE_4BYTE_ABGR);
            gScreen = screen.createGraphics();
        }

        
        //black out the background
        gScreen.setPaint(Color.black);
        gScreen.fillRect(0, 0, screen.getWidth(), screen.getHeight());
        //draw bg that covers screen
        gScreen.drawImage(finComp, 
                bounds.x - offsetX, 
                bounds.y - bounds.height + offsetY + comp.getHeight(),
                null);

        
        mergeDynProps(offsetX, offsetY);

        
        g.drawImage(screen, 
                0, 
                0,
                null);

        
    }

    /**
     * Merge dynamic props over the background.
     * @param offsetX camera offset in X
     * @param offsetY camera offset in Y
     */
    private void mergeDynProps(int offsetX, int offsetY) {
        for (Drawable prop : dynProps) {
            mergeDynProp(prop, offsetX, offsetY);
        }
    }
    
    /**
     * Merge a dynamic prop over the background.
     * @param prop The prop to draw onto the screen
     * @param offsetX camera offset in X
     * @param offsetY camera offset in Y
     */
    private void mergeDynProp(Drawable prop, int offsetX, int offsetY) {
        byte[] pixels = ((DataBufferByte) 
                            screen
                            .getRaster()
                            .getDataBuffer())
                            .getData();
        Rectangle r = prop.getBounds();
        byte[] hPixels = prop.getData();
        
        int col = 0;
        int row = 0;
        double mix;
        double mixa;
        int pi;
        for (int i = 0; i < hPixels.length; i += Util.CHANNELS) {
            pi = col + ((r.x - offsetX) << 2) 
                 +  (row * screen.getWidth() << 2)
                 +  ((screen.getHeight() 
                     - (r.y - offsetY + r.height)) 
                     * screen.getWidth() << 2);
            if (pi < 0 || pi > pixels.length) {
                System.out.println("caught a " + pi);
                break;
            }
            if (hPixels[i] == (byte) -1) {
                pixels[pi + Util.B] = hPixels[i + Util.B];
                pixels[pi + Util.G] = hPixels[i + Util.G];
                pixels[pi + Util.R] = hPixels[i + Util.R];
            } else {
                mix = (double) (hPixels[i] & Util.B_MAX) / Util.B_MAX;
                mixa = 1.0 - mix;
                pixels[pi + Util.B] = (byte)
                        ((hPixels[i + Util.B] & Util.B_MAX) * mix 
                       + (pixels[pi + Util.B] & Util.B_MAX) * mixa);
                pixels[pi + Util.G] = (byte)
                        ((hPixels[i + Util.G] & Util.B_MAX) * mix 
                       + (pixels[pi + Util.G] & Util.B_MAX) * mixa);
                pixels[pi + Util.R] = (byte)
                        ((hPixels[i + Util.R] & Util.B_MAX) * mix 
                       + (pixels[pi + Util.R] & Util.B_MAX) * mixa);
            }
            
            col += Util.CHANNELS;
            if (col >= r.width * Util.CHANNELS) {
                col = 0;
                row++;
            }
            
            //System.out.println("Row: " + row + "; Col: " + col);
        }
    }
}
