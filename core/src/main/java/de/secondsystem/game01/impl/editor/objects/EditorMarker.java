package de.secondsystem.game01.impl.editor.objects;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.RectangleShape;
import org.jsfml.graphics.RenderTarget;
import org.jsfml.system.Vector2f;

import de.secondsystem.game01.model.IDimensioned;
import de.secondsystem.game01.model.IDrawable;
import de.secondsystem.game01.util.Tools;

public class EditorMarker implements IDrawable, IDimensioned {
	private RectangleShape shape  = new RectangleShape();
	private Vector2f relativePos = new Vector2f(0.f, 0.f);

	
	public EditorMarker(Color outlineColor, float outlineThickness, Color fillColor) {
		shape.setOutlineColor(outlineColor);
		shape.setOutlineThickness(outlineThickness);
		shape.setFillColor(fillColor);
	}
	
	public boolean isMouseOver(Vector2f mappedMousePos) {
		return Tools.isInside(shape, mappedMousePos);
	}

	@Override
	public void draw(RenderTarget renderTarget) {
		renderTarget.draw(shape);
	}
	
	public void update(IEditorObject editorObject) {
		Vector2f newSize = new Vector2f(editorObject.getWidth(), editorObject.getHeight());
		Tools.setRectangleShape(shape, newSize, new Vector2f(newSize.x / 2f - relativePos.x, newSize.y / 2f - relativePos.y), 
				editorObject.getPosition(), editorObject.getRotation());
	}
	
	public void setWidth(float width) {
		shape.setSize(new Vector2f(width, shape.getSize().y));
	}
	
	public void setHeight(float height) {
		shape.setSize(new Vector2f(shape.getSize().x, height));		
	}
	
	public void setSize(Vector2f size) {
		shape.setSize(size);
	}
	
	public void setRelativePos(Vector2f pos) {
		relativePos = pos;
	}
	
	public void setRelativePos(float x, float y) {
		relativePos = new Vector2f(x, y);
	}
	
	@Override
	public float getWidth() {
		return shape.getSize().x;
	}

	@Override
	public float getHeight() {
		return shape.getSize().y;
	}
	
	public boolean isInside(Vector2f p) {
		return Tools.isInside(shape, p);
	}
	
	public Vector2f getVertex(int i) {
		return shape.getTransform().transformPoint(shape.getPoint(i));
	}
	
	public RectangleShape getShape() {
		return shape;
	}
}



