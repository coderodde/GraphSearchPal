package net.coderodde.gsp.model.support;

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
        protected MinimumPriorityQueue<N> OPEN;
        protected final Set<N> CLOSED;
        protected final Map<N, N> PARENTS = new HashMap<>();
        protected final Map<N, Double> DISTANCE = new HashMap<>();
        protected final AbstractHeuristicFunction<N> heuristicFunction;
        protected final AbstractGraphWeightFunction<N> weightFunction;
        protected final PathLengthHolder<N> pathLengthHolder;
        protected final N source;
        protected final N target;
        
        SearchThread(Set<N> CLOSED, 
                     AbstractHeuristicFunction<N> heuristicFunction,
                     AbstractGraphWeightFunction<N> weightFunction,
                     PathLengthHolder<N> pathLengthHolder,
                     N source,
                     N target) {
            this.CLOSED = CLOSED;
            this.heuristicFunction = heuristicFunction;
            this.weightFunction = weightFunction;
            this.pathLengthHolder = pathLengthHolder;
            this.source = source;
            this.target = target;
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
    }
    
    private static final class 
            ForwardSearchThread<N extends AbstractGraphNode<N>> 
    extends SearchThread<N> {
        
        ForwardSearchThread(MinimumPriorityQueue<N> queue,
                            Set<N> CLOSED,
                            AbstractGraphWeightFunction<N> weightFunction,
                            AbstractHeuristicFunction<N> heuristicFunction,
                            PathLengthHolder<N> pathLengthHolder,
                            N source,
                            N target) {
            
            super(CLOSED, 
                  heuristicFunction, 
                  weightFunction,
                  pathLengthHolder, 
                  source, 
                  target);
            
            this.OPEN = queue == null ? new DaryHeap<>() : queue.spawn();
            this.F = heuristicFunction.estimate(source, target);
        }
        
        @Override
        public void run() {
            F = heuristicFunction.estimate(source, target);
            DISTANCE.put(source, 0.0);
            PARENTS.put(source, null);
            OPEN.add(source, F);
            
            while (!finished) {
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
                    }
                }
            }
        }
    }
    
    private static final class 
            BackwardSearchThread<N extends AbstractGraphNode<N>> 
    extends SearchThread<N> {
        
        BackwardSearchThread(MinimumPriorityQueue<N> queue,
                             Set<N> CLOSED,
                             AbstractGraphWeightFunction<N> weightFunction,
                             AbstractHeuristicFunction<N> heuristicFunction,
                             PathLengthHolder<N> pathLengthHolder,
                             N source,
                             N target) {
            super(CLOSED, 
                  heuristicFunction, 
                  weightFunction,
                  pathLengthHolder, 
                  source, 
                  target);
            
            this.OPEN = queue == null ? new DaryHeap<>() : queue.spawn();
            this.F = heuristicFunction.estimate(source, target);
        }
        
        @Override
        public void run() {
            F = heuristicFunction.estimate(source, target);
            OPEN.add(target, F);
            
            while (!finished) {
                N current = OPEN.extractMinimum();
                
                if (!CLOSED.contains(current)) {
                    
                }
                
                if (!OPEN.isEmpty()) {
                    //...
                } else {
                    brotherThread.finish();
                    break;
                }
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
