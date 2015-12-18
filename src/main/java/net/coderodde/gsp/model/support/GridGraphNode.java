package net.coderodde.gsp.model.support;

import java.util.Set;
import net.coderodde.gsp.model.AbstractGraphNode;

/**
 *
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Dec 18, 2015)
 */
public class GridGraphNode extends AbstractGraphNode<GridGraphNode> {

    private final int x;
    private final int y;
    
    public GridGraphNode(int x, int y) {
        super("(" + x + ", " + y + ")");
        this.x = x;
        this.y = y;
    }
    
    @Override
    public void addChild(GridGraphNode child) {
        if (child.x == x && child.y == y) {
            throw new IllegalArgumentException("Requesting a self-loop.");
        }
        
        if (Math.abs(x - child.x) > 1) {
            throw new IllegalArgumentException(
                    "The input node is not a neighbor of this node.");
        }
        
        if (Math.abs(y - child.y) > 1) {
            throw new IllegalArgumentException(
                    "The input node is not a neighbor of this node.");
        }
    }

    @Override
    public boolean hasChild(GridGraphNode child) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void removeChild(GridGraphNode child) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Set<GridGraphNode> children() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Set<GridGraphNode> parents() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
