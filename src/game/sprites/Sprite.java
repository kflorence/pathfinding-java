/*
 * Sprite.java
 *
 * Created on November 10, 2006, 6:19 PM
 */

package game.sprites;

import java.awt.Image;
import java.awt.geom.AffineTransform;

/**
 *
 * @author Kyle Florence
 */
public class Sprite {
    private Image image;
    
    // Constructor
    public Sprite(Image image) {
        this.image = image;
    }
    
    // Sprite width in pixels
    public int getWidth() {
        return image.getWidth(null);
    }
    
    // Sprite height in pixels
    public int getHeight() {
        return image.getHeight(null);
    }
    
    // Draw the sprite at x, y of graphics context g
    public void draw(java.awt.Graphics2D g, double ang, int x, int y)
    {
    	// Store the previous transform and grab a clone of it for the new one
    	AffineTransform oldTransform = g.getTransform();
    	AffineTransform newTransform = (AffineTransform)(oldTransform.clone());
    	
    	// Create our new transform by rotating by ang
    	newTransform.rotate(ang, x + (getWidth() / 2), y + (getHeight() / 2));
    	
    	// Set our new transform, draw the image, then change it back to how it was
    	g.setTransform(newTransform);
        g.drawImage(image, x, y, null);
        g.setTransform(oldTransform);
    }
}
