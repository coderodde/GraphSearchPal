package net.coderodde.gsp.model;

/**
 * This interface defines the API for heuristic functions.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Oct 13, 2015)
 */
public interface HeuristicFunction {
    
    public double estimate(DirectedGraphNode source, DirectedGraphNode target);
}
