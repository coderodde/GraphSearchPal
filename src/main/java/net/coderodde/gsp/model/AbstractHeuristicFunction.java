package net.coderodde.gsp.model;

/**
 * This interface defines the API for heuristic functions.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Oct 13, 2015)
 * @param <N> the actual graph node type.
 */
public abstract class 
        AbstractHeuristicFunction<N extends AbstractGraphNode<N>> {
    
    /**
     * Returns a distance estimate for a path from {@code source} to 
     * {@code target}.
     * 
     * @param source the source node.
     * @param target the target node.
     * @return a shortest path estimate for a path from {@code source} to 
     *         {@code target}.
     */
    public abstract double estimate(N source, N target);
}
