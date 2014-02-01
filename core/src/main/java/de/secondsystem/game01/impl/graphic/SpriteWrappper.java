package de.secondsystem.game01.impl.graphic;

import org.jsfml.graphics.ConstTexture;
import org.jsfml.graphics.RenderTarget;
import org.jsfml.graphics.Sprite;
import org.jsfml.system.Vector2f;

import de.secondsystem.game01.util.Tools;

public class SpriteWrappper implements ISpriteWrapper {
	protected final Sprite sprite;
	protected float width;
	protected float height;
	protected boolean visible = true;
	
	protected ConstTexture normalMap;

	public SpriteWrappper(ConstTexture tex, ConstTexture normalMap) {
		this(tex);
		this.normalMap = normalMap;
	}
	public SpriteWrappper(ConstTexture tex) {
		this(tex.getSize().x, tex.getSize().y);
		setTexture(tex);
	}
	public SpriteWrappper(float width, float height) {
		sprite = new Sprite();	
		
		this.width  = width;
		this.height = height;
	}

	public void setTexture(ConstTexture tex, ConstTexture normalMap) {
		setTexture(tex);
		this.normalMap = normalMap;
	}
	public void setTexture(ConstTexture tex) {
		sprite.setTexture(tex, true);
		sprite.setOrigin(sprite.getTexture().getSize().x/2, sprite.getTexture().getSize().y/2);
		sprite.setScale(width/tex.getSize().x, height/tex.getSize().y);
	}
	
	@Override
	public void draw(RenderTarget renderTarget) {
		if( visible ) {
			if( renderTarget instanceof LightMap ) {
				((LightMap)renderTarget).draw(sprite, normalMap);
			} else {
				renderTarget.draw(sprite);
			}
		}
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

	@Override
	public void setWidth(float width) {
		this.width = width;
	}

	@Override
	public void setHeight(float height) {
		this.height = height;
	}

	@Override
	public void setDimensions(float width, float height) {
		sprite.setScale(width/sprite.getTexture().getSize().x, height/sprite.getTexture().getSize().y);
		this.width = width;
		this.height = height;
	}
	
	@Override
	public boolean inside(Vector2f point) {
		return Tools.isInside(this, point);
	}

	@Override
	public void setVisibility(boolean visible) {
		this.visible = visible;
	}
}
