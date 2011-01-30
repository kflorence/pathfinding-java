package game.map;

import java.awt.Point;
import java.awt.Rectangle;

// Grid cell class
public class Cell
{	
	private Point cell;						// The location of this cell; Point (r, c)
    private Point location;					// The location of this cell; Point (x, y)
    private Rectangle bounds;				// The rectangle representing this cell
    
    private boolean isGoal 		= false;	// Whether or not this cell is a goal point
    private boolean isSpawn 	= false;	// Whether or not this cell is a spawn point
    private boolean isPlayable 	= false;	// Whether or not entities can enter this cell
  
    // Constructor, creates a new cell for the grid
    public Cell(int r, int c)
    {
        // Row and column numbers for this cell
        this.cell = new Point(r, c);
    	
        // The top-left (x, y) coordinates of the cell in pixels
        this.location = Grid.locationFromCell(cell);
        
        // Creates a rectangle that represents the cell
        this.bounds = new Rectangle(location.x, location.y, Grid.getCellSize(), Grid.getCellSize());
    }
    
    // Returns whether or not the cell is a spawn point
    public boolean isSpawn() {
        return isSpawn;
    }
    
    // Returns whether or not the cell is a an exit point
    public boolean isGoal() {
        return isGoal;
    }
    
    // Returns whether or not the cell is playable
    public boolean isPlayable() {
        return isPlayable;
    }
    
    // Set whether or not the cell is a spawn point
    public void setSpawn(boolean bool) {
        isSpawn = bool;
    }
    
    // Set whether or not the cell is an exit point
    public void setGoal(boolean bool) {
        isGoal = bool;
    }
    
    // Set whether or not the cell is playable
    public void setPlayable(boolean bool) {
        isPlayable = bool;
    }
    
    // Toggle whether or not the cell is a spawn point
    public void toggleSpawn() {
        isSpawn = !isSpawn;
    }
    
    // Toggle whether or not the cell is an exit point
    public void toggleGoal() {
        isGoal = !isGoal;
    }
    
    // Toggle whether or not the cell is playable
    public void togglePlayable() {
        isPlayable = !isPlayable;
    }
    
    // Returns rectangular bounds
    public Rectangle getBounds() {
        return bounds;
    }
    
    // Returns Point(x, y)
    public Point getLocation() {
        return location;
    }
    
    // Returns Point(r, c)
    public Point getGridLocation() {
        return cell;
    }
    
    // Return x-coordinate from Point(x, y)
    public int getX() {
    	return location.x;
    }
    
    // Return y-coordinate from Point(x, y)
    public int getY() {
    	return location.y;
    }
    
    // Return row from Point(r, c)
    public int getR() {
    	return cell.x;
    }
    
    // Return column from Point(r, c)
    public int getC() {
    	return cell.y;
    }
    
    // Override toString method
    public String toString()
    {
        StringBuffer sb = new StringBuffer();
 
        sb.append("\n======= Begin Cell object =======");
        sb.append("\ncell = " + cell.toString());
        sb.append("\nisGoal = " + (isGoal ? "true" : "false"));
        sb.append("\nisSpawn = " + (isSpawn ? "true" : "false"));
        sb.append("\nisPlayable = " + (isPlayable ? "true" : "false"));
        sb.append("\n======= End Cell object ========");

        return (new String(sb));
    }

}
