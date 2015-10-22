package net.coderodde.gsp.model.support;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;
import net.coderodde.gsp.model.AbstractGraphNode;
import net.coderodde.gsp.model.AbstractGraphWeightFunction;
import net.coderodde.gsp.model.AbstractHeuristicFunction;
import net.coderodde.gsp.model.AbstractPathFinder;
import net.coderodde.gsp.model.queue.MinimumPriorityQueue;
import net.coderodde.gsp.model.queue.support.DaryHeap;

/**
 * This class implements a PNBA* (Parallel New Bidirectional A*) by Luis 
 * Henrique Oliveira Rios and Luiz Chaimowic.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Oct 14, 2015)
 * @param <N> the actual graph node type.
 */
public class ParallelNewBidirectionalAStarPathFinder
<N extends AbstractGraphNode<N>> 
extends AbstractPathFinder<N> {

    private final AbstractGraphWeightFunction<N> weightFunction;
    private final AbstractHeuristicFunction<N> heuristicFunction;
    
    private N source;
    private N target;
    
    public ParallelNewBidirectionalAStarPathFinder(
            AbstractGraphWeightFunction<N> weightFunction,
            AbstractHeuristicFunction<N> heuristicFunction) {
        Objects.requireNonNull(weightFunction, "The weight function is null.");
        Objects.requireNonNull(heuristicFunction,
                               "The heuristic function is null.");
        this.weightFunction = weightFunction;
        this.heuristicFunction = heuristicFunction;
    }
    
    private ParallelNewBidirectionalAStarPathFinder(
            AbstractGraphWeightFunction<N> weightFunction,
            AbstractHeuristicFunction<N> heuristicFunction,
            N source,
            N target) {
        this.weightFunction = weightFunction;
        this.heuristicFunction = heuristicFunction;
        this.source = source;
        this.target = target;
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
        
        return new ParallelNewBidirectionalAStarPathFinder(weightFunction,
                                                           heuristicFunction,
                                                           source,
                                                           target).search();
    }
    
    private List<N> search() {
        MinimumPriorityQueue<N> OPEN = getQueue() == null ?
                                       new DaryHeap<>() :
                                       getQueue().spawn();
        
        Set<N> CLOSED = Collections.<N>newSetFromMap(new ConcurrentHashMap<>());
        PathLengthHolder<N> pathLengthHolder = new PathLengthHolder<>();
        
        SearchThread<N> forwardThread = 
                new ForwardSearchThread<>(OPEN.spawn(),
                                          CLOSED,
                                          weightFunction,
                                          heuristicFunction,
                                          pathLengthHolder,
                                          source,
                                          target);
        
        SearchThread<N> backwardThread = 
                new BackwardSearchThread(OPEN.spawn(),
                                         CLOSED,
                                         weightFunction,
                                         heuristicFunction,
                                         pathLengthHolder,
                                         source,
                                         target);
        
        forwardThread.setBrotherThread(backwardThread);
        backwardThread.setBrotherThread(forwardThread);
        
        forwardThread.start();
        backwardThread.start();
        
        try {
            forwardThread.join();
            backwardThread.join();
        } catch (InterruptedException ex) {
            return null;
        }
        
        N touchNode = pathLengthHolder.getTouchNode();
        
        if (touchNode == null) {
            return Collections.<N>emptyList();
        }
   
        return tracebackPath(touchNode,
                             forwardThread.getParentMap(),
                             backwardThread.getParentMap());
    }
    
    private static class SearchThread<N extends AbstractGraphNode<N>> 
    extends Thread {
        
        protected volatile boolean finished;
        protected volatile double F;
        protected SearchThread brotherThread;
        protected final MinimumPriorityQueue<N> OPEN;
        protected final Set<N> CLOSED;
        protected final Map<N, N> PARENTS = new HashMap<>();
        protected final Map<N, Double> DISTANCE = new HashMap<>();
        protected final AbstractHeuristicFunction<N> heuristicFunction;
        protected final AbstractGraphWeightFunction<N> weightFunction;
        protected final PathLengthHolder<N> pathLengthHolder;
        protected final N source;
        protected final N target;
        
        SearchThread(MinimumPriorityQueue<N> OPEN,
                     Set<N> CLOSED, 
                     AbstractHeuristicFunction<N> heuristicFunction,
                     AbstractGraphWeightFunction<N> weightFunction,
                     PathLengthHolder<N> pathLengthHolder,
                     N source,
                     N target) {
            this.OPEN = OPEN;
            this.CLOSED = CLOSED;
            this.heuristicFunction = heuristicFunction;
            this.weightFunction = weightFunction;
            this.pathLengthHolder = pathLengthHolder;
            this.source = source;
            this.target = target;
        }
        
        SearchThread<N> getBrotherThread() {
            return this.brotherThread;
        }
        
        void setBrotherThread(SearchThread brotherThread) {
            this.brotherThread = brotherThread;
        }
        
        void finish() {
            finished = true;
            brotherThread.finished = true;
        }
        
        double getF() {
            return F;
        }
        
        Map<N, N> getParentMap() {
            return PARENTS;
        }
        
        Map<N, Double> getDistanceMap() {
            return DISTANCE;
        }
    }
    
    private static final class 
            ForwardSearchThread<N extends AbstractGraphNode<N>> 
    extends SearchThread<N> {
        
        ForwardSearchThread(MinimumPriorityQueue<N> OPEN,
                            Set<N> CLOSED,
                            AbstractGraphWeightFunction<N> weightFunction,
                            AbstractHeuristicFunction<N> heuristicFunction,
                            PathLengthHolder<N> pathLengthHolder,
                            N source,
                            N target) {
            
            super(OPEN,
                  CLOSED, 
                  heuristicFunction, 
                  weightFunction,
                  pathLengthHolder, 
                  source, 
                  target);
        }
        
        @Override
        public void run() {
            F = heuristicFunction.estimate(source, target);
            PARENTS.put(source, null);
            DISTANCE.put(source, 0.0);
            OPEN.add(source, F);
            
            while (!finished) {
                if (OPEN.isEmpty()) {
                    finish();
                    return;
                }
                
                N current = OPEN.extractMinimum();
                
                if (CLOSED.contains(current)) {
                    continue;
                }
                
                double f = DISTANCE.get(current) + 
                           heuristicFunction.estimate(current, target);
                double L = pathLengthHolder.read();
                double tmp = DISTANCE.get(current) + 
                             brotherThread.getF() - 
                             heuristicFunction.estimate(current, source);
                
                if (f < L && tmp < L) {
                    for (N child : current.children()) {
                        if (CLOSED.contains(child)) {
                            continue;
                        }
                        
                        double tentativeScore = DISTANCE.get(current) + 
                                                weightFunction.get(current, 
                                                                   child);
                        
                        if (!DISTANCE.containsKey(child)) {
                            DISTANCE.put(child, tentativeScore);
                            PARENTS.put(child, current);
                            OPEN.add(child, 
                                     tentativeScore + 
                                     heuristicFunction.estimate(child, target));
                            
                            Map<N, Double> OTHER_DISTANCE = getBrotherThread()
                                                           .getDistanceMap();
                            
                            Double g2 = OTHER_DISTANCE.get(child);
                            
                            if (g2 != null) {
                                double tmpDist = g2 + DISTANCE.get(child);
                                
                                if (pathLengthHolder.read() > tmpDist) {
                                    pathLengthHolder.tryUpdate(tmpDist, child);
                                }
                            }
                        } else if (DISTANCE.get(child) > tentativeScore) {
                            DISTANCE.put(child, tentativeScore);
                            PARENTS.put(child, current);
                            OPEN.decreasePriority(
                                     child,
                                     tentativeScore +
                                     heuristicFunction.estimate(child, target));
                            
                            Map<N, Double> OTHER_DISTANCE = getBrotherThread()
                                                           .getDistanceMap();
                            
                            Double g2 = OTHER_DISTANCE.get(child);
                            
                            if (g2 != null) {
                                double tmpDist = g2 + DISTANCE.get(child);
                                
                                if (pathLengthHolder.read() > tmpDist) {
                                    pathLengthHolder.tryUpdate(tmpDist, child);
                                }
                            }
                        }
                    }
                    
                    CLOSED.add(current);
                }
                
                if (OPEN.isEmpty()) {
                    finish();
                    return;
                }
                
                this.F = DISTANCE.get(OPEN.min()) + 
                         heuristicFunction.estimate(OPEN.min(), target);
            }
        }
    }
    
    private static final class 
            BackwardSearchThread<N extends AbstractGraphNode<N>> 
    extends SearchThread<N> {
        
        BackwardSearchThread(MinimumPriorityQueue<N> OPEN,
                             Set<N> CLOSED,
                             AbstractGraphWeightFunction<N> weightFunction,
                             AbstractHeuristicFunction<N> heuristicFunction,
                             PathLengthHolder<N> pathLengthHolder,
                             N source,
                             N target) {
            super(OPEN,
                  CLOSED, 
                  heuristicFunction, 
                  weightFunction,
                  pathLengthHolder, 
                  source, 
                  target);
            
            this.F = heuristicFunction.estimate(source, target);
        }
        
        @Override
        public void run() {
            F = heuristicFunction.estimate(source, target);
            PARENTS.put(target, null);
            DISTANCE.put(target, 0.0);
            OPEN.add(target, F);
            
            while (!finished) {
                if (OPEN.isEmpty()) {
                    finish();
                    return;
                }
                
                N current = OPEN.extractMinimum();
                
                if (CLOSED.contains(current)) {
                    continue;
                }
                
                double f = DISTANCE.get(current) + 
                           heuristicFunction.estimate(current, source);
                double L = pathLengthHolder.read();
                double tmp = DISTANCE.get(current) + 
                             brotherThread.getF() - 
                             heuristicFunction.estimate(current, target);
                
                if (f < L && tmp < L) {
                    for (N parent : current.parents()) {
                        if (CLOSED.contains(parent)) {
                            continue;
                        }
                        
                        double tentativeScore = DISTANCE.get(current) + 
                                                weightFunction.get(parent, 
                                                                   current);
                        
                        if (!DISTANCE.containsKey(parent)) {
                            DISTANCE.put(parent, tentativeScore);
                            PARENTS.put(parent, current);
                            OPEN.add(parent, 
                                     tentativeScore + 
                                     heuristicFunction.estimate(parent, 
                                                                source));
                            
                            Map<N, Double> OTHER_DISTANCE = getBrotherThread()
                                                           .getDistanceMap();
                            
                            Double g2 = OTHER_DISTANCE.get(parent);
                            
                            if (g2 != null) {
                                double tmpDist = g2 + DISTANCE.get(parent);
                                
                                if (pathLengthHolder.read() > tmpDist) {
                                    pathLengthHolder.tryUpdate(tmpDist, parent);
                                }
                            }
                        } else if (DISTANCE.get(parent) > tentativeScore) {
                            DISTANCE.put(parent, tentativeScore);
                            PARENTS.put(parent, current);
                            OPEN.decreasePriority(
                                     parent,
                                     tentativeScore +
                                     heuristicFunction.estimate(parent, 
                                                                source));
                            
                            Map<N, Double> OTHER_DISTANCE = getBrotherThread()
                                                           .getDistanceMap();
                            
                            Double g2 = OTHER_DISTANCE.get(parent);
                            
                            if (g2 != null) {
                                double tmpDist = g2 + DISTANCE.get(parent);
                                
                                if (pathLengthHolder.read() > tmpDist) {
                                    pathLengthHolder.tryUpdate(tmpDist, parent);
                                }
                            }
                        }
                    }
                    
                    CLOSED.add(current);
                }
                
                if (OPEN.isEmpty()) {
                    finish();
                    return;
                }
                
                this.F = DISTANCE.get(OPEN.min()) + 
                         heuristicFunction.estimate(OPEN.min(), target);
            }
        }
    }
    
    private static final class PathLengthHolder<N> {
        
        private final Semaphore mutex = new Semaphore(1);
        private volatile double length = Double.POSITIVE_INFINITY;
        private N touchNode;
        
        double read() {
            return length;
        }
        
        void tryUpdate(double length, N touchNode) {
            mutex.acquireUninterruptibly();
            
            if (this.length > length) {
                this.length = length;
                this.touchNode = touchNode;
            }
            
            mutex.release();
        }
        
        N getTouchNode() {
            return touchNode;
        }
    }
}
