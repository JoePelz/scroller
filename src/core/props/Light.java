/** Joe Pelz, Set A, A00893517 */
package core.props;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

import core.Texture;
import core.Trigger;
import core.world.World;

/**
 * <p>Represents a light in the world, at a particular position.</p>
 * @author Joe Pelz, Set A, A00893517
 * @version 1.0
 */
public class Light implements Filter, Trigger {
    /** Bit-shift value for red in integer colors. */
    private static final byte R_SHIFT = 16;
    /** Bit-shift value for green in integer colors. */
    private static final byte G_SHIFT = 8;
    /** how many milliseconds to wait before allowing retriggering. */
    private static final short COOLDOWN = 500;

    /** The position of the light in world pixels. */
    private Point pos;
    /** The position of the light in the world array. */
    private Point wpos;
    /** The area of influence for the light. */
    private Rectangle bounds;
    /** Whether the light is on or off. */
    private boolean isOn;
    /** Whether the light is active. */
    private boolean isActive;

    /** The red component of the light color. */
    private int r = 255;
    /** The green component of the light color. */
    private int g = 255;
    /** The blue component of the light color. */
    private int b = 255;
    
    /** The light radius, default 100. */
    private short lightRadius = 100;
    /** Handle to world, for manipulating. */
    private World worldHandle;
    /** The next time it is safe to trigger the light. */
    private long timeSafe = 0;
    
    /**
     * Constructor to create a new light at the given coordinates.
     * @param x X position in world pixels.
     * @param y Y position in world pixels.
     */
    public Light(int x, int y, World world) {
        pos = new Point(x, y);
        isActive = true;
        isOn = true;
        this.worldHandle = world;
        wpos = new Point(x / World.CELL_SIZE, y / World.CELL_SIZE);
        updateBounds(world);
    }

    /**
     * Calculates the bounds of the light's influence
     * @param world The world to clamp the light's influence by
     */
    private void updateBounds(World world) {
        int posX = pos.x;
        int posY = world.getHeightPixels() - pos.y;
        bounds = new Rectangle(
                Math.max(posX - lightRadius, 0), 
                Math.max(posY - lightRadius, 0), 
                Math.min(lightRadius * 2, world.getWidthPixels() - (posX - lightRadius)), 
                Math.min(lightRadius * 2, world.getHeightPixels() - (posY - lightRadius)));
    }

    /**
     * Set the color of the light. 
     * @param red the red value for the light color.
     * @param green the green value for the light color.
     * @param blue the blue value for the light color.
     */
    public void setColor(int red, int green, int blue) {
        r = red;
        g = green;
        b = blue;
    }
    
    /**
     * Get the integer representation of the color of this light.
     * @return The color of the light.
     */
    public int getColor() {
        return ((int) r) << R_SHIFT + ((int) g) << G_SHIFT + (int) b; 
    }
    
    /**
     * Get the light position.
     * @return The position of the light (world pixels)
     */
    public Point getLight() {
        return pos;
    }

    /**
     * Get the light position.
     */
    public Point getPos() {
        return pos;
    }
    /**
     * Turn the light on or off.
     * @param on True if the light should be on.
     */
    public void setOn(boolean on) {
        isActive = on;
    }
    /**
     * Check if the light is on or off.
     * @return true if the light is on.
     */
    public boolean isOn(boolean on) {
        return isOn;
    }
    
    /**
     * Get the distance between two points.
     * @param x1 Point 1's x coordinate
     * @param x2 Point 2's x coordinate
     * @param y1 Point 1's y coordinate
     * @param y2 Point 2's y coordinate
     * @return The distance between the points, as a double.
     */
    private double getDistance(int x1, int x2, int y1, int y2) {
        x1 = x2 - x1;
        y1 = y2 - y1;
        return Math.sqrt(x1 * x1 + y1 * y1);
    }

    @Override
    public void filter(BufferedImage source, BufferedImage destination) {
        int posY = source.getHeight() - pos.y;
        WritableRaster raster;
        //I'm not sure if this is a good idea...
        raster = (WritableRaster) source.getData(bounds);
        if (!isOn) {
            destination.setData(raster);
            return;
        }
        
        int[] c = {255, 255, 255, 255};
        double distance;
        
        for (int y = bounds.y; y < bounds.y + bounds.height; y++) {
            for (int x = bounds.x; x < bounds.x + bounds.width; x++) {
                c = raster.getPixel(x, y, c); 
//                if (x % 5 == 0 && y % 5 == 0)
//                    System.out.println("0: " + c[0] + "\t1: " + c[1] + "\t2: " + c[2] + "\t3: " + c[3]);
                //Get pixel distance from light source
                distance = getDistance(x, pos.x, y, posY);
                /* Convert distance to 0:1 with 1 at the light 
                 * and 0 at the perimeter circle. Clamp negatives.*/
                distance = Math.max(0, 1 - (distance / lightRadius));
                distance = Math.pow(distance, 2);
                
                // Add the lighting to the pixels
                c[0] = (int) (distance * r + c[0]);
                c[1] = (int) (distance * g + c[1]);
                c[2] = (int) (distance * b + c[2]);

                c[0] = Math.min(c[0], 255);
                c[1] = Math.min(c[1], 255);
                c[2] = Math.min(c[2], 255);
                
                raster.setPixel(x, y, c);
            }
        }
        
        destination.setData(raster);
    }

    @Override
    public boolean isInRange(Rectangle r) {
        return true;
    }

    @Override
    public boolean isActive() {
        return isActive;
    }

    @Override
    public boolean isTriggered(Rectangle bounds) {
        boolean result = (
                   pos.x > bounds.x 
                && pos.x < (bounds.x + bounds.width)
                && pos.y > bounds.y
                && pos.y < (bounds.y + bounds.height)
                && System.currentTimeMillis() > timeSafe);
        return result;
    }

    @Override
    public void triggerAction() {
        //toggle light
        timeSafe = System.currentTimeMillis() + COOLDOWN;
        if (isOn) {
            isOn = false;
            //set world brick dark
            worldHandle.setCell(wpos.x, wpos.y, Texture.bgLightDead);
        } else {
            isOn = true;
            //set world brick light
            worldHandle.setCell(wpos.x, wpos.y, Texture.bgLight);
        }
        //filter the world
        worldHandle.filter(this);
        
    }
}
