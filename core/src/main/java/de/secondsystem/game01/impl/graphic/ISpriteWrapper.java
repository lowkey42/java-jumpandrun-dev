package de.secondsystem.game01.impl.graphic;

import org.jsfml.system.Vector2f;

import de.secondsystem.game01.model.IDimensioned;
import de.secondsystem.game01.model.IDrawable;
import de.secondsystem.game01.model.IMoveable;

public interface ISpriteWrapper extends IDrawable, IMoveable, IDimensioned {
	void setWidth(float width);
	void setHeight(float height);
	void setDimensions(float width, float height);
	void setVisibility(boolean visible);
	boolean inside(Vector2f point);
}
