package net.coderodde.gsp.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import net.coderodde.gsp.model.queue.MinimumPriorityQueue;

/**
 * This interface defines the API and a couple of utility functions for path
 * finding algorithms.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Oct 13, 2015)
 */
public abstract class PathFinder {
    
    protected MinimumPriorityQueue<DirectedGraphNode> queue;
    
    /**
     * Performs a shortest path search from {@code source} to {@code target} 
     * using {@code weightFunction} as a weight function.
     * 
     * @param source         the source node.
     * @param target         the target node.
     * @return a list of nodes constituting the shortest path from 
     *         {@code source} to {@code target}, or an empty list if 
     *         {@code target} is not reachable from {@code source}.
     */
    public abstract List<DirectedGraphNode> search(DirectedGraphNode source, 
                                                   DirectedGraphNode target);
       
    public MinimumPriorityQueue<DirectedGraphNode> getQueue() {
        return queue;
    }
        
    public void setQueue(MinimumPriorityQueue<DirectedGraphNode> queue) {
        this.queue = queue;
    }
        
    /**
     * Constructs a path found by a bidirectional pathfinding algorithm.
     * 
     * @param touch    the node where the two search frontiers "meet".
     * @param parentsA the parent map in forward search.
     * @param parentsB the parent map in backward search.
     * @return a shortest path.
     */
    public List<DirectedGraphNode> 
        tracebackPath(DirectedGraphNode touch,
                      Map<DirectedGraphNode, DirectedGraphNode> parentsA,
                      Map<DirectedGraphNode, DirectedGraphNode> parentsB) {
        DirectedGraphNode current = touch;
        List<DirectedGraphNode> path = new ArrayList<>();
        
        while (current != null) {
            path.add(current);
            current = parentsA.get(current);
        }
        
        Collections.<DirectedGraphNode>reverse(path);
        
        if (parentsB != null) {
            current = parentsB.get(touch);
            
            while (current != null) {
                path.add(current);
                current = parentsB.get(current);
            }
        }
        
        return path;
    }
        
    /**
     * Constructs a path found by unidirectional pathfinding algorithms.
     * 
     * @param target  the target node.
     * @param parents the parent map.
     * @return a shortest path.
     */
    public List<DirectedGraphNode> 
        tracebackPath(DirectedGraphNode target,
                      Map<DirectedGraphNode, DirectedGraphNode> parents) {
        return tracebackPath(target, parents, null);
    }
}
