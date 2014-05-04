package de.secondsystem.game01.model;

public interface IAnimated extends IPlayable {

	/**
	 * Starts a new animation (the current animation (if any) will be stopped)
	 * @param animation unique name of animation 
	 * @param speedFactor 1.0=normal speed; <1 slower; >1 faster
	 * @param repeated the animation is looped
	 */
	void play( AnimationType animation, float speedFactor, boolean repeated);
	
	AnimationType getCurrentAnimationType();
	
	public enum AnimationType {
		JUMP,
		MOVE,
		IDLE,
		CLIMB_UP,
		CLIMB_DOWN,
		CLIMB_SIDEWAYS,
		USED,
		UNUSED,
		OPEN,
		CLOSED,
		
		// GUI animations
		MOUSE_OVER,
		CLICKED
	}
	
}
