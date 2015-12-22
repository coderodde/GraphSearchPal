package net.coderodde.gsp.model.support;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.coderodde.gsp.model.AbstractGraphNode;

/**
 * This class implements a grid graph node.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Dec 21, 2015)
 */
public class GridGraphNode extends AbstractGraphNode<GridGraphNode> {

    private GridGraphNode north;
    private GridGraphNode east;
    private GridGraphNode south;
    private GridGraphNode west;
    
    private GridGraphNode northEast;
    private GridGraphNode southEast;
    private GridGraphNode southWest;
    private GridGraphNode northWest;
    
    private final int x;
    private final int y;
    
    private final GridGraphConfiguration configuration;
    
    public GridGraphNode(int id, 
                         int x, 
                         int y, 
                         GridGraphConfiguration configuration) {
        super(id);
        this.x = x;
        this.y = y;
        this.configuration = configuration;
    }
    
    public int getX() {
        return x;
    }
    
    public int getY() {
        return y;
    }
    
    public void setNorth(GridGraphNode north) {
        if (north == null) {
            if (this.north != null) {
                this.north.south = null;
                this.north = null;
            }
            
            return;
        }
        
        this.north = north;
        north.south = this;
    }
    
    public void setEast(GridGraphNode east) {
        if (east == null) {
            if (this.east != null) {
                this.east.west = null;
                this.east = null;
            }
            
            return;
        }
        
        this.east = east;
        east.west = this;
    }
    
    public void setSouth(GridGraphNode south) {
        if (south == null) {
            if (this.south != null) {
                this.south.north = null;
                this.south = null;
            }
            
            return;
        }
        
        this.south = south;
        south.north = this;
    }
    
    public void setWest(GridGraphNode west) {
        if (west == null) {
            if (this.west != null) {
                this.west.east = null;
                this.west = null;
            }
            
            return;
        }
        
        this.west = west;
        west.east = this;
    }
    
    public void setNorthEast(GridGraphNode northEast) {
        if (northEast == null) {
            if (this.northEast != null) {
                this.northEast.southWest = null;
                this.northEast = null;
            }
            
            return;
        }
        
        this.northEast = northEast;
        northEast.southWest = this;
    }
    
    public void setSouthEast(GridGraphNode southEast) {
        if (southEast == null) {
            if (this.southEast != null) {
                this.southEast.northWest = null;
                this.southEast = null;
            }
            
            return;
        }
        
        this.southEast = southEast;
        southEast.northWest = this;
    }
    
    public void setSouthWest(GridGraphNode southWest) {
        if (southWest == null) {
            if (this.southWest != null) {
                this.southWest.northEast = null;
                this.southWest = null;
            }
            
            return;
        }
        
        this.southWest = southWest;
        southWest.northEast = this;
    }
    
    public void setNorthWest(GridGraphNode northWest) {
        if (northWest == null) {
            if (this.northWest != null) {
                this.northWest.southEast = null;
                this.northWest = null;
            }
            
            return;
        }
        
        this.northWest = northWest;
        northWest.southEast = this;
    }
    
    @Override
    public void addChild(GridGraphNode child) {
        throw new UnsupportedOperationException(
                "Operation not supported for GridGraphNode.");
    }

    @Override
    public boolean hasChild(GridGraphNode child) {
        throw new UnsupportedOperationException(
                "Operation not supported for GridGraphNode.");
    }

    @Override
    public void removeChild(GridGraphNode child) {
        throw new UnsupportedOperationException(
                "Operation not supported for GridGraphNode.");
    }

    @Override
    public Collection<GridGraphNode> children() {
        return gatherNeighborsIntoList();
    }

    @Override
    public Collection<GridGraphNode> parents() {
        return gatherNeighborsIntoList();
    }

    @Override
    public void clear() {
        setNorth(null);
        setEast(null);
        setSouth(null);
        setWest(null);
        
        clearDiagonalEdges();
    }
    
    public void clearDiagonalEdges() {
        setNorthEast(null);
        setSouthEast(null);
        setSouthWest(null);
        setNorthWest(null);
    }
    
    private Collection<GridGraphNode> gatherNeighborsIntoList() {
        List<GridGraphNode> ret = new ArrayList<>(8);
       
        tryAdd(ret, north);
        tryAdd(ret, east);
        tryAdd(ret, south);
        tryAdd(ret, west);
       
        if (configuration.diagonalsAllowed() == false) {
            return ret;
        }
        
        if (configuration.crossingCornersAllowed()) {
            tryAdd(ret, northEast);
            tryAdd(ret, southEast);
            tryAdd(ret, southWest);
            tryAdd(ret, northWest);
            
            return ret;
        }
        
        // Once here, diagonal edges are allowed, yet crossing the corners is 
        // not.
        boolean northIsPassable = isPassable(north);
        boolean eastIsPassable  = isPassable(east);
        boolean southIsPassable = isPassable(south);
        boolean westIsPassable  = isPassable(west);
        
        if (isPassable(northEast) && northIsPassable && eastIsPassable) {
            ret.add(northEast);
        }
        
        if (isPassable(southEast) && southIsPassable && eastIsPassable) {
            ret.add(southEast);
        }
        
        if (isPassable(southWest) && southIsPassable && westIsPassable) {
            ret.add(southWest);
        }
        
        if (isPassable(northWest) && northIsPassable && westIsPassable) {
            ret.add(northWest);
        }
        
        return ret;
    }
    
    private boolean isPassable(GridGraphNode node) {
        return node != null && !configuration.isWallNode(node);
    }
    
    private void tryAdd(Collection<GridGraphNode> collection, 
                        GridGraphNode node) {
        if (node != null && !configuration.isWallNode(node)) {
            collection.add(node);
        }
    }
}
