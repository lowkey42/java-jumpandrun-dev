package de.secondsystem.game01.impl.graphic;

import org.jsfml.graphics.ConstTexture;
import org.jsfml.graphics.RenderTarget;
import org.jsfml.graphics.Sprite;
import org.jsfml.system.Vector2f;

import de.secondsystem.game01.model.IDimensioned;
import de.secondsystem.game01.model.IDrawable;
import de.secondsystem.game01.model.IMoveable;

public class SpriteWrappper implements IDrawable, IMoveable, IDimensioned {
	protected final Sprite  sprite;
	protected final float width;
	protected final float height;
	
	public SpriteWrappper(float width, float height) {
		sprite = new Sprite();	
		
		this.width  = width;
		this.height = height;
	}
	
	public void setTexture(ConstTexture tex) {
		sprite.setTexture(tex);
		sprite.setOrigin(sprite.getTexture().getSize().x/2, sprite.getTexture().getSize().y/2);
		sprite.setScale(width/tex.getSize().x, height/tex.getSize().y);
	}
	
	@Override
	public void draw(RenderTarget renderTarget) {
    	renderTarget.draw(sprite);
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
	public float getRotation() {
		return sprite.getRotation();
	}

	@Override
	public Vector2f getPosition() {
		return sprite.getPosition();
	}
	
	@Override
	public float getHeight() {
		return height;
	}


	@Override
	public float getWidth() {
		return width;
	}
	
	
}
