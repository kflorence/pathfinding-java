/*
 * Grid.java
 *
 * Created on January 9, 2007, 2:50 PM
 *
 */

package game.map;

import game.Game;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Kyle Florence
 */
public class Grid
{    
    // Grid and cell dimensions (in pixels)
    private static int rows;
    private static int columns;
    private static int cellSize;
    
    // Minimum and maximum X and Y coordinates
    private static int minX, minY, maxX, maxY;
    
    // The rectangular clip for the gameplay region
    private static Rectangle clip = null;
    
    // Our list of each cell in this grid
    private static HashMap<Point, Cell> cells = null;
    
    // Creates the cell list
    public static void createGrid(int r, int c, int s)
    {
        rows        = r;    // The width of the grid (in cells)
        columns     = c;    // The height of the grid (in cells)
        cellSize    = s;    // The size of the cells (in pixels)
        
        // Create our cellList
        cells = new HashMap<Point, Cell>(rows * columns);
        
        // Set the minX and minY coordinates
        minX = ((Game.WIDTH - (columns * cellSize)) / 2); 
        minY = ((Game.HEIGHT - (rows * cellSize)) / 2);
        
        // Set the maxX and maxY coordinates based on grid and cell size
        maxX = (minX + (columns * cellSize));
        maxY = (minY + (rows * cellSize));
        
        // Set clip rectangle
        clip = new Rectangle(getMinX(), getMinY(), (columns * getCellSize()) + 1, (rows * getCellSize()) + 1);
    }
    
    // Adds a cell to the cell list
    public static void addCell(Cell cell)
    {
        cells.put(cell.getGridLocation(), cell);
    }
    
    // Return the cell list
    public static HashMap<Point, Cell> getCells()
    {
        return cells;
    }
    
    // Return a cell from the grid (Point cell)
    public static Cell getCell(Point cell) {
        return cells.get(cell);
    }
    
    // Return a cell from the grid (int row, int column)
    public static Cell getCell(int r, int c) {
    	return getCell(new Point(r, c));
    }
    
    // Return a cell from the grid (int x, int y)
    public static Cell getCellFromLocation(int x, int y) {
        return getCell(cellFromLocation(x, y));
    }
    
    // Return a cell from the grid (Point loc)
    public static Cell getCellFromLocation(Point loc) {
        return getCell(cellFromLocation(loc.x, loc.y));
    }
    
    // Return this grid's clipping rectangle
    public static Rectangle getClip() {
        return clip;
    }
    
    // Return this grid's cell size
    public static int getCellSize() {
        return cellSize;
    }
  
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
    public static int getRows() {
        return rows;
    }
    
    // Return the height of this grid in cells
    public static int getColumns() {
        return columns;
    }
    
    /**
     *
     * Useful functions
     *
     **/
    
    // Return x from r
    public static int XFromRow(int r)
    {
        return (minX + ((r - 1) * cellSize));
    }
    
    // Return y from c
    public static int YFromColumn(int c)
    {
        return (minY + ((c - 1) * cellSize));
    }
    
    // Return r from x
    public static int rowFromX(int x)
    {
        return ((x - minX + cellSize) / cellSize);
    }
    
    // Return c from y
    public static int columnFromY(int y)
    {
        return ((y - minY + cellSize) / cellSize);
    }
    
    // Return Point(x, y) from Point(r, c)
    public static Point locationFromCell(Point cell) { 
        return locationFromCell(cell.x, cell.y); 
    }
    
    // Return Point(x, y) from (r, c)
    public static Point locationFromCell(int r, int c)
    {
    	// Make sure this point is within our grid
    	if (r <= rows && c <= columns)
    	{
	    	return new Point(XFromRow(r), YFromColumn(c));
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
	    	return new Point(rowFromX(x), columnFromY(y));
        }

    	// Otherwise, return Point(0, 0)
        return new Point();
    }
    
    // Convenience method for rayTrace(Point a, Point b)
    public static ArrayList<Cell>  rayTrace(int ax, int ay, int bx, int by)
    {
        return rayTrace(new Point(ax, ay), new Point(bx, by));
    }
    
    // Traces a line from Point A to Point B and returns an array of the cells it touches
    /*
     * Algorithm:
     * 
     * Start at the square containing the line segment’s starting endpoint.
     * For the number of intersected squares:
     * If the next intersection is with a vertical grid line:
     *      Move one square horizontally toward the other endpoint
     * Else:
     *      Move one square vertically toward the other endpoint
     */
    public static ArrayList<Cell> rayTrace(Point a, Point b)
    {        
        // Store current R and C values
        int r = a.x;
        int c = a.y;
        
        // Increment values (depend on if the line is sloping up or down)
        int inc_r = ((b.x > a.x) ? 1 : -1);
        int inc_c = ((b.y > a.y) ? 1 : -1);
        
        // The number of squares the line covers horizontally and vertically
        int dr = Math.abs(a.x - b.x);
        int dc = Math.abs(a.y - b.y);
        
        // The number of cells this line spans
        int numCells = (dr + dc + 1);
        
        // The orientation of movement (positive = horizontal, negative = vertical)
        int orientation = (dr - dc);
        
        // Our list to store touched cells
        ArrayList<Cell> cellList = new ArrayList<Cell>(numCells);
        
        // Loop through all possible cells and check for obstructions on the line
        for (; numCells > 0; numCells--)
        {            
            // Add the cell we are currently in
            cellList.add(getCell(r, c));
            
            // If the orientation is positive, we are moving horizontally
            if (orientation >= 0)
            {
                // Increment X
                r += inc_r;
                
                // Decrement orientation
                orientation -= dc;
            }
            
            // If the orientation is negative, we are moving vertically
            else
            {
                // Increment Y
                c += inc_c;
                
                // Decrement orientation
                orientation += dr;
            }
        }
        
        // Return our list
        return cellList;
    }
    
    // Returns points along a line spaced apart by int spacing length
    public static Point[] pointsAlongLine(Point start, Point end, int spacing)
    {        
        // Find the difference between the points
        int xDif = (end.x - start.x);
        int yDif = (end.y - start.y);
        
        // Find the length of the line [sqrt(x^2 + y^2)]
        int lineLength = (int)Math.sqrt((Math.pow(xDif, 2) + Math.pow(yDif, 2)));
        
        // The number of steps is equal to the length of the line divided by the spacing
        int steps = lineLength / spacing;
        
        // The number of x and y steps is equal to the x and y difference divided by the number of steps needed
        int xStep = xDif / steps;
        int yStep = yDif / steps;

        // Store our result in an array of points
        Point[] result = new Point[steps];

        // Return the points on a line
        for (int i = 0; i < steps; i++)
        {
            int x = start.x + (xStep * i);
            int y = start.y + (yStep * i);
            result[i] = new Point(x, y);
        }
        
        // Return the points on a line
        return result;         
    }
}
