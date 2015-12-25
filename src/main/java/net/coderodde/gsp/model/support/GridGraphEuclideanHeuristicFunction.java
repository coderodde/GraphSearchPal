package net.coderodde.gsp.model.support;

import net.coderodde.gsp.model.AbstractHeuristicFunction;

/**
 * This class implements a heuristic function based on the Euclidean metric 
 * between two nodes.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Dec 25, 2015)
 */
public class GridGraphEuclideanHeuristicFunction
extends AbstractHeuristicFunction<GridGraphNode> {

    @Override
    public double estimate(GridGraphNode source, GridGraphNode target) {
        double dx = source.getX() - target.getX();
        double dy = source.getY() - target.getY();
        return Math.sqrt(dx * dx + dy * dy);
    }
}
