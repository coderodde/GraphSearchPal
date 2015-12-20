package net.coderodde.gsp.gui;

import net.coderodde.gsp.gui.GraphPalPanel;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import javax.swing.JFrame;
import net.coderodde.gsp.gui.data.DataModel;
import net.coderodde.gsp.model.AbstractPathFinder;
import net.coderodde.gsp.model.support.DijkstraPathFinder;
import net.coderodde.gsp.model.support.DirectedGraphNode;
import net.coderodde.gsp.model.support.DirectedGraphWeightFunction;

/**
 * This class manages the main frame of Graph Search Pal.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 - alpha_1 (Oct 13, 2015)
 */
public class GraphPalApp {
    
    private static final String VERSION = "1.6 - alpha_1";
    private final JFrame frame;
    private final GraphPalPanel panel;
    
    public GraphPalApp() {
        Dimension screenDimension = 
                Toolkit.getDefaultToolkit().getScreenSize();
        
        this.frame = new JFrame("Graph Search Pal " + VERSION);
        this.frame.setSize(screenDimension);
        this.panel = new GraphPalPanel();
        
//        this.dataModel = new DataModel(screenDimension.width,
//                                       screenDimension.height,
//                                       null);
        
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        this.frame.getContentPane().add(panel);
        this.frame.setVisible(true);
    }
    
    public void runShortestPathFinder(
            AbstractPathFinder<DirectedGraphNode> finder) {
        this.panel.begin();
        
//        DijkstraPathFinder finder = new DijkstraPathFinder();
    }
    
    private static final class SearchThread extends Thread {
        
        private final AbstractPathFinder<DirectedGraphNode> finder;
        private final DirectedGraphNode source;
        private final DirectedGraphNode target;
        
        SearchThread(AbstractPathFinder<DirectedGraphNode> finder,
                     DirectedGraphNode source,
                     DirectedGraphNode target,
                     DirectedGraphWeightFunction weightFunction) {
            this.finder = finder;
            this.source = source;
            this.target = target;
        }
        
        @Override
        public void run() {
            finder.search(source, target);
        }
    }
    
}
