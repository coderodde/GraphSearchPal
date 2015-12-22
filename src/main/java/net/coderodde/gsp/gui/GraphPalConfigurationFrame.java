package net.coderodde.gsp.gui;

import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * This class implements a window for configuration controls.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Dec 22, 2015)
 */
public final class GraphPalConfigurationFrame extends JFrame {
    
    private static final String TITLE = "Configuration";
    
    private static final class Labels {
        final JLabel algorithmChoiceLabel = new JLabel("Algorithm:");
        final JLabel heapChoiceLabel      = new JLabel("Heap:");
        
        final JLabel allowDiagonalEdgesLabel = 
                new JLabel("Allow diagonal edges:");
        
        final JLabel crossCornersLabel    = new JLabel("Cross corners:");
        final JLabel wallBrushSizeLabel   = new JLabel("Wall brush size:");
        final JLabel pathCostLabel        = new JLabel("Path cost:");
        final JLabel pathLengthLabel      = new JLabel("Path length:");
        final JLabel timeLabel            = new JLabel("Time:");
        final JLabel closedSizeLabel      = new JLabel("Closed size:");
        final JLabel peakOpenSizeLabel    = new JLabel("Peak open size:");
    }
    
    private static final class Panels {
        final JPanel algorithmPanel  = new JPanel();
        final JPanel edgePanel       = new JPanel();
        final JPanel brushPanel      = new JPanel();
        final JPanel controlPanel    = new JPanel();
        final JPanel statisticsPanel = new JPanel();
        final JPanel colorPanel      = new JPanel();
    }
    
    private final Labels labels = new Labels();
    private final Panels panels = new Panels();
    
    public GraphPalConfigurationFrame() {
        super(TITLE);
        
        this.setLayout(new GridLayout(6, 1));
        
        panels.algorithmPanel.setLayout(new GridLayout(2, 2));
        panels.algorithmPanel.add(labels.algorithmChoiceLabel);
        panels.algorithmPanel.add(new JButton("FDs"));
        panels.algorithmPanel.add(labels.heapChoiceLabel);
        panels.algorithmPanel.add(new JButton("FSFDSFD"));
        
        this.getContentPane().add(panels.algorithmPanel);
        this.pack();
        this.setVisible(true);
    }
}
