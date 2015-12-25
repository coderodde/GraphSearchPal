package net.coderodde.gsp.model.support;

import net.coderodde.gsp.model.AbstractHeuristicFunction;


/**
 * This class implements a heuristic function based on the octile distance.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Dec 25, 2015)
 */
public class GridGraphOctileHeuristicFunction 
extends AbstractHeuristicFunction<GridGraphNode> {

    private static final double FACTOR = Math.sqrt(2.0) - 1.0;
    
    @Override
    public double estimate(GridGraphNode source, GridGraphNode target) {
        double dx = Math.abs(source.getX() - target.getX());
        double dy = Math.abs(source.getY() - target.getY());
        return Math.max(dx, dy) + FACTOR * Math.min(dx, dy);
    }
}
