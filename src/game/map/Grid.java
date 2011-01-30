/*
 * Grid.java
 *
 * Created on January 9, 2007, 2:50 PM
 *
 */

package game.map;

import game.Main;

import java.awt.Point;
import java.util.HashMap;

/**
 *
 * @author Kyle Florence
 */
public class Grid
{    
    private static int width;					// Grid Width
    private static int height;					// Grid Height
    private static int cellSize;				// The height and width of each individual Cell
    
    private static int minX;					// The lowest x-coordinate of the gameplay region
    private static int maxX;					// The highest x-coordinate of the gameplay region
    private static int minY;					// The lowest y-coordinate of the gameplay region
    private static int maxY;					// The highest y-coordinate of the gameplay region
    
    // Our list of each cell in this grid
    private static HashMap<Point, Cell> cellList;
    
    // Creates a new instance of Grid
    public Grid(int w, int h, int cs)
    {        
        width 		= w;	// The width of the grid
        height 		= h;	// The height of the grid
        cellSize 	= cs;	// T width and height of the cells in the grid
        
    	// Create our cellList
    	cellList = new HashMap<Point, Cell>(w * h);
        
        // Set the minX and minY coordinates
        minX = ((Main.WIDTH - (w * cellSize)) / 2); 
        minY = ((Main.HEIGHT - (h * cellSize)) / 2);
        
        // Set the maxX and maxY coordinates based on grid and cell size
        maxX = (minX + (w * cellSize));
        maxY = (minY + (h * cellSize));
        
        // Populate the grid
        populate();
    }
    
    // Populates the Grid with cells (based on width/height)
    private void populate()
    {
        int r, c;
        Cell cell;
        
        // (Horizontally oriented grid)
        //if (width >= height)
        //{
            for (r = 1; r <= width; r++)
            {                
                for (c = 1; c <= height; c++)
                {                    
                    // Create new cell for this (row, column) location
                    cell = new Cell(r, c);
                    
                    cellList.put(cell.getGridLocation(), cell);
                    
                    // TESTING ONLY, REMOVE LATER
                    // Set up playable area with spawns and goals for testing
                    if ((r > (width / 2 - 5)) && (r <= (height / 2 + 5)))
                    {                      
                        // Make cell playable
                    	cell.togglePlayable();
                        
                        // Set spawns and exits
                        if (c == 1) cell.toggleSpawn();
                        if (c == height) cell.toggleGoal();
                    }
                }
            }
        //}
        // (Vertically oriented grid)
/*        else
        {
            for (c = 1; c <= height; c++)
            {
                for (r = 1; r <= width; r++)
                {
                    // Create new cell for this (row, column) location
                    cell = new Cell(r, c);
                    cellList.put(cell.getGridLocation(), cell);
                    
                    // TESTING ONLY, REMOVE LATER
                    // Set up playable area with spawns and goals for testing
                    if ((r > (width / 2 - 5)) && (r <= (width / 2 + 5)))
                    {                        
                    	// Make cell playable
                    	cell.togglePlayable();
                        
                    	// Set spawns and exits
                        if (c == 1) cell.toggleSpawn();
                        if (c == height) cell.toggleGoal();
                    }
                }
            }           
        }  */ 
        
        // For testing:
        cellList.get(new Point(6,3)).togglePlayable();
        cellList.get(new Point(7,3)).togglePlayable();
        cellList.get(new Point(8,3)).togglePlayable();
        cellList.get(new Point(9,3)).togglePlayable();
        cellList.get(new Point(10,3)).togglePlayable();
        cellList.get(new Point(11,3)).togglePlayable();
        cellList.get(new Point(12,3)).togglePlayable();
        cellList.get(new Point(13,3)).togglePlayable();
        cellList.get(new Point(14,3)).togglePlayable();
        
        cellList.get(new Point(7,5)).togglePlayable();
        cellList.get(new Point(7,6)).togglePlayable();
        cellList.get(new Point(7,7)).togglePlayable();
        cellList.get(new Point(7,8)).togglePlayable();
        cellList.get(new Point(7,9)).togglePlayable();
        cellList.get(new Point(7,10)).togglePlayable();
        cellList.get(new Point(7,11)).togglePlayable();
        cellList.get(new Point(7,12)).togglePlayable();
        
        cellList.get(new Point(8,12)).togglePlayable();
        cellList.get(new Point(9,12)).togglePlayable();
        cellList.get(new Point(10,12)).togglePlayable();
        cellList.get(new Point(11,12)).togglePlayable();
        cellList.get(new Point(12,12)).togglePlayable();
        cellList.get(new Point(13,12)).togglePlayable();
        cellList.get(new Point(14,12)).togglePlayable();
        cellList.get(new Point(15,12)).togglePlayable();
    }
    
    // Return this grid's cell size
    public static int getCellSize() {
        return cellSize;
    }
    
    // Return a cell from the grid (int row, int column)
    public static Cell getCell(int r, int c) {
    	return cellList.get(new Point(r, c));
    }
    
    // Return a cell from the grid (Point cell)
    public static Cell getCell(Point cell) {
    	return cellList.get(cell);
    }
    
    // Return a reference to the cellList
  
    // Return the minX coordinate
    public static int getMinX() {
        return minX;
    }    
    
    // Return the minY coordinate
    public static int getMinY() {
        return minY;
    }
    
    // Return the maxX coordinate
    public static int getMaxX() {        
        return maxX;
    }
    
    // Return the maxY coordinate
    public static int getMaxY() {        
        return maxY;
    }
    
    // Return the width of this grid in cells
    public static int getWidth() {
        return width;
    }
    
    // Return the height of this grid in cells
    public static int getHeight() {
        return height;
    }
    
    /**
     *
     * Useful functions
     *
     **/
    
    // Return Point(x, y) from Point(r, c)
    public static Point locationFromCell(Point cell) { 
        return locationFromCell(cell.x, cell.y); 
    }
    
    // Return Point(x, y) from (r, c)
    public static Point locationFromCell(int r, int c)
    {
    	// Make sure this point is within our grid
    	if (r <= width && c <= height)
    	{
	    	return new Point(
				(minX + ((r - 1) * cellSize)),
				(minY + ((c - 1) * cellSize))
			);
    	}
    	
    	// Otherwise, return Point(0, 0)
        return new Point();
    }
    
    // Return Point(r, c) from Point(x, y)
    public static Point cellFromLocation(Point loc) { 
        return cellFromLocation(loc.x, loc.y); 
    }
    
    // Return Point(r, c) from (x, y)
    public static Point cellFromLocation(int x, int y)
    {    	
    	// Make sure this point is within our grid
    	if ((x <= maxX && x >= minX) && (y <= maxY && y >= minY))
        {
	    	return new Point(
	            ((x - minX + cellSize) / cellSize), 
	            ((y - minY + cellSize) / cellSize)
	        );
        }

    	// Otherwise, return Point(0, 0)
        return new Point();
    }
}
