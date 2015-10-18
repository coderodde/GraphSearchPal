package net.coderodde.gsp.model.support;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import net.coderodde.gsp.model.AbstractGraphNode;

/**
 * This class represents a puzzle node for <tt>(n^2 - 1)</tt>-puzzle game.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Oct 18, 2015)
 */
public class PuzzleGraphNode extends AbstractGraphNode<PuzzleGraphNode> {
   
    private static final int MINIMUM_DEGREE = 3;
    private static final int MAXIMUM_DEGREE = 11;
    
    /**
     * This field stores the dimension of the puzzle node being represented.
     */
    private final int degree; 
    
    /**
     * This matrix stores the actual puzzle node cells.
     */
    private final byte[][] matrix;
    
    /**
     * Compactly store the coordinates of empty cell.
     */
    private byte emptyIndex;
    
    /**
     * Creates the goal state of the puzzle graph.
     * 
     * @param degree the degree of the node.
     */
    public PuzzleGraphNode(final int degree) {
        super("");
        
        checkDegree(degree);
        
        this.degree = degree;
        this.matrix = new byte[degree][degree];
        
        byte b = 1;
        
        for (int y = 0; y < degree; ++y) {
            for (int x = 0; x < degree; ++x) {
                matrix[y][x] = b++;
            }
        }
        
        matrix[degree - 1][degree - 1] = 0;
        emptyIndex = (byte)((byte)(degree - 1) | 
                     (byte)(degree - 1) << 4);
    }
    
    /**
     * Creates a clone puzzle node.
     * 
     * @param copy the node to copy.
     */
    private PuzzleGraphNode(PuzzleGraphNode copy) {
        super("");
        
        this.degree = copy.degree;
        this.matrix = new byte[degree][degree];
        
        for (int y = 0; y < degree; ++y) {
            for (int x = 0; x < degree; ++x) {
                this.matrix[y][x] = copy.matrix[y][x];
            }
        }
        
        this.emptyIndex = copy.emptyIndex;
    }
    
    @Override
    public boolean hasChild(final PuzzleGraphNode other) {
        if (getDegree() != other.getDegree()) {
            return false;
        }
        
        int differences = 0;
        
        for (int y = 0; y < degree; ++y) {
            for (int x = 0; x < degree; ++x) {
                if (matrix[y][x] != other.matrix[y][x]) {
                    ++differences;
                    
                    if (differences > 2) {
                        return false;
                    }
                }
            }
        }
        
        return differences == 2;
    }
    
    public PuzzleGraphNode randomSwap(final Random rnd) {
        final PuzzleGraphNode newNode = new PuzzleGraphNode(this);
        
        int sourceX = rnd.nextInt(degree);
        int sourceY = rnd.nextInt(degree);
        
        for (;;) {
            if (matrix[sourceY][sourceX] == 0) {
                sourceX = rnd.nextInt(degree);
                sourceY = rnd.nextInt(degree);
            } else {
                break;
            }
        }
        
        for (;;) {
            int targetX = sourceX;
            int targetY = sourceY;
            
            switch (rnd.nextInt(4)) {
                case 0:
                    --targetX;
                    break;
                    
                case 1:
                    ++targetX;
                    break;
                    
                case 2:
                    --targetY;
                    break;
                    
                case 3:
                    ++targetY;
                    break;
            }
            
            if (targetX < 0 || targetY < 0) {
                continue;
            }
            
            if (targetX >= degree || targetY >= degree) {
                continue;
            }
            
            if (matrix[targetY][targetX] == 0) {
                continue;
            }
            
            byte tmp = newNode.matrix[sourceY][sourceX];
            newNode.matrix[sourceY][sourceX] = newNode.matrix[targetY][targetX];
            newNode.matrix[targetY][targetX] = tmp;
            return newNode;
        }
    }
    
    /**
     * Returns the degree of this node.
     * 
     * @return the degree.
     */
    public int getDegree() {
        return degree;
    }
    
    /**
     * Reads the contents of the cell with coordinates <code>(x, y)</code>.
     * 
     * @param  x the x-coordinate of the cell to read.
     * @param  y the y-coordinate of the cell to read.
     * @return the contents of the specified cell.
     */
    public byte get(final int x, final int y) {
        return matrix[y][x];
    }
    
    /**
     * If there is cells above the current empty cell, moves the latter one cell
     * up.
     * 
     * @return the new puzzle node with the empty cell moved upwards.
     */
    public PuzzleGraphNode moveUp() {
        final int y = getEmptySlotY();
        
        if (y == 0) {
            // Not possible to move up.
            return null;
        }
        
        final int x = getEmptySlotX();
        final PuzzleGraphNode node = new PuzzleGraphNode(this);
        
        node.matrix[y][x] = node.matrix[y - 1][x];
        node.matrix[y - 1][x] = 0;
        node.emptyIndex = createIndex(x, y - 1);
        
        return node;
    }
    
    /**
     * If there is cells on the right of the current empty cell, moves the 
     * latter one cell to the right.
     * 
     * @return the new puzzle node with the empty cell moved to the right or
     *         <code>null</code> if it is not possible to move to the right.
     */
    public PuzzleGraphNode moveRight() {
        final int x = getEmptySlotX();
        
        if (x == degree - 1) {
            // Not possible to move right.
            return null;
        }
        
        final int y = getEmptySlotY();
        final PuzzleGraphNode node = new PuzzleGraphNode(this);
        
        node.matrix[y][x] = node.matrix[y][x + 1];
        node.matrix[y][x + 1] = 0;
        node.emptyIndex = createIndex(x + 1, y);
        
        return node;
    }
    
    /**
     * If there is cells below the current empty cell, moves the latter one cell
     * down.
     * 
     * @return the new puzzle node with the empty cell moved downwards, or
     *         <code>null</code> if it is not possible to move it down.
     */
    public PuzzleGraphNode moveDown() {
        final int y = getEmptySlotY();
        
        if (y == degree - 1) {
            // Not possible to move down.
            return null;
        }
        
        final int x = getEmptySlotX();
        final PuzzleGraphNode node = new PuzzleGraphNode(this);
        
        node.matrix[y][x] = node.matrix[y + 1][x];
        node.matrix[y + 1][x] = 0;
        node.emptyIndex = createIndex(x, y + 1);
        
        return node;
    }
    
    /**
     * If there is cells to the left of  the current empty cell, moves the 
     * latter one cell to the left.
     * 
     * @return the new puzzle node with the empty cell moved to the left, or
     *         <code>null</code> if it is not possible to move left anymore.
     */
    public PuzzleGraphNode moveLeft() {
        final int x = getEmptySlotX();
        
        if (x == 0) {
            // Not possible to move left.
            return null;
        }
        
        final int y = getEmptySlotY();
        final PuzzleGraphNode node = new PuzzleGraphNode(this);
        
        node.matrix[y][x] = node.matrix[y][x - 1];
        node.matrix[y][x - 1] = 0;
        node.emptyIndex = createIndex(x - 1, y);
        
        return node;
    }
    
    /**
     * Returns the string representation of this puzzle node.
     * 
     * @return the string representation.
     */
    @Override
    public String toString() {
        final int totalCells = degree * degree;
        final byte maxNumber = (byte)(totalCells - 1);
        final int numberLength = Byte.toString(maxNumber).length();
        final String formatString = "%-" + numberLength + "d";
        
        final StringBuilder sb = 
                new StringBuilder(totalCells * (numberLength + 1) + degree);
        
        for (int y = 0; y < degree; ++y) {
            for (int x = 0; x < degree; ++x) {
                sb.append(String.format(formatString, matrix[y][x]))
                  .append(' ');
            }
            
            sb.append('\n');
        }
        
        return sb.toString();
    }
    
    /**
     * Returns an <code>Iterable</code> over this node's parent nodes.
     * 
     * @return an <code>Iterable</code>.
     */
    @Override
    public Set<PuzzleGraphNode> children() {
        Set<PuzzleGraphNode> set = new HashSet<>(4);
        PuzzleGraphNode node = moveUp();
        
        if (node != null) {
            set.add(node);
        }
        
        node = moveRight();
        
        if (node != null) {
            set.add(node);
        }
        
        node = moveDown();
        
        if (node != null) {
            set.add(node);
        }
        
        node = moveLeft();
        
        if (node != null) {
            set.add(node);
        }
        
        return set;
    }
    
    /**
     * Returns an <code>Iterable</code> over this node's parent nodes.
     * 
     * @return an <code>Iterable</code>.
     */
    @Override
    public Set<PuzzleGraphNode> parents() {
        Set<PuzzleGraphNode> set = new HashSet<>(4);
        PuzzleGraphNode node = moveUp();
        
        if (node != null) {
            set.add(node);
        }
        
        node = moveRight();
        
        if (node != null) {
            set.add(node);
        }
        
        node = moveDown();
        
        if (node != null) {
            set.add(node);
        }
        
        node = moveLeft();
        
        if (node != null) {
            set.add(node);
        }
        
        return set;
    }

    /**
     * Returns the hash code of this node.
     * 
     * @return the hash code. 
     */
    @Override
    public int hashCode() {
        int hash = 0;
        int factor = 1;
        
        for (final byte[] row : matrix) {
            for (byte b : row) {
                hash += b * factor++;
            }
        }
        
        return hash;
    }

    /**
     * Checks whether this node and <code>o</code> encode the same node.
     * 
     * @param  o another object.
     * @return <code>true</code> only if the two objects are deemed to encode 
     *         the same node.
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof PuzzleGraphNode)) {
            return false;
        }
        
        PuzzleGraphNode other = (PuzzleGraphNode) o;
        
        if (degree != other.degree) {
            return false;
        }
        
        for (int y = 0; y < degree; ++y) {
            for (int x = 0; x < degree; ++x) {
                if (matrix[y][x] != other.matrix[y][x]) {
                    return false;
                }
            }
        }
        
        return true;
    }
    
    /**
     * Extracts the x-coordinate of the empty cell.
     * 
     * @return the x-coordinate.
     */
    public final int getEmptySlotX() {
        return emptyIndex & 0xf;
    }
    
    /**
     * Extracts the y-coordinate of the empty cell.
     * 
     * @return the y-coordinate. 
     */
    public final int getEmptySlotY() {
        return (emptyIndex & 0xf0) >>> 4;
    }
    
    /**
     * Encodes the coordinates of an empty cell.
     * 
     * @param  x the x-coordinate.
     * @param  y the y-coordinate.
     * @return the encoding of the coordinates.
     */
    private final byte createIndex(final int x, final int y) {
        return (byte)((y << 4) | x);
    }
    
    /**
     * Checks the degree.
     * 
     * @param degree the degree to check.
     * @throws java.lang.IllegalArgumentException if the input degree 
     *                                            is invalid.
     */
    private static void checkDegree(final int degree) {
        if (degree < MINIMUM_DEGREE) {
            throw new IllegalArgumentException(
                    "The input degree is too small: " + degree + " but must " +
                    "be at least " + MINIMUM_DEGREE + ".");
        }
        
        if (degree > MAXIMUM_DEGREE) {
            throw new IllegalArgumentException(
                    "The input degree is too large: " + degree + " but must " +
                    "be at most " + MAXIMUM_DEGREE + ".");
        }
    }

    @Override
    public void addChild(PuzzleGraphNode child) {
        
    }

    @Override
    public void removeChild(PuzzleGraphNode child) {
    
    }
}
