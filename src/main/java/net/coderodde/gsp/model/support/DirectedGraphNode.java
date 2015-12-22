package net.coderodde.gsp.model.support;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import net.coderodde.gsp.model.AbstractGraphNode;

/**
 * This class implements a directed graph node.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Oct 13, 2015)
 */
public class DirectedGraphNode extends AbstractGraphNode<DirectedGraphNode> {
    
    private final Set<DirectedGraphNode> children;
    private final Set<DirectedGraphNode> parents;
    
    private final Set<DirectedGraphNode> childrenWrapper;
    private final Set<DirectedGraphNode> parentWrapper;
    
    public DirectedGraphNode(int id) {
        super(id);
        
        this.children = new LinkedHashSet<>();
        this.parents  = new LinkedHashSet<>();
        
        this.childrenWrapper = Collections.unmodifiableSet(children);
        this.parentWrapper   = Collections.unmodifiableSet(parents);
    }
    
    @Override
    public void addChild(DirectedGraphNode child) {
        children.add(child);
        child.parents.add(this);
    }
    
    @Override
    public boolean hasChild(DirectedGraphNode childCandidate) {
        return children.contains(childCandidate);
    }
    
    @Override
    public Collection<DirectedGraphNode> children() {
        return childrenWrapper;
    }
    
    @Override
    public Collection<DirectedGraphNode> parents() {
        return parentWrapper;
    }
    
    @Override
    public String toString() {
        return "[DirectedGraphNode " + id + "]";
    }

    @Override
    public void removeChild(DirectedGraphNode child) {
        child.parents.remove(this);
        children.remove(child);
    }

    @Override
    public void clear() {
        for (DirectedGraphNode child : children) {
            child.parents.remove(this);
        }
        
        for (DirectedGraphNode parent : parents) {
            parent.children.remove(this);
        }
        
        children.clear();
        parents.clear();
    }
}
