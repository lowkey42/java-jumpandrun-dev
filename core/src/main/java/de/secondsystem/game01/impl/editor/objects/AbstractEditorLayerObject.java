package de.secondsystem.game01.impl.editor.objects;

import org.jsfml.graphics.IntRect;
import org.jsfml.graphics.RenderTarget;
import org.jsfml.system.Vector2f;

import de.secondsystem.game01.impl.map.IGameMap;
import de.secondsystem.game01.impl.map.ILayerObject;
import de.secondsystem.game01.impl.map.objects.SpriteLayerObject;
import de.secondsystem.game01.model.Attributes;

public abstract class AbstractEditorLayerObject implements IEditorLayerObject {	
	protected ILayerObject layerObject = null;
	protected static final float SCALE_FACTOR = 1.1f;

	protected Vector2f pos;
	
	protected float rotation = 0.f;
	protected float height   = 1.f;
	protected float width    = 1.f;
	
	protected boolean repeated;
	protected boolean repeatTexture;
	protected float textureRectWidth;
	protected float textureRectHeight;
	protected Attributes attributes = new Attributes();
	protected IGameMap map;
	
	// TODO: solve mystery: find out total width/height of a sprite
	
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
		width  *= factor;
		height *= factor;
	}
	
	@Override
	public void zoom(int mouseWheelOffset, float mouseWheelDelta) {
		if (mouseWheelOffset == 1) {
			width  *= mouseWheelDelta * SCALE_FACTOR;
			height *= mouseWheelDelta * SCALE_FACTOR;
		}
		else {
			width /= mouseWheelDelta * SCALE_FACTOR * -1;
			height /= mouseWheelDelta * SCALE_FACTOR * -1;
		}
	}
	
	@Override
	public void refresh() {
		layerObject.setRotation(rotation);
		if( repeatTexture ) {
			((SpriteLayerObject) layerObject).setTextureRect( new IntRect(0, 0, (int) (textureRectWidth), (int) (textureRectHeight)) );
		}
		else
			layerObject.setDimensions(width, height);
	}
	
	@Override
	public void draw(RenderTarget rt) {
	//	layerObject.draw(rt);
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
		//	((SpriteLayerObject) layerObject).setRepeatedTexture(repeated);
			this.repeated = repeated;
		}
	}

	@Override
	public void repeatTexture(boolean repeat) {
		if( layerObject instanceof SpriteLayerObject )
			repeatTexture = repeat;
	} 
	

	@Override
	public Attributes getAttributes() {
		updateAttributes();
		return attributes;
	}

	@Override
	public void applyAttributes(Attributes newAttributes) {
		attributes.clear();
		attributes.putAll(newAttributes);
		
		width  = attributes.getFloat("width");
		height = attributes.getFloat("height");
		pos = new Vector2f(attributes.getFloat("x"), attributes.getFloat("y"));
		rotation = attributes.getFloat("rotation");
	}
	
	@Override
	public void updateAttributes() {
		attributes.put("width", width);
		attributes.put("height", height);
		attributes.put("x", pos.x);
		attributes.put("y", pos.y);
		attributes.put("rotation", rotation);
		attributes.put("worldId", map.getActiveWorldId());
	}
}
