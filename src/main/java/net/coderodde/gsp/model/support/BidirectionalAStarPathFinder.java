package net.coderodde.gsp.model.support;

import java.util.ArrayList;
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

public class BidirectionalAStarPathFinder<N extends AbstractGraphNode<N>> 
extends AbstractPathFinder<N> {
    
    private MinimumPriorityQueue<N> OPENA;
    private MinimumPriorityQueue<N> OPENB;
    
    private Set<N> CLOSEDA;
    private Set<N> CLOSEDB;
    
    private Map<N, N> PARENTSA;
    private Map<N, N> PARENTSB;
    
    private Map<N, Double> DISTANCEA;
    private Map<N, Double> DISTANCEB;
    
    private N source;
    private N target;
    
    private final AbstractGraphWeightFunction<N> weightFunction;
    private final AbstractHeuristicFunction<N> heuristicFunction;
    
    private double bestPathLength;
    private N touchNode;
    
    public BidirectionalAStarPathFinder(
            AbstractGraphWeightFunction<N> weightFunction,
            AbstractHeuristicFunction<N> heuristicFunction) {
        Objects.requireNonNull(weightFunction, "The weight function is null.");
        Objects.requireNonNull(heuristicFunction, 
                               "The heuristic function is null.");
        this.weightFunction = weightFunction;
        this.heuristicFunction = heuristicFunction;
    }
        
    private BidirectionalAStarPathFinder(
            N source,
            N target,
            AbstractGraphWeightFunction<N> weightFunction,
            AbstractHeuristicFunction<N> heuristicFunction) {
        OPENA = getQueue() == null ? new DaryHeap<>() : getQueue().spawn();
        OPENB = OPENA.spawn();
        
        CLOSEDA = new HashSet<>();
        CLOSEDB = new HashSet<>();
        
        PARENTSA = new HashMap<>();
        PARENTSB = new HashMap<>();
        
        DISTANCEA = new HashMap<>();
        DISTANCEB = new HashMap<>();
        
        this.source = source;
        this.target = target;
        this.weightFunction = weightFunction;
        this.heuristicFunction = heuristicFunction;
        
        this.bestPathLength = Double.POSITIVE_INFINITY;
        this.touchNode = null;
    }
    
    @Override
    public List<N> search(N source, N target) {
        Objects.requireNonNull(source, "The source node is null.");
        Objects.requireNonNull(target, "The target node is null.");
        
        return new BidirectionalAStarPathFinder(source, 
                                                target,
                                                weightFunction,
                                                heuristicFunction).search();
    }
    
    private void updateForwardFrontier(N node, double nodeScore) {
        if (CLOSEDB.contains(node)) {
            double pathLength = DISTANCEB.get(node) + nodeScore;
            
            if (bestPathLength > pathLength) {
                bestPathLength = pathLength;
                touchNode = node;
            }
        }
    }
    
    private void updateBackwardFrontier(N node, double nodeScore) {
        if (CLOSEDA.contains(node)) {
            double pathLength = DISTANCEA.get(node) + nodeScore;
            
            if (bestPathLength > pathLength) {
                bestPathLength = pathLength;
                touchNode = node;
            }
        }
    }
    
    private void expandForwardFrontier() {
        N current = OPENA.extractMinimum();
        CLOSEDA.add(current);
        
        for (N child : current.children()) {
            if (!CLOSEDA.contains(child)) {
                double tentativeScore = DISTANCEA.get(current) + 
                                        weightFunction.get(current, child);
                
                if (!DISTANCEA.containsKey(child)) {
                    DISTANCEA.put(child, tentativeScore);
                    PARENTSA.put(child, current);
                    OPENA.add(child, 
                              tentativeScore + heuristicFunction
                              .estimate(child, target));
                    updateForwardFrontier(child, tentativeScore);
                } else if (DISTANCEA.get(child) > tentativeScore) {
                    DISTANCEA.put(child, tentativeScore);
                    PARENTSA.put(child, current);
                    OPENA.decreasePriority(child,
                                           tentativeScore + heuristicFunction
                                           .estimate(child, target));
                    updateForwardFrontier(child, tentativeScore);
                }
            }
        }
    }
    
    private void expandBackwardFrontier() {
        N current = OPENB.extractMinimum();
        CLOSEDB.add(current);
        
        for (N parent : current.parents()) {
            if (!CLOSEDB.contains(parent)) {
                double tentativeScore = DISTANCEB.get(current) + 
                                        weightFunction.get(parent, current);
                
                if (!DISTANCEB.containsKey(parent)) {
                    DISTANCEB.put(parent, tentativeScore);
                    PARENTSB.put(parent, current);
                    OPENB.add(parent, 
                              tentativeScore + heuristicFunction
                              .estimate(parent, source));
                    updateBackwardFrontier(parent, tentativeScore);
                } else if (DISTANCEB.get(parent) > tentativeScore) {
                    DISTANCEB.put(parent, tentativeScore);
                    PARENTSB.put(parent, current);
                    OPENB.decreasePriority(parent, 
                                           tentativeScore + heuristicFunction
                                           .estimate(parent, source));
                    updateBackwardFrontier(parent, tentativeScore);
                }
            }
        }
    }
    
    private List<N> search() {
        if (source.equals(target)) {
            // Bidirectional search algorithms cannont handle the case where
            // source and target nodes are same.
            List<N> path = new ArrayList<>(1);
            path.add(source);
            return path;
        }
        
        OPENA.add(source, 0.0);
        OPENB.add(target, 0.0);
        
        PARENTSA.put(source, null);
        PARENTSB.put(target, null);
        
        DISTANCEA.put(source, 0.0);
        DISTANCEB.put(target, 0.0);
        
        while (!OPENA.isEmpty() && !OPENB.isEmpty()) {
            if (touchNode != null) {
                N minA = OPENA.min();
                N minB = OPENB.min();
                
                double distanceA = DISTANCEA.get(minA) + 
                                   heuristicFunction.estimate(minA, target);
                
                double distanceB = DISTANCEB.get(minB) + 
                                   heuristicFunction.estimate(minB, source);
                
                if (bestPathLength <= Math.max(distanceA, distanceB)) {
                    return tracebackPath(touchNode, PARENTSA, PARENTSB);
                }
            }
            
            if (OPENA.size() + CLOSEDA.size() <
                OPENB.size() + CLOSEDB.size()) {
                expandForwardFrontier();
            } else {
                expandBackwardFrontier();
            }
        }
        
        return Collections.<N>emptyList();
    }
}