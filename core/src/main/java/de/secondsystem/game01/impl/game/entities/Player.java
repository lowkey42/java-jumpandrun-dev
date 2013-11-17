package de.secondsystem.game01.impl.game.entities;

import java.util.UUID;

import org.jsfml.graphics.RenderTarget;
import org.jsfml.graphics.Sprite;

import de.secondsystem.game01.impl.graphic.AnimatedSprite;
import de.secondsystem.game01.impl.map.IGameMap;
import de.secondsystem.game01.impl.map.IGameMap.IWorldSwitchListener;
import de.secondsystem.game01.impl.map.physics.IPhysicsBody;
import de.secondsystem.game01.model.Attributes;
import de.secondsystem.game01.model.IDrawable;

public class Player extends ControllableGameEntity implements IWorldSwitchListener{
	
	public Player(UUID uuid, GameEntityManager em, IGameMap map,
			Attributes attributes) {
		super(uuid, em, map, attributes);

		assert( representation instanceof AnimatedSprite );
        
		animatedSprite = (AnimatedSprite) representation;
		sprite = animatedSprite.getSprite();
	}


	private float currentFrame;
	private Sprite sprite;
	private AnimatedSprite animatedSprite;
	
	
	public void update(long frameTimeMs) {
		final float xMove = hDirection==null ? 0 : hDirection==HDirection.LEFT ? -1 : 1;

		// update animations
		if( xMove == 1 )
		{
			currentFrame += frameTimeMs/100.f;
    		if( currentFrame >= 4)
    			currentFrame = 1.f;
    		if( sprite.getScale().x < 0 )
    			animatedSprite.flip();
		}
		else
			if( xMove == -1 )
			{
	    		currentFrame += frameTimeMs/100.f;
	    		if( currentFrame >= 4)
	    			currentFrame = 1.f;
	    		if( sprite.getScale().x > 0 )
	    			animatedSprite.flip();	
			}
		
		if( !moved )
			currentFrame = 0.f;
		
		animatedSprite.setFrame(currentFrame);
		
		super.update(frameTimeMs);
	}
	
}
