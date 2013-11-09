package de.secondsystem.game01.impl.map.objects;

import org.jsfml.graphics.RenderTarget;
import org.jsfml.graphics.Sprite;
import org.jsfml.system.Vector2f;

import de.secondsystem.game01.impl.map.LayerObject;
import de.secondsystem.game01.impl.map.Tileset;

public class SpriteLayerObject implements LayerObject {

	private Sprite sprite;
	
	public SpriteLayerObject(Sprite sprite) {
		this.sprite = sprite;
	}
	public SpriteLayerObject(Tileset tileset, int tileId, float x, float y, float rotation, float scale) {
		sprite = new Sprite();
		sprite.setTexture(tileset.tiles.get(tileId));
		sprite.setOrigin(sprite.getTexture().getSize().x/2, sprite.getTexture().getSize().y/2);
		sprite.setPosition(x, y);
		sprite.setRotation(rotation);
		sprite.setScale(scale, scale);
	}
	
	@Override
	public void draw(RenderTarget rt) {
		rt.draw(sprite);
	}

	@Override
	public boolean inside(Vector2f point) {
		return sprite.getGlobalBounds().contains(point);
	}

	@Override
	public void setPosition(Vector2f pos) {
		sprite.setPosition(pos);
	}

	@Override
	public void setRotation(float degree) {
		sprite.setRotation(degree);
	}

	@Override
	public void setScale(float scale) {
		sprite.setScale(scale, scale);
	}

	@Override
	public int getHeight() {
		return sprite.getTexture().getSize().y;
	}

	@Override
	public int getWidth() {
		return sprite.getTexture().getSize().y;
	}

	@Override
	public float getScale() {
		return sprite.getScale().x;
	}

	@Override
	public Vector2f getOrigin() {
		return sprite.getOrigin();
	}

	@Override
	public float getRotation() {
		return sprite.getRotation();
	}

	@Override
	public Vector2f getPosition() {
		return sprite.getPosition();
	}

}
