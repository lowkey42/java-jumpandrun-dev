package de.secondsystem.game01.model;

public interface IAnimated {

	/**
	 * Starts a new animation (the current animation (if any) will be stopped)
	 * @param animation unique name of animation 
	 * @param speedFactor 1.0=normal speed; <1 slower; >1 faster
	 */
	void play( AnimationType animation, float speedFactor, boolean repeated, boolean cancelCurrentAnimation, boolean flipTexture );
	void stop();
	void resume();
	void flip();
	boolean isFlipped();
	AnimationType getCurrentAnimationType();
	
	public enum AnimationType {
		JUMP,
		MOVE_LEFT,
		MOVE_RIGHT,
		IDLE,
		CLIMB_UP,
		CLIMB_DOWN,
		CLIMB_RIGHT,
		CLIMB_LEFT,
		USED
	}
	
}
