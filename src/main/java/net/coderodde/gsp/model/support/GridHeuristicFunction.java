package net.coderodde.gsp.model.support;

import java.awt.Point;
import java.util.HashMap;
import java.util.Map;
import net.coderodde.gsp.model.AbstractHeuristicFunction;

/**
 * This class implements a heuristic function over grid graphs.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Oct 13, 2015)
 */
public class GridHeuristicFunction 
extends AbstractHeuristicFunction<DirectedGraphNode> {

    private static final double SQRT2 = Math.sqrt(2.0);
    private final Map<DirectedGraphNode, Point> map = new HashMap<>();
    
    /**
     * Associates {@code node} with the point {@code point}.
     * 
     * @param node  a graph node.
     * @param point the coordinates of {@code node}.
     */
    public void put(DirectedGraphNode node, Point point) {
        map.put(node, new Point(point));
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public double estimate(DirectedGraphNode source, DirectedGraphNode target) {
        Point sourcePoint = map.get(source);
        Point targetPoint = map.get(target);
        
        int dx = Math.abs(sourcePoint.x - targetPoint.x);
        int dy = Math.abs(sourcePoint.y - targetPoint.y);
        
        return Math.min(dx, dy) * SQRT2 + Math.max(dx, dy) - Math.min(dx, dy);
    }
}
