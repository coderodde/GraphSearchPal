package net.coderodde.gsp.model.support;

import java.util.ArrayList;
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

public class BidirectionalAStarPathFinder extends PathFinder {
    
    private MinimumPriorityQueue<DirectedGraphNode> OPENA;
    private MinimumPriorityQueue<DirectedGraphNode> OPENB;
    
    private Set<DirectedGraphNode> CLOSEDA;
    private Set<DirectedGraphNode> CLOSEDB;
    
    private Map<DirectedGraphNode, DirectedGraphNode> PARENTSA;
    private Map<DirectedGraphNode, DirectedGraphNode> PARENTSB;
    
    private Map<DirectedGraphNode, Double> DISTANCEA;
    private Map<DirectedGraphNode, Double> DISTANCEB;
    
    private DirectedGraphNode source;
    private DirectedGraphNode target;
    
    private final DirectedGraphWeightFunction weightFunction;
    private final HeuristicFunction heuristicFunction;
    
    private double bestPathLength;
    private DirectedGraphNode touchNode;
    
    public BidirectionalAStarPathFinder(
            DirectedGraphWeightFunction weightFunction,
            HeuristicFunction heuristicFunction) {
        Objects.requireNonNull(weightFunction, "The weight function is null.");
        Objects.requireNonNull(heuristicFunction, 
                               "The heuristic function is null.");
        this.weightFunction = weightFunction;
        this.heuristicFunction = heuristicFunction;
    }
        
    private BidirectionalAStarPathFinder(
            DirectedGraphNode source,
            DirectedGraphNode target,
            DirectedGraphWeightFunction weightFunction,
            HeuristicFunction heuristicFunction) {
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
    public List<DirectedGraphNode> search(DirectedGraphNode source, 
                                          DirectedGraphNode target) {
        Objects.requireNonNull(source, "The source node is null.");
        Objects.requireNonNull(target, "The target node is null.");
        
        return new BidirectionalAStarPathFinder(source, 
                                                target,
                                                weightFunction,
                                                heuristicFunction).search();
    }
    
    private void updateForwardFrontier(DirectedGraphNode node,
                                       double nodeScore) {
        if (CLOSEDB.contains(node)) {
            double pathLength = DISTANCEB.get(node) + nodeScore;
            
            if (bestPathLength > pathLength) {
                bestPathLength = pathLength;
                touchNode = node;
            }
        }
    }
    
    private void updateBackwardFrontier(DirectedGraphNode node,
                                        double nodeScore) {
        if (CLOSEDA.contains(node)) {
            double pathLength = DISTANCEA.get(node) + nodeScore;
            
            if (bestPathLength > pathLength) {
                bestPathLength = pathLength;
                touchNode = node;
            }
        }
    }
    
    private void expandForwardFrontier() {
        DirectedGraphNode current = OPENA.extractMinimum();
        CLOSEDA.add(current);
        
        for (DirectedGraphNode child : current.children()) {
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
        DirectedGraphNode current = OPENB.extractMinimum();
        CLOSEDB.add(current);
        
        for (DirectedGraphNode parent : current.parents()) {
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
    
    private List<DirectedGraphNode> search() {
        if (source.equals(target)) {
            // Bidirectional search algorithms cannont handle the case where
            // source and target nodes are same.
            List<DirectedGraphNode> path = new ArrayList<>(1);
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
                DirectedGraphNode minA = OPENA.min();
                DirectedGraphNode minB = OPENB.min();
                
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
        
        return Collections.<DirectedGraphNode>emptyList();
    }
}