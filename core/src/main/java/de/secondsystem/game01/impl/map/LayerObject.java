package de.secondsystem.game01.impl.map;

import org.jsfml.graphics.RenderTarget;
import org.jsfml.system.Vector2f;

public interface LayerObject {
	
	void draw(RenderTarget rt);
	boolean inside(Vector2f point);
	void setPosition(Vector2f pos);
	void setRotation(float degree);
	void setDimensions(float height, float width);

	int getHeight();
	int getWidth();
	Vector2f getOrigin();
	float getRotation();
	Vector2f getPosition();
	
	LayerObject copy();
}