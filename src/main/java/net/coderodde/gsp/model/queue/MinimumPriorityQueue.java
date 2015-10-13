package net.coderodde.gsp.model.queue;

import net.coderodde.gsp.model.Spawnable;

/**
 * This abstract class defines the API for the various minimum-priority queue
 * data structures.
 * 
 * @author Rodion "rodde" Efremov
 * @param <E> the type of elements stored by the implementation.
 * @version 1.6 (Oct 13, 2015)
 */
public interface MinimumPriorityQueue<E> 
extends Spawnable<MinimumPriorityQueue<E>>{

    /**
     * Adds {@code element} to this queue and assigns the priority 
     * {@code priority} as its key.
     * 
     * @param element  the element to store.
     * @param priority the priority of the element.
     */
    public void add(E element, double priority);
    
    /**
     * Decreases the priority of the element {@code element} if it is present.
     * If the element is not in this heap, or new priority does not
     * improve the current priority, does nothing.
     * 
     * @param element     the element whose priority to decrease.
     * @param newPriority the new priority of the input element.
     */
    public void decreasePriority(E element, double newPriority);
    
    /**
     * Extracts the element with the lowest priority.
     * 
     * @return the element with the lowest priority.
     * 
     * @throws java.util.NoSuchElementException if the heap is empty.
     */
    public E extractMinimum();
    
    /**
     * Returns but does not remove the minimum element.
     * 
     * @return the minimum element. 
     */
    public E min();
    
    /**
     * Returns the amount of elements in the heap.
     * 
     * @return the amount of elements in the heap. 
     */
    public int size();
    
    /**
     * Returns {@code true} it this heap is empty. {@code false} otherwise.
     * 
     * @return {@code true} or {@code false}.
     */
    public boolean isEmpty();
    
    /**
     * Removes all elements from this heap.
     */
    public void clear();
    
    /**
     * Returns a string indicating the actual implementation type.
     * 
     * @return a string indicating implementation type.
     */
    @Override
    public abstract String toString();
}
