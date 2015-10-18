package net.coderodde.gsp;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.List;
import java.util.Random;
import net.coderodde.gsp.Utils.GraphData;
import static net.coderodde.gsp.Utils.choose;
import static net.coderodde.gsp.Utils.getPathLength;
import static net.coderodde.gsp.Utils.getRandomGraphData;
import net.coderodde.gsp.model.support.DirectedGraphNode;
import net.coderodde.gsp.model.support.DirectedGraphWeightFunction;
import net.coderodde.gsp.model.queue.MinimumPriorityQueue;
import net.coderodde.gsp.model.queue.support.FibonacciHeap;
import net.coderodde.gsp.model.support.AStarPathFinder;
import net.coderodde.gsp.model.support.BidirectionalAStarPathFinder;
import net.coderodde.gsp.model.support.BidirectionalDijkstraPathFinder;
import net.coderodde.gsp.model.support.DijkstraPathFinder;
import net.coderodde.gsp.model.support.GridHeuristicFunction;
import net.coderodde.gsp.model.support.NewBidirectionalAStarPathFinder;
import net.coderodde.gsp.model.support.ParallelNewBidirectionalAStarPathFinder;

public class Demo {

    public static void main(String[] args) {
        demoGridGraph();
        demoGeneralGraph();
    }
    
    private static DirectedGraphNode getSource(GraphData data) {
        DirectedGraphNode node = data.graph.get(0);
        
        double x = data.heuristicFunction.get(node).x;
        double y = data.heuristicFunction.get(node).y;
        
        Point2D.Double origin = new Point2D.Double(0.0, 0.0);
        Point2D.Double tmp = new Point2D.Double(x, y);
        
        for (DirectedGraphNode n : data.graph) {
            Point2D.Double p = data.heuristicFunction.get(n);
            
            if (p.distance(origin) < tmp.distance(origin)) {
                tmp.x = p.x;
                tmp.y = p.y;
                node = n;
            }
        }
        
        return node;
    }
    
    private static DirectedGraphNode getTarget(GraphData data) {
        DirectedGraphNode node = data.graph.get(0);
        
        double x = data.heuristicFunction.get(node).x;
        double y = data.heuristicFunction.get(node).y;
        
        Point2D.Double origin = new Point2D.Double(0.0, 0.0);
        Point2D.Double tmp = new Point2D.Double(x, y);
        
        for (DirectedGraphNode n : data.graph) {
            Point2D.Double p = data.heuristicFunction.get(n);
            
            if (p.distance(origin) > tmp.distance(origin)) {
                tmp.x = p.x;
                tmp.y = p.y;
                node = n;
            }
        }
        
        return node;
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
    
    private static void demoGridGraph() {
        long seed = System.currentTimeMillis();
        Random random = new Random(seed);
        GridGraphData data = createGridGraph(400, 400);
        
//        DirectedGraphNode source = getRandomNode(data.graph, random);
//        DirectedGraphNode target = getRandomNode(data.graph, random);
        
        DirectedGraphNode source = data.graph[0][0];
        DirectedGraphNode target = data.graph[399][399];
        
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
        
        startTime = System.currentTimeMillis();
        List<DirectedGraphNode> path6 = 
                new ParallelNewBidirectionalAStarPathFinder(
                        data.weightFunction, 
                        data.heuristicFunction).search(source, target);
        endTime = System.currentTimeMillis();
        
        System.out.println("ParallelNewBidirectionalAStarPathFinder in " + 
                           (endTime - startTime) + " milliseconds.");
        System.out.println("Path length: " + 
                getPathLength(path6, data.weightFunction));
        
        System.out.println();
    }
    
    private static void demoGeneralGraph() {
        long seed = System.currentTimeMillis();
        Random random = new Random(seed);
        
        MinimumPriorityQueue<DirectedGraphNode> queue = new FibonacciHeap<>();
        GraphData data2 = getRandomGraphData(1_000_000, 4_000_000, random);
        
        DirectedGraphNode source = choose(data2.graph, random);
        DirectedGraphNode target = choose(data2.graph, random);
//        source = getSource(data2);
//        target = getTarget(data2);
        
        System.out.println("Source: " + source);
        System.out.println("Target: " + target);
        
        long startTime = System.currentTimeMillis();
        List<DirectedGraphNode> path1 
                = new DijkstraPathFinder(data2.weightFunction)
                      .setQueue(null)
                      .search(source, target);
        long endTime = System.currentTimeMillis();
        
        System.out.println("DijkstraPathFinder in " + (endTime - startTime) +
                           " milliseconds.");
        System.out.println("Path length: " + 
                getPathLength(path1, data2.weightFunction));
        
        startTime = System.currentTimeMillis();
        List<DirectedGraphNode> path2 = 
                new BidirectionalDijkstraPathFinder(data2.weightFunction)
                    .setQueue(null)
                    .search(source, target);
        endTime = System.currentTimeMillis();
        
        System.out.println("BidirectionalDijkstraPathFinder in " + 
                           (endTime - startTime) + " milliseconds.");
        System.out.println("Path length: " + 
                getPathLength(path2, data2.weightFunction));
        
        startTime = System.currentTimeMillis();
        List<DirectedGraphNode> path3 = 
                new AStarPathFinder(data2.weightFunction, 
                                    data2.heuristicFunction)
                    .setQueue(null).search(source, target);
        endTime = System.currentTimeMillis();
        
        System.out.println("AStarPathFinder in " + 
                           (endTime - startTime) + " milliseconds.");
        System.out.println("Path length: " + 
                getPathLength(path3, data2.weightFunction));
        
        startTime = System.currentTimeMillis();
        List<DirectedGraphNode> path4 = new BidirectionalAStarPathFinder(
                        data2.weightFunction, 
                        data2.heuristicFunction)
                    .setQueue(null).search(source, target);
        endTime = System.currentTimeMillis();
        
        System.out.println("BidirectionalAStarPathFinder in " + 
                           (endTime - startTime) + " milliseconds.");
        System.out.println("Path length: " + 
                getPathLength(path4, data2.weightFunction));
        
        startTime = System.currentTimeMillis();
        List<DirectedGraphNode> path5 = new NewBidirectionalAStarPathFinder(
                        data2.weightFunction, 
                        data2.heuristicFunction)
                    .setQueue(null).search(source, target);
        endTime = System.currentTimeMillis();
        
        System.out.println("NewBidirectionalAStarPathFinder in " + 
                           (endTime - startTime) + " milliseconds.");
        System.out.println("Path length: " + 
                getPathLength(path5, data2.weightFunction));
        
        startTime = System.currentTimeMillis();
        List<DirectedGraphNode> path6 = 
                new ParallelNewBidirectionalAStarPathFinder(
                        data2.weightFunction, 
                        data2.heuristicFunction)
                    .setQueue(null).search(source, target);
        endTime = System.currentTimeMillis();
        
        System.out.println("ParallelNewBidirectionalAStarPathFinder in " + 
                           (endTime - startTime) + " milliseconds.");
        System.out.println("Path length: " + 
                getPathLength(path6, data2.weightFunction));
        
        System.out.println();
    }
}
