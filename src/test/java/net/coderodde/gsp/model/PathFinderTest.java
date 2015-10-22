package net.coderodde.gsp.model;

import net.coderodde.gsp.model.support.DirectedGraphNode;
import java.util.List;
import java.util.Random;
import net.coderodde.gsp.Utils.GraphData;
import static net.coderodde.gsp.Utils.choose;
import net.coderodde.gsp.model.support.AStarPathFinder;
import net.coderodde.gsp.model.support.BidirectionalAStarPathFinder;
import net.coderodde.gsp.model.support.BidirectionalDijkstraPathFinder;
import net.coderodde.gsp.model.support.DijkstraPathFinder;
import net.coderodde.gsp.model.support.NewBidirectionalAStarPathFinder;
import org.junit.Test;
import static net.coderodde.gsp.Utils.getPathLength;
import static net.coderodde.gsp.Utils.getRandomGraphData;
import net.coderodde.gsp.model.support.ParallelNewBidirectionalAStarPathFinder;
import static org.junit.Assert.*;

public class PathFinderTest {
    
    @Test
    public void test() {
        long seed = System.currentTimeMillis();
        Random random = new Random(seed);
        GraphData data = getRandomGraphData(10000, 50000, random);
        
        AbstractPathFinder[] finders = new AbstractPathFinder[6];
        
        finders[0] = new DijkstraPathFinder(data.weightFunction);
        finders[1] = new BidirectionalDijkstraPathFinder(data.weightFunction);
        finders[2] = new AStarPathFinder(data.weightFunction, 
                                         data.heuristicFunction);
        finders[3] = new BidirectionalAStarPathFinder(data.weightFunction,  
                                                      data.heuristicFunction);
        finders[4] = 
                new NewBidirectionalAStarPathFinder(data.weightFunction,
                                                    data.heuristicFunction);
        
        finders[5] =
                new ParallelNewBidirectionalAStarPathFinder(
                        data.weightFunction,
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
}
