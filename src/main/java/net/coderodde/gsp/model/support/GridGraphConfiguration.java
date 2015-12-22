package net.coderodde.gsp.model.support;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * This class implements a grid graph configuration object.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Dec 22, 2015)
 */
public class GridGraphConfiguration {
   
    private boolean allowDiagonals = true;
    private boolean crossCorners = true;
    private final Set<GridGraphNode> wallNodeSet = new LinkedHashSet<>();
    
    public void markAsWall(GridGraphNode node) {
        wallNodeSet.add(node);
    }
    
    public void unmarkAsWall(GridGraphNode node) {
        wallNodeSet.remove(node);
    }
    
    public boolean isWallNode(GridGraphNode node) {
        return wallNodeSet.contains(node);
    }
    
    public void clear() {
        wallNodeSet.clear();
    }
    
    public void setAllowDiagonals(boolean allowDiagonals) {
        this.allowDiagonals = allowDiagonals;
    }
    
    public void setCrossCorners(boolean crossCorners) {
        this.crossCorners = crossCorners;
    }
    
    public boolean diagonalsAllowed() {
        return allowDiagonals;
    }
    
    public boolean crossingCornersAllowed() {
        return crossCorners;
    }
}
