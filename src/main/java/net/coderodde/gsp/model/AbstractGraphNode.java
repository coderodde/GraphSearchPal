package net.coderodde.gsp.model;

import java.util.Objects;
import java.util.Set;

/**
 * This abstract class defines the API for graph nodes.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Oct 18, 2015)
 */
public abstract class AbstractGraphNode<N extends AbstractGraphNode<N>> {
    protected final String name;
    
    public AbstractGraphNode(String name) {
        Objects.requireNonNull(name, "The node name is null.");
        this.name = name;
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
    public abstract Set<N> children();
    
    /**
     * Returns a set view of all the parent nodes of this node.
     * 
     * @return a view of parent nodes. 
     */
    public abstract Set<N> parents();
    
    @Override
    public int hashCode() {
        return name.hashCode();
    }   
    
    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        
        if (!getClass().equals(o.getClass())) {
            return false;
        }
        
        return name.equals(((N) o).name);
    }
}
