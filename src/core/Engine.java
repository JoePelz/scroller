/** Joe Pelz, Set A, A00893517 */
package core;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import javax.swing.JPanel;

import core.creatures.Hero;
import core.effects.Burst;
import core.props.Light;
import core.world.Level;
import core.world.RandomLevel;
import core.world.World;

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
    /** Longest allowable time passage between frames. */
    private static final int MAX_FRAME = 50000000;
    
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
    /** How far you can fall before game over. */
    private static final int LOWER_BOUND = -100;
    
    
    /** The thread that runs the main loop and animates the worm. */
    private Thread thread;
    /** Boolean variable to control whether the main loop is running. */
    private boolean running;
    /** frame counter. */
    private int frame;
    /** How many milliseconds between frames. */
    private long targetTime = MS_PER_S / INIT_FPS;
    
    /** The rendering engine. */
    private Renderer renderer = new Renderer();
    /** The actual world to explore. */
    private Level world;
    /** The camera's x position. */
    private int offX;
    /** The camera's y position. */
    private int offY;
    /** The forces applied by key presses. */
    private Vector2D keyForce = new Vector2D(0.0, 0.0);
    /** The hero of this adventure. */
    private Hero hero;
    /** The set of textures to use. */
    private TexturePack tp = new TexturePack("/images/");
    /** The special effects used. */
    private ArrayList<Entity> effects = new ArrayList<Entity>();
    /** The triggerable events in use. */
    private ArrayList<Trigger> triggers = new ArrayList<Trigger>();
    
    /**
    * Constructor: Sets up this panel and loads the images.
    */
    public Engine() {
        addKeyListener(new Controller());
        
        setBackground(Color.black);
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setFocusable(true);
        
        //init world
        final int defWidth = 150;
        final int defHeight = 15;
//        world = RandomLevel.genWorldHills(defWidth, defHeight, tp);
//        world = RandomLevel.genWorldPlatform(defWidth, defHeight, tp);
        world = RandomLevel.genWorldRandom(defWidth, defHeight, tp);
//        world = new Level("house.txt", tp);

        //init hero
        hero = new Hero();
        hero.setImage(tp.get(Texture.hero));
        hero.setPos(world.getStart());
        renderer.addDynProp(hero);
        renderer.setWorld(world);
        
        //init test light
        Point[] bgLights = world.getAll(Texture.bgLightDead);
        Light tempLight;
        final int cso = 15; //Cell size offset.
        final int rRange = 100;
        final int rMin = 75;
        
        for (Point light : bgLights) {
            tempLight = new Light(light.x + cso, light.y + cso, world);
            tempLight.setRadius((short) (Math.random() * rRange + rMin));
            renderer.addStaticProp(tempLight);
            triggers.add(tempLight);
        }

        //Don't start this loop until setup is complete! 
        if (thread == null) {
            running = true;
            thread = new Thread(this);
            thread.start();
        }
    }

    /**
    * Draws everything onto the main panel.
    * @param page Graphics component to draw on
    */
    public void paintComponent(Graphics page) {
        super.paintComponent(page);
        setForeground(Color.cyan);
        
        renderer.draw(page, this, offX, offY);
        
//        hero.draw(this, page, offX, offY);
        
        for (Entity effect : effects) {
            if (effect instanceof Burst) {
                Burst pop = (Burst) effect;
                pop.draw(this, page, offX, offY);
            }
        }
        
        //Draw hero position information
        Rectangle r = hero.getBounds();
        page.drawString("Position: x = " + (r.x + (r.width >> 1)), 
                SCORE_PLACE_X, 
                SCORE_PLACE_Y);
        final int tab = 100; 
        final int lineHeight = 20;
        page.drawString("y = " + (r.y + (r.height >> 1)), 
                SCORE_PLACE_X + tab, 
                SCORE_PLACE_Y);
        page.drawString("Frame: " + frame, 
                SCORE_PLACE_X, 
                SCORE_PLACE_Y + lineHeight);
        
        
        if (!running) {
            page.drawString("Game over :(", 
                    WIDTH / 2, 
                    HEIGHT / 2);
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
//        bad = null;
        if (bad != null) {
            int resolution = world.escapeX(obj, vel.x * seconds, bad);

            obj.move(resolution, 0);
            obj.getVel().setX(0);
        }
        
        // 4. b)  move y
        obj.move(0, (int) (vel.y * seconds));

        
        // 5. b) resolve collisions in y
        bad = getWorldCollision(obj, Texture.brick);
//        bad = null;
        if (bad != null) {
            int resolution = world.escapeY(obj, vel.y * seconds, bad);
            if (vel.y < 0) {
                hero.setOnGround(true);
                hero.setImage(tp.get(Texture.heroGround));
            }
            obj.move(0, resolution);
            obj.getVel().setY(0);
        } else {
            hero.setOnGround(false);
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
        Rectangle r = obj.getCollisionBox();
        Point lowerLeft = new Point(r.x, r.y);
        Point upperRight = new Point(r.x + r.width - 1, 
                r.y + r.height - 1);
        int cell = World.CELL_SIZE;

        //If x is negative, offset by 1. It sucks but is necessary.
        if (lowerLeft.x < 0) {
            lowerLeft.x -= cell;
        }
        if (upperRight.x < 0) {
            upperRight.x -= cell;
        }
        
        //Convert pixel coordinates to world coordinates.
        lowerLeft.x /= cell;
        lowerLeft.y /= cell;
        upperRight.x /= cell;
        upperRight.y /= cell;
        
        //Test for overlap with the target block type
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
        /** Whether the <up> button is currently down. */
        private boolean up;
        /** Whether the <down> button is currently down. */
        private boolean down;
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
            case KeyEvent.VK_W:
                up = true;
                break;
            case KeyEvent.VK_S:
                down = true;
                break;
            case KeyEvent.VK_A:
                left = true;
                break;
            case KeyEvent.VK_D:
                right = true;
                break;
            case KeyEvent.VK_SPACE:
                if (hero.isOnGround()) {
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
            case KeyEvent.VK_W:
                up = false;
                break;
            case KeyEvent.VK_S:
                down = false;
                break;
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

            if (up && !down) {
                keyForce.offset(0, SPEED);
            } else if (down && !up) {
                keyForce.offset(0, -SPEED);
            }
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
            elapsed = Math.min(MAX_FRAME, elapsed);
            start = System.nanoTime();
            Point pos = hero.getPos();
            frame++;
            
            
            //Physics!
            simulate(hero, elapsed);
            
            //Move camera
            updateCamPos();
            
            //Test 'victory' conditions
            if (pos.y < LOWER_BOUND) {
                running = false;
            }
            
            //Test world events (like touching a light)
            testTriggers(triggers);
            
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
     * @param trigs A list of triggers to test.
     */
    private void testTriggers(ArrayList<Trigger> trigs) {
        for (Trigger trigger : trigs) {
            if (trigger.isTriggered(hero.getBounds())) {
                trigger.triggerAction();
                Entity burst = new Burst(tp);
                burst.setPos(((Light) trigger).getPos());
                effects.add(burst);
                
                if (trigger instanceof Drawable) {  
                    renderer.invalidate(((Drawable) trigger).getBounds());
                }
            }
        }
    }

    /**
     * Removes dead effects and other inactive entities.
     */
    private void deleteDeadEntities() {
        ArrayList<Entity> deadFX = new ArrayList<Entity>();
        
        for (Entity effect : effects) {
            if (effect instanceof Burst) {
                Burst pop = (Burst) effect;
                if (pop.isDead()) {
                    deadFX.add((Entity) pop);
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
