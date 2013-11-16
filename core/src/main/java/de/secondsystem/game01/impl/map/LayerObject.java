package de.secondsystem.game01.impl.map;

import java.util.Map;

import org.jsfml.system.Vector2f;

import de.secondsystem.game01.impl.map.objects.LayerObjectType;
import de.secondsystem.game01.model.IDrawable;

public interface LayerObject extends IDrawable {
	
	boolean inside(Vector2f point);
	void setPosition(Vector2f pos);
	void setRotation(float degree);
		
	void setDimensions(float width, float height);

	float getHeight();
	float getWidth();
	Vector2f getOrigin();
	float getRotation();
	Vector2f getPosition();
	
	LayerObjectType typeUuid();
	Map<String, Object> getAttributes();
}