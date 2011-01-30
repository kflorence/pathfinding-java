package game.entities;

import game.map.Grid;
import game.Main;
import game.sprites.*;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;

// The base class for all entities
public abstract class Entity
{
    private double x;									// The x-coordinate this entity will be drawn at
    private double y;									// The y-coordinate this entity will be drawn at
    private double angle;								// The angle this entity will be drawn at
    private Sprite sprite;								// The sprite that will be used for the entity

    private int number 			= Main.entityNumber();	// The unique number assigned to this entity
    private Rectangle me 		= new Rectangle();		// The bounding box for this entity (FIXME)
    private boolean canSpawn 	= false;				// Determine whether or not this entity spawns
    private boolean isLogical	= false;				// Determine whether or not this entity is logical
    
    private boolean debugMode 	= true;					// Whether or not to enable debugging for entities
    
    // This entities path color (for debugging)
    private Color color = new Color((int)(Math.random() * 255), (int)(Math.random() * 255), (int)(Math.random() * 255));
    
    // Creates a new instance of Entity, no base sprite 
    public Entity(int r, int c) { 
        this("", r, c); 
    }
    
    // Creates a new instance of Entity with base sprite
    public Entity(String ref, int r, int c)
    {    	
    	if (debugOn()) System.out.println("Entity #" + getNumber() + ": Initialized.");
    	
    	// The location of this cell in pixels (x, y)
    	setLocation(Grid.getCell(r,c).getLocation());
        
        // If base sprite is given, load it.
        if (!ref.isEmpty()) this.sprite = SpriteStore.get().getSprite(ref);
        
        // Otherwise, set the sprite to null
        else this.sprite = null;
    }
    
    /**
     * 
     * Entity Information
     * 
     **/
    
    // Whether or not this entity can spawn
    public boolean canSpawn() {
        return canSpawn;
    }
    
    // Whether or not this entity is logical
    public boolean isLogical() {
        return isLogical;
    }
    
    public boolean debugOn() {
    	return debugMode;
    }
    
    /**
     * 
     * SET properties
     * 
     **/
    
    // Set the current sprite
    public void setSprite(Sprite sprite) {
        this.sprite = sprite;
    }
    
    // Set angle
    public void setAngle(double angle) {
    	this.angle = angle;
    }
    
    // Set location via Point (x, y)
    public void setLocation(Point loc)
    {
    	this.x = loc.x;
    	this.y = loc.y;
    }
    
    // Set location via x and y coordinates
    public void setLocation(double x, double y)
    {
    	this.x = x;
    	this.y = y;
    }
    
    // Make this entity logical
    public void setLogic(boolean isLogical)
    {        
    	this.isLogical = isLogical;
    }
    
    // Make this entity spawnable
    public void setSpawn(boolean canSpawn) {
        this.canSpawn = canSpawn;
    }
    
    /**
     * 
     * GET properties
     * 
     **/
    
    // Return current sprite
    public Sprite getSprite() {
        return sprite;
    }
    
    // Return angle
    public double getAngle() {
        return angle;
    }
    
    // Return x coordinate
    public double getX() {
        return x;
    }
    
    // Return y coordinate
    public double getY() {
        return y;
    }
    
    // Return this entity's color
    public Color getColor() {
    	return color;
    }
    
    // Return this entity's number
    public int getNumber() {
    	return number;
    }
    
    // Return a rectangle representation of this entity
    public Rectangle getRectangle()
    {
        // Create rectangle at current location
    	me.setBounds((int) x, (int) y, sprite.getWidth(), sprite.getHeight());
        
    	// Return our rectangle
        return me;
    }
    
    // Return the Node this entity is currently in
    public Point getCellLocation() {
        return Grid.cellFromLocation((int) x, (int) y);
    }
    
    // Drawing this entity on a particular graphics context
    public void draw(java.awt.Graphics2D g) {
        sprite.draw(g, angle, (int) x, (int) y);
    }
    
    // Remove entity
    public void destroy()
    {
    	Main.removeEntity(this);
    }
    
    /**
     * 
     * ABSTRACT functions (overwritten by class extensions)
     * 
     **/
    
    // For path debug
    public abstract java.awt.Image getPathImage();
    
    // Updates the entity's movement, if needed
    public abstract void move(long delta);
    
    // Updates the entities animation, if needed
    public abstract void update(long delta);
    
    // Updates the entities logic, if needed
    public abstract void doLogic();
    
    // What to do if this entity collides with another entity
    public abstract void collidedWith(Entity other);
}
