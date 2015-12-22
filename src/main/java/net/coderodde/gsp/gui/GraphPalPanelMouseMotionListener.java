package net.coderodde.gsp.gui;

import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

/**
 * This class implements the mouse motion listener for the main Graph Pal panel.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Dec 21, 2015)
 */
class GraphPalPanelMouseMotionListener implements MouseMotionListener {

    private final GraphPalPanel panel;
    
    GraphPalPanelMouseMotionListener(GraphPalPanel panel) {
        this.panel = panel;
    }
    
    @Override
    public void mouseDragged(MouseEvent e) {
        panel.setNodeAsWall(e.getX(), e.getY());
        panel.repaint();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        
    }
}
