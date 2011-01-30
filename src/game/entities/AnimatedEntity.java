/*
 * AnimatedEntity.java
 *
 * Created on November 26, 2006, 6:42 PM
 */

package game.entities;

import game.sprites.*;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Kyle Florence
 */
public abstract class AnimatedEntity extends Entity
{
    // The current sprite states
    private String currentSpriteKey;
    private Animation currentSpriteAnimation;
    
    // Keep track of different sprite states
    private HashMap<String, Sprite> spriteStates = new HashMap<String, Sprite>();
    private HashMap<String, Animation> spriteAnimations = new HashMap<String, Animation>();
    
    // Animation update variables
    private int frameNumber = 0;
    private long frameDuration = 0;
    private long lastFrameChange = 0;
    
    // How fast the entity updates, in pixels per second
    private double speed = 50;
    
    // Animation class - keeps track of individual animations
    public static class Animation
    {
        private long duration;
        private ArrayList<Sprite> frames = new ArrayList<Sprite>();
      
        /**
         * @Constructor Animation(double d)
         * 
         * Converts the frame duration (d) from pixels/second to nanoseconds.
         * We do (3 / d) so that as the speed of the entity increases, the
         * frame duration decreases (hence the animation is faster).
         *
         */
        public Animation(double d) {
            setDuration(d);
        }
        
        // Set a frame duration (converts pixels/second to nanoseconds)
        public void setDuration(double d) {
            this.duration = (long)(((3 / d) * 10000) * 1000000);
        }
    }
    
    // Creates a new instance of AnimatedEntity
    public AnimatedEntity(int r, int c) { 
        super(r, c); 
    }
    
    // Return speed
    public double getSpeed() {
        return speed;
    }
    
    // Set the overall speed of the entity (pixels/second)
    public void setSpeed(double speed) {
        this.speed = speed;
    }
    
    // Return the current sprite key
    public String getState() {
        return currentSpriteKey;
    }
    
    // Add another state to the sprite
    public void addState(String key, String ref) {
        spriteStates.put(key, SpriteStore.get().getSprite(ref));
    }
    
    // Add another state to the sprite, add sprite to animation
    public void addState(String key, String ref, String label)
    {
        spriteStates.put(key, SpriteStore.get().getSprite(ref));
        
        // If there isn't an animation with key 'label,' create one.
        if (!spriteAnimations.containsKey(label))
        {
            Animation a = new Animation(getSpeed());
            a.frames.add(spriteStates.get(key));
            
            spriteAnimations.put(label, a);
        } 
        
        // Else add another frame to existing animation 'label.'
        else
        {
            Animation a = spriteAnimations.get(label);
            a.frames.add(spriteStates.get(key));
        }
    }
    
    // Change the state of the sprite
    public void setState(String key)
    {
        // Check to see if the key represents an animation
        if (spriteAnimations.containsKey(key))
        {
            currentSpriteAnimation = spriteAnimations.get(key);
            
            currentSpriteKey = null;
        } 
        
        // State is not an animation
        else
        {
            if (currentSpriteKey != key)
            {
                // Set current sprite and key
                currentSpriteKey = key;
                setSprite(spriteStates.get(key));
                
                // Tell update() there is no need to animate
                currentSpriteAnimation = null;
                frameDuration = 0;
            }
        }
    }
    
    @Override
	public void update(long delta)
    {
        /** 
         * Updates are based on the direction the entity is facing as well as
         * whether or not the entity is moving.  If the entity is moving, it 
         * usually requires an animation, however the setState() method will 
         * make the final determination of whether or not the entity needs 
         * animating.
         *
         */
        
	    // Figure out how much time has passed since the last frame change
        lastFrameChange += delta;
        
        // Change state based on frameDuration
	    if (lastFrameChange > frameDuration)
	    {
            // Reset our frame change time counter
            lastFrameChange = 0;
            
            // Entity needs animation update
            if (currentSpriteAnimation != null)
            {
                setSprite(currentSpriteAnimation.frames.get(frameNumber));
                
                // Set frameDuration to match present animation
                frameDuration = currentSpriteAnimation.duration;

                // Update frameNumber
                if (frameNumber >= (currentSpriteAnimation.frames.size() - 1)) frameNumber = 0;
                else frameNumber++;
            }
	    }
    }
}
