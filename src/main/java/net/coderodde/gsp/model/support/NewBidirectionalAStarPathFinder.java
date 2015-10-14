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
 * This pathfinding algorithm is due to Wim Pijls and Henk Post in
 * "Yet another bidirectional algorithm for shortest paths." 15 June 2009.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Oct 14, 2015)
 */
public class NewBidirectionalAStarPathFinder extends PathFinder {

    private DirectedGraphWeightFunction weightFunction;
    private HeuristicFunction heuristicFunction;
    
    public NewBidirectionalAStarPathFinder(
            DirectedGraphWeightFunction weightFunction,
            HeuristicFunction heuristicFunction) {
        Objects.requireNonNull(weightFunction, "The weight function is null.");
        Objects.requireNonNull(heuristicFunction,
                               "The heuristic function is null.");
        this.weightFunction = weightFunction;
        this.heuristicFunction = heuristicFunction;
    }
    
    @Override
    public List<DirectedGraphNode> search(DirectedGraphNode source, 
                                          DirectedGraphNode target) {
        Objects.requireNonNull(source, "The source node is null.");
        Objects.requireNonNull(target, "The target node is null.");
        
        MinimumPriorityQueue<DirectedGraphNode> OPEN = 
                getQueue() != null ? 
                getQueue().spawn() :
                new DaryHeap<>();
        Set<DirectedGraphNode> CLOSED = new HashSet<>();
        Map<DirectedGraphNode, DirectedGraphNode> PARENTSA = new HashMap<>();
        Map<DirectedGraphNode, DirectedGraphNode> PARENTSB = new HashMap<>();
        Map<DirectedGraphNode, Double> DISTANCEA = new HashMap<>();
        Map<DirectedGraphNode, Double> DISTANCEB = new HashMap<>();
        
        double L = Double.POSITIVE_INFINITY;
        DirectedGraphNode touchNode = null;
        double forward_f  = heuristicFunction.estimate(source, target);
        double backward_f = heuristicFunction.estimate(target, source);
        
        OPEN.add(source, forward_f);
        DISTANCEA.put(source, 0.0);
        DISTANCEB.put(target, 0.0);
        PARENTSA.put(source, null);
        PARENTSB.put(target, null);
        
        while (!OPEN.isEmpty()) {
            DirectedGraphNode current = OPEN.extractMinimum();
            CLOSED.add(current);

            if (DISTANCEA.get(current) + 
                heuristicFunction.estimate(current, target) -
                heuristicFunction.estimate(target, target) >= L
                    || 
                    DISTANCEA.get(current) + backward_f -
                    heuristicFunction.estimate(current, source) >= L) {
                // Reject.
            } else {
                for (DirectedGraphNode child : current.children()) {
                    if (CLOSED.contains(child)) {
                        continue;
                    }

                    double tentativeScore = DISTANCEA.get(current) +
                                            weightFunction.get(current, 
                                                               child);

                    if (!DISTANCEA.containsKey(child)) {
                        DISTANCEA.put(child, tentativeScore);
                        PARENTSA.put(child, current);
                        OPEN.add(child, 
                                 tentativeScore + 
                                 heuristicFunction.estimate(child, target));

                        if (DISTANCEB.containsKey(child)) {
                            double pathLength = tentativeScore +
                                                DISTANCEB.get(child);

                            if (L > pathLength) {
                                L = pathLength;
                                touchNode = child;
                            }
                        }
                    } else if (DISTANCEA.get(child) > tentativeScore) {
                        DISTANCEA.put(child, tentativeScore);
                        PARENTSA.put(child, current);
                        OPEN.decreasePriority(
                                child,
                                tentativeScore + 
                                heuristicFunction.estimate(child, target));

                        if (DISTANCEB.containsKey(child)) {
                            double pathLength = tentativeScore +
                                                DISTANCEB.get(child);

                            if (L > pathLength) {
                                L = pathLength;
                                touchNode = child;
                            }
                        }
                    }
                }
            }

            if (OPEN.isEmpty()) {
                break;
            }
            
            DirectedGraphNode minimumNode = OPEN.min();
            forward_f = DISTANCEA.get(minimumNode) +
                        heuristicFunction.estimate(minimumNode, target);
            
            current = OPEN.extractMinimum();
            CLOSED.add(current);

            if (DISTANCEB.get(current) + 
                heuristicFunction.estimate(current, source) -
                heuristicFunction.estimate(source, source) >= L
                    || 
                    DISTANCEB.get(current) + forward_f -
                    heuristicFunction.estimate(current, target) >= L) {
                // Reject.
            } else {
                for (DirectedGraphNode parent : current.parents()) {
                    if (CLOSED.contains(parent)) {
                        continue;
                    }

                    double tentativeScore = DISTANCEB.get(current) +
                                            weightFunction.get(parent,
                                                               current);

                    if (!DISTANCEB.containsKey(parent)) {
                        DISTANCEB.put(parent, tentativeScore);
                        PARENTSB.put(parent, current);
                        OPEN.add(parent, 
                                 tentativeScore + 
                                 heuristicFunction.estimate(parent, 
                                                            source));

                        if (DISTANCEA.containsKey(parent)) {
                            double pathLength = tentativeScore +
                                                DISTANCEA.get(parent);

                            if (L > pathLength) {
                                L = pathLength;
                                touchNode = parent;
                            }
                        }
                    } else if (DISTANCEB.get(parent) > tentativeScore) {
                        DISTANCEB.put(parent, tentativeScore);
                        PARENTSB.put(parent, current);
                        OPEN.decreasePriority(
                                parent,
                                tentativeScore + 
                                heuristicFunction.estimate(parent, source));

                        if (DISTANCEA.containsKey(parent)) {
                            double pathLength = tentativeScore +
                                                DISTANCEA.get(parent);

                            if (L > pathLength) {
                                L = pathLength;
                                touchNode = parent;
                            }
                        }
                    }
                }
            }
            
            if (OPEN.isEmpty()) {
                break;
            }

            minimumNode = OPEN.min();
            forward_f = DISTANCEA.get(minimumNode) +
                        heuristicFunction.estimate(minimumNode, target);
        }
        
        return touchNode == null ?
                Collections.<DirectedGraphNode>emptyList() :
                tracebackPath(touchNode, PARENTSA, PARENTSB);
    }
}
