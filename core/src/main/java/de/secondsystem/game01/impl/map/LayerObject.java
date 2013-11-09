package de.secondsystem.game01.impl.map;

import org.jsfml.graphics.RenderTarget;
import org.jsfml.system.Vector2f;

public interface LayerObject {
	void draw(RenderTarget rt);
	boolean inside(Vector2f point);
	void setPosition(Vector2f pos);
	void setRotation(float degree);
	void setScale(float scale);

	int getHeight();
	int getWidth();
	float getScale();
	Vector2f getOrigin();
	float getRotation();
	Vector2f getPosition();
}