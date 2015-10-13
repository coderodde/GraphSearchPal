package net.coderodde.gsp.model.queue.support;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import net.coderodde.gsp.model.queue.MinimumPriorityQueue;

/**
 * This class implements binomial heap.
 * 
 * @author Rodion Efremov
 * @version 1.6
 * @param <E> the element type.
 */
public class BinomialHeap<E> implements MinimumPriorityQueue<E> {

    /**
     * The default map capacity.
     */
    private static final int DEFAULT_MAP_CAPACITY = 1 << 10;
    
    /**
     * This class implements a binomial tree in a binomial heap.
     * 
     * @param <E> the element type.
     * @param <P> the type of priority keys.
     */
    private static final class BinomialTree<E> {
        
        /**
         * The actual element of this node.
         */
        E element;
        
        /**
         * The priority key of this node.
         */
        double priority;
        
        /**
         * The parent node.
         */
        BinomialTree<E> parent;
        
        /**
         * Immediate sibling of this node to the right.
         */
        BinomialTree<E> sibling;
        
        /**
         * The leftmost child of this node.
         */
        BinomialTree<E> child;
        
        /**
         * The amount of children of this node.
         */
        int degree;
        
        /**
         * Constructs a new node and initialize it with mandatory data.
         * 
         * @param element the element to store in this node.
         * @param priority the priority of the element stored.
         */
        BinomialTree(E element, double priority) {
            this.element = element;
            this.priority = priority;
        }
    }
    
    /**
     * Caches the amount of elements in this binomial heap.
     */
    private int size;
    
    /**
     * Points to the leftmost node in the root list of this heap.
     */
    private BinomialTree<E> head;
    
    /**
     * Caches the binomial tree with the least priority key.
     */
    private BinomialTree<E> minimumTree;
    
    /**
     * Maps each element in the heap to its respective node.
     */
    private final Map<E, BinomialTree<E>> map;
    
    /**
     * Constructs a new {@code BinomialHeap} with default settings.
     */
    public BinomialHeap() {
        this(DEFAULT_MAP_CAPACITY);
    }
    
    /**
     * Constructs a new {@code BinomialHeap} using <code>mapCapacity</code> as 
     * the initial capacity for the underlying map.
     * 
     * @param mapCapacity the initial map capacity.
     */
    public BinomialHeap(int mapCapacity) {
        this.map = new HashMap<>(mapCapacity);
    }
    
    /**
     * Constructs a binomial heap with only one element. Used for the sake of 
     * {@code add} operation, which simply unites the current heap with the
     * one created by this constructor.
     * 
     * @param element the application-specific satellite data.
     * @param priority the priority of the new element.
     */
    private BinomialHeap(E element, double priority) {
        BinomialTree<E> tree = new BinomialTree<>(element, priority);
        head = tree;
        minimumTree = tree;
        size = 1;
        map = null;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void add(E element, double priority) {
        if (map.containsKey(element)) {
            // element already in this heap, use decreaseKey instead.
            return;
        }
        
        BinomialHeap<E> h = new BinomialHeap<>(element, priority);
        
        if (size == 0) {
            this.head = h.head;
            this.minimumTree = h.head;
            this.map.put(element, this.head);
            this.size = 1;
        } else {
            heapUnion(h.head);
            this.map.put(element, h.head);
            this.size++;
            
            if (minimumTree.priority > h.minimumTree.priority) {
                minimumTree = h.minimumTree;
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void decreasePriority(E element, double newPriority) {
        if (!map.containsKey(element)) {
            // No element here.
            return;
        } 
        
        BinomialTree<E> target = map.get(element);
        
        if (target.priority <= newPriority) {
            // The priority key of element won't improve.
            return;
        }
        
        target.priority = newPriority;
        
        BinomialTree<E> z = target.parent;
        BinomialTree<E> y = target;
        
        while (z != null && y.priority < z.priority) {
            // Exchange priority keys.
            double tmp = y.priority;
            y.priority = z.priority;
            z.priority = tmp;
            
            // Exchange satellite data elements.
            E tmp2 = y.element;
            y.element = z.element;
            z.element = tmp2;
            
            // Move one level up.
            y = z;
            z = z.parent;
        }
        
        if (minimumTree.priority > y.priority) {
            minimumTree = y;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public E extractMinimum() {
        if (size == 0) {
            throw new NoSuchElementException(
                    "Reading from an empty binomial heap.");
        }
        
        BinomialTree<E> x = head;
        BinomialTree<E> prevx = null;
        BinomialTree<E> best = x;
        BinomialTree<E> bestprev = null;
        double minPriorityKey = x.priority;
        
        // Find the tree T with the least priority element and the tree 
        // preceding T.
        while (x != null) {
            if (minPriorityKey > x.priority) {
                minPriorityKey = x.priority;
                best = x;
                bestprev = prevx;
            }
            
            prevx = x;
            x = x.sibling;
        }
        
        // Remove from root list the tree with the least priority root.
        if (bestprev == null) {
            head = best.sibling;
        } else {
            bestprev.sibling = best.sibling;
        }
        
        // Unite this heap with the reversed list of children of the tree whose
        // root contained the extracted element.
        heapUnion(reverseRootList(best.child));
        
        // Update the cached minimum tree.
        if (--size > 0) {
            BinomialTree<E> minTree = head;
            BinomialTree<E> t = head.sibling;
            double minPriority = head.priority;
            
            while (t != null) {
                if (minPriority > t.priority) {
                    minPriority = t.priority;
                    minTree = t;
                }
                
                t = t.sibling;
            }
            
            minimumTree = minTree;
        }
        
        return best.element;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public E min() {
        if (size == 0) {
            throw new NoSuchElementException("Reading from an empty heap.");
        }
        
        return minimumTree.element;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int size() {
        return size;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clear() {
        this.head = null;
        this.map.clear();
        this.size = 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MinimumPriorityQueue<E> spawn() {
       return new BinomialHeap<>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "BinomialHeap";
    }
    
    /**
     * Makes {@code y} a leftmost child of {@code z}.
     * 
     * @param y the node to become a child of {@code z}.
     * @param z the node to become a parent of {@code y}.
     */
    private void link(BinomialTree<E> child, 
                      BinomialTree<E> parent) {
        child.parent = parent;
        child.sibling = parent.child;
        parent.child = child;
        parent.degree++;
    }
    
    /**
     * Merges the root lists of this heap and {@code other}.
     * 
     * @param other another binomial heap whose root list to merge.
     * 
     * @return the head of the merged root list. 
     */
    private BinomialTree<E> mergeRoots(BinomialTree<E> other) {
        BinomialTree<E> a = head;
        BinomialTree<E> b = other;
        
        if (a == null) {
            return b;
        } else if (b == null) {
            return a;
        }
        
        BinomialTree<E> rootListHead;
        BinomialTree<E> rootListTail;
        
        // Initialize rootListHead and rootListTail.
        if (a.degree < b.degree) {
            rootListHead = a;
            rootListTail = a;
            a = a.sibling;
        } else {
            rootListHead = b;
            rootListTail = b;
            b = b.sibling;
        }
        
        while (a != null && b != null) {
            if (a.degree < b.degree) {
                rootListTail.sibling = a;
                rootListTail = a;
                a = a.sibling;
            } else {
                rootListTail.sibling = b;
                rootListTail = b;
                b = b.sibling;
            }
        }
        
        if (a != null) {
            // Just append the rest.
            rootListTail.sibling = a;
        } else {
            // Just append the rest.
            rootListTail.sibling = b;
        }
        
        return rootListHead;
    }
    
    /**
     * Reverses the root list as to facilitate the {@code extractMinimum}.
     * Sets the parent references to <code>null</code> also.
     * 
     * @param first the head node of the root list to reverse.
     * 
     * @return the reversed root list. 
     */
    private BinomialTree<E> reverseRootList(BinomialTree<E> first) {
        BinomialTree<E> tmp = first; // This is the cursor over the list.
        BinomialTree<E> tmpnext;
        BinomialTree<E> newHead = null;
     
        while (tmp != null) {
            tmpnext = tmp.sibling;
            tmp.sibling = newHead;
            newHead = tmp;
            tmp = tmpnext;
        }
        
        return newHead;
    }
    
    /**
     * Unites this heap with {@code other}. This subroutine is used in both
     * {@code add} and {@code extractMinimum}.
     * 
     * @param other the heap to unite with this heap. 
     */
    private void heapUnion(BinomialTree<E> other) {
        if (other == null) {
            return;
        }
        
        BinomialTree<E> t = mergeRoots(other);
        BinomialTree<E> prev = null;
        BinomialTree<E> x = t;
        BinomialTree<E> next = x.sibling;
        
        while (next != null) {
            if ((x.degree != next.degree)
                    || (next.sibling != null 
                    && next.sibling.degree == x.degree)) {
                prev = x;
                x = next;
            } else if (x.priority <= next.priority) {
                x.sibling = next.sibling;
                link(next, x);
            } else {
                if (prev == null) {
                    t = next;
                } else {
                    prev.sibling = next;
                }
                
                link(x, next);
                x = next;
            }
            
            next = x.sibling;
        }
        
        this.head = t;
    }
}
