package net.coderodde.gsp.gui;

import net.coderodde.gsp.gui.GraphPalPanel;
import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.JFrame;
import net.coderodde.gsp.model.AbstractPathFinder;
import net.coderodde.gsp.model.support.DijkstraPathFinder;
import net.coderodde.gsp.model.support.GridGraphConfiguration;
import net.coderodde.gsp.model.support.UndirectedGraphNode;

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
    private final ProgressFrame progressFrame = new ProgressFrame();
    
    public GraphPalApp() {
        Dimension screenDimension = 
                Toolkit.getDefaultToolkit().getScreenSize();
        
        this.frame = new JFrame("Graph Search Pal " + VERSION);
        this.frame.setSize(screenDimension);
        this.panel = new GraphPalPanel();
        
        GraphPalPanelMouseMotionListener motionListener = 
                new GraphPalPanelMouseMotionListener(this.panel);
        
        this.panel.addMouseMotionListener(motionListener);
        this.panel.addMouseListener(motionListener);
        
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        this.frame.getContentPane().add(panel);
        this.frame.setResizable(false);
        this.frame.setVisible(true);
        
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        
        System.out.println("Screen size: " + dim);
        System.out.println("Width:  " + panel.getWidth());
        System.out.println("Height: " + panel.getHeight());
       
        GridGraphConfiguration configuration = new GridGraphConfiguration();
        
        panel.setProgressFrame(progressFrame);
        panel.createGridGraph(panel.getWidth(),
                              panel.getHeight(),
                              configuration);
        System.out.println("fdsafdsa");
                              
        panel.getWallBrush().setHeight(40);
        panel.getWallBrush().setWidth(40);
        
        new GraphPalConfigurationFrame();
        
//        AbstractPathFinder<UndirectedGraphNode> finder =
//                new DijkstraPathFinder<>(panel.getWeightFunction());
//        
//        finder.setGraphSearchListener(panel);
//        
//        panel.runSearch(panel.getNode(20, 20),
//                        panel.getNode(400, 400),
//                        finder);
//        
//        try {
//            for (int i = 10; i > 0; --i) {
//                System.out.println("fds " + i);
//                Thread.sleep(1000L);
//            }
//        } catch (InterruptedException ex) {
//            
//        }
    }
}
