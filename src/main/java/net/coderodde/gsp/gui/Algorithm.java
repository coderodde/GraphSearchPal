package net.coderodde.gsp.gui;

/**
 * This enumeration enumerates supported shortest path search algorithms.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Dec 26, 2015)
 */
public enum Algorithm {
   
    BFS         ("BFS"),
    BI_BFS      ("Bidirectional BFS"),
    DIJKSTRA    ("Dijkstra"),
    BI_DIJKSTRA ("Bidrectional Dijkstra"),
    A_STAR      ("A*"),
    BHPA        ("BHPA"),
    NBA         ("NBA*"),
    PNBA        ("PNBA*");
    
    private final String name;
    
    private Algorithm(String name) {
        this.name = name;
    }
    
    @Override
    public String toString() {
        return name;
    }
    
    public static String[] getAlgorithmNames() {
        Algorithm[] all = values();
        String[] ret = new String[all.length];
        
        for (int i = 0; i < ret.length; ++i) {
            ret[i] = all[i].name;
        }
        
        return ret;
    }
}
