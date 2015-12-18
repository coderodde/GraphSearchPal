package net.coderodde.gsp;

import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.JFrame;
import net.coderodde.gsp.model.AbstractPathFinder;
import net.coderodde.gsp.model.support.DirectedGraphNode;

/**
 * This class manages the main frame of Graph Search Pal.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 - alpha_1 (Oct 13, 2015)
 */
public class GraphPal {
    
    private static final String VERSION = "1.6 - alpha_1";
    private final JFrame frame;
    private final GraphPanel panel;
    
    public GraphPal() {
        this.frame = new JFrame("Graph Search Pal " + VERSION);
        this.panel = new GraphPanel();
        System.out.println("fds");
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        this.frame.getContentPane().add(panel);
        this.frame.setVisible(true);
    }
    
    public void runShortestPathFinder(
            AbstractPathFinder<DirectedGraphNode> finder) {
        
    }
}
