package game.modules;

import game.Game;
import game.map.Cell;
import game.map.Grid;
import game.map.Map;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

public class MouseEvents implements MouseListener
{
    // The list of places the user has clicked, for grid redrawing
    private static ArrayList<Cell> clickList = new ArrayList<Cell>();
    
    // Returns the list of clicks
    public static ArrayList<Cell> getClickList()
    {
        return clickList;
    }
    
    // Process any clicks that have occurred since the last loop
    public static void processList()
    {
        // Does the grid need to be redrawn?
        if (!clickList.isEmpty())
        {
            // Loop through clicks and toggle the cells
            for (Cell cell : clickList) cell.togglePlayable();
            
            // Clear the clickList
            clickList.clear();
            
            // Redraw the map
            Map.drawMap();
        } 
    }
    
    @Override
    public void mouseClicked(MouseEvent e)
    {        
        switch(Game.getState())
        { 
            // In-game
            case Game.PLAY:
                Cell cell;
                
                // Make sure we are in-between rounds
                if (Game.isWaiting())
                {                   
                    // Grab the cell that was clicked on
                    if ((cell = Grid.getCell(Grid.cellFromLocation(e.getPoint()))) != null)
                        clickList.add(cell);
                }
                break;
        }
    }

    @Override
    public void mouseEntered(MouseEvent e)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseExited(MouseEvent e)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void mousePressed(MouseEvent e)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseReleased(MouseEvent e)
    {
        // TODO Auto-generated method stub

    }
}
