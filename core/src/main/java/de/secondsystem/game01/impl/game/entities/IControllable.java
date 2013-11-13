package de.secondsystem.game01.impl.game.entities;

public interface IControllable {
	
	public static enum Direction {
		UP, DOWN, LEFT, RIGHT, CENTER;
	}
	
	void move(Direction direction);
	
	void jump();
	void stopJump();
	
	// TODO: attack?
	
	
}
