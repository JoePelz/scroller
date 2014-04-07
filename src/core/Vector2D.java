/** Joe Pelz, Set A, A00893517 */
package core;


/**
 * <p>Represents a 2d vector in double floating point.</p>
 * @author Joe Pelz, Set A, A00893517
 * @version 1.0
 */
public class Vector2D {
    /** The vector's x component. */
    public double x;
    /** The vector's y component. */
    public double y;
    
    /**
     * Vector constructor, from two axial components.
     * @param x The x axis value
     * @param y The y axis value
     */
    public Vector2D(double x, double y) {
        this.x = x;
        this.y = y;
    }
    
    /**
     * Vector constructor that duplicates the given vector in memory.
     * @param v the vector to replicate.
     */
    public Vector2D(Vector2D v) {
        x = v.x;
        y = v.y;
    }

    /**
     * X component accessor.
     * @return The x component of the vector
     */
    public double getX() {
        return x;
    }
    /**
     * Y component accessor.
     * @return The y component of the vector
     */
    public double getY() {
        return y;
    }
    /**
     * X component mutator.
     * @param newX The replacement x value
     */
    public void setX(double newX) {
        x = newX;
    }
    /**
     * Y component mutator.
     * @param newY The replacement y value
     */
    public void setY(double newY) {
        y = newY;
    }
    /**
     * Full mutator to replace both components of the vector.
     * @param newX The replacement x value
     * @param newY The replacement y value
     */
    public void set(double newX, double newY) {
        this.x = newX;
        this.y = newY;
    }
    
    /**
     * Get the length of the vector.
     * @return the vector's length.
     */
    public double length() {
        return Math.sqrt(x * x + y * y);
    }
    
    /**
     * Normalize the vector to unit length. Does not mutate. 
     * @return A new normalized vector. 
     */
    public Vector2D normalize() {
        double a = length();
        return new Vector2D(x / a, y / a);
    }

    /**
     * Negate each axis in the vector. Does not mutate.
     * @return A new negated vector.
     */
    public Vector2D negate() {
        return new Vector2D(-x, -y);
    }

    /**
     * Multiplies each component of the vector by the given factor. 
     * Does not mutate.
     * @param factor The value to multiply by
     * @return A new, multiplied vector.
     */
    public Vector2D multiply(double factor) {
        return new Vector2D(x * factor, y * factor);
    }

    /**
     * Offsets (adds) the vector by the given vector's values. Mutates.
     * @param v Offset by the components of this vector.
     */
    public void offset(Vector2D v) {
        x += v.x;
        y += v.y;
    }
    /**
     * Offsets (adds) the vector by the given vector's values. Mutates.
     * @param offsetX offsets the vector's X axis by this amount
     * @param offsetY offsets the vector's Y axis by this amount
     */
    public void offset(double offsetX, double offsetY) {
        x += offsetX;
        y += offsetY;
    }
}
