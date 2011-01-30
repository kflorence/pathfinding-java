package game.entities;

import game.Game;
import game.map.Grid;
import game.map.Cell;
import game.modules.Graphics;
import game.modules.pathfinding.AStar;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Transparency;

// MoveableEntity: for entities that can move
public class MovableEntity extends AnimatedEntity
{
	// DEBUG STUFF
	private static int pathSize = 0;
	private static Image path = Graphics.getImage(Game.WIDTH, Game.HEIGHT, Transparency.BITMASK);
    
    // The direction of movement on the x/y-axis
    private double dx;
    private double dy;
    
    // Starting position and goal position
    private Cell startLocation;
    private Cell goalLocation;
    
    // The AStar pathfinding class
    private AStar astar = new AStar(this);
    
    // The turning radius of this entity
    //private int radius = 3;
	
    // Movement States
    private static final int WAITING      = 0; // Waiting for the next node
    private static final int MOVING       = 1; // Moving towards next node
    private static final int REACHED_GOAL = 2; // Reached the goal
    
    // The current location we are on the generated path
    private Cell currentCell;
    private int pathLocation  = 1;
    private int movementState = WAITING;

    // Constructor
	public MovableEntity(Point start, Point goal)
	{
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
    	astar.setStepLimit(n);
    }
    
    // Start building a path for this entity
    public void startMoving() {
    	astar.newPath(startLocation, goalLocation);
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
    
    // Convenience method for canMove(int, int)
    public boolean canMove(Point p)
    {
        return canMove(p.x, p.y);
    }
    
    // Check to see whether or not this entity can move
    // FIXME
    public boolean canMove(int x, int y)
    {
        // Construct a bounding box for location x, y
        Rectangle me = getBounds(x, y);
        
    	// Check top-left corner
        if (!Grid.getCellFromLocation(me.x, me.y).isPlayable())
            return false;
        
        // Check top-right corner
        if (!Grid.getCellFromLocation(me.x + me.width, me.y).isPlayable())
            return false;
        
        // Check bottom-left corner
        if (!Grid.getCellFromLocation(me.x, me.y + me.height).isPlayable())
            return false;
        
        // Check bottom-right corner
        if (!Grid.getCellFromLocation(me.x + me.width, me.y + me.height).isPlayable())
            return false;
        
        // Entity can move
        return true;
    }
    
    /**
     * 
     * ABSTRACT FUNCTION OVERRIDES
     * 
     **/

	// This entities path Image
	public Image getPath()
	{
    	// Keep drawing the path as we get it
    	if (astar.pathExists() && (pathSize != astar.getPathSize()))
    	{
           	Cell first, second;
           	Graphics2D g = (Graphics2D) path.getGraphics();
           	
            // Clear the old path so we can build a new one
            Graphics.clear(g);
            
          	// Set the entities color
           	g.setColor(getColor());
           	
            // Store the current size of the path
            pathSize = astar.getPathSize();
           	
           	// Loop through this entity's path and draw it
           	for (int x = 1; x < astar.getPathSize(); x++)
           	{
                first  = astar.getNode(x).getCell();
                second = astar.getNode(x + 1).getCell();
                
                g.setColor(Color.gray);
                
                // Fill in waypoint cells
                g.fill(first.getBounds());
                g.fill(second.getBounds());
                
                g.setColor(getColor());
                
                // Draw line segment
           		g.drawLine(first.getLocation().x, first.getLocation().y, second.getLocation().x, second.getLocation().y);
           	}
	        
	        // Dispose of this graphics content
	        g.dispose();
    	}
    	
    	return path;
    }
	
	@Override
	// Update state for animation
	public void update(long delta)
	{
    	// Update animation state
        if (this.isMoving()) setState("moving");
        
        // Not moving
        else setState("still");
        
        // The rest of the work is done in AnimatedEntity
        super.update(delta);
	}
	
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
            astar.findPath();
        }
    }

	@Override
	// Update this entities location
	public void move(long delta)
    {    	
    	// Reset our directional movements
        resetMovement();
    	
    	// Make sure we have a path to move along
    	if (astar.pathExists() && (pathLocation <= astar.getPathSize()))
    	{	    	
			// Which movementState are we in?
    		switch (movementState)
    		{
	    		// We are waiting for the next move
	    		case WAITING:
			        // Grab the next node we need to move to
			    	currentCell = astar.getNode(pathLocation).getCell();
			    	
			    	System.out.println("moving towards: " + currentCell.getGridLocation().toString());
			    	
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
	    // Clear the entities path
	    //Graphics2D g = (Graphics2D) path.getGraphics();
	    //Graphics.clear(g);
	    
        // Pass this call to the parent class
        super.destroy();
	}
}
