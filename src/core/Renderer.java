/** Joe Pelz, Set A, A00893517 */
package core;

import java.util.ArrayList;

import core.creatures.Hero;
import core.props.Light;
import core.world.Level;

/**
 * <p></p>
 * @author Joe Pelz, Set A, A00893517
 * @version 1.0
 */
public class Renderer {
    private static final byte R = 0;
    private static final byte G = 1;
    private static final byte B = 2;
    private static final byte A = 3;
    
    //store the background
    private Level background;
    //store the level
    private Level world; 
    //store the hero
    private Hero hero;
    //store the creatures
    //private ArrayList<Creature> creatures;
    //store the props
    //private ArrayList<Prop> prop;
    //store the lights
    private ArrayList<Light> lights;
    //store the effects
//    private ArrayList<Effect> effects;
    
}
