package de.secondsystem.game01.model;

import org.jsfml.system.Vector2f;

public interface IMoveable {
	void setPosition(Vector2f pos);
	void setRotation(float degree);
	float getRotation();
	Vector2f getPosition();
}
