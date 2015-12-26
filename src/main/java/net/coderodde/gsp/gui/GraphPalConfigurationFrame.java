package net.coderodde.gsp.gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;
import net.coderodde.gsp.model.support.DijkstraPathFinder;
import javax.swing.JCheckBox;

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
        final JLabel heuristicLabel       = new JLabel("Heuristic function:");
        final JLabel degreeLable          = new JLabel("Degree:");
        final JLabel allowDiagonalEdgesLabel = 
                new JLabel("Allow diagonal edges:");
        
        final JLabel crossCornersLabel    = new JLabel("Cross corners:");
        final JLabel wallBrushSizeLabel   = new JLabel("Wall brush size:");
        final JLabel pathCostLabel        = new JLabel("Path cost:");
        final JLabel pathLengthLabel      = new JLabel("Path length:");
        final JLabel timeLabel            = new JLabel("Time:");
        final JLabel closedSizeLabel      = new JLabel("Closed size:");
        final JLabel peakOpenSizeLabel    = new JLabel("Peak open size:");
        final JLabel heapDegreeLabel      = new JLabel("Heap degree:");
    }
    
    private static final class Panels {
        final JPanel algorithmPanel  = new JPanel();
        final JPanel graphPanel      = new JPanel();
//        final JPanel edgePanel       = new JPanel();
//        final JPanel brushPanel      = new JPanel();
//        final JPanel controlPanel    = new JPanel();
//        final JPanel statisticsPanel = new JPanel();
//        final JPanel colorPanel      = new JPanel();
    }
    
    private final Labels labels = new Labels();
    private final Panels panels = new Panels();
    
    private final JTabbedPane tabbedPane = new JTabbedPane();
    
    private final JComboBox algorithmComboBox;
    private final JComboBox heapComboBox;
    private final JComboBox heuristicFunctionComboBox;
    private final JSpinner degreeSpinner;
    private final JPanel tabHolderPanel = new JPanel();
    private final JCheckBox allowDiagonalEdgesBox = new JCheckBox();
    private final JCheckBox allowCrossingCornersBox = new JCheckBox();
    
    public GraphPalConfigurationFrame(String[] algorithmNames,
                                      String[] heapNames,
                                      String[] heuristicFunctionNames) {
        super(TITLE);
        
        this.algorithmComboBox = new JComboBox(algorithmNames);
        this.heapComboBox = new JComboBox(heapNames);
        this.heuristicFunctionComboBox = new JComboBox(heuristicFunctionNames);
        this.degreeSpinner = new JSpinner();
        
        degreeSpinner.setModel(new SpinnerNumberModel(2, 
                                                      2, 
                                                      Integer.MAX_VALUE,
                                                      1));
        
        panels.algorithmPanel.setLayout(new GridLayout(4, 2, 10, 10));
        panels.algorithmPanel.add(labels.algorithmChoiceLabel);
        panels.algorithmPanel.add(algorithmComboBox);
        panels.algorithmPanel.add(labels.heapChoiceLabel);
        panels.algorithmPanel.add(heapComboBox);
        panels.algorithmPanel.add(labels.degreeLable);
        panels.algorithmPanel.add(degreeSpinner);
        panels.algorithmPanel.add(labels.heuristicLabel);
        panels.algorithmPanel.add(heuristicFunctionComboBox);
        
        panels.graphPanel.setLayout(new GridLayout(4, 2, 10, 10));
        panels.graphPanel.add(labels.allowDiagonalEdgesLabel);
        panels.graphPanel.add(allowDiagonalEdgesBox);
        panels.graphPanel.add(labels.crossCornersLabel);
        panels.graphPanel.add(allowCrossingCornersBox);
        
        JLabel dummy = new JLabel();
        
        panels.graphPanel.add(dummy);
        panels.graphPanel.add(dummy);
        panels.graphPanel.add(dummy);
        panels.graphPanel.add(dummy);
        
        tabbedPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        tabbedPane.addTab("Algorithms", panels.algorithmPanel);
        tabbedPane.addTab("Graph", panels.graphPanel);
//        tabbedPane.addTab("Graph", panels.edgePanel);
//        tabbedPane.addTab("Colors", rootPane);
        
//        this.setLayout(new GridLayout(6, 1));
//        
//        panels.algorithmPanel.setLayout(new GridLayout(2, 2, 10, 10));
//        panels.algorithmPanel.add(labels.algorithmChoiceLabel);
//        
//        JComboBox algorithmBox = new JComboBox(algorithmNames);
//        panels.algorithmPanel.add(algorithmBox);
//        
//        panels.algorithmPanel.add(labels.heapChoiceLabel);
//        
//        JComboBox heapBox = new JComboBox(heapNames);
//        panels.algorithmPanel.add(heapBox);
//        
//        this.getContentPane().add(panels.algorithmPanel);
        tabHolderPanel.add(tabbedPane);
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(tabHolderPanel);
        this.pack();
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.setVisible(true);
    }
}
