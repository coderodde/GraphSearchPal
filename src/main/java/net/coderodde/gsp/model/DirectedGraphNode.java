package net.coderodde.gsp.model;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

/**
 * This class implements a directed graph node.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Oct 13, 2015)
 */
public class DirectedGraphNode {
    
    private final String name;
    
    private final Set<DirectedGraphNode> children;
    private final Set<DirectedGraphNode> parents;
    
    private final Set<DirectedGraphNode> childrenWrapper;
    private final Set<DirectedGraphNode> parentWrapper;
    
    public DirectedGraphNode(String name) {
        Objects.requireNonNull(name, "The node name is null.");
        this.name = name;
        
        this.children = new LinkedHashSet<>();
        this.parents  = new LinkedHashSet<>();
        
        this.childrenWrapper = Collections.unmodifiableSet(children);
        this.parentWrapper   = Collections.unmodifiableSet(parents);
    }
    
    public void addChild(DirectedGraphNode child) {
        children.add(child);
        child.parents.add(this);
    }
    
    public boolean hasChild(DirectedGraphNode childCandidate) {
        return children.contains(childCandidate);
    }
    
    public Set<DirectedGraphNode> children() {
        return childrenWrapper;
    }
    
    public Set<DirectedGraphNode> parents() {
        return parentWrapper;
    }
    
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
        
        return name.equals(((DirectedGraphNode) o).name);
    }
    
    @Override
    public String toString() {
        return "[" + name + "]";
    }
}
