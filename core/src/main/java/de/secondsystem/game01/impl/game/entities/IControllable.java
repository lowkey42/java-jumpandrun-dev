package de.secondsystem.game01.impl.game.entities;

public interface IControllable {
	
	/**
	 * @param direction false=left, true=right
	 */
	void move(boolean direction);
	void stopMove();
	
	void look(float degree);
	
	void jump();
	void stopJump();
	
	// TODO: attack?
	
}
