package game.modules.pathfinding;

import game.map.Cell;

// Defines the movement costs for Cells
public class Node
{
	private Cell cell			= null;		// A reference to the Cell this cost applies to
	private Node parent			= null;		// A reference to this node's parent Node
	
    private int totalCost 		= 0; 		// F() = Movement Cost + Estimated Cost
    private int movementCost 	= 0; 		// G() = Movement Cost from parent cell to this cell
    private int estimatedCost 	= 0; 		// H() = Estimated Cost from this cell to goal cell
    
    // Constructor
    public Node(Cell cell)
    {
        // The cell we are defining the cost for
        this.cell = cell;
    }
    
    // Sets the parent Cell
    public void setParent(Node parent) {
    	this.parent = parent;
    }
    
    // Set costs - F = G() + H()
    public void setCosts(int g, int h)
    {
    	// G() - Movement cost from parent Cell to this Cell
    	this.movementCost = g;
    	
    	// H() - Estimated cost from this Cell to the goal Cell
    	this.estimatedCost = h;
    	
    	// F = G() + H() - Totalcost for this Cell
    	this.totalCost = g + h;
    }
    
    // Return this Cell's total cost
    public int totalCost() {
    	return totalCost;
    }
    
    // Return this Cell's movement cost
    public int movementCost() {
    	return movementCost;
    }
    
    // Return this Cell's estimated cost
    public int estimatedCost() {
    	return estimatedCost;
    }
    
    // Returns this cell
    public Cell getCell() {
    	return cell;
    }
    
    // Return this Cell's parent
    public Node getParent() {
    	return parent;
    }
}
