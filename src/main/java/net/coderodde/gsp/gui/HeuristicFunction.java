package net.coderodde.gsp.gui;

/**
 * This enumeration enumerates the available heuristic functions.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Dec 26, 2015)
 */
public enum HeuristicFunction {
   
    CHEBYSHEV ("Chebyshev"),
    MANHATTAN ("Manhattan"),
    OCTILE    ("Octile"),
    EUCLIDEAN ("Euclidean");
    
    private final String name;
    
    private HeuristicFunction(String name) {
        this.name = name;
    }
    
    public static String[] getHeuristicFunctionNames() {
        HeuristicFunction[] all = values();
        String[] ret = new String[all.length];
        
        for (int i = 0; i < all.length; ++i) {
            ret[i] = all[i].name;
        }
        
        return ret;
    }
}
