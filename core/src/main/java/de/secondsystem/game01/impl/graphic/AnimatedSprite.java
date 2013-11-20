package de.secondsystem.game01.impl.graphic;

import org.jsfml.graphics.RenderTarget;
import org.jsfml.graphics.Sprite;
import org.jsfml.system.Vector2f;

import de.secondsystem.game01.impl.graphic.AnimationTexture.AnimationData;
import de.secondsystem.game01.model.IAnimated;
import de.secondsystem.game01.model.IDimensioned;
import de.secondsystem.game01.model.IDrawable;
import de.secondsystem.game01.model.IMoveable;
import de.secondsystem.game01.model.IUpdateable;


public class AnimatedSprite implements IDrawable, IMoveable, IAnimated, IUpdateable, IDimensioned {
	
	private final Sprite  sprite = new Sprite();
	private final AnimationTexture animationTexture;
	
	private boolean repeated = false;
	private boolean playing = false;
	private AnimationData currentAnimationData;
	private AnimationType currentAnimationType;
	private float   currentFrame;
	private float animationSpeed;
	
	public AnimatedSprite(AnimationTexture animationTexture) {
		this.animationTexture = animationTexture;
	}


	@Override
	public void draw(RenderTarget renderTarget) {
    	renderTarget.draw(sprite);
	}

	@Override
	public void setPosition(Vector2f pos) {
		sprite.setPosition(pos);	
	}

	@Override
	public void setRotation(float degree) {
		sprite.setRotation(degree);
	}

	@Override
	public float getRotation() {
		return sprite.getRotation();
	}

	@Override
	public Vector2f getPosition() {
		return sprite.getPosition();
	}


	@Override
	public float getHeight() {
		return currentAnimationData.frameHeight;
	}


	@Override
	public float getWidth() {
		return currentAnimationData.frameWidth;
	}


	@Override
	public void update(long frameTimeMs) {
		if( playing )
		{
			currentFrame = currentAnimationData.calculateNextFrame(currentFrame, frameTimeMs*animationSpeed, repeated);
			if( currentFrame<0 )
				play(AnimationType.IDLE, 1.f, true, false, isFlipped());
			
			sprite.setTextureRect(currentAnimationData.calculateTextureFrame(currentFrame));
		}
	}


	@Override
	public void play(AnimationType animation, float speedFactor,
			boolean repeated, boolean cancelCurrentAnimation,
			boolean flipTexture) {
		if (currentAnimationType != animation || cancelCurrentAnimation) {
			currentAnimationData = animationTexture.get(animation);
			sprite.setTexture(currentAnimationData.texture);
			sprite.setOrigin(currentAnimationData.frameWidth/2.f, currentAnimationData.frameHeight/2.f);
			assert( currentAnimationData != null );
			currentAnimationType = animation;
			currentFrame = currentAnimationData.frameStart;
			if( flipTexture )
				flip();
		}

		animationSpeed = speedFactor;
		this.repeated = repeated;
		playing = true;
	}
	
	@Override
	public void stop() {	
		playing = false;
	}


	@Override
	public void resume() {
		playing = true;
	}


	@Override
	public AnimationType getCurrentAnimationType() {
		return currentAnimationType;
	}


	@Override
	public void flip() {
		sprite.scale(-1.f, 1.f);	
	}


	@Override
	public boolean isFlipped() {
		return sprite.getScale().x < 0;
		
	}
	

}
