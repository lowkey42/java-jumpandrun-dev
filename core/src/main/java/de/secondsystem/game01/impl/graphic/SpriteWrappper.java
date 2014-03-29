package de.secondsystem.game01.impl.graphic;

import org.jsfml.graphics.ConstTexture;
import org.jsfml.graphics.IntRect;
import org.jsfml.graphics.RenderTarget;
import org.jsfml.graphics.Sprite;
import org.jsfml.graphics.Texture;
import org.jsfml.system.Vector2f;

public class SpriteWrappper implements ISpriteWrapper {
	protected final Sprite sprite;
	protected float width;
	protected float height;
	protected boolean visible = true;
	private boolean flipped;
	
	protected ConstTexture normalMap;

	public SpriteWrappper(ConstTexture tex, ConstTexture normalMap, IntRect clip) {
		this(tex, clip);
		this.normalMap = normalMap;
	}
	public SpriteWrappper(ConstTexture tex) {
		this(tex.getSize().x, tex.getSize().y);
		setTexture(tex);
	}
	public SpriteWrappper(ConstTexture tex, IntRect clip) {
		this(tex.getSize().x, tex.getSize().y);
		setTexture(tex, clip);
	}
	public SpriteWrappper(float width, float height) {
		sprite = new Sprite();	
		
		this.width  = width;
		this.height = height;
	}

	public void setTexture(ConstTexture tex, ConstTexture normalMap, IntRect clip) {
		setTexture(tex, clip);
		this.normalMap = normalMap;
	}
	public void setTexture(ConstTexture tex) {
		setTexture(tex, null);
	}
	public void setTexture(ConstTexture tex, IntRect clip) {
		sprite.setTexture(tex, true);
		float texWidth=tex.getSize().x, texHeight=tex.getSize().y;
		if( clip!=null ) {
			sprite.setTextureRect(clip);
			texWidth = clip.width;
			texHeight = clip.height;
		}
		
		sprite.setOrigin(texWidth/2, texHeight/2);
		sprite.setScale(width/texWidth, height/texHeight);
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
		this.width  = width;
		this.height = height;
		updateScale();
	}
	protected void updateScale() {
		sprite.setScale(width/sprite.getTexture().getSize().x * (isFlipped()?-1:1), height/sprite.getTexture().getSize().y);
	}

	@Override
	public void flip() {
		flipped = !flipped;
		updateScale();
	}

	@Override
	public boolean isFlipped() {
		return flipped;
	}
	
	@Override
	public void setFlip(boolean flip) {
		if( this.flipped == flip )
			return;
		
		this.flipped = flip;
		updateScale();
	}
	
	@Override
	public boolean inside(Vector2f point) {
		return sprite.getGlobalBounds().contains(point); // checks the AABB not the OBB
	}

	@Override
	public void setVisibility(boolean visible) {
		this.visible = visible;
	}
	
	@Override
	public void setRepeatedTexture(boolean repeated) {
		if( sprite.getTexture().isRepeated() != repeated ) {
			Texture texture = new Texture(sprite.getTexture());
			texture.setRepeated(repeated);
			setTexture(texture);
		}
	}
	
	@Override
	public boolean isTextureRepeated() {
		return sprite.getTexture().isRepeated();
	}
	
	@Override
	public void setTextureRect(IntRect rect) {
		sprite.setTextureRect(rect);
	}
}
