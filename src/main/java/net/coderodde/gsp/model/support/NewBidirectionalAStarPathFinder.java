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
        
        if (source.equals(target)) {
            List<DirectedGraphNode> path = new ArrayList<>(1);
            path.add(source);
            return path;
        }
        
        MinimumPriorityQueue<DirectedGraphNode> OPENA = 
                getQueue() != null ? 
                getQueue().spawn() :
                new DaryHeap<>();
        MinimumPriorityQueue<DirectedGraphNode> OPENB = OPENA.spawn();
        Set<DirectedGraphNode> CLOSED = new HashSet<>();
        Map<DirectedGraphNode, DirectedGraphNode> PARENTSA = new HashMap<>();
        Map<DirectedGraphNode, DirectedGraphNode> PARENTSB = new HashMap<>();
        Map<DirectedGraphNode, Double> DISTANCEA = new HashMap<>();
        Map<DirectedGraphNode, Double> DISTANCEB = new HashMap<>();
        
        double bestPathLength = Double.POSITIVE_INFINITY;
        DirectedGraphNode touchNode = null;
        double fA = heuristicFunction.estimate(source, target);
        double fB = heuristicFunction.estimate(target, source);
        
        OPENA.add(source, fA);
        OPENB.add(target, fB);
        PARENTSA.put(source, null);
        PARENTSB.put(target, null);
        DISTANCEA.put(source, 0.0);
        DISTANCEB.put(target, 0.0);
        
        while (!OPENA.isEmpty() && !OPENB.isEmpty()) {
            if (OPENA.size() < OPENB.size()) {   
                DirectedGraphNode current = OPENA.extractMinimum();
                CLOSED.add(current);

                if (DISTANCEA.get(current) + 
                        heuristicFunction.estimate(current, target) -
                        heuristicFunction.estimate(target, target) 
                        >= bestPathLength
                        ||
                        DISTANCEA.get(current) + fB 
                        - heuristicFunction.estimate(current, source)
                        >= bestPathLength) {
                    // Reject the node 'current'.
                } else {
                    // Stabilize the node 'current'.
                    for (DirectedGraphNode child : current.children()) {
                        if (CLOSED.contains(child)) {
                            continue;
                        }

                        double tentativeScore = DISTANCEA.get(current) +
                                                weightFunction.get(current, child);

                        if (!DISTANCEA.containsKey(child)) {
                            DISTANCEA.put(child, tentativeScore);
                            PARENTSA.put(child, current);
                            OPENA.add(child, 
                                      tentativeScore +
                                      heuristicFunction.estimate(child, target));

                            if (DISTANCEB.containsKey(child)) {
                                double pathLength = tentativeScore +
                                                    DISTANCEB.get(child);

                                if (bestPathLength > pathLength) {
                                    bestPathLength = pathLength;
                                    touchNode = child;
                                }
                            }
                        } else if (DISTANCEA.get(child) > tentativeScore) {
                            DISTANCEA.put(child, tentativeScore);
                            PARENTSA.put(child, current);
                            OPENA.decreasePriority(
                                    child,
                                    tentativeScore +
                                    heuristicFunction.estimate(child, target));

                            if (DISTANCEB.containsKey(child)) {
                                double pathLength = tentativeScore +
                                                    DISTANCEB.get(child);

                                if (bestPathLength > pathLength) {
                                    bestPathLength = pathLength;
                                    touchNode = child;
                                }
                            }
                        }
                    }
                }

                if (!OPENA.isEmpty()) {
                    DirectedGraphNode node = OPENA.min();
                    fA = DISTANCEA.get(node) + heuristicFunction.estimate(node, 
                                                                          target);
                }
            } else {
                DirectedGraphNode current = OPENB.extractMinimum();
                CLOSED.add(current);

                if (DISTANCEB.get(current) + 
                        heuristicFunction.estimate(current, source) -
                        heuristicFunction.estimate(source, source) 
                        >= bestPathLength
                        ||
                        DISTANCEB.get(current) + fA 
                        - heuristicFunction.estimate(current, target)
                        >= bestPathLength) {
                    // Reject the node 'current'.
                } else {
                    // Stabilize the node 'current'.
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
                            OPENB.add(parent, 
                                      tentativeScore +
                                      heuristicFunction.estimate(parent, source));

                            if (DISTANCEA.containsKey(parent)) {
                                double pathLength = tentativeScore +
                                                    DISTANCEA.get(parent);

                                if (bestPathLength > pathLength) {
                                    bestPathLength = pathLength;
                                    touchNode = parent;
                                }
                            }
                        } else if (DISTANCEB.get(parent) > tentativeScore) {
                            DISTANCEB.put(parent, tentativeScore);
                            PARENTSB.put(parent, current);
                            OPENB.decreasePriority(
                                    parent,
                                    tentativeScore +
                                    heuristicFunction.estimate(parent, source));

                            if (DISTANCEA.containsKey(parent)) {
                                double pathLength = tentativeScore +
                                                    DISTANCEA.get(parent);

                                if (bestPathLength > pathLength) {
                                    bestPathLength = pathLength;
                                    touchNode = parent;
                                }
                            }
                        }
                    }
                }
            }
        }
        
        return touchNode == null ?
                Collections.<DirectedGraphNode>emptyList() :
                tracebackPath(touchNode, PARENTSA, PARENTSB);
    }
}
