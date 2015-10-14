package net.coderodde.gsp.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.coderodde.gsp.model.support.AStarPathFinder;
import net.coderodde.gsp.model.support.BidirectionalAStarPathFinder;
import net.coderodde.gsp.model.support.BidirectionalDijkstraPathFinder;
import net.coderodde.gsp.model.support.DijkstraPathFinder;
import org.junit.Test;
import static net.coderodde.gsp.Utils.getPathLength;
import static org.junit.Assert.*;

public class PathFinderTest {
    
    @Test
    public void test() {
        long seed = 1444808340163L; System.currentTimeMillis();
        Random random = new Random(seed);
        GraphData data = getRandomGraphData(10, 40, random);
        
        PathFinder[] finders = new PathFinder[4];
        
        finders[0] = new DijkstraPathFinder(data.weightFunction);
        finders[1] = new BidirectionalDijkstraPathFinder(data.weightFunction);
        finders[2] = new AStarPathFinder(data.weightFunction, 
                                         data.heuristicFunction);
        finders[3] = new BidirectionalAStarPathFinder(data.weightFunction,  
                                                      data.heuristicFunction);
        
        System.out.println("PathFinderTest, seed = " + seed);
        
        for (int i = 0; i < 10; ++i) {
            List<DirectedGraphNode>[] paths = new List[finders.length];
            DirectedGraphNode source = choose(data.graph, random);
            DirectedGraphNode target = choose(data.graph, random);
            
            for (int j = 0; j < finders.length; ++j) {
                paths[j] = finders[j].search(source, target);
            }
            
            double pathLength = getPathLength(paths[0], data.weightFunction);
            
            for (int j = 1; j < finders.length; ++j) {
                assertEquals(pathLength, 
                             getPathLength(paths[j], data.weightFunction),
                             0.0001);
            }
        }
    }
    
    private static final class GraphData {
        List<DirectedGraphNode> graph;
        DirectedGraphWeightFunction weightFunction;
        HeuristicFunction heuristicFunction;
    }
    
    private static GraphData getRandomGraphData(int nodes,
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
    
    private static <T> T choose(List<T> list, Random random) {
        return list.get(random.nextInt(list.size()));
    }
    
    private static final class GraphHeuristicFunction 
    implements HeuristicFunction {

        private final Map<DirectedGraphNode, Point2D.Double> map =
                new HashMap<>();
        
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
