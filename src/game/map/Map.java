/*
 * Map.java
 *
 * Created on July 21, 2007, 8:23 PM
 *
 */

package game.map;

import game.Main;
import game.entities.AlienEntity;
import game.entities.Entity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;

/**
 *
 * TODO: this file loads everything attributed to the map, this includes:
 * 
 * - The grid
 * - The tiles
 * - Level files
 * - Entity files 
 * 
 */

// Takes care of the graphical side of Grid
public class Map extends Grid
{
    private static Image map;					// The Image representation of this grid
    private static Graphics2D g;				// The graphics context on which to generate the image
    private static Rectangle clip;				// The rectangular clip for the gameplay region
    private static boolean drawGrid = false;	// Whether or not to draw the grid
    
	// Colors, will eventually be replaced with image tiles
    private static Color background  = Color.white;
    private static Color imageGrid   = Color.black;
    private static Color playable    = Color.lightGray;
    private static Color spawnable   = Color.green;
    private static Color exitable    = Color.red;
    
    // Constructor: width, height, cell size
	public Map(int w, int h, int cs)
	{
		// Pass width, height, and cell size on to Grid
		super(w, h, cs);
		
		// Set clip rectangle
        clip = new Rectangle(getMinX(), getMinY(), (w * getCellSize()), (h * getCellSize()));
        
        // Draw the map
        draw();
	}
	
	// Return the Map Image
	public static Image getImage()
	{
		return map;
	}
    
    // DRAW GRID/CELLS: this is only for testing
	// TODO: make the map tile-able
    public static void draw()
    {
        int r, c;
    	Cell cell;
        
        // Create an image of the grid, match width/height of game window
        map = Main.getGC().createCompatibleImage(Main.WIDTH, Main.HEIGHT);
        g = (Graphics2D) map.getGraphics();
        
        // Set clip
        g.setClip(clip);
        
        // Draw background color for playable area
        g.setColor(background);
        g.fill(clip);
        	
        for (r = 1; r <= getWidth(); r++)
        {                
            for (c = 1; c <= getHeight(); c++)
            {                    
                // Create new cell for this (row, column) location
                cell = new Cell(r, c);
                
                // Grab the next cell        		
                if ((cell = getCell(r, c)) != null)
                {                   
                    // Fill the cell if it needs to be filled
                    if (cell.isPlayable())
                    {
                        // Set Color
                  	    if (cell.isSpawn()) g.setColor(spawnable);
                        else if (cell.isGoal()) g.setColor(exitable);
                        else g.setColor(playable);
                    	   
                   	    // Fill cell
                        g.fill(cell.getBounds());
                    }
            	}
            }
        }   
        
        // Paint the grid
        if (drawGrid)
        {        	
            // Set grid color
	        g.setColor(imageGrid);
	        
	        // Draw vertical lines
	        for (c = 0; c <= getWidth(); c++)
	        	g.drawLine(getMinX() + (getCellSize() * c), getMinY(), getMinX() + (getCellSize() * c), getMaxX());
	
	        // Draw horizontal lines
	        for (r = 0; r <= getHeight(); r++)
	            g.drawLine(getMinX(),  getMinY() + (getCellSize() * r), getMaxX(), getMinY() + (getCellSize() * r));
        }
        
        // Debug mode
        if (Main.inDebugMode())
        {	                            
            // Loop through entity's and draw their path and binding box
        	for (Entity entity : Main.getEntities())
        	{
        		// Set this entity's color
        		g.setColor(entity.getColor());
	            
	            // Draw this entity's path (if it has one)
        		if (entity instanceof AlienEntity)
        		{
    	            g.drawImage(entity.getPathImage(), 0, 0, null);
        		}
        	}
        }
        
        // Dispose of this graphics content
        g.dispose();
    }
    
    // Toggle the grid
    public static void toggleGrid() {
    	drawGrid = !drawGrid;
    }
    
    // Return this grid's clipping rectangle
    public static Rectangle getClip() {
        return clip;
    }
}
