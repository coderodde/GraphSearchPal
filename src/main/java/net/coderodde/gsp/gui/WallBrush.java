package net.coderodde.gsp.gui;

/**
 * This class represents a wall brush used for drawing the walls in the panel.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Dec 22, 2015)
 */
final class WallBrush {
    
    private static final int DEFAULT_WIDTH = 10;
    private static final int DEFAULT_HEIGHT = 10;
    
    private int width  = DEFAULT_WIDTH;
    private int height = DEFAULT_HEIGHT;
    
    public void setWidth(int width) {
        this.width = Math.max(1, width);
    }
    
    public void setHeight(int height) {
        this.height = Math.max(1, height);
    }
    
    public int getWidth() {
        return width;
    }
    
    public int getHeight() {
        return height;
    }
}
