package net.coderodde.gsp.model.support;

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
    
    public DirectedGraphNode(String name) {
        super(name);
        
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
    public Set<DirectedGraphNode> children() {
        return childrenWrapper;
    }
    
    @Override
    public Set<DirectedGraphNode> parents() {
        return parentWrapper;
    }
    
    @Override
    public String toString() {
        return "[" + name + "]";
    }

    @Override
    public void removeChild(DirectedGraphNode child) {
        child.parents.remove(this);
        children.remove(child);
    }
}
