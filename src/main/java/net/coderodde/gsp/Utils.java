package net.coderodde.gsp;

import java.util.List;
import net.coderodde.gsp.model.DirectedGraphNode;
import net.coderodde.gsp.model.DirectedGraphWeightFunction;

/**
 * This class provides miscellaneous utility stuff.
 * 
 * @author Rodion "rodde" Efremov
 * @version (Oct 14, 2015)
 */
public class Utils {
    
    public static double 
        getPathLength(List<DirectedGraphNode> path,
                      DirectedGraphWeightFunction weightFunction) {
        double length = 0.0;
        
        for (int i = 0; i < path.size() - 1; ++i) {
            if (!path.get(i).hasChild(path.get(i + 1))) {
                return Double.NaN;
            }
            
            length += weightFunction.get(path.get(i), path.get(i + 1));
        }
        
        return length;
    }
}
