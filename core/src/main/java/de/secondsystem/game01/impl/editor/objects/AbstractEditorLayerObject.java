package de.secondsystem.game01.impl.editor.objects;

import org.jsfml.graphics.IntRect;
import org.jsfml.graphics.RenderTarget;
import org.jsfml.system.Vector2f;

import de.secondsystem.game01.impl.map.ILayerObject;
import de.secondsystem.game01.impl.map.objects.SpriteLayerObject;

public abstract class AbstractEditorLayerObject implements IEditorLayerObject {	
	protected ILayerObject layerObject = null;
	protected static final float SCALE_FACTOR = 1.1f;

	protected Vector2f pos;
	
	protected float rotation = 0.f;
	protected float zoom     = 1.f;
	protected float height   = 1.f;
	protected float width    = 1.f;
	
	protected boolean repeated;
	protected boolean repeatTexture;
	protected float textureRectWidth;
	protected float textureRectHeight;
	
	// TODO: solve mystery: find out total width/height of a sprite
	
	@Override
	public float getWidth() {	
		return layerObject.getWidth();
	}
	
	@Override
	public float getHeight() {
		return layerObject.getHeight();
	}
	
	@Override
	public void setWidth(float width) {
		if( repeatTexture )
			textureRectWidth = width;
		else
			this.width = width;
	}
	
	@Override
	public void setHeight(float height) {
		if( repeatTexture )
			textureRectHeight = height;
		else
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
		// TODO: fix marker pos
		if (mouseWheelOffset == 1)
			zoom *= mouseWheelDelta * SCALE_FACTOR;
		else
			zoom /= mouseWheelDelta * SCALE_FACTOR * -1;
	}
	
	@Override
	public void refresh() {
		layerObject.setRotation(rotation);
		if( repeatTexture ) {
			((SpriteLayerObject) layerObject).setTextureRect( new IntRect(0, 0, (int) (textureRectWidth), (int) (textureRectHeight)) );
		}
		else
			layerObject.setDimensions(width * zoom, height * zoom);
	}
	
	@Override
	public void draw(RenderTarget rt) {
		layerObject.draw(rt);
	}
	
	@Override
	public void update(boolean movedObj, RenderTarget rt, int mousePosX, int mousePosY, float zoom, long frameTimeMs) {	
		if( pos != null )
			layerObject.setPosition(pos);
	}

	@Override
	public boolean inside(Vector2f point) {
		return layerObject != null && layerObject.inside(point);
	}

	@Override
	public void deselect() {
		// not yet used
	}
	
	@Override
	public void setRepeatedTexture(boolean repeated) {
		if( layerObject instanceof SpriteLayerObject ) {
			((SpriteLayerObject) layerObject).setRepeatedTexture(repeated);
			this.repeated = repeated;
		}
	}

	@Override
	public void repeatTexture(boolean repeat) {
		repeatTexture = repeat;
	} 
}
