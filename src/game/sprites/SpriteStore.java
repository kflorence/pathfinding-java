/*
 * SpriteStore.java
 *
 * Created on November 10, 2006, 7:20 PM
 */

package game.sprites;

import game.modules.Graphics;

import java.awt.Image;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

import javax.imageio.ImageIO;


/**
 * A resource manager for sprites in the game.
 * 
 * Its often quite important how and where you get your game resources from. In most cases
 * it makes sense to have a central resource loader that goes away, gets
 * your resources and caches them for future use. [singleton]
 *
 * @author Kevin Glass
 */
public class SpriteStore
{
	// Singleton for the SpriteStore class
	private static SpriteStore single = new SpriteStore();
	
	// A hashMap which holds references to our cached sprites
	private HashMap<String, Sprite> sprites = new HashMap<String, Sprite>();
	
	// A reference to the singleton
	public static SpriteStore get() {
		return single;
	}
	
	/**
	 * Retrieve a sprite from the store
	 * 
	 * @param ref The reference to the image to use for the sprite
	 * @return A sprite instance containing an accelerate image of the request reference
	 */
	public Sprite getSprite(String ref)
	{
		URL 			url;
		Image 			image;
		Sprite 			sprite;
		BufferedImage 	sourceImage = null;
		
		// If the sprite already exists in cache, return it
		if (sprites.get(ref) != null) return sprites.get(ref);
		
        url = this.getClass().getResource(ref);
		
        // If we have a valid URL
		if (url != null)
		{
			// Try to read in the image
			try {
				sourceImage = ImageIO.read(url);
			}
			
			// Unable to load reference
			catch (IOException e) {
				fail("Failed to load: " + ref);
			}
		}
		
		// Failed to load resource
		else fail("Failed to load: " + ref);
			
		// Create an accelerated image of the right size to store our sprite in
		image = Graphics.getGraphicsConfig().
		    createCompatibleImage(sourceImage.getWidth(), sourceImage.getHeight(), Transparency.BITMASK);
			
		// Draw our source image into the accelerated image
		image.getGraphics().drawImage(sourceImage, 0, 0, null);
			
		// Create a sprite and add it to the cache
		sprites.put(ref, sprite = new Sprite(image));
		
		// Return the sprite
		return sprite;
	}
	
	// Handles resource load failure
	// TODO: graceful failure
	private void fail(String message)
	{
		System.err.println(message);
		System.exit(0);
	}
}
