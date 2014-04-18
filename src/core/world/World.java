/** Joe Pelz, Set A, A00893517 */
package core.world;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;

import core.Texture;
import core.TexturePack;


/**
 * <p>This class represents a game world, randomely generated, 
 * to run across and around.</p>
 * <p>It generates a grid of tiles, and assigns them various types to 
 * collide or not, and for the player to run over.</p>
 * @author Joe Pelz, Set A, A00893517
 * @version 1.0
 */
public class World {
    /** The size of the blocks (pixels) in this world. */
    public static final int CELL_SIZE = 30;
    /** Width of the world. */
    private static final int WORLD_WIDTH = 150;
    /** Height of the world. */
    private static final int WORLD_HEIGHT = 15;
    /** Size of background image buffer. */
    private static final int BG_BUFFER_SIZE = 4;
    
    /** The texture pack to use. NOT toilet paper. */
    private TexturePack tp;
    /** The output image off all the tiles. */
    private BufferedImage fincomp;
    /** The pre-computed image to draw for the bg. */
    private BufferedImage background;
    /** Graphics handle for the background. */
    private Graphics gBG;
    
    /**
     * World constructor, to make a new world of the given dimensions.
     * @param width The number of blocks wide the world is
     * @param height The number of blocks high the world is
     * @param texPack The texture set to use
     */
    public World(int width, int height, TexturePack texPack) {
        tp = texPack;
                
        fincomp = new BufferedImage(CELL_SIZE * WORLD_WIDTH, 
                CELL_SIZE * WORLD_HEIGHT, 
                BufferedImage.TYPE_INT_ARGB);
        background = new BufferedImage(CELL_SIZE * BG_BUFFER_SIZE, 
                CELL_SIZE * BG_BUFFER_SIZE,
                BufferedImage.TYPE_INT_RGB);
        gBG = background.createGraphics();
        fillBGBuffer();
    }

    /** 
     * Draw into the tiling background buffer.
     */
    public void fillBGBuffer() {
        //set the brush to background bricks
        
        for (int y = 0; y < BG_BUFFER_SIZE; y++) {
            for (int x = 0; x < BG_BUFFER_SIZE; x++) {
                gBG.drawImage(tp.get(Texture.bg), 
                        x * CELL_SIZE, 
        // Y is upside down, but in this case doesn't matter
                        y * CELL_SIZE,  
                        null);            
            }
        }
    }
    
    /**
     * Draw the world to the screen, filling in absent array entries with 
     * bg bricks, offset by the camera's position.  THe coordinates 
     * correspond to the bottom left of the image.
     * @param comp The component used as observer
     * @param page The graphics context
     * @param offsetX The camera's x position
     * @param offsetY The camera's y position
     */
    public void draw(Graphics page, Component comp, int offsetX, int offsetY) {
        int x = 0;
        int y = 0;
        
        //Move with the camera
        x = -offsetX;
        y = offsetY;
        
        //Allow for window resizing
        y = y + comp.getHeight() - WORLD_HEIGHT * CELL_SIZE;

        //Draw the infinite background
        int h;
        int v = offsetY % CELL_SIZE + comp.getHeight();
        for (; v > -background.getHeight(); v -= background.getHeight()) {
            h = -(offsetX % CELL_SIZE) - CELL_SIZE;
            for (; h < comp.getWidth(); h += background.getWidth()) {
                page.drawImage(background, h, v, null);
            }
        }
        
        //Draw the important tiles into place.
        page.drawImage(fincomp, x, y, null);
    }
    
    /**
     * Take from Stack Exchange. Duplicates the given buffered image.
     * http://stackoverflow.com/questions/3514158/
     * how-do-you-clone-a-bufferedimage
     * 
     * @param bi BufferedImage to copy
     * @return duplicate of input BufferedImage
     */
    static BufferedImage deepCopy(BufferedImage bi) {
        ColorModel cm = bi.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = bi.copyData(null);
        return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
    }
}
