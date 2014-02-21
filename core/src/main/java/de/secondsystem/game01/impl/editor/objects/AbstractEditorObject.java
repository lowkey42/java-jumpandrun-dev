package de.secondsystem.game01.impl.editor.objects;

import org.jsfml.system.Vector2f;

public abstract class AbstractEditorObject implements IEditorObject {
	protected static final float SCALE_FACTOR = 1.1f;

	protected Vector2f pos;
	
	protected float rotation = 0.f;
	protected float zoom     = 1.f;
	protected float height   = 1.f;
	protected float width    = 1.f;
	
	@Override
	public float getWidth() {
		return width;
	}
	
	@Override
	public float getHeight() {
		return height;
	}
	
	@Override
	public void setWidth(float width) {
		this.width = width;
	}
	
	@Override
	public void setHeight(float height) {
		this.height = height;
	}
	
	@Override
	public float getRotation() {
		return rotation;
	}
	
	@Override
	public void setRotation(float rotation) {
		this.rotation = rotation;
	}
	
	@Override
	public void rotate(float rotation) {
		this.rotation += rotation;
	}
	
	@Override
	public void setPosition(Vector2f pos) {
		this.pos = pos;
	}
	
	@Override
	public Vector2f getPosition() {
		return this.pos;
	}
	
	@Override
	public void zoom(float factor) {
		this.zoom *= factor;
	}
	
	@Override
	public void zoom(int mouseWheelOffset, float mouseWheelDelta) {
		if (mouseWheelOffset == 1)
			zoom *= mouseWheelDelta * SCALE_FACTOR;
		else
			zoom /= mouseWheelDelta * SCALE_FACTOR * -1;
	}
}
