package net.coderodde.gsp;

import javax.swing.JFrame;

/**
 * This class manages the main frame of Graph Search Pal.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 - alpha_1 (Oct 13, 2015)
 */
public class GraphPal {
    
    private static final String VERSION = "1.6 - alpha_1";
    private final JFrame frame;
    
    public GraphPal() {
        this.frame = new JFrame("Graph Search Pal " + VERSION);
    }
}
