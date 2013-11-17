package de.secondsystem.game01.model;

public interface IAnimated {

	/**
	 * Starts a new animation (the current animation (if any) will be stopped)
	 * @param animation unique name of animation 
	 * @param speedFactor 1.0=normal speed; <1 slower; >1 faster
	 */
	void play( String animation, float speedFactor );
	
}
