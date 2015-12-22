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
import net.coderodde.gsp.model.AbstractPathFinder;
import net.coderodde.gsp.model.GraphSearchListener;
import net.coderodde.gsp.model.queue.MinimumPriorityQueue;
import net.coderodde.gsp.model.queue.support.DaryHeap;

/**
 * This class implements the famous Dijkstra's shortest path algorithm.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Oct 13, 2015)
 * @param <N> the actual graph node type.
 */
public class DijkstraPathFinder<N extends AbstractGraphNode<N>> 
extends AbstractPathFinder<N> {

    private MinimumPriorityQueue<N> OPEN;
    private Set<N> CLOSED;
    private Map<N, N> PARENTS;
    private Map<N, Double> DISTANCE;
    private N target;
    private final AbstractGraphWeightFunction<N> weightFunction;
    
    public DijkstraPathFinder(AbstractGraphWeightFunction<N> weightFunction) {
        Objects.requireNonNull(weightFunction, "The weight function is null.");
        this.weightFunction = weightFunction;
        this.listener = null;
    }
    
    private DijkstraPathFinder(N source,
                               N target,
                               AbstractGraphWeightFunction<N> weightFunction,
                               GraphSearchListener<N> listener) {
        OPEN = getQueue() == null ? new DaryHeap<>() : getQueue().spawn();
        CLOSED = new HashSet<>();
        PARENTS = new HashMap<>();
        DISTANCE = new HashMap<>();
        
        OPEN.add(source, 0.0);
        PARENTS.put(source, null);
        DISTANCE.put(source, 0.0);
        
        this.target = target;
        this.weightFunction = weightFunction;
        this.listener = listener;
    }
    
    private void expand(N current) {
        for (N child : current.children()) {
            if (!CLOSED.contains(child)) {
                double tentativeCost = DISTANCE.get(current) + 
                                       weightFunction.get(current, child);

                if (!DISTANCE.containsKey(child)) {
                    DISTANCE.put(child, tentativeCost);
                    PARENTS.put(child, current);
                    OPEN.add(child, tentativeCost);
                    
                    if (listener != null) {
                        listener.reached(child);
                    }
                } else if (DISTANCE.get(child) > tentativeCost) {
                    DISTANCE.put(child, tentativeCost);
                    PARENTS.put(child, current);
                    OPEN.decreasePriority(child, tentativeCost);
                }
            }
        }
    }
    
    private List<N> search() {
        if (listener != null) {
            listener.begin();
        }
        
        System.out.println("Dijkstra!" + (listener != null));
        
        while (!OPEN.isEmpty()) {
            N current = OPEN.extractMinimum();
            
            if (current.equals(target)) {
                List<N> path = tracebackPath(current, PARENTS);
                System.out.println(listener);
                
                if (listener != null) {
                    listener.done(path);
                }
                
                return tracebackPath(current, PARENTS);
            }
            
            CLOSED.add(current);
            
            if (listener != null) {
                listener.closed(current);
            }
            
            expand(current);
        }
            
        List<N> emptyPath = Collections.<N>emptyList();
        
        if (listener != null) {
            listener.done(emptyPath);
        }
        
        return emptyPath;
    }
    
    @Override
    public List<N> search(N source, N target) {
        Objects.requireNonNull(source, "The source node is null.");
        Objects.requireNonNull(target, "The target node is null.");
        return new DijkstraPathFinder(source, target, weightFunction, super.listener).search();
    }

    @Override
    public String humanReadableName() {
        return "Dijkstra's algorithm";
    }
}
