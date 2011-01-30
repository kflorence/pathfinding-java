package game;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;

import javax.swing.JFrame;

import game.map.*;
import game.modules.Graphics;
import game.modules.MouseEvents;
import game.modules.WindowEvents;
import game.entities.*;

// Static class Game
public class Game
{	
	/**
	 * 
	 * DEBUG AND TESTING VARIABLES
	 * 
	 **/
	
    // Screen refresh information
    private static long timeElapsed, frameDelay;
    private static int frameCount, framesPerSecond;
	
    // Number of entities in our game (will be dynamic later)
    private static int entityCount = 1;

	/**
	 * 
	 * PERMANENT VARIABLES
	 * 
	 **/
    
    // Variables relating to the game environment
    private static Map map;
    
    // Variables for the window environment
    private static JFrame frame;
    
    // The title of the game
    public static final String GAMETITLE = "jUntitled";
    
    // Font settings
    public static Font font;
    public static int fontWidth;
    public static int fontHeight;
    public static FontMetrics fontMetrics;
    
    // Game States
    public static final int MENU     = 0;
    public static final int ABOUT    = 1;
    public static final int SCORES   = 2;
    public static final int PLAY     = 3;
    public static final int GAMEOVER = 4;
    
    // Initial game state
    private static int state = MENU;
    
    // The width and height of the game window
    public static final int WIDTH  = 800;
    public static final int HEIGHT = 600;
    
    // Frame delay rates (milliseconds)
    private static final int DELAY 		= 10;
    private static final int DEBUGDELAY = 10;
    
    // Variables relating to the state of the game
    private static boolean debug    = true;
    private static boolean waiting  = true;
    private static boolean running  = true;
    
    // KeyHandler conditions
    private static boolean paused   = false;
    private static boolean locked   = false;
    
    // Entity lists for keeping track of the entities in the game
    private static ArrayList<Entity> entityList = new ArrayList<Entity>();
    private static ArrayList<Entity> removeList = new ArrayList<Entity>();

    /**
    *
    * Private functions
    *
    **/
    
    // Initializes the game
    private static void initialize()
    {    	
        // TODO: this is where most of the initial loading/parsing will take place
        // - load resource file (links to images, sounds and files)
        // - load map file (grid size, node information) * passed to Map
        // - load entity file (entity images, states and other information)
        
        // Initialize frame and set properties
        frame = new JFrame();
        
        // Frame settings
        frame.setTitle(GAMETITLE);
        frame.setResizable(false);
        
        // Font settings
        font = new Font("Arial", Font.PLAIN, 12);
        fontMetrics = frame.getFontMetrics(font);
        fontWidth = fontMetrics.getMaxAdvance();
        fontHeight = fontMetrics.getHeight();
        
        // Add our graphics handler
        frame.add(new Graphics());
        
        // Add a window listener to the frame
        frame.addWindowListener(new WindowEvents());
        
        // Make this window visible
        frame.pack();
        frame.setVisible(true);
        
        // TODO: make this dynamic (FileChooser)
        map = new Map("maps/default.xml");
        
        // Start the game loop
        Game.loop();
    }
    
    // The bread and butter of Main, this is the games infinite loop.
    private static void loop()
    {
    	String s;
    	Graphics2D g;
        long delta, lastLoopTime = System.nanoTime();       
        
        // Grab our strategy from GraphicsHandler
        BufferStrategy strategy = Graphics.getStrategy();
        
        // The main game loop
        while (running)
        {            
            // The elapsed time between now and the last loop, for movement purposes
            delta = System.nanoTime() - lastLoopTime;
            lastLoopTime = System.nanoTime();
            
            // Grab the graphics context and buffer strategy from GraphicsHandler
            g = Graphics.getGraphics2D();
            
            // Blank graphics context for the accelerated graphics
            g.setColor(Color.black);
            g.fillRect(0, 0, WIDTH, HEIGHT);
            
            // Game state handler
            switch(state)
            {    
                // The user is playing the game    
                case PLAY:
                    // Set clip for graphics context (drawable area)
                    g.setClip(0, 0, WIDTH, HEIGHT);
                    
                    // Draw the Map image
                    g.drawImage(Map.getMap(), 0, 0, null);
                    
                    // Draw grid if showGrid is enabled
                    if (Map.showGrid()) g.drawImage(Map.getGrid(), 0, 0, null);
                    
                    //g.drawImage(Map.getTest(), 0, 0, null);
                    
                    // Process mouse events
                    MouseEvents.processList();
                    
                    // What to draw if the game is paused
                    if (paused)
                    {
                        // Display notification
                    	g.setColor(Color.white);
                        s = "The game is paused.";
                        g.drawString(s, 10, 20);
                    }
                    
                    // What to draw if game is in waiting state
                    else if (waiting)
                    {
                        // Display notification
                    	g.setColor(Color.white);
                        s = "Press SPACEBAR to start the next round.";
                        g.drawString(s, 10, 20);
                    }
                    
                    // What to draw if the game is in debug mode
                    if (debug)
                    {
                        // Update frameCount and elapsed time
                        frameCount++;
                        timeElapsed += delta;

                        // If a second has passed...
                        if (timeElapsed >= 1000000000)
                        {
                            // Set our new FPS
                            framesPerSecond = frameCount;
                            
                            // Reset our variables
                            frameCount = 0;
                            timeElapsed = 0;
                        }
                        
                        // Display FPS
                        g.setColor(Color.white);
                        s = "Frame Delay: " + frameDelay + " (FPS: " + framesPerSecond + ")";
                        g.drawString(s, WIDTH - (fontMetrics.stringWidth(s)) - 10, 20);
                    }
                    
                    // Update entity position, draw it, and update it's logic for the next loop
                    for (Entity entity : entityList)
                    {                    	
                    	// Make sure the game isn't paused
	                	if (!paused && !waiting)
	                   	{
		                  	// If it's a logical entity, update it's logic
		                  	if (entity.isLogical()) entity.doLogic();
	                		
	                		// If it's an animated entity, update it's animation
		                    if (entity instanceof AnimatedEntity) entity.update(delta);
		                  	
	                		// If it's a moveable entity, update it's movement
		                    if (entity instanceof MovableEntity) entity.move(delta);
	                    }
	                	
	                	// Draw bounding box and path if in debug mode
	                	if (debug)
	                	{
		            		// Set this entity's color
		            		g.setColor(entity.getColor());
		                	
		            		// Draw this entity's bounding box
		    	            g.fill(entity.getBounds());
		    	            
		    	            // Draw path
		    	            g.drawImage(((MovableEntity) entity).getPath(), 0, 0, null);
	                	}
	                	
	                    // Add the entity to our graphics context
	                    entity.draw(g);
	                }
	                
                    // Remove any entities that need to be removed here
                    if (!removeList.isEmpty())
                    {
	                    // Remove any entities that have been marked for deletion
	                    entityList.removeAll(removeList);
	                        
	                    // Clear the remove list
	                    removeList.clear();
	                    
	                    // Clear visible entity paths
	                    //if (isDebug()) Map.draw();
                    }
                	
                    // Populate our entityList if it's empty
                	if (entityList.isEmpty())
                	{
                        // The round has ended, wait for key press to start next round
                		waiting = true;
                		
                		// Create the entities for the next round
	            		// TODO: make dynamic later, as well as their placement positions
	                    for (int i = 0; i < entityCount; i++)
	                    {
	                        entityList.add(
	                        	new AlienEntity(
	                        		new java.awt.Point(6 + i, 1),
	                        		new java.awt.Point(6 + i, 20)
	                        	)
	                        );
	                    }
                	}       
                    break;

                // Main menu
                case MENU:
                    g.setColor(Color.white);
                    s = "Menu will be here soon. Press 'S' to start.";
                    g.drawString(s, 10, 20);
                    break;
            
                // About section    
                case ABOUT:
                    g.setColor(Color.white);
                    s = "About: Game development by Kyle Florence.";
                    g.drawString(s, 10, 20);
                    break;
            
                // High scores section
                case SCORES:
                    g.setColor(Color.white);
                    s = "Someday, there will be high scores here.";
                    g.drawString(s, 10, 20);
                    break;
                    
                // Game over, player has no more lives left    
                case GAMEOVER:
                    break;
            }
            
            // We are done drawing, clean up
            g.dispose();
            
            // Flip over our buffered strategy (page flipping)
            strategy.show();
            
            try {
                /* 
                 * Each frame is shown for 10 milliseconds.  This is to give the
                 * game some leeway in running through the loop process.  That 
                 * way our animation stays smooth instead of getting choppy when
                 * certain loops take longer than others.  So, in order to know
                 * how long we have left to wait we calculate the time of the last
                 * loop plus 10 milliseconds, then subtract the time now and we
                 * are left with our answer (a little buggy with large calculations).
                 */
                frameDelay = ((lastLoopTime + (debug ? DEBUGDELAY : DELAY) * 1000000 - System.nanoTime()) / 1000000);
                
                // Sleep for specified time
                Thread.sleep(frameDelay); 
            } 
            
            // This loop took longer than 10 milliseconds
            catch (Exception e) { /* Do nothing */ }
        }
    }
    
    /**
    *
    * Public functions
    *
    */
   
    // Remove an entity from the game
    public static void removeEntity(Entity entity)
    {
        // Mark this entity for deletion
        removeList.add(entity);
    }
    
    // FOR DEBUG
    // Returns the number of the most recent entity in entityList
    public static int entityNumber() {
        return entityList.size() + 1;
    }
    
    // Returns the list of entities
    public static ArrayList<Entity> getEntities() {
    	return entityList;
    }
   
    // Returns a reference to the Map class
    public static Map getMap() {
        return map;
    }
    
    // Return the state of the game
    public static int getState() {
        return state;
    }
    
    // Toggle waiting state
    public static void toggleWait() {
        waiting = !waiting;
    }
    
    // Toggle debug mode
    public static void toggleDebug() {
        debug = !debug;
    }
    
    // Toggle paused mode
    public static void togglePause() {
        paused = !paused;
    }
    
    // Toggle key lock
    public static void toggleLock() {
        locked = !locked;
    }
    
    // Whether or not the game is in the waiting state
    public static boolean isWaiting() {
    	return waiting;
    }
    
    // Whether or not we are in debug mode
    public static boolean isDebug() {
    	return debug;
    }
    
    // Whether or not the game is paused
    public static boolean isPaused() {
    	return paused;
    }
    
    // Whether or not the keys are locked
    public static boolean isLocked() {
        return locked;
    }
    
    // Starts the game
    public static void start()
    {
        if (getState() != PLAY)
        {
            state = PLAY;
            paused = false;
        }
    }
    
    // This function is called when the JAR is opened
    public static void main(String[] args)
    {
        // Handle arguments here
            // ...
        
        Game.initialize();
    }
    
    // Not yet implemented
    //private void newGame() {}
    //private void removeEntity() {}
    //private void notifyLevelChange() {}
    //private void notifyGameOver() {}
}