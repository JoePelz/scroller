/** Joe Pelz, Set A, A00893517 */
package core;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Point;
import java.util.Random;

import javax.swing.ImageIcon;

/**
 * <p>block types available for usage.</p>
 * @author Joe Pelz, Set A, A00893517
 * @version 1.0
 */
enum Block {
    /** Background blocks, non colliding. */
    bg,
    /** Background light, non colliding. */
    bgLight,
    /** Background broken light, non colliding. */
    bgLightDead,
    /** Solid bricks, in the foreground. */
    wall
}

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
    /** Brick density. */
    private static final float BRICK_DENSITY = 0.8f;
    /** Probability of a light in the bg. */
    private static final float LIGHT_PBTY = 0.02f;
    
    /** The world itself, in memory. */
    private Block[][] world = new Block[WORLD_HEIGHT][WORLD_WIDTH];

    /** The icon to use for background tiles. */
    private ImageIcon gBG;
    /** The icon to use for background lights. */
    private ImageIcon gBGLight;
    /** The icon to use for broken background lights. */
    private ImageIcon gBGLightDead;
    /** The icon to use for solid bricks. */
    private ImageIcon gWall;
    /** The icon to use for extra-worldly tiles. */
    private ImageIcon gEmpty;
    /** The current icon to paint with. */
    private ImageIcon brush;
    
    /**
     * World constructor, to make a new world of the given dimensions.
     * @param width The number of blocks wide the world is
     * @param height The number of blocks high the world is
     */
    public World(int width, int height) {
        for (int row = 0; row < world.length; row++) {
            for (int col = 0; col < world[0].length; col++) {
                if (GEN.nextDouble() * row < BRICK_DENSITY) {
                    //solid bricks
                    world[row][col] = Block.wall;
                } else {
                    //background bricks
                    if (GEN.nextDouble() < LIGHT_PBTY) {
                        world[row][col] = Block.bgLight;
                    } else {
                        world[row][col] = Block.bg;
                    }
                }
            }
        }
        brush = gBG;
    }
    
    /**
     * If the block at the given coordinates is inside of bounds, 
     * return what type of block it is.
     * @param x The x coordinate to fetch.
     * @param y The y coordinate to fetch.
     * @return The block type
     */
    public Block getCell(int x, int y) {
        if (x >= 0 
                && x < WORLD_WIDTH
                && y >= 0
                && y < WORLD_HEIGHT) {
            return world[y][x];
        }
        return Block.bg;
    }
    /**
     * If the block at the given coordinates is inside of bounds, 
     * Set that block to be the given type.
     * @param x The x coordinate to fetch.
     * @param y The y coordinate to fetch.
     * @param type The type to set the block to
     */
    public void setCell(int x, int y, Block type) {
        if (x >= 0 
                && x < WORLD_WIDTH
                && y >= 0
                && y < WORLD_HEIGHT) {
            world[y][x] = type;
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
        //if the obj was moving right
        if (velocity > 0) {
            //nudge it left into safety.
            escape = -(pos.x % CELL_SIZE) + (obj.getSize().width % CELL_SIZE);
            
        //but if the obj was moving left
        } else if (velocity < 0) {
            //nudge it right into safety
            escape = CELL_SIZE - (pos.x % CELL_SIZE);
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
        //if the obj was moving up
        if (velocity > 0) {
            //nudge it down into safety.
            escape = -(pos.y % CELL_SIZE) + (obj.getSize().height % CELL_SIZE);
            
        //but if the obj was moving down
        } else if (velocity < 0) {
            //nudge it up into safety
            escape = CELL_SIZE - (pos.y % CELL_SIZE);
        }
        
        return escape;
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
        
        int resolutionOffset = comp.getHeight() - CELL_SIZE;

        if (offsetX < 0) {
            offsetX -= CELL_SIZE;
        }
        if (offsetY < 0) {
            offsetY -= CELL_SIZE;
        }
        //first visible block
        int minX = offsetX / CELL_SIZE;
        //last visible block
        int maxX = minX + comp.getWidth() / CELL_SIZE + 1;
        
        //lowest visible block
        int minY = offsetY / CELL_SIZE;
        //highest visible block
        int maxY = minY + comp.getHeight() / CELL_SIZE + 1;
        
        int subX = offsetX % CELL_SIZE + ((offsetX < 0) ? CELL_SIZE : 0);
        int subY = -offsetY % CELL_SIZE - ((offsetY < 0) ? CELL_SIZE : 0);
        
        //infinitely scrolling background
        for (int row = minY; row <= maxY; row++) {
            for (int col = minX; col <= maxX; col++) {
                //if brick is in range, use the world array
                if (row >= 0 
                        && row < world.length 
                        && col >= 0 
                        && col < world[0].length) {
                    switch (world[row][col]) {
                    case bg:
                        brush = gBG;
                        break;
                    case bgLight:
                        brush = gBGLight;
                        break;
                    case bgLightDead:
                        brush = gBGLightDead;
                        break;
                    case wall:
                        brush = gWall;
                        break;
                    default:
                        brush = gBG;
                        break;
                    }
                //if brick is not in range, use the empty image
                } else {
                    brush = gEmpty;
                }
                
                x = (col - minX) * CELL_SIZE - subX;
                y = (row - minY) * CELL_SIZE + subY;
                y = (y * -1) + (resolutionOffset);

                //hehehehe.  Fun.
                //x += gen.nextInt(3) - 1;
                //y += gen.nextInt(3) - 1;
                brush.paintIcon(comp, page, x, y);
            }
        }

        //print coordinates:
        //page.drawString("minX: " + minX, 20, 40);
        //page.drawString("maxX: " + maxX, 20, 60);
        //page.drawString("minY: " + minY, 100, 40);
        //page.drawString("maxY: " + maxY, 100, 60);
        
    }

    /**
     * Sets the images to use for the world.
     * @param tp The texture pack to use
     */
    public void setTextures(TexturePack tp) {
        gBG = tp.getBG();
        gBGLight = tp.getBGLight();
        gBGLightDead = tp.getBGLightDead();
        gWall = tp.getWall();
        gEmpty = tp.getBG();
        brush = tp.getBG();
        
    }
}
