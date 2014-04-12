/** Joe Pelz, Set A, A00893517 */
package core.world;

import java.awt.Point;
import java.util.Random;

import core.Texture;

/**
 * <p>This is a static class that generates random levels for play.</p>
 * @author Joe Pelz, Set A, A00893517
 * @version 1.0
 */
public class RandomLevel {
    /** Random number generator for level building. */
    private static final Random GEN = new Random();
    /** Probability of a light generating. */
    private static final double P_LIGHT = 0.15;
    /** Height range for random lights. */
    private static final int LIGHT_RANGE = 3;
    /** Minimum height above ground for random lights. */
    private static final int LIGHT_MIN = 2;
    
    
    /**
     * Populate the world with random simple blocks.
     * @param cols The number of columns (wide) the level is.
     * @param rows The number of rows (tall) the level is.
     * @return The generated level.
     */
    public static Level genWorldRandom(int cols, int rows) {
        /** Brick density. */
        final double pBrick = 0.8;
        /** Probability of a light in the bg. */
        final double pLight = 0.02;
        Level level = new Level(cols, rows);
        
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                if (GEN.nextDouble() * row < pBrick) {
                    //solid bricks
                    level.setCell(col, row, Texture.brick);
                } else {
                    //background bricks
                    if (GEN.nextDouble() < pLight) {
                        level.setCell(col, row, Texture.bgLight);
                    } else {
                        level.setCell(col, row, Texture.bg);
                    }
                }
            }
        }

        level.setStart(new Point(0, rows));
        return level;
    }

    /**
     * Populate the world with a flat(ish) landscape.
     * @param cols The number of columns (wide) the level is.
     * @param rows The number of rows (tall) the level is.
     * @return The generated level.
     */
    public static Level genWorldHills(int cols, int rows) {
        //Chance for ground level to rise
        final double pUp = 0.2;
        //Chance for ground level to drop
        final double pDown = 0.2;

        //probability of a hole in the world
        final double pHole = 0.1;
        //base probability the hole is bigger than 1 block 
        //(pblty diminishes with each additional block)
        final double pHoleIsBig = 0.5;
        
        Level level = new Level(cols, rows);


        boolean hole = false;
        boolean lit = false;
        int litEl = 0;
        double direction = 0.0;
        int height = 1;
        double pBigHole = 0.0;

        for (int col = 0; col < cols; col++) {
            //move up, down, or stay at the same height.
            direction = GEN.nextDouble();
            if (direction < pUp) {
                height = Math.min(height + 1, rows - 1);
            } else if (direction < pUp + pDown) {
                height = Math.max(height - 1, 1);
            }

            //determine if there should be a hole. 
            if (!hole) {
                hole = (GEN.nextDouble() < pHole);
                pBigHole = pHoleIsBig;
            } else {
                hole = (GEN.nextDouble() < pBigHole);
                pBigHole /= 2.0;
            }

            lit = (GEN.nextDouble() < P_LIGHT);

            if (lit) {
                litEl = GEN.nextInt(LIGHT_RANGE) + LIGHT_MIN;
                litEl = Math.min(height + litEl, rows - 1);
            }

            for (int row = 0; row < rows; row++) {
                level.setCell(col, row, (row < height && !hole) 
                                        ? Texture.brick 
                                        : Texture.bg);
                if (lit && row == litEl) {
                    level.setCell(col, litEl, Texture.bgLight);
                }
            }
        }

        //ensure the player starts on a brick.
        level.setCell(0, 0, Texture.brick);
        level.setStart(new Point(0, rows));
        return level;
    }


    /**
     * Populate the world with a platform sequence landscape.
     * @param cols The number of columns (wide) the level is.
     * @param rows The number of rows (tall) the level is.
     * @return The generated level.
     */
    public static Level genWorldPlatform(int cols, int rows) {
        //initialize an empty world
        Level level = new Level(cols, rows);

        final double pHole = 0.3;
        Point pos = new Point(0, 0);
        int litEl = 0;
        int xDist = 0;

        for (int col = 0; col < cols; col++) {
            //place a brick, if the current column has a brick
            if (col == pos.x) {
                level.setCell(pos.x, pos.y, Texture.brick);

                if (GEN.nextDouble() > pHole) {
                    pos.x += 1;
                } else {
                    xDist = GEN.nextInt(6) + 2; //(2 - 7)
                    if (xDist == 7 && pos.y <= 1) {
                        xDist = 6;
                    }
                    pos.x += xDist;
                    switch (xDist) {
                    case 2:
                    case 3:
                    case 4:
                        pos.y += GEN.nextInt(7) - 3; //(-3 : 3)
                        break;
                    case 5:
                        pos.y += GEN.nextInt(6) - 3; //(-3 : 2)
                        break;
                    case 6:
                        pos.y += GEN.nextInt(4) - 2; //(-2 : 1)
                        break;
                    case 7:
                        pos.y += GEN.nextInt(3) - 3; // (-3 : -1)
                        break;
                    default:
                        break;
                    }
                }

                if (pos.y > rows - 1) {
                    pos.y = rows - 1;
                } else if (pos.y < 0) {
                    pos.y = 0;
                }
            }

            //place a light
            if (GEN.nextDouble() < P_LIGHT) {
                litEl = GEN.nextInt(LIGHT_RANGE) + LIGHT_MIN;
                litEl = Math.min(pos.y + litEl, rows - 1);
                level.setCell(col, litEl, Texture.bgLight);
            }
        }
        level.setStart(new Point(0, rows));
        return level;
    }

    /**
     * Populate the world with fragments from a text file.
     * @param cols The number of columns (wide) the level is.
     * @param rows The number of rows (tall) the level is.
     */
    public static void genWorldFragments(int cols, int rows) {
        Level testLev = new Level(cols, rows);

        Level testLev3 = new Level("fragments.txt");
    }
}
