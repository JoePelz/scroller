/** Joe Pelz, Set A, A00893517 */
package core;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import javax.swing.JPanel;

/**
 * 
 * <p>This class does the primary computation in making this 
 * side-scrolling world visible and interactive. </p>
 * @author Joe Pelz, Set A, A00893517
 * @version 1.0
 */
public class Engine extends JPanel implements Runnable {
    /** Unique version of this panel. */
    private static final long serialVersionUID = -8488309313313664194L;
    
    /** Preferred panel width. */
    private static final int WIDTH = 450;
    /** Preferred panel height. */
    private static final int HEIGHT = 450;
    /** The FPS to start at. */
    private static final int INIT_FPS = 24;
    /** Milliseconds per second. */
    private static final int MS_PER_S = 1000;
    /** Nanoseconds per second. */
    private static final long NS_PER_S = 1000000000L;
    /** Nanoseconds per millisecond. */
    private static final int NS_PER_MS = 1000000;
    
    /** Score offset distance in pixels. (x direction) */
    private static final int SCORE_PLACE_X = 20;
    /** Score offset distance in pixels. (y direction) */
    private static final int SCORE_PLACE_Y = 20;
    
    /** gravitational force (9.8 m/s). * 30 pixels per meter. */
    private static final double GRAVITY = -1200;
    /** Hero movement force. */
    private static final double SPEED = 1000;
    /** Hero jumping speed. */
    private static final double JUMP  = 600;
    /** World movement speed in pixels per click. */
    private static final int SCROLL_SPEED = 5;
    
    
    /** The thread that runs the main loop and animates the worm. */
    private Thread thread;
    /** Boolean variable to control whether the main loop is running. */
    private boolean running;
    /** frame counter. */
    private int frame;
    /** How many milliseconds between frames. */
    private long targetTime = MS_PER_S / INIT_FPS;
    
    /** The actual world to explore. */
    private World world;
    /** The camera's x position. */
    private int offX;
    /** The camera's y position. */
    private int offY;
    /** The forces applied by keypresses. */
    private Vector2D keyForce = new Vector2D(0.0, 0.0);
    /** The hero of this adventure. */
    private Hero hero;
    /** The set of textures to use. */
    private TexturePack tp = new TexturePack("/images/");
    /** The special effects used. */
    private ArrayList<Drawable> effects = new ArrayList<Drawable>();
    
    //private Graphics g;
    //private BufferedImage image;

    /**
    * Constructor: Sets up this panel and loads the images.
    */
    public Engine() {
        addKeyListener(new Controller());
        
        setBackground(Color.black);
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setFocusable(true);
        
        //init world
        world = new World(WIDTH, HEIGHT, tp);

        //init hero
        hero = new Hero();
        hero.setImage(tp.get(Texture.hero));
        //hero.setImage(Texture.hero.get());
        final int startLocation = 300;
        hero.setPos(0, startLocation);

        if (thread == null) {
            running = true;
            thread = new Thread(this);
            thread.start();
        }
    }

    /**
    * Draws the image in the current location.
    * @param page Graphics component to draw on
    */
    public void paintComponent(Graphics page) {
        super.paintComponent(page);
        setForeground(Color.cyan);
        
        world.draw(page, this, offX, offY);
        
        hero.draw(this, page, offX, offY);
        
        for (Drawable effect : effects) {
            if (effect instanceof Burst) {
                Burst pop = (Burst) effect;
                pop.draw(this, page, offX, offY);
            }
        }

        //Draw hero position information
        /*Point pos = hero.getPos();
        Dimension size = hero.getSize();
        page.drawString("hero MinX: " + pos.x, 
                SCORE_PLACE_X, 
                SCORE_PLACE_Y);
        page.drawString("hero MinY: " + pos.y, 
                SCORE_PLACE_X, 
                SCORE_PLACE_Y + 20);
        page.drawString("MaxX: " + (pos.x + size.width), 
                SCORE_PLACE_X + 100, 
                SCORE_PLACE_Y);
        page.drawString("MaxY: " + (pos.y + size.height), 
                SCORE_PLACE_X + 100, 
                SCORE_PLACE_Y + 20);
        */
        
        if (!running) {
            page.drawString("Game over :(", 
                    WIDTH / 2, 
                    HEIGHT / 2);
            page.drawString("Frame: " + frame, 
                    SCORE_PLACE_X, 
                    SCORE_PLACE_Y);
            //draw this if game stops running. 
        }
    }

    /**
     * Calculate the physics in our world for the given object.
     * @param obj The object to move
     * @param timeStep The time since last evaluation in nanoseconds
     */
    public void simulate(Dynamic obj, long timeStep) {
        /* Actions:
         * 1. apply drag
         * 2. add forces to object
         *     - gravity (unless resting on block)
         *     - arrow keys
         *     
         * 3. apply forces to object. (done by the object)
         * 
         * 4. a) move in x axis
         * 5. a) resolve collisions in x axis
         * 
         * 4. b) move in y axis
         * 5. b) resolve collisions in y axis
         */
        double seconds = timeStep / (double) NS_PER_S;
        
        // 1. apply drag
        obj.applyDrag(seconds);
        
        // 2. add forces
        obj.addForce(new Vector2D(0, GRAVITY));
        obj.addForce(keyForce);
        
        // 3. apply forces
        obj.applyForces(seconds);
        
        
        // get velocity in preparation for step 4
        Vector2D vel = obj.getVel();
        hero.setImage(tp.get(Texture.hero));

        // 4. a)  move x
        obj.move((int) (vel.x * seconds), 0);
        
        // 5. a) resolve collisions in x
        Point bad = getWorldCollision(obj, Texture.brick);
        if (bad != null) {
            hero.setImage(tp.get(Texture.heroNoise));
//            System.out.println("Fixed a X collision at " 
//                    + bad.x + "," + bad.y + ".");
            int resolution = world.escapeX(obj, vel.x * seconds, bad);
            
            obj.move(resolution, 0);
            obj.getVel().setX(0);
        }
        
        // 4. b)  move y
        obj.move(0, (int) (vel.y * seconds));

        
        // 5. b) resolve collisions in y
        bad = getWorldCollision(obj, Texture.brick);
        if (bad != null) {
            hero.setImage(tp.get(Texture.heroNoise));
//            System.out.println("Fixed a Y collision at " 
//                    + bad.x + "," + bad.y + ".");
            int resolution = world.escapeY(obj, vel.y * seconds, bad);
            
            obj.move(0, resolution);
            obj.getVel().setY(0);
        }
    }
    
    /**
     * Compare an object to the world to identify any collisions. The obj 
     * is compared to blocks it is directly overtop of. Returns null if 
     * no collisions are found.
     * @param obj The entity to test against the world.
     * @param target The block type to test for
     * @return the first collision found, if any. Null if none.
     */
    private Point getWorldCollision(Dynamic obj, Texture target) {
        Point lowerLeft = new Point(obj.getPos());
        Dimension d = obj.getSize();
        Point upperRight = new Point(lowerLeft.x + d.width - 1, 
                lowerLeft.y + d.height - 1);
        int cell = World.CELL_SIZE;
        
        lowerLeft.x /= cell;
        lowerLeft.y /= cell;
        upperRight.x /= cell;
        upperRight.y /= cell;
        
        for (int x = (lowerLeft.x); x <= (upperRight.x); x++) {
            for (int y = (lowerLeft.y); y <= (upperRight.y); y++) {
                if (world.getCell(x, y) == target) {
                    return new Point(x, y);
                }
            }
        }
        return null;
    }

    /**
     * Represents the listener for keyboard activity.
     */
    private class Controller extends KeyAdapter {
        /** Whether the <right> button is currently down. */
        private boolean right;
        /** Whether the <left> button is currently down. */
        private boolean left;
        /**
        * Responds to the user pressing arrow keys by adjusting the
        * image and image location accordingly.
        * @param event The event corresponding to the key press
        */
        public void keyPressed(KeyEvent event) {
            switch (event.getKeyCode()) {
            case KeyEvent.VK_UP:
                offY += SCROLL_SPEED;
                break;
            case KeyEvent.VK_DOWN:
                offY -= SCROLL_SPEED;
                break;
            case KeyEvent.VK_LEFT:
                offX -= SCROLL_SPEED;
                break;
            case KeyEvent.VK_RIGHT:
                offX += SCROLL_SPEED;
                break;
            /*case KeyEvent.VK_W:
                hero.move(0, 5);
                break;
            case KeyEvent.VK_S:
                hero.move(0, -5);
                break;*/
            case KeyEvent.VK_A:
                left = true;
                break;
            case KeyEvent.VK_D:
                right = true;
                break;
            case KeyEvent.VK_SPACE:
                boolean isOnGround = (hero.getVel().y == 0);
                if (isOnGround) {
                    hero.setVel(new Vector2D(hero.getVel().x, JUMP)); 
                }
                break;
            default:
                // ignore other characters
            }
            
            //add actual forces
            updateForces();
        }
        
        /**
         * This handles when the user releases a key.
         * @param event the key that was released.
         */
        public void keyReleased(KeyEvent event) {
            switch (event.getKeyCode()) {
            case KeyEvent.VK_A:
                left = false;
                break;
            case KeyEvent.VK_D:
                right = false;
                break;
            default:
                // ignore other characters
            }
            
            //add actual forces
            updateForces();
        }
        
        /**
         * checks which keys are currently pressed, 
         * and applies forces accordingly.
         */
        private void updateForces() {
            keyForce.set(0.0, 0.0);
            
            if (left && !right) {
                keyForce.offset(-SPEED, 0);
            } else if (right && !left) {
                keyForce.offset(SPEED, 0);
            }
        }
    }
    
    /**
     * The main loop, that controls all the action. 
     * It repeats until game over.
     */
    public void run() {

        long start = System.nanoTime();
        long elapsed;
        long wait;
        final int minTimeBetweenFrames = 5;

        while (running) {
            //Timing
            //elapsed is the number of nanoseconds since last frame
            elapsed = System.nanoTime() - start;
            start = System.nanoTime();
            Point pos = hero.getPos();
            frame++;
            
            
            //Physics!
            simulate(hero, elapsed);
            
            //Move camera
            updateCamPos();
            
            //Test 'victory' conditions
            if (pos.y < 0) {
                running = false;
            }
            
            //Test world events (like touching a light)
            testWorldEvents();
            
            //Delete (release) dead effects.
            deleteDeadEntities();
            
            //redraw everything
            repaint();
            
            //"elapsed" is reassigned to the number of nanoseconds 
            //since this frame began
            elapsed = System.nanoTime() - start;
            
            //wait is how much time is remaining 
            //before the next frame needs to be drawn.
            wait = targetTime - elapsed / NS_PER_MS;
            
            //wait at least 5 ms between frames.
            if (wait < 0) {
                wait = minTimeBetweenFrames;
            }
            try {
                Thread.sleep(wait);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        } //end while
    } // end run

    /**
     * Tests if the hero triggers world events, like by touching a light.
     */
    private void testWorldEvents() {
        Point light = getWorldCollision(hero, Texture.bgLight);
        if ((light != null)) {
            Drawable burst = new Burst(tp);
            burst.setPos(light.x * World.CELL_SIZE + World.CELL_SIZE / 2, 
                         light.y * World.CELL_SIZE + World.CELL_SIZE / 2);
            world.setCell(light.x, light.y, Texture.bgLightDead);
            effects.add(burst);
        }
    }

    /**
     * Removes dead effects and other inactive entities.
     */
    private void deleteDeadEntities() {
        ArrayList<Drawable> deadFX = new ArrayList<Drawable>();
        
        for (Drawable effect : effects) {
            if (effect instanceof Burst) {
                Burst pop = (Burst) effect;
                if (pop.isDead()) {
                    deadFX.add((Drawable) pop);
                }
            }
        }
        effects.removeAll(deadFX);
    }

    /**
     * Move the camera so that our hero doesn't go off-screen.
     */
    private void updateCamPos() {
        final int marginX = (int) (getWidth() * 0.25);
        final int marginTop = (int) (getHeight() * 0.2);
        final int marginBottom = (int) (getHeight() * 0.2);
        Point pos = hero.getPos();
        Dimension dim = hero.getSize();

        //      update x motion
        // If too far left
        if (pos.x - marginX < offX) {
            offX = pos.x - marginX;
        //if too far right
        } else if (pos.x + marginX > offX + getWidth() - dim.width) {
            offX = pos.x + marginX - getWidth() + dim.width;
        }
        //      update y motion
        //If too low
        if (pos.y - marginBottom < offY) {
            offY = pos.y - marginBottom;
        //If too high
        } else if (pos.y + marginTop > offY + getHeight() - dim.height) {
            offY = pos.y + marginTop - getHeight() + dim.height;
        }
    }
}
