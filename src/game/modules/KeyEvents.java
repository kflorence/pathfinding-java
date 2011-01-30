package game.modules;

import game.Game;
import game.map.Map;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyEvents implements KeyListener
{    
	@Override
    public void keyTyped(KeyEvent e) {}
	
	@Override
	public void keyPressed (KeyEvent e)
	{	    
	    // If keys are locked, don't do anything
	    if (Game.isLocked()) return;
	     
	    // Check for other keys
	    switch(e.getKeyChar())
	    {
	        // Toggle pause
	        case 'p':
	        {
	            Game.togglePause();
	            break;
	        }

	        // Toggle debug
	        case 'd':
	        {
	            Game.toggleDebug();
	            break;
	        }
	               
	        // Toggle grid
	        case 'g':
	        {
	            Map.toggleGrid();
	            break;
	        }
	        
	        // Start game (temp fix)
	        case 's':
	        {
                Game.start();
	            break;
	        }
	             
	        // SPACE, Toggle gameWaiting
	        case 32:
	        {
	            if (Game.isWaiting()) Game.toggleWait();
	            break;
	        }
	           
	        // ESC, exit game
	        case 27:
	        {
	            System.exit(0);
	            break;
	        }
	    }
	}
	
	@Override
    public void keyReleased(KeyEvent e) {}
}