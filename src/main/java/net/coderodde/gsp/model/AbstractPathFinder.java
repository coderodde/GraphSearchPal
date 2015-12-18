package net.coderodde.gsp.model;

import net.coderodde.gsp.model.support.DirectedGraphNode;
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
 * @param <N>
 */
public abstract class AbstractPathFinder<N extends AbstractGraphNode<N>> {
    
    protected GraphSearchListener<N> listener;
    protected MinimumPriorityQueue<N> queue;
    
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
    public abstract List<N> search(N source, N target);
       
    public MinimumPriorityQueue<N> getQueue() {
        return queue;
    }
     
    public void setGraphSearchListener(GraphSearchListener<N> listener) {
        this.listener = listener;
    }
    
    public AbstractPathFinder<N> setQueue(MinimumPriorityQueue<N> queue) {
        this.queue = queue;
        return this;
    }
        
    /**
     * Constructs a path found by a bidirectional pathfinding algorithm.
     * 
     * @param touch    the node where the two search frontiers "meet".
     * @param parentsA the parent map in forward search.
     * @param parentsB the parent map in backward search.
     * @return a shortest path.
     */
    public List<N> tracebackPath(N touch,
                                 Map<N, N> parentsA, 
                                 Map<N, N> parentsB) {
        N current = touch;
        List<N> path = new ArrayList<>();
        
        while (current != null) {
            path.add(current);
            current = parentsA.get(current);
        }
        
        Collections.<N>reverse(path);
        
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
    public List<N> tracebackPath(N target, Map<N, N> parents) {
        return tracebackPath(target, parents, null);
    }
}
