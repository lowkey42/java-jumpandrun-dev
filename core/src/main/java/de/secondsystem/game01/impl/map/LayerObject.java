package de.secondsystem.game01.impl.map;

import java.util.Map;

import org.jsfml.graphics.RenderTarget;
import org.jsfml.system.Vector2f;

import de.secondsystem.game01.impl.map.objects.LayerObjectType;

public interface LayerObject {
	
	void draw(RenderTarget rt);
	boolean inside(Vector2f point);
	void setPosition(Vector2f pos);
	void setRotation(float degree);
	
	// ADDED // TODO: REMOVE COMMENT
	void onGameWorldSwitch(int gameWorldId);
	
	// altered ! reason: wtf... (APPROPRIATE ADJUSTMENTS IN THE DERIVED CLASSES) // TODO: REMOVE COMMENT
	void setDimensions(float width, float height);

	// altered ! reason: int -> loss of precision (APPROPRIATE ADJUSTMENTS IN THE DERIVED CLASSES) // TODO: REMOVE COMMENT
	float getHeight();
	float getWidth();
	Vector2f getOrigin();
	float getRotation();
	Vector2f getPosition();
	
	LayerObject copy();
	
	LayerObjectType typeUuid();
	Map<String, Object> getAttributes();
}