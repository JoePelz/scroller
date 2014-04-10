/** Joe Pelz, Set A, A00893517 */
package core;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.Random;


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
    /** Random gen of blocks. For fun. */
    private static final Random GEN = new Random();
    /** Width of the world. */
    private static final int WORLD_WIDTH = 150;
    /** Height of the world. */
    private static final int WORLD_HEIGHT = 15;
    /** Size of background image buffer. */
    private static final int BG_BUFFER_SIZE = 4;
    /** Brick density. */
    private static final float BRICK_DENSITY = 0.8f;
    /** Probability of a light in the bg. */
    private static final float LIGHT_PBTY = 0.02f;
    
    /** The world itself, in memory. */
    private Texture[][] world = new Texture[WORLD_HEIGHT][WORLD_WIDTH];


    /** The current icon to paint with. */
    private Image brush;
    /** The texture pack to use. NOT toilet paper. */
    private TexturePack tp;
    /** The pre-computed image to draw for the bg. */
    private BufferedImage precomp;
    /** The pre-computed image to draw for the bg. */
    private BufferedImage background;
    /** Graphics handle for the buffered image. */
    private Graphics g;
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
        
        for (int row = 0; row < world.length; row++) {
            for (int col = 0; col < world[0].length; col++) {
                if (GEN.nextDouble() * row < BRICK_DENSITY) {
                    //solid bricks
                    world[row][col] = Texture.brick;
                } else {
                    //background bricks
                    if (GEN.nextDouble() < LIGHT_PBTY) {
                        world[row][col] = Texture.bgLight;
                    } else {
                        world[row][col] = Texture.bg;
                    }
                }
            }
        }
        brush = tp.get(Texture.bg);
        
        precomp = new BufferedImage(CELL_SIZE * WORLD_WIDTH, 
                CELL_SIZE * WORLD_HEIGHT, 
                BufferedImage.TYPE_INT_ARGB);
        background = new BufferedImage(CELL_SIZE * BG_BUFFER_SIZE, 
                                       CELL_SIZE * BG_BUFFER_SIZE,
                                       BufferedImage.TYPE_INT_RGB);
        g = precomp.createGraphics();
        gBG = background.createGraphics();
        fillBuffer();
        fillBGBuffer();
    }
    
    /**
     * If the block at the given coordinates is inside of bounds, 
     * return what type of block it is.
     * @param x The x coordinate to fetch.
     * @param y The y coordinate to fetch.
     * @return The block type
     */
    public Texture getCell(int x, int y) {
        if (x >= 0 
                && x < WORLD_WIDTH
                && y >= 0
                && y < WORLD_HEIGHT) {
            return world[y][x];
        }
        return Texture.bg;
    }
    /**
     * If the block at the given coordinates is inside of bounds, 
     * Set that block to be the given type.
     * @param x The x coordinate to fetch.
     * @param y The y coordinate to fetch.
     * @param type The type to set the block to
     */
    public void setCell(int x, int y, Texture type) {
        if (x >= 0 
                && x < WORLD_WIDTH
                && y >= 0
                && y < WORLD_HEIGHT) {
            world[y][x] = type;
            brush = tp.get(type);
            g.drawImage(brush, 
                        x * CELL_SIZE, 
                        (WORLD_HEIGHT - 1 - y) * CELL_SIZE, 
                        null);
        }
    }

    /**
     * Calculate the x-axis movement required for the given object to not 
     * overlap with the given block coordinate. 
     * @param obj The entity that has collided
     * @param velocity The motion of the entity (for reversal. Unused yet.)
     * @param impactZone The block that has been collided with.
     * @return The distance to move to no longer be colliding.
     */
    public int escapeX(Dynamic obj, double velocity, Point impactZone) {
        Point pos = obj.getPos();
        int escape = 0; 
        //if the obj was moving right nudge it left into safety.
        if (velocity > 0) {
            escape = (impactZone.x * CELL_SIZE - obj.getSize().width) - pos.x;
            
        //but if the obj was moving left nudge it right into safety.
        } else if (velocity < 0) {
            escape = CELL_SIZE - (pos.x % CELL_SIZE);
        }

        //Clamp the velocity;
        if (Math.abs(escape) > Math.abs(velocity)) {
            escape = 0;
        }
        return escape;
    }
    /**
     * Calculate the y-axis movement required for the given object to not 
     * overlap with the given block coordinate. 
     * @param obj The entity that has collided
     * @param velocity The motion of the entity (for reversal. Unused yet.)
     * @param impactZone The block that has been collided with.
     * @return The distance to move to no longer be colliding.
     */
    public int escapeY(Dynamic obj, double velocity, Point impactZone) {
        Point pos = obj.getPos();
        int escape = 0; 

        //if the obj was moving up nudge it down into safety.
        if (velocity > 0) {
            escape = (impactZone.y * CELL_SIZE - obj.getSize().height) - pos.y;
            
        //but if the obj was moving down nudge it up into safety.
        } else if (velocity < 0) {
            escape = CELL_SIZE - (pos.y % CELL_SIZE);
        }
        
        //Clamp the velocity;
        if (Math.abs(escape) > Math.abs(velocity)) {
            escape = 0;
        }
        return escape;
    }

    /** 
     * Draw into the tiling background buffer.
     */
    public void fillBGBuffer() {
        //set the brush to background bricks
        brush = tp.get(Texture.bg);
        
        for (int y = 0; y < BG_BUFFER_SIZE; y++) {
            for (int x = 0; x < BG_BUFFER_SIZE; x++) {
                gBG.drawImage(brush, 
                        x * CELL_SIZE, 
        // Y is upside down, but in this case doesn't matter
                        y * CELL_SIZE,  
                        null);            
            }
        }
    }
    
    /**
     * Draw all the tiles to the buffered precomp.
     */
    public void fillBuffer() {
        for (int y = 0; y < WORLD_HEIGHT; y++) {
            for (int x = 0; x < WORLD_WIDTH; x++) {
                g.drawImage(tp.get(world[y][x]), 
                            x * CELL_SIZE, 
                            (WORLD_HEIGHT - 1 - y) * CELL_SIZE, 
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
        int v = offsetY % CELL_SIZE - CELL_SIZE;
        for (; v < comp.getHeight(); v += background.getHeight()) {
            h = -(offsetX % CELL_SIZE) - CELL_SIZE;
            for (; h < comp.getWidth(); h += background.getWidth()) {
                page.drawImage(background, h, v, null);
            }
        }
        
        //Draw the important tiles into place.
        page.drawImage(precomp, x, y, null);
    }
}
