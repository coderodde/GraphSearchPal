package net.coderodde.gsp;

import java.awt.Point;
import java.util.List;
import java.util.Random;
import net.coderodde.gsp.model.DirectedGraphNode;
import net.coderodde.gsp.model.DirectedGraphWeightFunction;
import net.coderodde.gsp.model.support.AStarPathFinder;
import net.coderodde.gsp.model.support.BidirectionalDijkstraPathFinder;
import net.coderodde.gsp.model.support.DijkstraPathFinder;
import net.coderodde.gsp.model.support.GridHeuristicFunction;

/**
 *
 * @author rodionefremov
 */
public class Demo {

    public static void main(String[] args) {
        long seed = System.currentTimeMillis();
        Random random = new Random(seed);
        GridGraphData data = createGridGraph(700, 700);
        DirectedGraphNode source = getRandomNode(data.graph, random);
        DirectedGraphNode target = getRandomNode(data.graph, random);
        System.out.println("Seed: " + seed);
        System.out.println("Source: " + source);
        System.out.println("Target: " + target);
        
        long startTime = System.currentTimeMillis();
        List<DirectedGraphNode> path1 = 
                new DijkstraPathFinder().search(source, 
                                                target, 
                                                data.weightFunction);
        long endTime = System.currentTimeMillis();
        
        System.out.println("DijkstraPathFinder in " + (endTime - startTime) +
                           " milliseconds.");
        System.out.println("Path length: " + 
                getPathLength(path1, data.weightFunction));
        
        startTime = System.currentTimeMillis();
        List<DirectedGraphNode> path2 = 
                new BidirectionalDijkstraPathFinder().search(source, 
                                                         target, 
                                                         data.weightFunction);
        endTime = System.currentTimeMillis();
        
        System.out.println("BidirectionalDijkstraPathFinder in " + 
                           (endTime - startTime) + " milliseconds.");
        System.out.println("Path length: " + 
                getPathLength(path2, data.weightFunction));
        
        startTime = System.currentTimeMillis();
        List<DirectedGraphNode> path3 = 
                new AStarPathFinder(data.heuristicFunction)
                        .search(source, 
                                target, 
                                data.weightFunction);
        endTime = System.currentTimeMillis();
        
        System.out.println("AStarPathFinder in " + 
                           (endTime - startTime) + " milliseconds.");
        System.out.println("Path length: " + 
                getPathLength(path3, data.weightFunction));
    }
    
    private static DirectedGraphNode getRandomNode(DirectedGraphNode[][] grid,
                                                   Random random) {
        return grid[random.nextInt(grid.length)]
                   [random.nextInt(grid[0].length)];
    }
    
    private static double 
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

