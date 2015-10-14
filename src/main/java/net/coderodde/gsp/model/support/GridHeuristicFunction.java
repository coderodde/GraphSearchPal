package net.coderodde.gsp.model.support;

import java.awt.Point;
import java.util.HashMap;
import java.util.Map;
import net.coderodde.gsp.model.HeuristicFunction;
import net.coderodde.gsp.model.DirectedGraphNode;

/**
 * This class implements a heuristic function over grid graphs.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Oct 13, 2015)
 */
public class GridHeuristicFunction implements HeuristicFunction {

    private static final double SQRT2 = Math.sqrt(2.0);
    private final Map<DirectedGraphNode, Point> map = new HashMap<>();
    
    public void put(DirectedGraphNode node, Point point) {
        map.put(node, new Point(point));
    }
    
    @Override
    public double estimate(DirectedGraphNode source, DirectedGraphNode target) {
        Point sourcePoint = map.get(source);
        Point targetPoint = map.get(target);
        
        int dx = Math.abs(sourcePoint.x - targetPoint.x);
        int dy = Math.abs(sourcePoint.y - targetPoint.y);
        
        return Math.min(dx, dy) * SQRT2 + Math.max(dx, dy) - Math.min(dx, dy);
    }
}
