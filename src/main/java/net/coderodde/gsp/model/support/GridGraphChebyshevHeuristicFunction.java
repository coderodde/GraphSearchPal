package net.coderodde.gsp.model.support;

import net.coderodde.gsp.model.AbstractHeuristicFunction;

/**
 * This class implements a heuristic function based on the
 * <a href="https://en.wikipedia.org/wiki/Chebyshev_distance">Chebyshev 
 * distance</a>.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Dec 25, 2015)
 */
public class GridGraphChebyshevHeuristicFunction 
extends AbstractHeuristicFunction<GridGraphNode> {

    @Override
    public double estimate(GridGraphNode source, GridGraphNode target) {
        double dx = Math.abs(source.getX() - target.getX());
        double dy = Math.abs(source.getY() - target.getY());
        return Math.max(dx, dy);
    }
}
