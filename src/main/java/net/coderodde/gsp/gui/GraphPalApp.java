package net.coderodde.gsp.gui;

import net.coderodde.gsp.gui.GraphPalPanel;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.Scanner;
import javax.swing.JFrame;
import net.coderodde.gsp.model.AbstractPathFinder;
import net.coderodde.gsp.model.support.DijkstraPathFinder;
import net.coderodde.gsp.model.support.GridGraphConfiguration;
import net.coderodde.gsp.model.support.GridGraphEuclideanHeuristicFunction;
import net.coderodde.gsp.model.support.NewBidirectionalAStarPathFinder;
import net.coderodde.gsp.model.support.UndirectedGraphNode;

/**
 * This class manages the main frame of Graph Search Pal.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 - alpha_1 (Oct 13, 2015)
 */
public class GraphPalApp {
    
    private static final String VERSION = "1.6 - alpha_1";
    private JFrame frame;
    private GraphPalPanel panel;
    
    public GraphPalApp() {
        new GraphPalConfigurationFrame(
                Algorithm.getAlgorithmNames(),
                Heap.getHeapNames(),
                HeuristicFunction.getHeuristicFunctionNames());
        
//        long startTime = System.currentTimeMillis();
//        
//        Dimension screenDimension = 
//                Toolkit.getDefaultToolkit().getScreenSize();
//        
//        this.frame = new JFrame("Graph Search Pal " + VERSION);
//        this.frame.setSize(screenDimension);
//        this.panel = new GraphPalPanel();
//        
//        GraphPalPanelMouseMotionListener motionListener = 
//                new GraphPalPanelMouseMotionListener(this.panel);
//        
//        this.panel.addMouseMotionListener(motionListener);
//        this.panel.addMouseListener(motionListener);
//        
//        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        this.frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
//        this.frame.getContentPane().add(panel);
//        this.frame.setResizable(false);
//        this.frame.setVisible(true);
//        
//        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
//        
//        System.out.println("Screen size: " + dim);
//        System.out.println("Width:  " + panel.getWidth());
//        System.out.println("Height: " + panel.getHeight());
//       
//        GridGraphConfiguration configuration = new GridGraphConfiguration();
//        
//        configuration.setAllowDiagonals(false);
//        
//        panel.createGridGraph(panel.getWidth(),
//                              panel.getHeight(),
//                              configuration);
//        
//        long endTime = System.currentTimeMillis();
//        System.out.println("Init: " + (endTime - startTime) + " milliseconds.");
//                              
//        panel.getWallBrush().setHeight(20);
//        panel.getWallBrush().setWidth(20);
//        
//        Scanner scanner = new Scanner(System.in);
//        
//        System.out.println("type sumthing... ");
//        
//        scanner.next();
//        
//        System.out.println("Yiihaaa!");
//        
//        panel.runSearch(new NewBidirectionalAStarPathFinder<>(panel.getWeightFunction(),
//                            new GridGraphEuclideanHeuristicFunction()));
//        panel.runSearch(new DijkstraPathFinder(panel.getWeightFunction()));
        
//        new GraphPalConfigurationFrame(
//                new String[]{ 
//                    "Dijkstra",
//                    "Bidirectional Dijkstra",
//                    "A*",
//                    "BHPA",
//                    "NBA*",
//                    "Parallel NBA*"
//                },
//                new String[]{
//                    "d-ary",
//                    "Binomial",
//                    "Fibonacci",
//                    "Pairing"
//                }
//        );
        
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
