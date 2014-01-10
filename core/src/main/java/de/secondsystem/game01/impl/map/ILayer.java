package de.secondsystem.game01.impl.map;

import org.jsfml.graphics.RenderTarget;
import org.jsfml.system.Vector2f;

import de.secondsystem.game01.model.Attributes;

public interface ILayer { //TODO: extends ISerializable

	void draw(RenderTarget rt);

	void addNode(ILayerObject obj);

	ILayerObject findNode(Vector2f point);

	void remove(ILayerObject s);

	void update(long frameTimeMs);

	boolean isVisible();

	boolean setVisible(boolean visible);
	
	Attributes serialize();

}