package net.coderodde.gsp.model.support;

import java.util.HashMap;
import java.util.Map;
import net.coderodde.gsp.model.AbstractGraphWeightFunction;

/**
 * This class implements a weight function of an undirected graph.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Dec 20, 2015)
 */
public class UndirectedGraphWeightFunction 
extends AbstractGraphWeightFunction<UndirectedGraphNode> {

    private final Map<UndirectedGraphNode, Map<UndirectedGraphNode, Double>> map
            = new HashMap<>();
    
    @Override
    public void put(UndirectedGraphNode tail, 
                    UndirectedGraphNode head, 
                    double weight) {
        map.putIfAbsent(tail, new HashMap<>());
        map.get(tail).put(head, weight);
        
        map.putIfAbsent(head, new HashMap<>());
        map.get(head).put(tail, weight);
    }

    @Override
    public double get(UndirectedGraphNode tail, UndirectedGraphNode head) {
        return map.get(tail).get(head);
    }
}
