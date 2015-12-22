package net.coderodde.gsp.model.support;

import java.util.HashMap;
import java.util.Map;
import net.coderodde.gsp.model.AbstractGraphWeightFunction;

/**
 * This class implements a weight function of a grid graph.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Dec 22, 2015)
 */
public class GridGraphWeightFunction
extends AbstractGraphWeightFunction<GridGraphNode> {

    public static final double SQRT2 = Math.sqrt(2.0);
    
    @Override
    public void put(GridGraphNode tail, GridGraphNode head, double weight) {
        throw new UnsupportedOperationException(
                "The edges of GridGraphNodes are implicitly weighted.");
    }

    @Override
    public double get(GridGraphNode tail, GridGraphNode head) {
        int dx = Math.abs(tail.getX() - head.getX());
        
        if (dx > 1) {
            throw new IllegalArgumentException(
                    "The head and the tail nodes are not adjacent.");
        }
        
        int dy = Math.abs(tail.getY() - head.getY());
        
        if (dx > 1) {
            throw new IllegalArgumentException(
                    "The head and the tail nodes are not adjacent.");
        }
        
        if (dx == 0) {
            return dy == 1 ? 1.0 : 0.0;
        }
        
        return dy == 1 ? SQRT2 : 1.0;
    }
}
