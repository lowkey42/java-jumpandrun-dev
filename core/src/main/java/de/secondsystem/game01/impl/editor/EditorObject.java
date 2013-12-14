package de.secondsystem.game01.impl.editor;

import org.jsfml.graphics.RenderTarget;
import org.jsfml.system.Vector2f;
import org.jsfml.system.Vector2i;
import de.secondsystem.game01.impl.map.ILayerObject;

public class EditorObject {
	protected static final float SCALE_FACTOR = 1.1f;

	protected float x, y;
	
	protected float rotation = 0.f;
	protected float zoom     = 1.f;
	protected float height   = 1.f;
	protected float width    = 1.f;
	
	protected ILayerObject layerObject = null;
	protected Vector2f mappedMousePos;
	
	public float getWidth() {
		return width;
	}
	
	public float getHeight() {
		return height;
	}
	
	public void setWidth(float width) {
		this.width = width;
	}
	
	public void setHeight(float height) {
		this.height = height;
	}
	
	public float getRotation() {
		return rotation;
	}
	
	public void setRotation(float rotation) {
		this.rotation = rotation;
	}
	
	public void rotate(float rotation) {
		this.rotation += rotation;
	}
	
	public void refresh() {
		layerObject.setRotation(rotation);
		layerObject.setDimensions(width * zoom, height * zoom);

	}
	
	public void draw(RenderTarget rt) {
		layerObject.draw(rt);
	}
	
	public void setPosition(Vector2f pos) {
		layerObject.setPosition(pos);
	}
	
	public Vector2f getPosition() {
		return layerObject.getPosition();
	}
	
	public void update(boolean movedObj, RenderTarget rt, int mousePosX, int mousePosY, float zoom) {
		setPosition(rt.mapPixelToCoords(new Vector2i(mousePosX, mousePosY)));
	}

	public void zoom(int mouseWheelOffset, float mouseWheelDelta) {
		if (mouseWheelOffset == 1)
			zoom *= mouseWheelDelta * SCALE_FACTOR;
		else
			zoom /= mouseWheelDelta * SCALE_FACTOR * -1;
	}
	
	public void zoom(float factor) {
		this.zoom *= factor;
	}
	
	public boolean isPointInside(Vector2f v) {
		return layerObject != null && layerObject.inside(v);
	}
}
