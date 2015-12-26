package net.coderodde.gsp.gui;

/**
 * This enumeration enumerates all the implemented heap data structures.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Dec 26, 2015)
 */
public enum Heap {
    
    DARY      ("d-ary"),
    BINOMIAL  ("Binomial"),
    FIBONACCI ("Fibonacci"),
    PAIRING   ("Pairing");
    
    private final String name;
    
    private Heap(String name) {
        this.name = name;
    }
    
    public static String[] getHeapNames() {
        Heap[] all = values();
        String[] ret = new String[all.length];
        
        for (int i = 0; i < all.length; ++i) {
            ret[i] = all[i].name;
        }
        
        return ret;
    }
}
