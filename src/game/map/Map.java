/*
 * Map.java
 *
 * Created on July 21, 2007, 8:23 PM
 *
 */

package game.map;

import game.Game;
import game.modules.Graphics;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Transparency;

// Takes care of the graphical side of Grid
public class Map extends Grid
{    
    // Map and grid images
    private static Image map = Graphics.getImage(Game.WIDTH, Game.HEIGHT);
    private static Image grid = Graphics.getImage(Game.WIDTH, Game.HEIGHT, Transparency.BITMASK);
    private static Image test = Graphics.getImage(Game.WIDTH, Game.HEIGHT, Transparency.BITMASK);
    
    // Whether or not to draw the grid
    private static boolean showGrid = false;
    
	// Colors, will eventually be replaced with image tiles
    private static Color bgColor    = Color.black;
    private static Color gridColor  = Color.lightGray;
    private static Color playable   = Color.white;
    private static Color spawnable  = Color.green;
    private static Color exitable   = Color.red;
    
    // The map parser class
    private static MapParser parser = new MapParser();
    
    // Constructor: width, height, cell size
	public Map(String file)
	{
	    // Parse the map file
	    parser.parseMap(file);
	    
	    // Draw the map
	    drawMap();
	    
	    // Draw the grid
	    drawGrid();
	    
	    //drawTest();
	}
	
    // Toggle the grid
    public static void toggleGrid() {
        showGrid = !showGrid;
    }
    
    // Returns showGrid boolean
    public static boolean showGrid() {
        return showGrid;
    }
    
    // Return the map image
    public static Image getMap()
    {
        return map;
    }
    
	// TODO: make the map tile-able (instead of just filling in cells)
    public static void drawMap()
    {        
        // Grab the graphics context for the map image
        Graphics2D g = (Graphics2D) map.getGraphics();
        
        // Set clip
        g.setClip(getClip());
        
        // Draw background color for playable area
        g.setColor(bgColor);
        g.fill(getClip());
        
        // Fill in cells
        for (Cell cell : getCells().values())
        {            
            // Only fill if it is playable
            if (cell.isPlayable())
            {                
                // Spawn
                if (cell.isSpawn()) g.setColor(spawnable);
                
                // Goal
                else if (cell.isGoal()) g.setColor(exitable);
                
                // Playable
                else g.setColor(playable);
                   
                // Fill cell
                g.fill(cell.getBounds());
            }
        }
        
        // Dispose of this graphics content
        g.dispose();
    }
    
    // Return the grid image
    public static Image getGrid()
    {
        return grid;
    }
    
    // Draw the grid to an image
    public static void drawGrid()
    {
        int r, c;
        
        // Grab the graphics context for the grid image
        Graphics2D g = (Graphics2D) grid.getGraphics();
        
        // Set clip
        g.setClip(getClip());
        
        // Set grid color
        g.setColor(gridColor);
        
        // Draw horizontal lines
        for (r = 0; r <= getColumns(); r++)
            g.drawLine(getMinX(),  getMinY() + (getCellSize() * r), getMaxX(), getMinY() + (getCellSize() * r));
        
        // Draw vertical lines
        for (c = 0; c <= getRows(); c++)
            g.drawLine(getMinX() + (getCellSize() * c), getMinY(), getMinX() + (getCellSize() * c), getMaxX());
        
        // Dispose of this graphics content
        g.dispose();
    }
    
    public static Image getTest()
    {
        return test;
    }
    
    // Draw the grid to an image
    public static void drawTest()
    {        
        // Grab the graphics context for the grid image
        Graphics2D g = (Graphics2D) test.getGraphics();
        
        // Set clip
        g.setClip(getClip());
        
        // Set grid color
        g.setColor(Color.blue);
        
        Cell a = getCell(8, 1);
        Cell b = getCell(12, 10);
        
        // Draw the line
        g.drawLine(a.getLocation().x, a.getLocation().y, b.getLocation().x, b.getLocation().y);
        
        g.setColor(Color.red);
        
        // Draw the points
        for (Point p : pointsAlongLine(a.getLocation(), b.getLocation(), (getCellSize() / 2)))
        {
            g.fillRect(p.x, p.y, 2, 2);
        }
        
        // Dispose of this graphics content
        g.dispose();
    }
}
