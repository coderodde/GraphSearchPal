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
implements GraphSearchListener<GridGraphNode> {
   
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
    
    private GridGraphConfiguration configuration;
    private GridGraphNode[][] graph;
    private final GridGraphWeightFunction weightFunction =
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
    
    private boolean locked = false;
    private BufferedImage image;
    
    public GridGraphWeightFunction getWeightFunction() {
        return weightFunction;
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
        
        initializeSource();
        initializeTarget();
        
        repaint();
    }
    
    private void initializeSource() {
        sourcePoint.y = getHeight() >> 1;
        sourcePoint.x = getWidth() / 3;
    }
    
    private void initializeTarget() {
        targetPoint.y = getHeight() >> 1;
        targetPoint.x = 2 * getWidth() / 3;
    }
    
    Point getSourcePoint() {
        return sourcePoint;
    }
    
    Point getTargetPoint() {
        return targetPoint;
    }
    
    public WallBrush getWallBrush() {
        return wallBrush;
    }
    
    public void runSearch(AbstractPathFinder<GridGraphNode> finder) {
        finder.setGraphSearchListener(this);
        
        System.out.println("Begin!");
        
        Thread searchThread = new Thread() {
          
            @Override
            public void run() {
                GridGraphNode source = graph[sourcePoint.y][sourcePoint.x];
                GridGraphNode target = graph[targetPoint.y][targetPoint.x];
                finder.search(source, target);
            }
        };
        
        RepainterThread repainterThread = new RepainterThread(this, 100);
        repainterThread.start();
        searchThread.start();
        
        try {
            searchThread.join();
        } catch (InterruptedException ex) {
            
        }
        
        repainterThread.exit();
        System.out.println("Done!");
    }
    
    private void createNodes(int width, int height) {
        graph = new GridGraphNode[height][width];
        
        int id = 0;
        
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                graph[y][x] = new GridGraphNode(id++, x, y, configuration);
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
        
        configuration.markAsWall(graph[y][x]);
        
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
    
    private void drawEndPoint(Graphics g, Point point, Color color) {
        g.setColor(color);
        
        int x = point.x;
        int y = point.y;
        
        g.fillRect(x - 1, y - 4, 3, 9);
        g.fillRect(x - 4, y - 1, 9, 3);
        g.drawArc(x - 6, y - 6, 12, 12, 0, 360);
    }
    
    @Override
    public void update(Graphics g) {
        g.drawImage(image, 0, 0, this);
        drawEndPoint(g, sourcePoint, sourceColor);
        drawEndPoint(g, targetPoint, targetColor);
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

    @Override
    public void begin() {
        int width = getWidth();
        int height = getHeight();
        
        // Clear everything.
        Graphics imgg = image.createGraphics();
        imgg.setColor(nonWallColor);
        imgg.fillRect(0, 0, width, height);
        
        int clr = wallColor.getRGB();
        
        // Draw the walls.
        for (GridGraphNode wallNode : configuration.getWallNodeSet()) {
            image.setRGB(wallNode.getX(), wallNode.getY(), clr);
        }
        
        repaint();
    }

    @Override
    public void reached(GridGraphNode node) {
        // Just color a pizel. It's up to the client programmer to repain().
        image.setRGB(node.getX(), node.getY(), openColor.getRGB());
    }

    @Override
    public void closed(GridGraphNode node) {
        image.setRGB(node.getX(), node.getY(), closedColor.getRGB());
    }

    @Override
    public void done(List<GridGraphNode> path) {
        for (GridGraphNode node : path) {
            image.setRGB(node.getX(), node.getY(), pathColor.getRGB());
        }
        
        repaint();
    }
    
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
    
//    @Override
//    public void begin() {
//        openNodeSet.clear();
//        closedNodeSet.clear();
//        repaint();
//    }
//
//    @Override
//    public void reached(UndirectedGraphNode node) {
////        openNodeSet.add(node);
//    }
//
//    @Override
//    public void closed(UndirectedGraphNode node) {
////        closedNodeSet.add(node);
//    }
//
//    @Override
//    public void done(List<UndirectedGraphNode> path) {
//        this.path.clear();
////        this.path.addAll(path);
//        repaint();
//    }
    static final class RepainterThread extends Thread {
        
        private volatile boolean exitRequested = false;
        private final GraphPalPanel panel;
        private final int sleepMilliseconds;
        
        RepainterThread(GraphPalPanel panel, int sleepMilliseconds) {
            this.panel = panel;
            this.sleepMilliseconds = sleepMilliseconds;
        }
        
        @Override
        public void run() {
            while (!exitRequested) {
                try {
                    panel.repaint();
                    Thread.sleep(sleepMilliseconds);
                } catch (InterruptedException ex) {
                    
                }
            }
            
            System.out.println("RepainterThread finished.");
        }
        
        public void exit() {
            exitRequested = true;
        }
    }
}
