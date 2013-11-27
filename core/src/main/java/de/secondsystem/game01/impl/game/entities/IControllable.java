package de.secondsystem.game01.impl.game.entities;


public interface IControllable {
	
	public static enum HDirection {
		LEFT, RIGHT;
	}
	public static enum VDirection {
		UP, DOWN;
	}
	
	void moveHorizontally(HDirection direction);
	void moveVertically(VDirection direction);
	
	void jump();
	
	void liftObject();
	void incThrowingPower();
	void switchWorlds();
	
	// TODO: attack
	
	
}
