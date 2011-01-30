package game.modules;

import game.Game;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.image.BufferStrategy;

public class Graphics extends Canvas
{
    private static final long serialVersionUID = 4860378716319913785L;
    
    private static BufferStrategy strategy;
    private static GraphicsConfiguration gc;
    
    public Graphics()
    {        
        // Tell AWT to ignore repaints (we will handle them)
        this.setIgnoreRepaint(true);
        
        // Create a default graphics configuration
        gc = GraphicsEnvironment.getLocalGraphicsEnvironment().
            getDefaultScreenDevice().getDefaultConfiguration();
        
        // Set size
        this.setBounds(0, 0, Game.WIDTH, Game.HEIGHT);
    }
    
    // When the Canvas becomes visible, create our buffer strategy
    public void addNotify()
    {
        // Make sure we pass it up the chain
        super.addNotify();
        
        // Create buffer strategy for off-screen accelerated graphics (page flipping)
        this.createBufferStrategy(2);
        
        // Store buffer strategy
        strategy = this.getBufferStrategy();
        
        this.addMouseListener(new MouseEvents());
        this.addKeyListener(new KeyEvents());
        
        // Request focus
        //this.setFocusable(true);
        this.requestFocus();
    }
    
    // Clear a graphics context
    public static void clear(Graphics2D g)
    {
        g.setBackground(new Color(0, true));
        g.clearRect(0, 0, Game.WIDTH, Game.HEIGHT);
    }
    
    // Return the graphics context
    public static Graphics2D getGraphics2D() {
        return (Graphics2D) strategy.getDrawGraphics();
    }
    
    // Return the buffer strategy
    public static BufferStrategy getStrategy()
    {
        return strategy;
    }
    
    // Return the graphics configuration
    public static GraphicsConfiguration getGraphicsConfig()
    {
        return gc;
    }
    
    // Returns a compatible image of size x, y
    public static Image getImage(int x, int y)
    {
        return getGraphicsConfig().createCompatibleImage(x, y);
    }
    
    // Returns a compatible image of size x, y and transparency t
    public static Image getImage(int x, int y, int t)
    {
        return getGraphicsConfig().createCompatibleImage(x, y, t);
    }
}
