package net.coderodde.gsp.model.support;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
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
    
    private List<DirectedGraphNode> search() {
        AtomicLong bestPathAtomic = new AtomicLong();
        Set<DirectedGraphNode> CLOSED = 
                Collections.<DirectedGraphNode>
                newSetFromMap(new ConcurrentHashMap<>());
        bestPathAtomic.set(Double.doubleToLongBits(Double.POSITIVE_INFINITY));
        
        SearchThread forwardThread = new ForwardSearchThread(getQueue(),
                                                             source,
                                                             bestPathAtomic,
                                                             CLOSED);
        SearchThread backwardThread = new BackwardSearchThread(getQueue(),
                                                               target,
                                                               bestPathAtomic,
                                                               CLOSED);
        
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
        
        return null;
    }
    
    private static class SearchThread<N> extends Thread {
        
        protected volatile boolean finished;
        protected SearchThread brotherThread;
        protected AtomicLong bestPathAtomic;
        protected Set<N> CLOSED;
        
        SearchThread(AtomicLong bestPathAtomic, Set<N> CLOSED) {
            this.bestPathAtomic = bestPathAtomic;
            this.CLOSED = CLOSED;
        }
        
        void setBrotherThread(SearchThread brotherThread) {
            this.brotherThread = brotherThread;
        }
        
        void finish() {
            finished = true;
            brotherThread.finished = true;
        }
    }
    
    private static final class ForwardSearchThread<N> extends SearchThread<N> {
        private MinimumPriorityQueue<N> OPEN;
        private N startNode;
        
        private Set<N> CLOSED = new HashSet<>();
        
        ForwardSearchThread(MinimumPriorityQueue<N> queue, 
                            N startNode,
                            AtomicLong bestPathAtomic,
                            Set<N> CLOSED) {
            super(bestPathAtomic, CLOSED);
            this.OPEN = queue == null ? new DaryHeap<>() : queue.spawn();
            this.startNode = startNode;
        }
        
        @Override
        public void run() {
            OPEN.add(startNode, MIN_PRIORITY);
            
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
    
    private static final class BackwardSearchThread<N> extends SearchThread<N> {
        private MinimumPriorityQueue<N> OPEN;
        private N startNode;
        
        private Set<DirectedGraphNode> CLOSED = new HashSet<>();
        
        BackwardSearchThread(MinimumPriorityQueue<N> queue,
                             N startNode,
                             AtomicLong bestPathAtomic,
                             Set<N> CLOSED) {
            super(bestPathAtomic, CLOSED);
            this.OPEN = queue == null ? new DaryHeap<>() : queue.spawn();
            this.startNode = startNode;
        }
        
        @Override
        public void run() {
            OPEN.add(startNode, MIN_PRIORITY);
            
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
}
