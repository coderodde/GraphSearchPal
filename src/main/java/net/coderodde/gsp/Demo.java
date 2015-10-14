package net.coderodde.gsp;

import java.awt.Point;
import java.util.List;
import java.util.Random;
import net.coderodde.gsp.Utils.GraphData;
import static net.coderodde.gsp.Utils.choose;
import static net.coderodde.gsp.Utils.getPathLength;
import static net.coderodde.gsp.Utils.getRandomGraphData;
import net.coderodde.gsp.model.DirectedGraphNode;
import net.coderodde.gsp.model.DirectedGraphWeightFunction;
import net.coderodde.gsp.model.support.AStarPathFinder;
import net.coderodde.gsp.model.support.BidirectionalAStarPathFinder;
import net.coderodde.gsp.model.support.BidirectionalDijkstraPathFinder;
import net.coderodde.gsp.model.support.DijkstraPathFinder;
import net.coderodde.gsp.model.support.GridHeuristicFunction;
import net.coderodde.gsp.model.support.NewBidirectionalAStarPathFinder;

/**
 *
 * @author rodionefremov
 */
public class Demo {

    public static void main(String[] args) {
        long seed = System.currentTimeMillis();
        Random random = new Random(seed);
        GridGraphData data = createGridGraph(200, 200);
        DirectedGraphNode source = getRandomNode(data.graph, random);
        DirectedGraphNode target = getRandomNode(data.graph, random);
        System.out.println("Seed: " + seed);
        System.out.println("Source: " + source);
        System.out.println("Target: " + target);
        
        long startTime = System.currentTimeMillis();
        List<DirectedGraphNode> path1 = 
                new DijkstraPathFinder(data.weightFunction).search(source, 
                                                                   target);
        long endTime = System.currentTimeMillis();
        
        System.out.println("DijkstraPathFinder in " + (endTime - startTime) +
                           " milliseconds.");
        System.out.println("Path length: " + 
                getPathLength(path1, data.weightFunction));
        
        startTime = System.currentTimeMillis();
        List<DirectedGraphNode> path2 = 
                new BidirectionalDijkstraPathFinder(data.weightFunction)
                        .search(source, target);
        endTime = System.currentTimeMillis();
        
        System.out.println("BidirectionalDijkstraPathFinder in " + 
                           (endTime - startTime) + " milliseconds.");
        System.out.println("Path length: " + 
                getPathLength(path2, data.weightFunction));
        
        startTime = System.currentTimeMillis();
        List<DirectedGraphNode> path3 = 
                new AStarPathFinder(data.weightFunction, 
                                    data.heuristicFunction).search(source,
                                                                   target);
        endTime = System.currentTimeMillis();
        
        System.out.println("AStarPathFinder in " + 
                           (endTime - startTime) + " milliseconds.");
        System.out.println("Path length: " + 
                getPathLength(path3, data.weightFunction));
        
        startTime = System.currentTimeMillis();
        List<DirectedGraphNode> path4 = 
                new BidirectionalAStarPathFinder(
                        data.weightFunction, 
                        data.heuristicFunction).search(source, target);
        endTime = System.currentTimeMillis();
        
        System.out.println("BidirectionalAStarPathFinder in " + 
                           (endTime - startTime) + " milliseconds.");
        System.out.println("Path length: " + 
                getPathLength(path4, data.weightFunction));
        
        startTime = System.currentTimeMillis();
        List<DirectedGraphNode> path5 = 
                new NewBidirectionalAStarPathFinder(
                        data.weightFunction, 
                        data.heuristicFunction).search(source, target);
        endTime = System.currentTimeMillis();
        
        System.out.println("NewBidirectionalAStarPathFinder in " + 
                           (endTime - startTime) + " milliseconds.");
        System.out.println("Path length: " + 
                getPathLength(path5, data.weightFunction));
        
        System.out.println("---");
        
        GraphData data2 = getRandomGraphData(1_000_000, 4_000_000, random);
        
        source = choose(data2.graph, random);
        target = choose(data2.graph, random);
        
        System.out.println("Source: " + source);
        System.out.println("Target: " + target);
        
        startTime = System.currentTimeMillis();
        path1 = new DijkstraPathFinder(data2.weightFunction).search(source, 
                                                                   target);
        endTime = System.currentTimeMillis();
        
        System.out.println("DijkstraPathFinder in " + (endTime - startTime) +
                           " milliseconds.");
        System.out.println("Path length: " + 
                getPathLength(path1, data2.weightFunction));
        
        startTime = System.currentTimeMillis();
        path2 = new BidirectionalDijkstraPathFinder(data2.weightFunction)
                        .search(source, target);
        endTime = System.currentTimeMillis();
        
        System.out.println("BidirectionalDijkstraPathFinder in " + 
                           (endTime - startTime) + " milliseconds.");
        System.out.println("Path length: " + 
                getPathLength(path2, data2.weightFunction));
        
        startTime = System.currentTimeMillis();
        path3 = new AStarPathFinder(data2.weightFunction, 
                                    data2.heuristicFunction).search(source,
                                                                    target);
        endTime = System.currentTimeMillis();
        
        System.out.println("AStarPathFinder in " + 
                           (endTime - startTime) + " milliseconds.");
        System.out.println("Path length: " + 
                getPathLength(path3, data2.weightFunction));
        
        startTime = System.currentTimeMillis();
        path4 = new BidirectionalAStarPathFinder(
                        data2.weightFunction, 
                        data2.heuristicFunction).search(source, target);
        endTime = System.currentTimeMillis();
        
        System.out.println("BidirectionalAStarPathFinder in " + 
                           (endTime - startTime) + " milliseconds.");
        System.out.println("Path length: " + 
                getPathLength(path4, data2.weightFunction));
        
        startTime = System.currentTimeMillis();
        path5 = new NewBidirectionalAStarPathFinder(
                        data2.weightFunction, 
                        data2.heuristicFunction).search(source, target);
        endTime = System.currentTimeMillis();
        
        System.out.println("NewBidirectionalAStarPathFinder in " + 
                           (endTime - startTime) + " milliseconds.");
        System.out.println("Path length: " + 
                getPathLength(path5, data2.weightFunction));
    }
    
    private static DirectedGraphNode getRandomNode(DirectedGraphNode[][] grid,
                                                   Random random) {
        return grid[random.nextInt(grid.length)]
                   [random.nextInt(grid[0].length)];
    }
    
    private static final class GridGraphData {
        DirectedGraphNode[][] graph;
        DirectedGraphWeightFunction weightFunction;
        GridHeuristicFunction heuristicFunction;
    }
    
    private static GridGraphData createGridGraph(int width, int height) {
        DirectedGraphNode[][] grid = new DirectedGraphNode[height][width];
        DirectedGraphWeightFunction weightFunction 
                = new DirectedGraphWeightFunction();
        GridHeuristicFunction heuristicFunction = new GridHeuristicFunction();
        
        Point point = new Point();
        
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                grid[y][x] = new DirectedGraphNode("(x = " + x + 
                                                   ", y = " + y + ")");
                point.x = x;
                point.y = y;
                heuristicFunction.put(grid[y][x], point);
            }
        }
        
        // Create horizontal arcs.
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width - 1; ++x) {
                connect(grid[y][x], grid[y][x + 1], weightFunction, 1.0);
            }
        }
        
        // Create vertical arcs.
        for (int y = 0; y < height - 1; ++y) {
            for (int x = 0; x < width; ++x) {
                connect(grid[y][x], grid[y + 1][x], weightFunction, 1.0);
            }
        }
        
        double SQRT2 = Math.sqrt(2.0);
        
        // Create diagonal \ edges.
        for (int y = 0; y < height - 1; ++y) {
            for (int x = 0; x < width - 1; ++x) {
                connect(grid[y][x], grid[y + 1][x + 1], weightFunction, SQRT2);
            }
        }
        
        // Create diagonal / edges.
        for (int y = 0; y < height - 1; ++y) {
            for (int x = 1; x < width; ++x) {
                connect(grid[y][x], grid[y + 1][x - 1], weightFunction, SQRT2);
            }
        }
        
        GridGraphData ret = new GridGraphData();
        ret.graph = grid;
        ret.weightFunction = weightFunction;
        ret.heuristicFunction = heuristicFunction;
        return ret;
    }
    
    private static void connect(DirectedGraphNode a,
                                DirectedGraphNode b,
                                DirectedGraphWeightFunction weightFunction,
                                double weight) {
        a.addChild(b);
        b.addChild(a);
        weightFunction.put(a, b, weight);
        weightFunction.put(b, a, weight);
    }
}

