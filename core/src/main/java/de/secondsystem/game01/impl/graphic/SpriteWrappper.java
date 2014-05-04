package de.secondsystem.game01.impl.graphic;

import org.jsfml.graphics.IntRect;
import org.jsfml.graphics.RenderTarget;
import org.jsfml.graphics.Sprite;
import org.jsfml.system.Vector2f;
import org.jsfml.system.Vector2i;

import de.secondsystem.game01.impl.map.IGameMap.WorldId;

public class SpriteWrappper implements ISpriteWrapper {
	protected final Sprite sprite;
	protected float width;
	protected float height;
	protected boolean visible = true;
	private boolean flippedH, flippedV;
	private SpriteTexture texture;

	public SpriteWrappper(SpriteTexture texture) {
		this(texture, new IntRect(Vector2i.ZERO,texture.texture.getSize()));
	}
	public SpriteWrappper(SpriteTexture texture, IntRect clip) {
		this(texture.texture.getSize().x, texture.texture.getSize().y);
		setTexture(texture, clip);
	}
	public SpriteWrappper(float width, float height) {
		sprite = new Sprite();	
		
		this.width  = width;
		this.height = height;
	}

	public void setTexture(SpriteTexture texture) {
		setTexture(texture, null);
	}
	public void setTexture(SpriteTexture texture, IntRect clip) {
		if( this.texture!=texture ) {
			this.texture = texture;
			sprite.setTexture(texture.texture, true);
		}
		
		sprite.setTextureRect(clip!=null ? clip : new IntRect(Vector2i.ZERO,texture.texture.getSize()));

		updateScale();
	}

	@Override
	public void draw(RenderTarget renderTarget) {
		draw(renderTarget, WorldId.MAIN);
	}
			
	@Override
	public void draw(RenderTarget renderTarget, WorldId worldId) {
		if( visible ) {
			switch( worldId ) {
				case MAIN:
					if( sprite.getTexture()!=texture.texture )
						sprite.setTexture(texture.texture, false);
					break;
					
				case OTHER:
					if( sprite.getTexture()!=texture.altTexture )
						sprite.setTexture(texture.altTexture, false);
					break;
			}
			
			if( renderTarget instanceof LightMap ) {
				((LightMap)renderTarget).draw(sprite, worldId==WorldId.MAIN ? texture.normals : texture.altNormals);
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
		if( sprite.getTexture().isRepeated() ) {
			sprite.setTextureRect(new IntRect(0, 0, (int)width, (int)height));
			sprite.setOrigin(width/2, height/2);
			
		} else {
			sprite.setOrigin(sprite.getTextureRect().width/2, sprite.getTextureRect().height/2);
			sprite.setScale(width/sprite.getTextureRect().width * (isFlippedHoriz()?-1:1), 
					height/sprite.getTextureRect().height * (isFlippedVert()?-1:1) );
		}
	}

	@Override
	public void flipHoriz() {
		flippedH = !flippedH;
		updateScale();
	}
	@Override
	public boolean isFlippedHoriz() {
		return flippedH;
	}
	@Override
	public void setFlipHoriz(boolean flip) {
		if( this.flippedH == flip )
			return;
		
		this.flippedH = flip;
		updateScale();
	}

	@Override
	public void flipVert() {
		flippedV = !flippedV;
		updateScale();
	}
	@Override
	public boolean isFlippedVert() {
		return flippedV;
	}
	@Override
	public void setFlipVert(boolean flip) {
		if( this.flippedV == flip )
			return;
		
		this.flippedV = flip;
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
	public void setTextureRect(IntRect rect) {
		sprite.setTextureRect(rect);
	}
	
	@Override
	public int getClipState() {
		return sprite.getTextureRect().hashCode();
	}
}
