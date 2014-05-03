package de.secondsystem.game01.impl.graphic;

import org.jsfml.graphics.IntRect;

import de.secondsystem.game01.impl.map.IWorldDrawable;
import de.secondsystem.game01.model.IDimensioned;
import de.secondsystem.game01.model.IDrawable;
import de.secondsystem.game01.model.IInsideCheck;
import de.secondsystem.game01.model.IMoveable;
import de.secondsystem.game01.model.IScalable;

public interface ISpriteWrapper extends IWorldDrawable, IDrawable, IMoveable, IDimensioned, IInsideCheck, IScalable {
	void setWidth(float width);
	void setHeight(float height);
	void setVisibility(boolean visible);
	void flip();
	void setFlip(boolean flip);
	boolean isFlipped();
	
	void setTextureRect(IntRect rect);
	
	/**
	 * returns a different integer for different clipping-values (e.g. other animation-frame)
	 * @return
	 */
	int getClipState();
}
