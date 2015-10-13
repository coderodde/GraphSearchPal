package net.coderodde.gsp.model;

import java.util.HashMap;
import java.util.Map;

/**
 * This class implements a directed graph weight function.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Oct 13, 2015)
 */
public class DirectedGraphWeightFunction {
    
    private final Map<DirectedGraphNode,
                      Map<DirectedGraphNode, Double>> map = new HashMap<>();
    
    /**
     * Sets the weight for arc {@code (tail, head}.
     * 
     * @param tail   the tail node of the arc.
     * @param head   the head node of the arc.
     * @param weight the weight of the arc. 
     */
    public void put(DirectedGraphNode tail,
                    DirectedGraphNode head, 
                    double weight) {
        map.putIfAbsent(tail, new HashMap<DirectedGraphNode, Double>());
        map.get(tail).put(head, weight);
    }
    
    /**
     * Returns the weight of the arc {@code (tail, head)}.
     * 
     * @param tail the tail node of the arc.
     * @param head the head node of the arc.
     * @return the weight of the input arc.
     */
    public double get(DirectedGraphNode tail,
                      DirectedGraphNode head) {
        return map.get(tail).get(head);
    }
}
