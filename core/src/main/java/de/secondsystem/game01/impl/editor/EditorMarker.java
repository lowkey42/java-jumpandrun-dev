package de.secondsystem.game01.impl.editor;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.RectangleShape;
import org.jsfml.graphics.RenderTarget;
import org.jsfml.system.Vector2f;

import de.secondsystem.game01.impl.map.ILayerObject;
import de.secondsystem.game01.model.IDimensioned;
import de.secondsystem.game01.model.IDrawable;
import de.secondsystem.game01.util.Tools;

public class EditorMarker implements IDrawable, IDimensioned {
	private RectangleShape shape  = new RectangleShape();
	
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
	
	public void update(ILayerObject layerObject) {
		Vector2f newSize = new Vector2f(layerObject.getWidth(), layerObject.getHeight());
		Tools.setRectangleShape(shape, newSize, new Vector2f(newSize.x / 2f, newSize.y / 2f), layerObject.getPosition(), layerObject.getRotation());
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
}



