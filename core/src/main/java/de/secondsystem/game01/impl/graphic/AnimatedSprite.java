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
	
	public AnimatedSprite(AnimationTexture animationTexture) {
		this(animationTexture, animationTexture.getDefault().frameWidth, animationTexture.getDefault().frameHeight);
	}
	public AnimatedSprite(AnimationTexture animationTexture, float width, float height) {
		super(width, height);
		
		this.animationTexture = animationTexture;
		play(animationTexture.getDefaultType(), 1, true);
	}
	
	@Override
	protected void updateScale() {
		sprite.setScale(width/currentAnimationData.frameWidth * (isFlipped()?-1:1), height/currentAnimationData.frameHeight);
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
			boolean repeated) {
		if( currentAnimationType!=animation ) {
			AnimationData animData = animationTexture.get(animation);
			
			if( animData == null )
				return;
			
			reverse = false;
			currentAnimationData = animData;
			sprite.setTexture(currentAnimationData.texture);
			sprite.setOrigin(Math.abs(currentAnimationData.frameWidth/2.f), Math.abs(currentAnimationData.frameHeight/2.f));
			currentAnimationType = animation;
			currentFrame = currentAnimationData.frameStart;
			sprite.setTextureRect(currentAnimationData.calculateTextureFrame(currentFrame));
			updateScale();
		}

		animationSpeed = speedFactor;
		this.repeated = repeated;
		playing = true;
	}

	@Override
	public void play() {
		play(currentAnimationType, animationSpeed, repeated);
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
	public void reverse() {
		reverse = true;
	}

	@Override
	public void pause() {
		playing = false;
	}

}
