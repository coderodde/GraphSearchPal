package net.coderodde.gsp.model.support;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import net.coderodde.gsp.model.AbstractGraphNode;
import net.coderodde.gsp.model.AbstractGraphWeightFunction;
import net.coderodde.gsp.model.AbstractHeuristicFunction;
import net.coderodde.gsp.model.AbstractPathFinder;
import net.coderodde.gsp.model.queue.MinimumPriorityQueue;
import net.coderodde.gsp.model.queue.support.DaryHeap;

/**
 * This class implements A* pathfinding algorithm.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Oct 13, 2015)
 * @param <N> the actual graph node implementation type.
 */
public class AStarPathFinder<N extends AbstractGraphNode<N>> 
extends AbstractPathFinder<N> {

    private MinimumPriorityQueue<N> OPEN;
    private Set<N> CLOSED;
    private Map<N, N> PARENTS;
    private Map<N, Double> DISTANCE;
    private N target;
    
    private final AbstractGraphWeightFunction<N> weightFunction;
    private final AbstractHeuristicFunction<N> heuristicFunction;
    
    public AStarPathFinder(AbstractGraphWeightFunction<N> weightFunction,
                           AbstractHeuristicFunction heuristicFunction) {
        Objects.requireNonNull(weightFunction, "The weight function is null.");
        Objects.requireNonNull(heuristicFunction,
                               "The heuristic function is null.");
        this.weightFunction = weightFunction;
        this.heuristicFunction = heuristicFunction;
    }
    
    @Override
    public List<N> search(N source, N target) {
        Objects.requireNonNull(source, "The source node is null.");
        Objects.requireNonNull(target, "The target node is null.");
        
        return new AStarPathFinder(source, 
                                   target, 
                                   weightFunction, 
                                   heuristicFunction).search();
    }

    private AStarPathFinder(N source,
                            N target,
                            AbstractGraphWeightFunction<N> weightFunction,
                            AbstractHeuristicFunction<N> heuristicFunction) {
        OPEN = getQueue() == null ? new DaryHeap<>() : getQueue().spawn();
        CLOSED = new HashSet<>();
        PARENTS = new HashMap<>();
        DISTANCE = new HashMap<>();
        
        OPEN.add(source, heuristicFunction.estimate(source, target));
        PARENTS.put(source, null);
        DISTANCE.put(source, 0.0);
        
        this.target = target;
        this.weightFunction = weightFunction;
        this.heuristicFunction = heuristicFunction;
    }
    
    private void expand(N current) {
        for (N child : current.children()) {
            if (!CLOSED.contains(child)) {
                double tentativeCost = DISTANCE.get(current) + 
                                       weightFunction.get(current, child);

                if (!DISTANCE.containsKey(child)) {
                    DISTANCE.put(child, tentativeCost);
                    PARENTS.put(child, current);
                    OPEN.add(child, tentativeCost + 
                            heuristicFunction.estimate(child, target));
                } else if (DISTANCE.get(child) > tentativeCost) {
                    DISTANCE.put(child, tentativeCost);
                    PARENTS.put(child, current);
                    OPEN.decreasePriority(child, tentativeCost +
                            heuristicFunction.estimate(child, target));
                }
            }
        }
    }
    
    private List<N> search() {
        while (!OPEN.isEmpty()) {
            N current = OPEN.extractMinimum();
            
            if (current.equals(target)) {
                return tracebackPath(current, PARENTS);
            }
            
            CLOSED.add(current);
            expand(current);
        }
            
        return Collections.<N>emptyList();
    }
}
