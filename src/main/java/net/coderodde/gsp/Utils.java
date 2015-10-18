package net.coderodde.gsp;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.coderodde.gsp.model.support.DirectedGraphNode;
import net.coderodde.gsp.model.support.DirectedGraphWeightFunction;
import net.coderodde.gsp.model.AbstractHeuristicFunction;

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
        
    public static <T> T choose(List<T> list, Random random) {
        return list.get(random.nextInt(list.size()));
    }
    
    public static final class GraphData {
        public List<DirectedGraphNode> graph;
        public DirectedGraphWeightFunction weightFunction;
        public GraphHeuristicFunction heuristicFunction;
    }
    
    public static GraphData getRandomGraphData(int nodes,
                                               int arcs,
                                               Random random) {
        List<DirectedGraphNode> graph = new ArrayList<>(nodes);
        GraphHeuristicFunction heuristicFunction = new GraphHeuristicFunction();
        DirectedGraphWeightFunction weightFunction = 
                new DirectedGraphWeightFunction();
        
        for (int i = 0; i < nodes; ++i) {
            DirectedGraphNode node = new DirectedGraphNode("" + i);
            graph.add(node);
            heuristicFunction
                    .put(node, 
                         new Point2D.Double(1000.0 * random.nextDouble(),
                                            1000.0 * random.nextDouble()));
        }
        
        while (arcs-- > 0) {
            DirectedGraphNode tail = choose(graph, random);
            DirectedGraphNode head = choose(graph, random);
            
            double distance = heuristicFunction.estimate(tail, head);
            
            tail.addChild(head);
            weightFunction.put(tail, head, 1.2 * distance);
        }
        
        GraphData ret = new GraphData();
        ret.graph = graph;
        ret.weightFunction = weightFunction;
        ret.heuristicFunction = heuristicFunction;
        return ret;
    }
    
    public static final class GraphHeuristicFunction 
    extends AbstractHeuristicFunction<DirectedGraphNode> {

        private final Map<DirectedGraphNode, Point2D.Double> map =
                new HashMap<>();
        
        public Point2D.Double get(DirectedGraphNode node) {
            return map.get(node);
        }
        
        void put(DirectedGraphNode node, Point2D.Double point) {
            map.put(node, point);
        }
        
        @Override
        public double estimate(DirectedGraphNode source, 
                               DirectedGraphNode target) {
            Point2D.Double pa = map.get(source);
            Point2D.Double pb = map.get(target);
            return pa.distance(pb);
        }
    }
}
