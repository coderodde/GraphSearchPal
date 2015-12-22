package net.coderodde.gsp.gui;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

/**
 * This class implements the mouse motion listener for the main Graph Pal panel.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Dec 21, 2015)
 */
class GraphPalPanelMouseMotionListener implements MouseMotionListener, 
                                                  MouseListener {

    private final GraphPalPanel panel;
    private Point draggedEndPoint;
    
    GraphPalPanelMouseMotionListener(GraphPalPanel panel) {
        this.panel = panel;
    }
    
    @Override
    public void mouseDragged(MouseEvent e) {
        int currentX = e.getX();
        int currentY = e.getY();
        
        if (draggedEndPoint != null) {
            draggedEndPoint.x = currentX;
            draggedEndPoint.y = currentY;
        } else {
            panel.setNodeAsWall(currentX, currentY);
        }
        
        panel.repaint();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        
    }

    @Override
    public void mousePressed(MouseEvent e) {
        Point p = panel.getSourcePoint();
        
        int currentX = e.getX();
        int currentY = e.getY();
        
        if (p.distance(currentX, currentY) < 5.0) {
            draggedEndPoint = p;
            return;
        }
        
        p = panel.getTargetPoint();
        
        if (p.distance(currentX, currentY) < 5.0) {
            draggedEndPoint = p;
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        draggedEndPoint = null;
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        
    }

    @Override
    public void mouseExited(MouseEvent e) {
        draggedEndPoint = null;
    }
}
