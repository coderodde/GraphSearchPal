package net.coderodde.gsp.model.support;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import net.coderodde.gsp.model.DirectedGraphNode;
import net.coderodde.gsp.model.DirectedGraphWeightFunction;
import net.coderodde.gsp.model.PathFinder;
import net.coderodde.gsp.model.queue.MinimumPriorityQueue;
import net.coderodde.gsp.model.queue.support.DaryHeap;

/**
 * This class implements the famous Dijkstra's shortest path algorithm.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Oct 13, 2015)
 */
public class DijkstraPathFinder extends PathFinder {

    private MinimumPriorityQueue<DirectedGraphNode> OPEN;
    private Set<DirectedGraphNode> CLOSED;
    private Map<DirectedGraphNode, DirectedGraphNode> PARENTS;
    private Map<DirectedGraphNode, Double> DISTANCE;
    private DirectedGraphNode target;
    private DirectedGraphWeightFunction weightFunction;
    
    public DijkstraPathFinder() {
        setQueue(new DaryHeap<>(2));
    }
    
    private DijkstraPathFinder(DirectedGraphNode source,
                               DirectedGraphNode target,
                               DirectedGraphWeightFunction weightFunction) {
        OPEN = getQueue() == null ? new DaryHeap<>() : getQueue().spawn();
        CLOSED = new HashSet<>();
        PARENTS = new HashMap<>();
        DISTANCE = new HashMap<>();
        
        OPEN.add(source, 0.0);
        PARENTS.put(source, null);
        DISTANCE.put(source, 0.0);
        
        this.target = target;
        this.weightFunction = weightFunction;
    }
    
    private void expand(DirectedGraphNode current) {
        for (DirectedGraphNode child : current.children()) {
            if (!CLOSED.contains(child)) {
                double tentativeCost = DISTANCE.get(current) + 
                                       weightFunction.get(current, child);

                if (!DISTANCE.containsKey(child)) {
                    DISTANCE.put(child, tentativeCost);
                    PARENTS.put(child, current);
                    OPEN.add(child, tentativeCost);
                } else if (DISTANCE.get(child) > tentativeCost) {
                    DISTANCE.put(child, tentativeCost);
                    PARENTS.put(child, current);
                    OPEN.decreasePriority(child, tentativeCost);
                }
            }
        }
    }
    
    private List<DirectedGraphNode> search() {
        while (!OPEN.isEmpty()) {
            DirectedGraphNode current = OPEN.extractMinimum();
            
            if (current.equals(target)) {
                return tracebackPath(current, PARENTS);
            }
            
            CLOSED.add(current);
            expand(current);
        }
            
        return Collections.<DirectedGraphNode>emptyList();
    }
    
    @Override
    public List<DirectedGraphNode> 
        search(DirectedGraphNode source, 
               DirectedGraphNode target, 
               DirectedGraphWeightFunction weightFunction) {
        Objects.requireNonNull(source, "The source node is null.");
        Objects.requireNonNull(target, "The target node is null.");
        Objects.requireNonNull(weightFunction, "The weight function is null.");
            
        return new DijkstraPathFinder(source, target, weightFunction).search();
    }
}
