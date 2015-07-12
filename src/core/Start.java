package core;

import javax.swing.JFrame;

/**
 * <p>Scroll sideways!</p>
 * @author Joe Pelz
 * @version 1.0
 */
public class Start {
    /**
    * Creates and displays the application frame.
    * @param args Unused
    */
    public static void main(String[] args) {
        JFrame frame = new JFrame("Direction");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.getContentPane().add(new Engine());

        frame.pack();
        frame.setVisible(true);
    }
}
	