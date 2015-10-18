package net.coderodde.gsp.model.support;

import net.coderodde.gsp.model.AbstractHeuristicFunction;

/**
 * This class implements a heuristic function in the <tt>n^2 - 1</tt> puzzle.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Oct 18, 2015)
 */
public class PuzzleGraphHeuristicFunction 
extends AbstractHeuristicFunction<PuzzleGraphNode> {

    private final int[] xArray1;
    private final int[] yArray1;
    private final int[] xArray2;
    private final int[] yArray2;
    
    public PuzzleGraphHeuristicFunction(int degree) {
        int n = degree * degree;
        this.xArray1 = new int[n];
        this.yArray1 = new int[n];
        this.xArray2 = new int[n];
        this.yArray2 = new int[n];
    }
    
    @Override
    public double estimate(PuzzleGraphNode source, PuzzleGraphNode target) {
        int n = source.getDegree();
        
        for (int y = 0; y < n; ++y) {
            for (int x = 0; x < n; ++x) {
                int tile1 = source.get(x, y);
                int tile2 = target.get(x, y);
                
                xArray1[tile1] = x;
                yArray1[tile1] = y;
                
                xArray2[tile2] = x;
                yArray2[tile2] = y;
            }
        }
        
        int estimate = 0;
        
        for (int i = 1; i < n; ++i) {
            estimate += Math.abs(xArray1[i] - xArray2[i]) + 
                        Math.abs(yArray1[i] - yArray2[i]);
        }
        
        return estimate;
    }
}
