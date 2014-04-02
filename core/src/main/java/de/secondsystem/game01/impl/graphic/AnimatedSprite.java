package de.secondsystem.game01.impl.graphic;

import org.jsfml.audio.Sound;

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
	private final Sound playingSound = new Sound();
	
	public AnimatedSprite(AnimationTexture animationTexture) {
		this(animationTexture, animationTexture.getDefault().frameWidth, animationTexture.getDefault().frameHeight);
	}
	public AnimatedSprite(AnimationTexture animationTexture, float width, float height) {
		super(width, height);
		
		this.animationTexture = animationTexture;
		playingSound.setMinDistance(400);
		playingSound.setVolume(100);
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
			
			if( currentFrame < 0 ) {
				playing = false;
				return;
			}
			
			playingSound.setPosition(getPosition().x, getPosition().y, 0.5f);
			
			sprite.setTextureRect(currentAnimationData.calculateTextureFrame(currentFrame));
		}
	}


	@Override
	public void play(AnimationType animation, float speedFactor,
			boolean repeated) {
		if( currentAnimationType!=animation || !playing ) {
			AnimationData animData = animationTexture.get(animation);
			
			if( animData == null )
				return;
			
			currentAnimationData = animData;
			sprite.setTexture(currentAnimationData.texture);
			sprite.setOrigin(Math.abs(currentAnimationData.frameWidth/2.f), Math.abs(currentAnimationData.frameHeight/2.f));
			currentAnimationType = animation;
			currentFrame = currentAnimationData.frameStart;
			sprite.setTextureRect(currentAnimationData.calculateTextureFrame(currentFrame));
			updateScale();
			
			if( animData.sound!=null ) {
				playingSound.setBuffer(animData.sound);
				playingSound.play();
			}
		}

		playingSound.setPitch(speedFactor);
		playingSound.setLoop(repeated);
		animationSpeed = speedFactor;
		this.repeated = repeated;
		this.reverse = false;
		playing = true;
	}

	@Override
	public void play() {
		play(currentAnimationType, animationSpeed, repeated);
	}
	
	@Override
	public void stop() {
		playingSound.stop();
		playing = false;
		reverse = false;
		currentFrame = currentAnimationData.frameStart;
		sprite.setTextureRect(currentAnimationData.calculateTextureFrame(currentFrame));
	}

	@Override
	public void resume() {
		playing = true;
		playingSound.play();
	}

	@Override
	public AnimationType getCurrentAnimationType() {
		return currentAnimationType;
	}

	@Override
	public void reverse() {
		reverse = true;
		playing=true;
		playingSound.play();
		if( currentFrame<=0 )
			currentFrame = currentAnimationData.frameEnd;
	}

	@Override
	public void pause() {
		playing = false;
		playingSound.pause();
	}

}
