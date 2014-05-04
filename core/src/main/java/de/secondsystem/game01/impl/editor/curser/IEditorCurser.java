package de.secondsystem.game01.impl.editor.curser;

import org.jsfml.system.Vector2f;

import de.secondsystem.game01.model.Attributes;
import de.secondsystem.game01.model.IDrawable;
import de.secondsystem.game01.model.IInsideCheck;

public interface IEditorCurser extends IDrawable, IInsideCheck {
	
	void zoom(float factorOffset);
	void rotate(float degrees);
	void resize(float widthDiff, float heightDiff);
	void flipHoriz();
	void flipVert();

	Attributes getAttributes();
	void setAttributes(Attributes attributes);

	boolean isDragged();
	void onDragged(Vector2f point);
	void onDragFinished(Vector2f point);
	
	void onMouseMoved(Vector2f point);
	
	void onDestroy();
	
	int getCurrentBrushIndex();
	int getBrushCount();

}