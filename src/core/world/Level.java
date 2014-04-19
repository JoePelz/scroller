/** Joe Pelz, Set A, A00893517 */
package core.world;

import java.awt.Point;
import java.awt.Rectangle;
import java.io.InputStream;
import java.util.Scanner;

import core.Drawable;
import core.Dynamic;
import core.Texture;
import core.TexturePack;
import core.Util;

/**
 * <p>This class merely stores data about a level. </p>
 * @author Joe Pelz, Set A, A00893517
 * @version 1.0
 */
public class Level implements Drawable {
    /** the size of the tiles used. */
    public static final int CELL_SIZE = 30;
    /** The directory levels are stored in. */
    private static final String BASE_PATH = "/levels/";
    /** The rows (tiles, not pixels) in this level. */
    private final int rows;
    /** The cols (tiles, not pixels) in this level. */
    private final int cols;
    /** The array that holds level data. */
    private Texture[][] map;
    /** Where the player should start. */
    private Point start = new Point(0, 2);
    /** Where the player should exit. */
    private Point exit = new Point(0, -1);
    
    /** The texture pack to draw from. Pun intended. */
    private TexturePack tp;
    /** The actual pixels of the rendering. */
    private double[][][] pixels;
    /** The region the pixels cover. */
    private Rectangle bounds;
    /** The draw type for this element. */
    private short drawType;
    
    /**
     * Constructor to initialize a level from a file.
     * @param path The name of the file to populate the level from.
     * @param tPack The texture pack to use.
     */
    public Level(String path, TexturePack tPack) {
        tp = tPack;
        //Open the file for reading
        InputStream in = getClass().getResourceAsStream(BASE_PATH + path);
        Scanner scan = new Scanner(in);
        
        //Set the array size
        cols = scan.nextInt();
        rows = scan.nextInt();
        pixels = new double[cols * CELL_SIZE][rows * CELL_SIZE][Util.CHANNELS];
        map = new Texture[cols][rows];
        char[][] cMap = new char[cols][rows];
        char c = '0';
        scan.nextLine();
        String line; 

        //fill library from text file
        for (int row = rows - 1; row >= 0; row--) {
            line = scan.nextLine();
            for (int col = 0; col < cols; col++) {
                c = line.charAt(col);
                cMap[col][row] = c;
                if (c == 'I') {
                    start = new Point(col * CELL_SIZE, row * CELL_SIZE);
                }
                if (c == 'O') {
                    exit = new Point(col, row);
                }
            }
        }
        scan.close();
        
        init(cMap);
        updatePixels();
    }

    /**
     * Constructor to initialize a level of a particular with bg blocks.
     * @param rows How tall the level is
     * @param cols How wide the level is
     * @param tPack the TexturePack to use.
     */
    public Level(int cols, int rows, TexturePack tPack) {
        tp = tPack;
        pixels = new double[cols * CELL_SIZE][rows * CELL_SIZE][Util.CHANNELS];
        
        this.cols = cols;
        this.rows = rows;
        map = new Texture[cols][rows];
        initEmpty();
        final int defaultStart = rows * CELL_SIZE;
        start = new Point(0, defaultStart);
        
        updatePixels();
    }
    
    /**
     * Initialize the level to be empty.
     */
    private void initEmpty() {
        for (int col = 0; col < cols; col++) {
            for (int row = 0; row < rows; row++) {
                map[col][row] = Texture.bg;
            }
        }
    }
    
    /**
     * Initialize a level from an byte array.
     * @param data An array representing the blocks to make the world from.
     */
    private void init(char[][] data) {
        if (data.length != map.length || data[0].length != map[0].length) {
            throw new IllegalArgumentException("data array "
                    + "does not match level size!");
        }
        
        for (int col = 0; col < cols; col++) {
            for (int row = 0; row < rows; row++) {
                map[col][row] = translate(data[col][row]);
            }
        }
    }

    /**
     * Translate a text file byte into a Texture. 
     * @param b the byte that was read.
     * @return The corresponding Texture type.
     */
    private Texture translate(char b) {
        switch (b) {
        case '-':
            return Texture.bg;
        case 'i':
            return Texture.bgLightDead;
        case '#':
            return Texture.brick;
        default:
            return Texture.bg;
        }
    }

    /**
     * paste a level into another level.
     * @param bottom the y coordinate to paste the bottom of the new level at.
     * @param left the x coordinate to paste the left edge of the new level at.
     * @param v The new level to paste on top.
     */
    public void pasteLevel(int bottom, int left, Level v) {
        for (int col = left; col < left + v.getCols(); col++) {
            for (int row = bottom; row < bottom + v.getRows(); row++) {
                map[col][row] = v.getCell(col, row);
            }
        }
    }
    
    /**
     * Test if a coordinate is in bounds.
     * @param col the col to test
     * @param row the row to test
     * @return true if (row,col) is inside the array
     */
    public boolean isInBounds(int col, int row) {
        if (row < 0 || row >= rows) {
            return false;
        }
        if (col < 0 || col >= cols) {
            return false;
        }
        return true;
    }
    
    /**
     * Set a cell in the level to a particular value.
     * @param col the col to adjust
     * @param row the row to adjust
     * @param tx the texture to set
     */
    public void setCell(int col, int row, Texture tx) {
        if (!isInBounds(col, row)) {
            //target (13,4) out of bounds (0:10), (0:50).
            throw new IllegalArgumentException("target (" + row
                    + "," + col + ") out of bounds (0:" + rows
                    + "), (0:" + cols + ".");
        }
        map[col][row] = tx;
        updatePixels(col, row);
    }
    
    /**
     * Get the cell's value at the given coordinates.
     * @param col the column to inspect
     * @param row the row to inspect 
     * @return The value at the given coordinate.
     */
    public Texture getCell(int col, int row) {
        if (isInBounds(col, row)) {
            return map[col][row];
        } else {
            return Texture.bg;
        }
    }

    /**
     * accessor for columns.
     * @return the number of columns (width) in the level
     */
    public int getCols() {
        return cols;
    }
    /**
     * accessor for rows.
     * @return the number of rows (height) in the level
     */
    public int getRows() {
        return rows;
    }
    /**
     * accessor for start location.
     * @return The point to start at
     */
    public Point getStart() {
        return start;
    }
    /**
     * mutator for start location.
     * @param newStart The point to start at
     */
    public void setStart(Point newStart) {
        start = newStart;
    }
    /**
     * accessor for exit location.
     * @return The point where the player exits the level
     */
    public Point getExit() {
        return exit;
    }

    /**
     * Gets all the bricks that match the given texture.
     * @param tx the texture to match
     * @return An array of matching locations.
     */
    public Point[] getAll(Texture tx) {
        //how much to increment the array by each time.
        final int increment = 20;
        //make array size 0
        Point[] result = new Point[0];
        Point[] temp;
        int row = 0;
        int col = 0;
        byte lightCount = 0;
        
        //while iteration is incomplete
        while (col < cols && row < rows) {
            
            //if out of space, resize the array
            if (lightCount == result.length) {
                temp = new Point[result.length + increment];
                for (int i = 0; i < temp.length; i++) {
                    if (i < result.length) {
                        temp[i] = result[i];
                    }
                }
                result = temp;
            }
            
            //Advance through the array
            row++;
            if (row >= rows) {
                row = 0;
                col++;
            }
            
            //add to array if matching
            if (getCell(col, row) == tx) {
                result[lightCount] = new Point(col * CELL_SIZE, 
                                               row * CELL_SIZE);
                lightCount++;
            }
        
        }
        
        //crop array to minimum size
        temp = result;
        result = new Point[lightCount];
        for (int i = 0; i < lightCount; i++) {
            result[i] = temp[i];
        }
        
        //return result
        return result;
    }
    
    /**
     * Update the entire pixel array for the level.
     */
    private void updatePixels() { 
        double[][][] iPixels;
        
        //for every block in the world,
        for (int col = 0; col < cols; col++) {
            for (int row = 0; row < rows; row++) {
                //get the pixels for that texture
                iPixels = tp.getP(map[col][row]);
                //paste them into the instance data pixels[][][];        
                for (int x = col * CELL_SIZE; 
                        x < col * CELL_SIZE + CELL_SIZE; 
                        x++) {
                    for (int y = row * CELL_SIZE; 
                            y < row * CELL_SIZE + CELL_SIZE; 
                            y++) {
                        pixels[x][y] = iPixels[x - (col * CELL_SIZE)]
                                              [(y - (row * CELL_SIZE))];
                    }
                }
            }
        }
    }
    
    /**
     * update a specific tile of the level's pixel array.
     * @param col the tile column to update.
     * @param row the tile row to update.
     */
    private void updatePixels(int col, int row) {
        double[][][] iPixels;

        //get the pixels for that texture
        iPixels = tp.getP(map[col][row]);
        //paste them into the instance data pixels[][][];        
        for (int x = col * CELL_SIZE; 
                x < col * CELL_SIZE + CELL_SIZE; 
                x++) {
            for (int y = row * CELL_SIZE; 
                    y < row * CELL_SIZE + CELL_SIZE; 
                    y++) {
                pixels[x][y] = iPixels[x - (col * CELL_SIZE)]
                                      [(y - (row * CELL_SIZE))];
            }
        }
    }
    
    @Override
    public double[][][] getPixels() {
//        System.out.println("at 15,30: R=" + pixels[15][30][Util.R] 
//                                 + ", G=" + pixels[15][30][Util.G] 
//                                 + ", B=" + pixels[15][30][Util.B]);
        return pixels;
    }

    @Override
    public Rectangle getBounds() {
        bounds = new Rectangle(0, 0, cols * CELL_SIZE, rows * CELL_SIZE);
        return bounds;
    }

    @Override
    public short getDrawType() {
        return drawType;
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
        Rectangle r = obj.getCollisionBox();
        int escape = 0; 
        //if the obj was moving right nudge it left into safety.
        if (velocity > 0) {
            escape = (impactZone.x * CELL_SIZE - r.width) - r.x;
            
        //but if the obj was moving left nudge it right into safety.
        } else if (velocity < 0) {
            escape = CELL_SIZE - (r.x % CELL_SIZE);
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
        Rectangle r = obj.getCollisionBox();
        int escape = 0; 

        //if the obj was moving up nudge it down into safety.
        if (velocity > 0) {
            escape = (impactZone.y * CELL_SIZE - r.height) - r.y;
            
        //but if the obj was moving down nudge it up into safety.
        } else if (velocity < 0) {
            escape = CELL_SIZE - (r.y % CELL_SIZE);
        }
        
        //Clamp the velocity;
        if (Math.abs(escape) > Math.abs(velocity)) {
            escape = 0;
        }
        return escape;
    }

    @Override
    public boolean isDrawn() {
        //always draw the level.
        return true;
    }
}
