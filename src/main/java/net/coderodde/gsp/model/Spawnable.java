package net.coderodde.gsp.model;

/**
 * This interface defines the API for objects that support the spawning 
 * operation: it creates a copy of itself.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Oct 13, 2015)
 * @param <T> the actual type being spawned.
 */
public interface Spawnable<T> {
    
    /**
     * Spawns another instance of the same class.
     * @return 
     */
    public T spawn();
}
