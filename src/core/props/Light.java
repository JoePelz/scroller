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
    private double r;
    /** The green component of the light color. */
    private double g;
    /** The blue component of the light color. */
    private double b;
    /** The pixels for the visual overlay of the light. */
    private double[][][] pixels;
    
    /** The light radius, default 100. */
    private short lightRadius;
    /** Handle to world, for manipulating. */
    private Level hLevel;
    /** The next time it is safe to trigger the light. */
    private long timeSafe;
    
    /**
     * Constructor to create a new light at the given coordinates.
     * @param x X position in world pixels.
     * @param y Y position in world pixels.
     * @param handle A connection to the level (for the trigger action).
     */
    public Light(int x, int y, Level handle) {
        hLevel = handle;
        pos = new Point(x, y);
        wpos = new Point(x / Level.CELL_SIZE, y / Level.CELL_SIZE);
        
        isActive = true;
        isOn = false;
        
        //default color
        final double defR = 0.8235;
        final double defG = 0.7059;
        final double defB = 0.5098;
        setColor(defR, defG, defB);
        
        //default radius
        final short defRadius = 100;
        setRadius(defRadius);
    }

    /**
     * Calculates the bounds of the light's influence.
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
    public void setColor(double red, double green, double blue) {
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
     * Set the light radius for the light.
     * @param radius The light range
     */
    public void setRadius(short radius) {
        lightRadius = radius;
        updateBounds();
        updatePixels();
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
     * @return a reference to the light's position,
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
    public boolean isOn() {
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

    /**
     * Check if the light's trigger is active.
     * @return true if the trigger is active.
     */
    public boolean isActive() {
        return isActive;
    }

    @Override
    public boolean isTriggered(Rectangle bbox) {
        boolean result = (
                   pos.x > bbox.x 
                && pos.x < (bbox.x + bbox.width)
                && pos.y > bbox.y
                && pos.y < (bbox.y + bbox.height)
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
    }

    /**
     * Update the pixel array for the light.
     */
    public void updatePixels() {
        pixels = new double[lightRadius << 1][lightRadius << 1][Util.CHANNELS];
        //Center of light glow
        double dR;
        double dG;
        double dB;
        double distance;
        for (int x = 0, ix = bounds.width - 1; 
                x < lightRadius; 
                x++, ix--) {
            for (int y = 0, iy = bounds.height - 1; 
                    y < lightRadius; 
                    y++, iy--) {
                
                //Get pixel distance from light source
                distance = getDistance(x, lightRadius, y, lightRadius);
                /* Convert distance to 0:1 with 1 at the light 
                 * and 0 at the perimeter circle. Clamp negatives.*/
                distance = Math.max(0, 1 - (distance / lightRadius));
                distance = Math.pow(distance, 2);

                dR = distance * r;
                dG = distance * g;
                dB = distance * b;
                pixels[x][y][Util.R] = dR;
                pixels[x][y][Util.G] = dG;
                pixels[x][y][Util.B] = dB;
                pixels[x][y][Util.A] = 1.0;
                pixels[ix][y][Util.R] = dR;
                pixels[ix][y][Util.G] = dG;
                pixels[ix][y][Util.B] = dB;
                pixels[ix][y][Util.A] = 1.0;
                pixels[x][iy][Util.R] = dR;
                pixels[x][iy][Util.G] = dG;
                pixels[x][iy][Util.B] = dB;
                pixels[x][iy][Util.A] = 1.0;
                pixels[ix][iy][Util.R] = dR;
                pixels[ix][iy][Util.G] = dG;
                pixels[ix][iy][Util.B] = dB;
                pixels[ix][iy][Util.A] = 1.0;
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

    @Override
    public boolean isDrawn() {
        return isOn;
    }
}
