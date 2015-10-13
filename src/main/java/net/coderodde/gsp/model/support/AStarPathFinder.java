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
import net.coderodde.gsp.model.HeuristicFunction;
import net.coderodde.gsp.model.PathFinder;
import net.coderodde.gsp.model.queue.MinimumPriorityQueue;
import net.coderodde.gsp.model.queue.support.DaryHeap;

/**
 * This class implements A* pathfinding algorithm.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Oct 13, 2015)
 */
public class AStarPathFinder extends PathFinder {

    private HeuristicFunction heuristicFunction;
    private MinimumPriorityQueue<DirectedGraphNode> queue;
    
    public AStarPathFinder(HeuristicFunction heuristicFunction,
                       MinimumPriorityQueue<DirectedGraphNode> queue) {
        this.heuristicFunction = heuristicFunction;
        this.queue = queue;
    }
    
    public AStarPathFinder(HeuristicFunction heuristicFunction) {
        this(heuristicFunction, null);
    }
    
    @Override
    public List<DirectedGraphNode> 
        search(DirectedGraphNode source, 
               DirectedGraphNode target, 
               DirectedGraphWeightFunction weightFunction) {
        Objects.requireNonNull(source, "The source node is null.");
        Objects.requireNonNull(target, "The target node is null.");
        Objects.requireNonNull(weightFunction, "The weight function is null.");
            
        MinimumPriorityQueue<DirectedGraphNode> OPEN = getQueue() == null ?
                                                       new DaryHeap<>() :
                                                       getQueue().spawn();
        Set<DirectedGraphNode> CLOSED = new HashSet<>();
        Map<DirectedGraphNode, DirectedGraphNode> parentMap = new HashMap<>();
        Map<DirectedGraphNode, Double> distanceMap = new HashMap<>();
        
        OPEN.add(source, heuristicFunction.estimate(source, target));
        parentMap.put(source, null);
        distanceMap.put(source, 0.0);
        
        while (!OPEN.isEmpty()) {
            DirectedGraphNode current = OPEN.extractMinimum();
            
            if (current.equals(target)) {
                return tracebackPath(current, parentMap);
            }
            
            CLOSED.add(current);
            
            for (DirectedGraphNode child : current.children()) {
                if (!CLOSED.contains(child)) {
                    double tentativeCost = distanceMap.get(current) + 
                                           weightFunction.get(current, child);
                    
                    if (!distanceMap.containsKey(child)) {
                        distanceMap.put(child, tentativeCost);
                        parentMap.put(child, current);
                        OPEN.add(child, 
                                 tentativeCost + 
                                 heuristicFunction.estimate(child, target));
                    } else if (distanceMap.get(child) > tentativeCost) {
                        distanceMap.put(child, tentativeCost);
                        parentMap.put(child, current);
                        OPEN.decreasePriority(
                                child, 
                                tentativeCost +
                                heuristicFunction.estimate(child, target));
                    }
                }
            }
        }
        
        return Collections.<DirectedGraphNode>emptyList();    
        }
}
