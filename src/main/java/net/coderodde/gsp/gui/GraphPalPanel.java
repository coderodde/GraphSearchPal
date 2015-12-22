package net.coderodde.gsp.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import javax.swing.JPanel;
import net.coderodde.gsp.model.AbstractPathFinder;
import net.coderodde.gsp.model.GraphSearchListener;
import net.coderodde.gsp.model.support.GridGraphConfiguration;
import net.coderodde.gsp.model.support.GridGraphNode;
import net.coderodde.gsp.model.support.GridGraphWeightFunction;
import net.coderodde.gsp.model.support.UndirectedGraphNode;

/**
 * This class implements the panel used for displaying the graph search progress
 * and for drawing the graph.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6
 */
public class GraphPalPanel extends JPanel 
implements GraphSearchListener<UndirectedGraphNode> {
   
    /**
     * The default color for the nodes that are not walls.
     */
    private static final Color DEFAULT_NON_WALL_COLOR = Color.WHITE;
    
    /**
     * The default color for the walls.
     */
    private static final Color DEFAULT_WALL_COLOR = Color.BLACK;
    
    /**
     * The default color of the source node.
     */
    private static final Color DEFAULT_SOURCE_COLOR = Color.GREEN;
    
    /**
     * The default color of the target node.
     */
    private static final Color DEFAULT_TARGET_COLOR = Color.RED;
    
    /**
     * The default color of the open nodes.
     */
    private static final Color DEFAULT_OPEN_COLOR = Color.ORANGE;
    
    /**
     * The default color of the closed nodes.
     */
    private static final Color DEFAULT_CLOSED_COLOR = Color.LIGHT_GRAY;
    
    /**
     * The default color of the shortest paths.
     */
    private static final Color DEFAULT_PATH_COLOR = Color.BLUE;
    
    /**
     * The cost of diagonal edges.
     */
    private static final double SQRT2 = Math.sqrt(2.0);
    
    private GridGraphConfiguration configuration;
    private GridGraphNode[][] graph;
    private GridGraphWeightFunction weightFunction =
            new GridGraphWeightFunction();
    
    private Set<GridGraphNode> openNodeSet;
    private Set<GridGraphNode> closedNodeSet;
    
    private List<GridGraphNode> path;
    
    private Color nonWallColor = DEFAULT_NON_WALL_COLOR;
    private Color wallColor    = DEFAULT_WALL_COLOR;
    private Color sourceColor  = DEFAULT_SOURCE_COLOR;
    private Color targetColor  = DEFAULT_TARGET_COLOR;
    private Color openColor    = DEFAULT_OPEN_COLOR;
    private Color closedColor  = DEFAULT_CLOSED_COLOR;
    private Color pathColor    = DEFAULT_PATH_COLOR;
    
    private WallBrush wallBrush = new WallBrush();
    
    private Point sourcePoint = new Point();
    private Point targetPoint = new Point();
    
    private ProgressFrame progressFrame;
    private boolean locked = false;
    private BufferedImage image;
    
    public void setProgressFrame(ProgressFrame progressFrame) {
        this.progressFrame = progressFrame;
    }
    
    public void createGridGraph(int width, 
                                int height, 
                                GridGraphConfiguration configuration) {
        this.configuration = configuration;
        createNodes(width, height);
        createEdges();
        
        this.image = new BufferedImage(width, 
                                       height, 
                                       BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();
        g.setColor(nonWallColor);
        g.fillRect(0, 0, width, height);
        repaint();
    }
    
    public WallBrush getWallBrush() {
        return wallBrush;
    }
    
    private void createNodes(int width, int height) {
        graph = new GridGraphNode[height][width];
        
        int id = 0;
        
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                graph[y][x] = new GridGraphNode(id++, x, y, configuration);
            }
            
            if (progressFrame != null) {
                progressFrame.add(1);
            }
        }
    }
    
    public void setNodeAsWall(int x, int y) {
        Graphics g = image.getGraphics();
        g.setColor(wallColor);
        
        int wallBrushWidth  = wallBrush.getWidth();
        int wallBrushHeight = wallBrush.getHeight();
        
        g.fillRect(x - (wallBrushWidth >> 1),
                   y - (wallBrushHeight >> 1),
                   wallBrushWidth,
                   wallBrushHeight);
        
        repaint();
    }
    
    private void createEdges() {
        createHorizontalEdges();
        createVerticalEdges();
        createTopLeftBottomRightDiagonalEdges();
        createTopRightBottomLeftDiagonalEdges();
    }
    
    private void createHorizontalEdges() {
        int width = graph[0].length;
        int height = graph.length;
        
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width - 1; ++x) {
                graph[y][x].setEast(graph[y][x + 1]);
            }
        }
    }
    
    private void createVerticalEdges() {
        int width = graph[0].length;
        int height = graph.length;
        
        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height - 1; ++y) {
                graph[y][x].setSouth(graph[y + 1][x]);
            }
        }
    }
    
    private void createTopLeftBottomRightDiagonalEdges() {
        int width = graph[0].length;
        int height = graph.length;
        
        for (int y = 0; y < height - 1; ++y) {
            for (int x = 0; x < width - 1; ++x) {
                graph[y][x].setSouthEast(graph[y + 1][x + 1]);
            }
        }
    }
    
    private void createTopRightBottomLeftDiagonalEdges() {
        int width = graph[0].length;
        int height = graph.length;
        
        for (int y = 0; y < height - 1; ++y) {
            for (int x = 1; x < width; ++x) {
                graph[y][x].setSouthWest(graph[y + 1][x - 1]);
            }
        }
    }
    
    public void setNonWallColor(Color c) {
        Objects.requireNonNull(c, "The color is null.");
        this.nonWallColor = c;
    }
    
    public void setWallColor(Color c) {
        Objects.requireNonNull(c, "The color is null.");
        this.wallColor = c;
    }
    
    public void setSourceColor(Color c) {
        Objects.requireNonNull(c, "The color is null.");
        this.sourceColor = c;
    }
    
    public void setTargetColor(Color c) {
        Objects.requireNonNull(c, "The color is null.");
        this.targetColor = c;
    }
    
    public void setOpenColor(Color c) {
        Objects.requireNonNull(c, "The color is null.");
        this.openColor = c;
    }
    
    public void setClosedColor(Color c) {
        Objects.requireNonNull(c, "The color is null.");
        this.closedColor = c;
    }
    
    @Override
    public void paint(Graphics g) {
        update(g);
    }
    
    @Override
    public void update(Graphics g) {
        g.drawImage(image, 0, 0, this);
//        g.setColor(Color.YELLOW);
//        g.drawRect(100, 100, 300, 200);
//        g.setColor(Color.blue);
//        int width  = getWidth();
//        int height = getHeight();
//        g.clearRect(0, 0, width, height);
//        BufferedImage image = new BufferedImage(width, 
//                                                height, 
//                                                BufferedImage.TYPE_INT_RGB);
//        Graphics2D imgg = image.createGraphics();
//        imgg.setColor(nonWallColor);
//        imgg.fillRect(0, 0, width, height);
//        
//        for (UndirectedGraphNode node : wallNodeSet) {
//            Point point = nodesToCoordinatesMap.get(node);
//            
//            if (point.x < width && point.y < height) {
//                image.setRGB(point.x, point.y, wallColor.getRGB());
//            }
//        }
//        
//        for (UndirectedGraphNode node : closedNodeSet) {
//            Point point = nodesToCoordinatesMap.get(node);
//            image.setRGB(point.x, point.y, closedColor.getRGB());
//        }
//        
//        for (UndirectedGraphNode node : openNodeSet) {
//            Point point = nodesToCoordinatesMap.get(node);
//            image.setRGB(point.x, point.y, openColor.getRGB());
//        }
//        
//        for (UndirectedGraphNode node : path) {
//            Point point = nodesToCoordinatesMap.get(node);
//            image.setRGB(point.x, point.y, pathColor.getRGB());
//        }
//        
//        g.drawImage(image, 0, 0, this);
    }
    
    public void runSearch(UndirectedGraphNode source,
                          UndirectedGraphNode target,
                          AbstractPathFinder<UndirectedGraphNode> finder) {
        SearchRunnerThread thread = new SearchRunnerThread(source, target, finder);
        thread.start();
        
        while (thread.isAlive()) {
            try {
                repaint();
                System.out.println("Sleep");
                Thread.sleep(200L);
            } catch (InterruptedException ex) {
            
            }
        }
        
        System.out.println("yoooo!");
    }
//    
//    public UndirectedGraphNode getNode(int x, int y) {
//        return graph[y][x];
//    }
//    
//    public void setAsWall(int x, int y) {
//        graph[y][x].clear();
//        wallNodeSet.add(graph[y][x]);
//    }
//    
    private final class SearchRunnerThread extends Thread {
        
        private final UndirectedGraphNode source;
        private final UndirectedGraphNode target;
        private final AbstractPathFinder<UndirectedGraphNode> finder;
        
        SearchRunnerThread(UndirectedGraphNode source,
                           UndirectedGraphNode target,
                           AbstractPathFinder<UndirectedGraphNode> finder) {
            this.source = source;
            this.target = target;
            this.finder = finder;
        }
        
        @Override
        public void run() {
            finder.search(source, target);
        }
    }
    
    @Override
    public void begin() {
        openNodeSet.clear();
        closedNodeSet.clear();
        repaint();
    }

    @Override
    public void reached(UndirectedGraphNode node) {
//        openNodeSet.add(node);
    }

    @Override
    public void closed(UndirectedGraphNode node) {
//        closedNodeSet.add(node);
    }

    @Override
    public void done(List<UndirectedGraphNode> path) {
        this.path.clear();
//        this.path.addAll(path);
        repaint();
    }
}
