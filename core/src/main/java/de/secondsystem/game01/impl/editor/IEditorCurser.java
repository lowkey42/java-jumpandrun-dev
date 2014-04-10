package de.secondsystem.game01.impl.editor;

import org.jsfml.graphics.RenderTarget;
import org.jsfml.system.Vector2f;

import de.secondsystem.game01.model.Attributes;
import de.secondsystem.game01.model.IDrawable;
import de.secondsystem.game01.model.IInsideCheck;

interface IEditorCurser extends IDrawable, IInsideCheck {
	
	void zoom(float factorOffset);

	void rotate(float degrees);

	void resize(float widthDiff, float heightDiff);

	Attributes getAttributes();

	void setAttributes(Attributes attributes);

	@Override
	boolean inside(Vector2f point);

	@Override
	void draw(RenderTarget renderTarget);

	boolean isDragged();

	void onDragged(Vector2f point);

	void onDragFinished(Vector2f point);
	
	void preDestroy();

}