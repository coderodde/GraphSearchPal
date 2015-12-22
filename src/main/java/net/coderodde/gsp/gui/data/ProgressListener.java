package net.coderodde.gsp.gui.data;

/**
 * This interface defines the API for listening for progress.
 * @author Rodion "rodde" Efremov
 */
public interface ProgressListener {
    
    public void init(int tokens, String description);
    public void add(int tokens);
    public void set(int tokens);
}
