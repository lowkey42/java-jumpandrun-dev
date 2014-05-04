package de.secondsystem.game01.impl.game.entities;

import de.secondsystem.game01.model.HDirection;
import de.secondsystem.game01.model.VDirection;


public interface IControllable {
	
	void moveHorizontally(HDirection direction, float factor);
	void moveVertically(VDirection direction, float factor);
	
	void jump();
	
	boolean liftOrThrowObject(float force);
	void switchWorlds();
	void use();
	
	void attack(float force);
	
}
