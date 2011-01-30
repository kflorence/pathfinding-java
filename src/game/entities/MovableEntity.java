package game.entities;

import game.Main;
import game.map.Grid;
import game.map.Cell;
import game.modules.pathfinding.AStar;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Transparency;

// MoveableEntity: for entities that can move (duh!)
public class MovableEntity extends AnimatedEntity
{
	// DEBUG STUFF
	private static Graphics2D g;
	private static int pathSize = 0;
	private static Image pathImage = null;
    
    // The direction of movement on the x/y-axis
    private double dx;
    private double dy;
    
    // Starting position and goal position
    private Cell startLocation;
    private Cell goalLocation;
    
    // The AStar pathfinding class
    private AStar path = new AStar(this);
    
    // The turning radius of this entity
    //private int radius = 3;
	
    // Movement States
    private static final int WAITING      = 0; // Waiting for the next node
    private static final int MOVING       = 1; // Moving towards next node
    private static final int REACHED_GOAL = 2; // Reached the goal
    
    // The current location we are on the generated path
    private Cell currentCell;
    private int movementState = WAITING;
    private int pathLocation  = 1;

	public MovableEntity(Point start, Point goal) {
		super(start.x, start.y);
		
		// Set start and goal locations
		this.startLocation = Grid.getCell(start);
		this.goalLocation  = Grid.getCell(goal);
	}
	
    /**
     * 
     * GET PROPERTIES
     * 
     **/
	
    // Return starting point
    public Cell startLocation() {
    	return startLocation;
    }
    
    // Return goal point
    public Cell goalLocation() {
    	return goalLocation;
    }
    
    // Return the reference to the AStar class
    public AStar getPath() {
        return path;
    }
    
    // Return movement of x coordinate
    public double movementX() {
        return dx;
    }
    
    // Return movement of y coordinate
    public double movementY() {
        return dy;
    }
    
    /**
     * 
     * SET PROPERTIES
     * 
     **/
    
    // Set this entities movement on the x-axis
    public void setMovementX(double dx)
    {
    	this.dx = dx;
    }
    
    // Set this entities movement on the y-axis
    public void setMovementY(double dy)
    {
    	this.dy = dy;
    }
    
    // Reset this entities movement directions to zero (stop moving)
    public void resetMovement() {
    	setMovementX(0);
    	setMovementY(0);
    }
    
    // Set the number of steps this entity will take per loop
    public void setStepLimit(int n) {
    	path.setStepLimit(n);
    }
    
    // Start building a path for this entity
    public void startMoving() {
    	path.newPath(startLocation, goalLocation);
    }
    
    /**
     * 
     * BOOLEAN OPERATIONS
     * 
     **/
    
    // Returns true if the entity is moving, false otherwise
    public boolean isMoving()
    {
        // Moving
    	if (movementX() != 0 || movementY() != 0) return true;
        
        // Not moving
        return false;
    }
    
    // Check to see whether or not this entity can move
    // FIXME
    public boolean canMove(int x, int y)
    {    
        Cell cell = Grid.getCell(Grid.cellFromLocation(x, y));
    	
    	// Top-Left corner (cell check)
        if (!cell.isPlayable()) return false;
        
        // Entity can move!
        else return true;
    }
    
    /**
     * 
     * ABSTRACT FUNCTION OVERRIDES
     * 
     **/

	@Override
	// TODO
	public void collidedWith(game.entities.Entity other) {}

    @Override
    // The entities logic 
    public void doLogic()
    {
    	// If this unit is logical, execute logic as needed
        if (this.isLogical())
        {
            // Build our path
        	path.findPath();
        }
    }

	@Override
	// This entities path Image
	public Image getPathImage()
	{
    	// If the path needs to be updated...
    	if (path.exists() && (pathSize != path.getSize()))
    	{
           	Point first, second;
    		
            // Store the size of the path
    		pathSize = path.getSize();
    		
    		// Create a transparent image of the grid, match width/height of game window
            pathImage = Main.getGC().createCompatibleImage(Main.WIDTH, Main.HEIGHT, Transparency.BITMASK);
            g = (Graphics2D) pathImage.getGraphics();
	            	
          	// Set the entities color
           	g.setColor(getColor());
            	
           	// Loop through this entity's path and draw it
           	for (int x = 1; x < path.getSize(); x++)
           	{
                first  = path.get(x).getCell().getLocation();
                second = path.get(x + 1).getCell().getLocation();
            		
                // Draw line segment
           		g.drawLine(first.x, first.y, second.x, second.y);
           	}
	        
	        // Dispose of this graphics content
	        g.dispose();
    	}
    	
        // No path to draw
        else
        {
        	pathSize 	= 0;
        	pathImage 	= null;
        }
    	
    	return pathImage;
    }
	
	@Override
	// Update state for animation
	public void update(long delta)
	{
    	// Update animation state
        if (this.isMoving()) {
        	setState("moving");
        }
        
        // Not moving
        else setState("still");
        
        // The rest of the work is done in AnimatedEntity
        super.update(delta);
	}

	@Override
	// Update this entities location
	public void move(long delta)
    {    	
    	// Reset our directional movements
        resetMovement();
    	
    	// Make sure we have a path to move along
    	if (path.exists() && (pathLocation <= path.getSize()))
    	{	    	
			// Which movementState are we in?
    		switch (movementState)
    		{
	    		// We are waiting for the next move
	    		case WAITING:
			        // Grab the next node we need to move to
			    	currentCell = path.getMove(pathLocation);
			    		
			    	// Change our movementState to moving
			    	movementState = MOVING;
			    	break;
    		
			    // We are currently moving to our next point
	    		case MOVING:
			    	// New x coordinate is greater than old x coordinate
		    		if (currentCell.getX() > getX()) setMovementX(getSpeed());
		    		
		    		// New x coordinate is less than old x coordinate
		    		if (currentCell.getX() < getX()) setMovementX(-getSpeed());
		    		
		    		// New y coordinate is greater than old y coordinate
		    		if (currentCell.getY() > getY()) setMovementY(getSpeed());
		    		
		    		// New y coordinate is less than old y coordinate
		    		if (currentCell.getY() < getY()) setMovementY(-getSpeed());;
		    		
		    		// Calculate the destination of our movement based on time elapsed
			        double nx = getX() + ((movementX() * delta) / 1000000000);
			        double ny = getY() + ((movementY() * delta) / 1000000000);
			        
		    		// Make sure x coordinate does not go past our next point (if so, correct)
		    		if ((movementX() > 0) && (nx > currentCell.getX())) nx = currentCell.getX();
		    		if ((movementX() < 0) && (nx < currentCell.getX())) nx = currentCell.getX();
		    		
		    		// Make sure y coordinate does not go past our next point (if so, correct)
			        if ((movementY() > 0) && (ny > currentCell.getY())) ny = currentCell.getY();
		    		if ((movementY() < 0) && (ny < currentCell.getY())) ny = currentCell.getY();
		    		
			        // FIXME: Make sure this is a valid move
			        //if (this.canMove((int) nx, (int) ny))
		    		//{
			            // Set the entities new position
		    			setLocation(nx, ny);
			            
			            // Set the entities new angle
			            setAngle(Math.atan2(movementY(), movementX()) + (Math.PI / 2));
			        //}
			    	break;
			    	
	    		// We have reached the goalNode
	    		case REACHED_GOAL:
			    	
			    	// Tell the console we are done
    				if (debugOn())
    					System.out.println("Entity #" + getNumber() + ": Path finished. Removing entity.");
	    			
			    	// Call this entity's destroy function
    				destroy();
	    			break;
			    	
	    		// Something weird has happened
			    default:
			    	System.out.println("Uh oh!");
			    	break;
    		}
			
            // Check to see if we have reached the point we were moving towards
            if (currentCell.getX() == getX() && currentCell.getY() == getY())
            {            	
            	// See if we reached the goal	            	
    			if (currentCell == goalLocation)
    			{    				
    				// Change movementState to reached goal
    				movementState = REACHED_GOAL;
    				
			    	// Reset pathLocation
			    	pathLocation = 1;
    			}
    			
    			// Otherwise, start working on the next point
    			else
    			{    				
    				// Change movementState to waiting
    				movementState = WAITING;
    				
    				// Advance our path location
    				pathLocation++;
    			}
            }
    	}
    }
	
	@Override
	public void destroy()
	{
		path = null;
		
		// Pass the call up the chain
		super.destroy();
	}
}
