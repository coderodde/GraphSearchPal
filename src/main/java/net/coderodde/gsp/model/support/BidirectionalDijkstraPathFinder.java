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

public class BidirectionalDijkstraPathFinder extends PathFinder {

    public BidirectionalDijkstraPathFinder() {
        
    }
    
    public BidirectionalDijkstraPathFinder
        (MinimumPriorityQueue<DirectedGraphNode> queue) {
        this.queue = queue;
    }
    
    @Override
    public List<DirectedGraphNode> 
        search(DirectedGraphNode source, 
               DirectedGraphNode target,
               DirectedGraphWeightFunction weightFunction) {
        Objects.requireNonNull(source, "The source node is null.");
        Objects.requireNonNull(target, "The target node is null.");
        Objects.requireNonNull(weightFunction, "The weight function is null.");
        
        MinimumPriorityQueue<DirectedGraphNode> OPENA = 
                getQueue() == null ?
                new DaryHeap<>() :
                getQueue().spawn();
        
        MinimumPriorityQueue<DirectedGraphNode> OPENB = OPENA.spawn();
        
        Set<DirectedGraphNode> CLOSEDA = new HashSet<>();
        Set<DirectedGraphNode> CLOSEDB = new HashSet<>();
        
        Map<DirectedGraphNode, DirectedGraphNode> PARENTSA = new HashMap<>();
        Map<DirectedGraphNode, DirectedGraphNode> PARENTSB = new HashMap<>();
        
        Map<DirectedGraphNode, Double> DISTANCEA = new HashMap<>();
        Map<DirectedGraphNode, Double> DISTANCEB = new HashMap<>();
        
        OPENA.add(source, 0.0);
        OPENB.add(target, 0.0);
        
        PARENTSA.put(source, null);
        PARENTSB.put(target, null);
        
        DISTANCEA.put(source, 0.0);
        DISTANCEB.put(target, 0.0);
        
        DirectedGraphNode touch = null;
        double m = Double.POSITIVE_INFINITY;
        
        
        while (!OPENA.isEmpty() && !OPENB.isEmpty()) {
            double mtmp = DISTANCEA.get(OPENA.min()) +
                          DISTANCEB.get(OPENB.min());
            
            if (mtmp >= m) {
                return tracebackPath(touch, PARENTSA, PARENTSB);
            }
            
            if (DISTANCEA.get(OPENA.min()) < DISTANCEB.get(OPENB.min())) {
                // Expand the forward frontier.
                DirectedGraphNode current = OPENA.extractMinimum();
                CLOSEDA.add(current);
                
                for (DirectedGraphNode child : current.children()) {
                    if (CLOSEDA.contains(child)) {
                        continue;
                    }
                    
                    double tentativeScore = DISTANCEA.get(current) +
                                            weightFunction.get(current, child);
                    
                    if (!DISTANCEA.containsKey(child)) {
                        DISTANCEA.put(child, tentativeScore);
                        PARENTSA.put(child, current);
                        OPENA.add(child, tentativeScore);
                        
                        if (CLOSEDB.contains(child)) {
                            double pathLength = tentativeScore + 
                                                DISTANCEB.get(child);
                            
                            if (m > pathLength) {
                                m = pathLength;
                                touch = child;
                            }
                        }
                    } else if (DISTANCEA.get(child) > tentativeScore) {
                        DISTANCEA.put(child, tentativeScore);
                        PARENTSA.put(child, current);
                        OPENA.decreasePriority(child, tentativeScore);
                        
                        if (CLOSEDB.contains(child)) {
                            double pathLength = tentativeScore +
                                                DISTANCEB.get(child);
                            
                            if (m > pathLength) {
                                m = pathLength;
                                touch = child;
                            }
                        }
                    }
                }
            } else {
                DirectedGraphNode current = OPENB.extractMinimum();
                CLOSEDB.add(current);
                
                for (DirectedGraphNode parent : current.parents()) {
                    if (CLOSEDB.contains(parent)) {
                        continue;
                    }
                    
                    double tentativeScore = DISTANCEB.get(current) + 
                                            weightFunction.get(current, parent);
                    
                    if (!DISTANCEB.containsKey(parent)) {
                        DISTANCEB.put(parent, tentativeScore);
                        PARENTSB.put(parent, current);
                        OPENB.add(parent, tentativeScore);
                        
                        if (CLOSEDA.contains(parent)) {
                            double pathLength = tentativeScore +
                                                DISTANCEA.get(parent);
                            
                            if (m > pathLength) {
                                m = pathLength;
                                touch = parent;
                            }
                        }
                    } else if (DISTANCEB.get(parent) > tentativeScore) {
                        DISTANCEB.put(parent, tentativeScore);
                        PARENTSB.put(parent, current);
                        OPENB.decreasePriority(parent, tentativeScore);
                        
                        if (CLOSEDA.contains(parent)) {
                            double pathLength = tentativeScore +
                                                DISTANCEA.get(parent);
                            
                            if (m > pathLength) {
                                m = pathLength;
                                touch = parent;
                            }
                        }
                    }
                }
            }
        }
        
        return Collections.<DirectedGraphNode>emptyList();
    }
}