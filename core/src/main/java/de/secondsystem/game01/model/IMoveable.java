package de.secondsystem.game01.model;

import org.jsfml.system.Vector2f;

public interface IMoveable {
	void setPosition(Vector2f pos);
	void setRotation(float degree);
	
	/** Get the rotation of the body
	 * @return The normalized (0-359) rotation in degrees
	 */
	float getRotation();
	Vector2f getPosition();
}
