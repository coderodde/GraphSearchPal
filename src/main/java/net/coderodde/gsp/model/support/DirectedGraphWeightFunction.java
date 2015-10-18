package net.coderodde.gsp.model.support;

import java.util.HashMap;
import java.util.Map;
import net.coderodde.gsp.model.AbstractGraphWeightFunction;

/**
 * This class implements a directed graph weight function.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Oct 13, 2015)
 */
public class DirectedGraphWeightFunction extends AbstractGraphWeightFunction<DirectedGraphNode>{
    
    private final Map<DirectedGraphNode,
                      Map<DirectedGraphNode, Double>> map = new HashMap<>();
    
    /**
     * {@inheritDoc }
     */
    @Override
    public void put(DirectedGraphNode tail,
                    DirectedGraphNode head, 
                    double weight) {
        map.putIfAbsent(tail, new HashMap<>());
        map.get(tail).put(head, weight);
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public double get(DirectedGraphNode tail,
                      DirectedGraphNode head) {
        return map.get(tail).get(head);
    }
}
