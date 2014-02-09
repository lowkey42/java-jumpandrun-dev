package de.secondsystem.game01.impl.editor;

import org.jsfml.graphics.RenderTarget;
import org.jsfml.system.Vector2f;

import de.secondsystem.game01.model.IDimensioned;
import de.secondsystem.game01.model.IDrawable;
import de.secondsystem.game01.model.IMoveable;

public interface IEditorObject extends IDrawable, IDimensioned, IMoveable {
	void setWidth(float width);
	void setHeight(float height);
	void rotate(float rotation);
	void zoom(float factor);
	void refresh();
	void zoom(int mouseWheelOffset, float mouseWheelDelta);
	void update(boolean movedObj, RenderTarget rt, int mousePosX, int mousePosY, float zoom, long frameTimeMs);
}
