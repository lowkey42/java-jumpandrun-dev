package de.secondsystem.game01.impl.game.entities;


public interface IControllable {
	
	public static enum HDirection {
		LEFT, RIGHT;
	}
	public static enum VDirection {
		UP, DOWN, FORWARD;
	}
	
	void moveHorizontally(HDirection direction, float factor);
	void moveVertically(VDirection direction, float factor);
	
	void jump();
	
	boolean liftOrThrowObject(float force);
	void switchWorlds();
	void use();
	
	void attack(float force);
	
}
