package game;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;

import game.map.*;
import game.entities.*;


public class Main implements WindowListener, MouseListener, KeyListener
{
	private static final long serialVersionUID = -8882280247883036030L;
	
	/**
	 * 
	 * DEBUG AND TESTING VARIABLES
	 * 
	 **/
	
    // Number of entities in our game (will be dynamic later)
    private static int entityCount = 1;
    
    // Screen refresh information
    private long 	timeElapsed;
    private long 	frameDelay;
    private int 	frameCount;
    private int 	framesPerSecond;
    
    // The list of places the user has clicked, for grid redrawing
    private static ArrayList<Cell> clickList = new ArrayList<Cell>();

	/**
	 * 
	 * PERMANENT VARIABLES
	 * 
	 **/
    
    // Our single eference to this class
    private static Main game;
    
    // The title of the game
    public static final String GAMETITLE = "jUntitled";
    
    // The width and height of the game window
    public static final int WIDTH  = 800;
    public static final int HEIGHT = 600;
    
	// Variables for the window environment
    private static Frame frame;
    
    // Game States
    private static final int MENU     = 0;
    private static final int ABOUT    = 1;
    private static final int SCORES   = 2;
    private static final int PLAY 	  = 3;
    private static final int GAMEOVER = 4;
    
    // Frame delay rates (milliseconds)
    private static final int DELAY 		= 10;
    private static final int DEBUGDELAY = 10;
    
    // Lives and rounds
    //private static final int LIVES = 5;
    //private static final int ROUNDS = 25;
    
    // Variables related to the game environment
    private static Map 				map;
    private static Graphics2D 		g;
    private static BufferStrategy 	strategy;
    
    // Entity list for keeping track of the entities in the game
    private static ArrayList<Entity> entityList = new ArrayList<Entity>();
    private static ArrayList<Entity> removeList = new ArrayList<Entity>();
    
    // Retrieve a graphics configuration according to the computer environment
    private static GraphicsConfiguration gc =
            GraphicsEnvironment.getLocalGraphicsEnvironment().
            getDefaultScreenDevice().getDefaultConfiguration();
    
    // Variables relating to the state of the game
    private static int 	   gameState   = MENU;
    private static boolean gameWaiting = true;
    private static boolean gameRunning = true;
    private static boolean gamePaused  = true;
    private static boolean gameDebug   = false;
    private static boolean gameKeyLock = false;
    
    // Font Stuffs
    Font font 			= new Font("Arial", Font.PLAIN, 12);
    FontMetrics fm 		= frame.getFontMetrics(font);
    int fontWidth 		= fm.getMaxAdvance();
    int fontHeight 		= fm.getHeight();
    
    // Create game window, initialize game variables, etc...
    public Main()
    {    
        // Create Frame
        frame = new Frame();
        frame.addWindowListener(this);
        
        // We are handling repaint's, not AWT
        setIgnoreRepaint(true);
        
        // Finalize Frame properties, then set visible
        frame.pack();
        frame.setResizable(false);
        frame.setVisible(true);
        
        // Create and retain focus for keyEvents
        addKeyListener(this);
        requestFocus();
        
        // Create mouse listener for mouseEvents
        addMouseListener(this);
        
        // Create buffer strategy for off-screen accelerated graphics
        createBufferStrategy(2);
	    strategy = getBufferStrategy();
        
        // Finally, initialize game        
        initialize();
    }

    /**
    *
    * Private functions
    *
    **/
    
    // Initializes the game
    private void initialize()
    {    	
        // TODO: this is where most of the initial loading/parsing will take place
        // - load resource file (links to images, sounds and files)
        // - load map file (grid size, node information) * passed to Map
        // - load entity file (entity images, states and other information)
    	
    	// New Map (20x20) with cell size of 20
        map = new Map(20, 20, 25);
    }
    
    // The bread and butter of Main, this is the games infinite loop.
    private void loop()
    {
    	String s;
        long lastLoopTime = System.nanoTime();       
        
        // The main game loop
        while (gameRunning)
        {    
            // The elapsed time between now and the last loop, for movement purposes
            long delta = System.nanoTime() - lastLoopTime;
            lastLoopTime = System.nanoTime();
            
            // Blank graphics context for the accelerated graphics
            g = (Graphics2D) strategy.getDrawGraphics();
            g.setColor(new Color(50, 50, 50));
            g.fillRect(0, 0, WIDTH, HEIGHT);
            
            // Game state handler
            switch(gameState)
            {    
                // The user is playing the game    
                case PLAY:
                    // Does the grid need to be redrawn?
                    if (!clickList.isEmpty())
                    {
                    	// Loop through clicks and toggle the cells
                    	for (Cell cell : clickList) cell.togglePlayable();
                    	
                    	// Redraw the grid
                    	Map.draw();
                    	
                    	// Clear the clickList
                    	clickList.clear();
                    }                	
                	
                    // Grab the grid's image
                    g.setClip(Map.getClip());
                    g.drawImage(Map.getImage(), 0, 0, null);
                    g.setClip(0, 0, WIDTH, HEIGHT);
                    
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
                    g.drawString(s, WIDTH - (fm.stringWidth(s)) - 10, 20);
                    
                    // The game is paused
                    if (gamePaused)
                    {
                        // Display notification
                    	g.setColor(Color.white);
                        s = "The game is paused.";
                        g.drawString(s, 10, 20);
                    }
                    
                    // Waiting for next round
                    else if (gameWaiting)
                    {
                        // Display notification
                    	g.setColor(Color.white);
                        s = "Press SPACEBAR to start the next round.";
                        g.drawString(s, 10, 20);
                    }
                    
                    // Update entity position, draw it, and update it's logic for the next loop
                    for (Entity entity : entityList)
                    {                    	
                    	// Make sure the game isn't paused
	                	if (!isPaused() && !isWaiting())
	                   	{
		                  	// If it's a logical entity, update it's logic
		                  	if (entity.isLogical()) entity.doLogic();
	                		
	                		// If it's an animated entity, update it's animation
		                    if (entity instanceof AnimatedEntity) entity.update(delta);
		                  	
	                		// If it's a moveable entity, update it's movement
		                    if (entity instanceof MovableEntity) entity.move(delta);
	                    }
	                	
	                	// If the game is in debug mode...
	                	if (inDebugMode())
	                	{
		            		// Set this entity's color
		            		g.setColor(entity.getColor());
		                	
		            		// Draw this entity's bounding box
		    	            g.fill(entity.getRectangle());
	                	}
	                	
	                    // Draw the entity on screen
	                    entity.draw(g);
	                }
	                
                    // Do we need to remove any entities?
                    if (!removeList.isEmpty())
                    {
	                    // Remove any entities that have been marked for deletion
	                    entityList.removeAll(removeList);
	                        
	                    // Clear the remove list
	                    removeList.clear();
                    }
                	
                    // Populate our entityList if it's empty
                	if (entityList.isEmpty())
                	{
                        // The round has ended, wait for key press to start next round
                		gameWaiting = true;
                		
                		// Create the entities for the next round
	            		// This number will be dynamic later, as well as their placement positions
	                    for (int i = 0; i < entityCount; i++)
	                    {
	                        entityList.add(
	                        	new AlienEntity(
	                        		new Point(6 + i, 1),
	                        		new Point(6 + i, 20)
	                        	)
	                        );
	                    }
	                    
	                    //entityCount++;
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
            
            // Finished drawing, display graphics context
            g.dispose();
            strategy.show();
            
            try {
                /* 
                 * Each frame is shown for 10 milliseconds.  This is to give the
                 * game some leeway in running through the loop process.  That 
                 * way our animation stays smooth instead of getting choppy when
                 * certain loops take longer than others.  So, in order to know
                 * how long we have left to wait we calculate the time of the last
                 * loop plus 10 milliseconds, then subtract the time now and we
                 * are left with our answer.
                 */
                frameDelay = ((lastLoopTime + (gameDebug ? DEBUGDELAY : DELAY) * 1000000 - System.nanoTime()) / 1000000);
                
                // Sleep for specified time
                Thread.sleep(frameDelay); 
            } 
            
            // Thread could not sleep
            catch (Exception e) {
            	//System.out.println("the main loop has insomnia.");
            }
        }
    }
    
    // Toggle waiting state
    private static void toggleWait() {
	    gameWaiting = !gameWaiting;
    }
    
    // Toggle debug mode
    private static void toggleDebug() {
    	gameDebug = !gameDebug;
    }
    
    // Toggle paused mode
    private static void togglePause() {
    	gamePaused = !gamePaused;
    }
    
    /**
    *
    * Public functions
    *
    */
   
    // This function is called when the JAR is opened
    public static void main(String[] args)
    {   	
        // Call constructor
    	game = new Main();
    	
    	// Start up the game loop
   		game.loop();
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
    
    // Returns the game's graphics configuration context
    public static GraphicsConfiguration getGC() {
        return gc;
    }
   
    // Returns a reference to the Map class
    public static Map getMap() {
        return map;
    }
    
    // Whether or not the game is in the waiting state
    public static boolean isWaiting() {
    	return gameWaiting;
    }
    
    // Whether or not we are in debug mode
    public static boolean inDebugMode() {
    	return gameDebug;
    }
    
    // Whether or not the game is paused
    public static boolean isPaused() {
    	return gamePaused;
    }
   
    // Remove an entity from the game
    public static void removeEntity(Entity entity)
    {
	    // Mark this entity for deletion
	    removeList.add(entity);
    }
   
    /**
     * This section contains the implementation of the WindowListener 
     * class (abstract overrides).  This controls aspects of the window
     * such as closing, opening, and minimizing.
     **/
   
    // Perhaps move Listeners to another file someday
    public void windowClosing(WindowEvent e) { System.exit(0);}
    public void windowOpened(WindowEvent e) {}
    public void windowClosed(WindowEvent e) {}
    public void windowIconified(WindowEvent e) {}
    public void windowDeiconified(WindowEvent e) {}
    public void windowActivated(WindowEvent e) {}
    public void windowDeactivated(WindowEvent e) {}
   
    /**
     * This section contains the implementation of the ActionListener 
     * class (abstract overrides).  This controls what happens to the
     * menubar and menu items.
     */
   
    /**
     * This section contains the implementation of the KeyListener 
     * class (abstract overrides).  This controls what happens when
     * the user presses and releases keys on the keyboard.
     *
     */
   
     public void keyTyped(KeyEvent e) {}
	
	 public void keyPressed (KeyEvent e)
	 {
	     // If keys are locked, don't do anything
	     if (gameKeyLock) return;
	     
	     // Check for other keys
	     switch(e.getKeyChar())
	     {
	         // Toggle pause
	         case 'p':
	             togglePause();
	             break;
	     
	         // Start game
	         case 's':
	        	 gameState = PLAY;
	             gamePaused = false;
	             break;
	     
	         // Toggle debug
	         case 'd':
	             toggleDebug();
	             Map.draw();
	             break;
	               
	         // Toggle grid
	         case 'g':
	        	 Map.toggleGrid();
	        	 Map.draw();
	             break;
	               
	         // SPACE, Toggle gameWaiting
	         case 32:
	             if (gameWaiting) toggleWait();
	             break;
	           
	         // ESC, exit game
	         case 27:
	             System.exit(0);
	             break;
	    }
    }

    public void keyReleased(KeyEvent e) {}

    /**
     * This section contains the implementation of the MouseListener 
     * class (abstract overrides).  This controls what happens when
     * the user interacts with the game with a mouse.
     *
     */    
   
    public void mouseClicked(MouseEvent e)
    {    	
        switch(gameState)
        { 
	        // In-game
            case PLAY:
            	Cell cell;
            	
        	    // Make sure we are in-between rounds
        	    if (gameWaiting)
        	    {        	    	
        	    	// Grab the cell that was clicked on
        		    if ((cell = Grid.getCell(Grid.cellFromLocation(e.getPoint()))) != null)
        		    	clickList.add(cell);
        	    }
        	    break;
        }
    }
   
    public void mousePressed(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
    
    // Not yet implemented
    //private void newGame() {}
    //private void removeEntity() {}
    //private void notifyLevelChange() {}
    //private void notifyGameOver() {}
}
