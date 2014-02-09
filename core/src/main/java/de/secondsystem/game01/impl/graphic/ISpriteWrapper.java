package de.secondsystem.game01.impl.graphic;

import de.secondsystem.game01.model.IDimensioned;
import de.secondsystem.game01.model.IDrawable;
import de.secondsystem.game01.model.IInsideCheck;
import de.secondsystem.game01.model.IMoveable;

public interface ISpriteWrapper extends IDrawable, IMoveable, IDimensioned, IInsideCheck {
	void setWidth(float width);
	void setHeight(float height);
	void setDimensions(float width, float height);
	void setVisibility(boolean visible);
}
