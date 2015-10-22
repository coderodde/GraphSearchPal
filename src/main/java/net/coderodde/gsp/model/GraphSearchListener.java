package net.coderodde.gsp.model;

import java.util.List;

/**
 * This interface defines the API for listening graph search progress.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Oct 22, 2015)
 * @param <N> the actual graph node implementation type.
 */
public interface GraphSearchListener<N extends AbstractGraphNode<N>> {
    
    /**
     * Called when the graph search is about to begin.
     */
    public void begin();
    
    /**
     * Called when the node {@code node} was added to the search frontier, or
     * namely, when the node is added to the open list.
     * 
     * @param node the node being reached.
     */
    public void reached(N node);
    
    /**
     * Called when the node is removed from the search frontier (the open list)
     * and is added to the closed list.
     * 
     * @param node the node being closed.
     */
    public void closed(N node);
    
    /**
     * Called when a shortest path is computed. If the target node is not 
     * reachable from the source node, an empty node list is expected.
     * 
     * @param path the shortest path or an empty list if target not reachable.
     */
    public void done(List<N> path);
}
