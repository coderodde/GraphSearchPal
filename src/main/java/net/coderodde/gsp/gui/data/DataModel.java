package net.coderodde.gsp.gui.data;

import java.awt.Point;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import net.coderodde.gsp.model.support.DirectedGraphNode;

/**
 * This class manages all the information of the Graph Pal.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Dec 18, 2015)
 */
public class DataModel {
   
    private final DirectedGraphNode[][] graph;
    private final Set<DirectedGraphNode> wallNodeSet;
    private final Set<DirectedGraphNode> openNodeSet;
    private final Set<DirectedGraphNode> closedNodeSet;
    private final Map<DirectedGraphNode, Point> nodesToCoordinatesMap;
    
    public DataModel(int width, int height, ProgressListener progressListener) {
        this.graph = new DirectedGraphNode[height][width];
        
        if (progressListener != null) {
            progressListener.init(height, "Building the graph.");
        }
        
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
//                graph[y][x] = new DirectedGraphNode("[" + x + "," + y + "]");
            }

            if (progressListener != null) {
                progressListener.add(1);
            }
        }
        
        this.wallNodeSet = new HashSet<>(height * width);
        this.openNodeSet = new HashSet<>();
        this.closedNodeSet = new HashSet<>(height * width);
        this.nodesToCoordinatesMap = new HashMap<>();
    }
    
    
}
