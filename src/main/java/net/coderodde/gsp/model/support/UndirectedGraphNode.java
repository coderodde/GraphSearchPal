package net.coderodde.gsp.model.support;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import net.coderodde.gsp.model.AbstractGraphNode;

/**
 * This class implements a undirected graph node. It is more space-efficient 
 * than {@link net.coderodde.gsp.model.support.DirectedGraphNode} by a factor of
 * about 2.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Dec 20, 2015)
 */
public class UndirectedGraphNode 
extends AbstractGraphNode<UndirectedGraphNode> {

    private final Set<UndirectedGraphNode> children = new LinkedHashSet<>();
    private final Set<UndirectedGraphNode> childrenWrapper = 
         Collections.<UndirectedGraphNode>unmodifiableSet(children);
    
    public UndirectedGraphNode(int id) {
        super(id);
    }
    
    @Override
    public void addChild(UndirectedGraphNode child) {
        children.add(child);
        child.children.add(this);
    }

    @Override
    public boolean hasChild(UndirectedGraphNode child) {
        return children.contains(child);
    }

    @Override
    public void removeChild(UndirectedGraphNode child) {
        children.remove(child);
    }

    @Override
    public Collection<UndirectedGraphNode> children() {
        return childrenWrapper;
    }

    @Override
    public Collection<UndirectedGraphNode> parents() {
        return childrenWrapper;
    }

    @Override
    public void clear() {
        for (UndirectedGraphNode neighbor : children) {
            neighbor.children.remove(this);
        }
        
        children.clear();
    }
}
