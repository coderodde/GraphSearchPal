package net.coderodde.gsp.model.support;

import java.util.ArrayList;
import java.util.Collection;
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
 * This pathfinding algorithm is due to Wim Pijls and Henk Post in
 * "Yet another bidirectional algorithm for shortest paths." 15 June 2009.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Oct 14, 2015)
 */
public class NewBidirectionalAStarPathFinder<N extends AbstractGraphNode<N>> 
extends AbstractPathFinder<N> {

    private AbstractGraphWeightFunction<N> weightFunction;
    private AbstractHeuristicFunction<N> heuristicFunction;
    
    public NewBidirectionalAStarPathFinder(
            AbstractGraphWeightFunction<N> weightFunction,
            AbstractHeuristicFunction<N> heuristicFunction) {
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
        
        if (source.equals(target)) {
            List<N> path = new ArrayList<>(1);
            path.add(source);
            return path;
        }
        
        MinimumPriorityQueue<N> OPENA = getQueue() != null ? 
                                        getQueue().spawn() :
                                        new DaryHeap<>();
        
        MinimumPriorityQueue<N> OPENB = OPENA.spawn();
        Set<N> CLOSED = new HashSet<>();
        Map<N, N> PARENTSA = new HashMap<>();
        Map<N, N> PARENTSB = new HashMap<>();
        Map<N, Double> DISTANCEA = new HashMap<>();
        Map<N, Double> DISTANCEB = new HashMap<>();
        
        double bestPathLength = Double.POSITIVE_INFINITY;
        double fA = heuristicFunction.estimate(source, target);
        double fB = heuristicFunction.estimate(target, source);
        N touchNode = null;
        
        OPENA.add(source, fA);
        OPENB.add(target, fB);
        PARENTSA.put(source, null);
        PARENTSB.put(target, null);
        DISTANCEA.put(source, 0.0);
        DISTANCEB.put(target, 0.0);
        
        if (listener != null) {
            listener.begin();
        }
        
        while (!OPENA.isEmpty() && !OPENB.isEmpty()) {
            if (OPENA.size() < OPENB.size()) {   
                N current = OPENA.extractMinimum();
                CLOSED.add(current);
                
                if (listener != null) {
                    listener.closed(current);
                }

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
                    for (N child : current.children()) {
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

                            if (listener != null) {
                                listener.reached(child);
                            }
                            
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
                    N node = OPENA.min();
                    fA = DISTANCEA.get(node) + heuristicFunction.estimate(node, 
                                                                          target);
                }
            } else {
                N current = OPENB.extractMinimum();
                CLOSED.add(current);
                
                if (listener != null) {
                    listener.closed(current);
                }

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
                    for (N parent : current.parents()) {
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

                            if (listener != null) {
                                listener.reached(parent);
                            }
                            
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
        
        List<N> path;
        
        if (touchNode == null) {
            path = Collections.<N>emptyList();
        } else {
            path = tracebackPath(touchNode, PARENTSA, PARENTSB);
        }
        
        if (listener != null) {
            listener.done(Collections.<N>unmodifiableList(path));
        }
        
        return path;
    }

    @Override
    public String humanReadableName() {
        return "NBA*";
    }
}
