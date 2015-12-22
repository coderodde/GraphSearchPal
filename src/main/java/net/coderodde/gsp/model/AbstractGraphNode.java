package net.coderodde.gsp.model;

import java.util.Collection;
import java.util.Objects;
import java.util.Set;

/**
 * This abstract class defines the API for graph nodes.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Oct 18, 2015)
 * @param <N> the actual graph node implementation type.
 */
public abstract class AbstractGraphNode<N extends AbstractGraphNode<N>> {
    
    protected final int id;
    
    public AbstractGraphNode(int id) {
        this.id = id;
    }
    
    /**
     * Makes {@code child} a child node of this node.
     * 
     * @param child the child node.
     */
    public abstract void addChild(N child);
    
    /**
     * Checks whether {@code child} is actually a child node of this node.
     * 
     * @param child the node to check.
     * @return {@code true} if {@code child} is a child node of this node.
     */
    public abstract boolean hasChild(N child);
    
    /**
     * Removes {@code child} from the child list of this node.
     * 
     * @param child the node to remove from the child list.
     */
    public abstract void removeChild(N child);
    
    /**
     * Returns a set view of all the children nodes of this node.
     * 
     * @return a view of child nodes.
     */
    public abstract Collection<N> children();
    
    /**
     * Returns a set view of all the parent nodes of this node.
     * 
     * @return a view of parent nodes. 
     */
    public abstract Collection<N> parents();
    
    /**
     * Removes all the edges incident on this node.
     */
    public abstract void clear();
    
    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }   
    
    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        
        if (!getClass().equals(o.getClass())) {
            return false;
        }
        
        return id == ((N) o).id;
    }
}
