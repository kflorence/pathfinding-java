package game.modules;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class WindowEvents implements WindowListener
{
    @Override
    public void windowClosing(WindowEvent e)
    {
        // Pass this to windowClosed
        this.windowClosed(e);
    }
    
    @Override
    public void windowClosed(WindowEvent e)
    {
        System.out.println("Window closed, Program exiting...");
        
        // Close the program
        System.exit(0);
    }
    
    @Override
    public void windowActivated(WindowEvent e) {}

    @Override
    public void windowDeactivated(WindowEvent e) {}

    @Override
    public void windowDeiconified(WindowEvent e) {}

    @Override
    public void windowIconified(WindowEvent e) {}

    @Override
    public void windowOpened(WindowEvent e) {}
}
