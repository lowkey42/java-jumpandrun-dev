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
	
	void onGameWorldSwitch(int gameWorldId);
	
	void setDimensions(float width, float height);

	float getHeight();
	float getWidth();
	Vector2f getOrigin();
	float getRotation();
	Vector2f getPosition();
	
	LayerObjectType typeUuid();
	Map<String, Object> getAttributes();
}