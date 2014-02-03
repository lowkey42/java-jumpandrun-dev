package de.secondsystem.game01.impl.graphic;

import de.secondsystem.game01.impl.graphic.AnimationTexture.AnimationData;
import de.secondsystem.game01.model.IAnimated;
import de.secondsystem.game01.model.IUpdateable;


public class AnimatedSprite extends SpriteWrappper implements IAnimated, IUpdateable {
	
	private final AnimationTexture animationTexture;
	
	private boolean repeated = false;
	private boolean playing = false;
	private boolean reverse = false;
	private AnimationData currentAnimationData;
	private AnimationType currentAnimationType;
	private float   currentFrame;
	private float animationSpeed;
	
	public AnimatedSprite(AnimationTexture animationTexture, float width, float height) {
		super(width, height);
		
		this.animationTexture = animationTexture;
	}
	
	@Override
	public void setDimensions(float width, float height) {
		float widthScale = width/currentAnimationData.frameWidth;
		sprite.setScale(sprite.getScale().x < 0 ? widthScale*(-1) : widthScale, height/currentAnimationData.frameHeight);
	}


	@Override
	public void update(long frameTimeMs) {
		if( playing )
		{
			if( reverse )
				currentFrame = currentAnimationData.calculateLastFrame(currentFrame, frameTimeMs*animationSpeed, repeated);
			else
				currentFrame = currentAnimationData.calculateNextFrame(currentFrame, frameTimeMs*animationSpeed, repeated);
			
			if( currentFrame < 0 )
				return;
			
			sprite.setTextureRect(currentAnimationData.calculateTextureFrame(currentFrame));
		}
	}


	@Override
	public void play(AnimationType animation, float speedFactor,
			boolean repeated, boolean cancelCurrentAnimation,
			boolean flipTexture) {
		if (currentAnimationType != animation || cancelCurrentAnimation) {
			AnimationData animData = animationTexture.get(animation);
			
			if( animData == null )
				return;
			
			reverse = false;
			currentAnimationData = animData;
			sprite.setTexture(currentAnimationData.texture);
			sprite.setOrigin(currentAnimationData.frameWidth/2.f, currentAnimationData.frameHeight/2.f);
			currentAnimationType = animation;
			currentFrame = currentAnimationData.frameStart;
			sprite.setTextureRect(currentAnimationData.calculateTextureFrame(currentFrame));
			setDimensions(width, height);
			if( flipTexture )
				flip();			
		}

		animationSpeed = speedFactor;
		this.repeated = repeated;
		playing = true;
	}

	@Override
	public void play() {
		play(currentAnimationType, animationSpeed, repeated, false, isFlipped());
	}
	
	@Override
	public void stop() {	
		playing = false;
		currentFrame = currentAnimationData.frameStart;
		sprite.setTextureRect(currentAnimationData.calculateTextureFrame(currentFrame));
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

	@Override
	public void reverse() {
		reverse = true;
	}

	@Override
	public void pause() {
		playing = false;
	}

}
