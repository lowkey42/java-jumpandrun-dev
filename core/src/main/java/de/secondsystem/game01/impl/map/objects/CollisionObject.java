package de.secondsystem.game01.impl.map.objects;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.RectangleShape;
import org.jsfml.graphics.RenderTarget;
import org.jsfml.system.Vector2f;

import de.secondsystem.game01.impl.map.LayerObject;

public class CollisionObject implements LayerObject {
	
	private RectangleShape shape;
	
	public CollisionObject(float x, float y, float height, float width, float rotation) {
		this.shape = new RectangleShape(new Vector2f(height, width));
		shape.setPosition(x, y);
		shape.setFillColor(new Color(160, 160, 160, 160));
		shape.setOutlineColor(Color.YELLOW);
		shape.setOutlineThickness(2);
		shape.setOrigin(height/2, width/2);
		shape.setRotation(rotation);
	}
	
	@Override
	public void draw(RenderTarget rt) {
		rt.draw(shape);
	}

	@Override
	public boolean inside(Vector2f point) {
		return shape.getGlobalBounds().contains(point);
	}

	@Override
	public void setPosition(Vector2f pos) {
		shape.setPosition(pos);
	}

	@Override
	public void setRotation(float degree) {
		shape.setRotation(degree);
	}

	@Override
	public void setScale(float scale) {
		shape.setScale(scale, scale);
	}

	@Override
	public int getHeight() {
		return (int) shape.getSize().x;
	}

	@Override
	public int getWidth() {
		return (int) shape.getSize().y;
	}

	@Override
	public float getScale() {
		return shape.getScale().x;
	}

	@Override
	public Vector2f getOrigin() {
		return shape.getOrigin();
	}

	@Override
	public float getRotation() {
		return shape.getRotation();
	}

	@Override
	public Vector2f getPosition() {
		return shape.getPosition();
	}

}
