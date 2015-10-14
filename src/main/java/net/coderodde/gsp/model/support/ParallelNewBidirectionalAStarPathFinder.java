package net.coderodde.gsp.model.support;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import net.coderodde.gsp.model.DirectedGraphNode;
import net.coderodde.gsp.model.DirectedGraphWeightFunction;
import net.coderodde.gsp.model.HeuristicFunction;
import net.coderodde.gsp.model.PathFinder;
import net.coderodde.gsp.model.queue.MinimumPriorityQueue;
import net.coderodde.gsp.model.queue.support.DaryHeap;

/**
 * This class implements a PNBA* (Parallel New Bidirectional A*) by Luis 
 * Henrique Oliveira Rios and Luiz Chaimowic.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Oct 14, 2015)
 */
public class ParallelNewBidirectionalAStarPathFinder extends PathFinder {

    private final DirectedGraphWeightFunction weightFunction;
    private final HeuristicFunction heuristicFunction;
    
    private DirectedGraphNode source;
    private DirectedGraphNode target;
    
    public ParallelNewBidirectionalAStarPathFinder(
            DirectedGraphWeightFunction weightFunction,
            HeuristicFunction heuristicFunction) {
        Objects.requireNonNull(weightFunction, "The weight function is null.");
        Objects.requireNonNull(heuristicFunction,
                               "The heuristic function is null.");
        this.weightFunction = weightFunction;
        this.heuristicFunction = heuristicFunction;
    }
    
    private ParallelNewBidirectionalAStarPathFinder(
        DirectedGraphWeightFunction weightFunction,
        HeuristicFunction heuristicFunction,
        DirectedGraphNode source,
        DirectedGraphNode target) {
        this.weightFunction = weightFunction;
        this.heuristicFunction = heuristicFunction;
        this.source = source;
        this.target = target;
    }
    
    @Override
    public List<DirectedGraphNode> search(DirectedGraphNode source, 
                                          DirectedGraphNode target) {
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
    
    private static class SearchThread extends Thread {
        
        protected volatile boolean finished;
        protected SearchThread brotherThread;
        protected AtomicLong bestPathAtomic;
        protected Set<DirectedGraphNode> CLOSED;
        
        SearchThread(AtomicLong bestPathAtomic, Set<DirectedGraphNode> CLOSED) {
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
    
    private static final class ForwardSearchThread extends SearchThread {
        private MinimumPriorityQueue<DirectedGraphNode> OPEN;
        private DirectedGraphNode startNode;
        
        private Set<DirectedGraphNode> CLOSED = new HashSet<>();
        
        ForwardSearchThread(MinimumPriorityQueue<DirectedGraphNode> queue,
                            DirectedGraphNode startNode,
                            AtomicLong bestPathAtomic,
                            Set<DirectedGraphNode> CLOSED) {
            super(bestPathAtomic, CLOSED);
            this.OPEN = queue == null ? new DaryHeap<>() : queue.spawn();
            this.startNode = startNode;
        }
        
        @Override
        public void run() {
            OPEN.add(startNode, MIN_PRIORITY);
            
            while (!finished) {
                DirectedGraphNode current = OPEN.extractMinimum();
                
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
    
    private static final class BackwardSearchThread extends SearchThread {
        private MinimumPriorityQueue<DirectedGraphNode> OPEN;
        private DirectedGraphNode startNode;
        
        private Set<DirectedGraphNode> CLOSED = new HashSet<>();
        
        BackwardSearchThread(MinimumPriorityQueue<DirectedGraphNode> queue,
                             DirectedGraphNode startNode,
                             AtomicLong bestPathAtomic,
                             Set<DirectedGraphNode> CLOSED) {
            super(bestPathAtomic, CLOSED);
            this.OPEN = queue == null ? new DaryHeap<>() : queue.spawn();
            this.startNode = startNode;
        }
        
        @Override
        public void run() {
            OPEN.add(startNode, MIN_PRIORITY);
            
            while (!finished) {
                DirectedGraphNode current = OPEN.extractMinimum();
                
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
