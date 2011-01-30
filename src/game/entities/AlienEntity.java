package game.entities;

// This entity can move and attack
public class AlienEntity extends MovableEntity
{	
    // Various attributes
    //private int lifeTotal;
    //private int lifeRemaining;
    //private int attackDamage;
    
    // Whether or not this entity will attack
    //private boolean attack;
    
    // Creates a new instance of AlienEntity
    public AlienEntity(java.awt.Point start, java.awt.Point goal)
    {
    	super(start, goal);		// Super class constructor
        
    	// TODO: this should all be parsed from an XML file in the future!
    	
        setSpawn(true);			// This entity can spawn
        setLogic(true);			// This entity is logical
        setSpeed(75);			// This entity's movement speed (pixels/second)
        loadAnimation();		// Set this entities animation states
        setStepLimit(25);		// Set the amount of steps this entity will calculate per loop
        startMoving();			// Start generating this entities path
    }
    
    // Set our animation states
    private void loadAnimation()
    {
        // Animation States: Still
        addState("still", "source/alien_still.gif");
    
        // Animation States: Moving
        addState("walk_1", "source/alien_walk_1.gif", "moving");
        addState("walk_2", "source/alien_walk_2.gif", "moving");
        
        // Set initial state
        setState("still");
    }
}