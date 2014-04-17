/** Joe Pelz, Set A, A00893517 */
package core.props;

import java.awt.Point;
import java.awt.Rectangle;

import core.Drawable;
import core.Texture;
import core.Trigger;
import core.Util;
import core.world.Level;

/**
 * <p>Represents a light in the world, at a particular position.</p>
 * @author Joe Pelz, Set A, A00893517
 * @version 1.0
 */
public class Light implements Trigger, Drawable {
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
    private int r = 210;
    /** The green component of the light color. */
    private int g = 180;
    /** The blue component of the light color. */
    private int b = 130;
    /** The pixels for the visual overlay of the light. */
    private double[][][] pixels;
    
    /** The light radius, default 100. */
    private short lightRadius = 100;
    /** Handle to world, for manipulating. */
    private Level hLevel;
    /** The next time it is safe to trigger the light. */
    private long timeSafe = 0;
    
    /**
     * Constructor to create a new light at the given coordinates.
     * @param x X position in world pixels.
     * @param y Y position in world pixels.
     */
    public Light(int x, int y, Level handle) {
        hLevel = handle;
        pos = new Point(x, y);
        isActive = true;
        isOn = false;
        wpos = new Point(x / Level.CELL_SIZE, y / Level.CELL_SIZE);
        updateBounds();
        pixels = new double[bounds.width][bounds.height][Util.CHANNELS];
        updatePixels();
    }

    /**
     * Calculates the bounds of the light's influence
     * @param world The world to clamp the light's influence by
     */
    private void updateBounds() {
        bounds = new Rectangle(
                pos.x - lightRadius, 
                pos.y - lightRadius, 
                lightRadius * 2, 
                lightRadius * 2);
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
            hLevel.setCell(wpos.x, wpos.y, Texture.bgLightDead);
        } else {
            isOn = true;
            //set world brick light
            hLevel.setCell(wpos.x, wpos.y, Texture.bgLight);
        }
        //filter the world
        updatePixels();
        
    }

    public void updatePixels() {
        //Center of light glow
        int cX = lightRadius;
        int cY = lightRadius;
        double distance;
        if(isOn) {
            for (int x = 0; x < bounds.width; x++) {
                for (int y = 0; y < bounds.height; y++) {
                    
                    //Get pixel distance from light source
                    distance = getDistance(x, cX, y, cY);
                    /* Convert distance to 0:1 with 1 at the light 
                     * and 0 at the perimeter circle. Clamp negatives.*/
                    distance = Math.max(0, 1 - (distance / lightRadius));
                    distance = Math.pow(distance, 3);
                    
                    //TODO: This should only need to calculate one quadrant
                    //It would be identical in other quadrants.
                    pixels[x][y][Util.R] = distance * r / 255.0;
                    pixels[x][y][Util.G] = distance * g / 255.0;
                    pixels[x][y][Util.B] = distance * b / 255.0;
                    pixels[x][y][Util.A] = 1.0;
                }
            }
        } else {
            for (int x = 0; x < bounds.width; x++) {
                for (int y = 0; y < bounds.height; y++) {
                    pixels[x][y][Util.R] = 0.0;
                    pixels[x][y][Util.G] = 0.0;
                    pixels[x][y][Util.B] = 0.0;
                    pixels[x][y][Util.A] = 0.0;
                }
            }
        }
    }
    
    @Override
    public double[][][] getPixels() {
        return pixels;
    }

    @Override
    public Rectangle getBounds() {
        return bounds;
    }

    @Override
    public short getDrawType() {
        // TODO Auto-generated method stub
        return 1;
    }
}
