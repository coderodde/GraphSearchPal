package net.coderodde.gsp.model;

/**
 * This abstract class defines the API for graph weight functions.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Oct 18, 2015)
 */
public abstract class 
        AbstractGraphWeightFunction<N extends AbstractGraphNode<N>> {
    
    /**
     * Associates the weight {@code weight} with the edge 
     * {@code (tail, head)}.
     * 
     * @param tail   the edge tail node.
     * @param head   the edge head node.
     * @param weight the edge weight
     */
    public abstract void put(N tail, N head, double weight);
    
    /**
     * Returns the weight of the edge {@code (tail, head)}.
     * 
     * @param tail the edge tail node.
     * @param head the edge head node.
     * @return the weight of the edge.
     */
    public abstract double get(N tail, N head);
}
