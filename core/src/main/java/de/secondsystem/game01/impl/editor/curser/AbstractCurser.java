package de.secondsystem.game01.impl.editor.curser;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.RectangleShape;
import org.jsfml.graphics.RenderTarget;
import org.jsfml.system.Vector2f;

import de.secondsystem.game01.impl.map.ILayerObject;
import de.secondsystem.game01.impl.map.IMapProvider;
import de.secondsystem.game01.model.Attributes;

abstract class AbstractCurser implements IEditorCurser {

	private static final int MIN_SIZE = 5;
	protected final IMapProvider mapProvider;
	
	private Float width;
	private Float height;
	protected float zoom = 1.f;
	protected boolean dragged = false;
	protected Vector2f dragOffset = Vector2f.ZERO;
	
	protected final RectangleShape shapeMarker = new RectangleShape();

	public AbstractCurser(IMapProvider mapProvider, Color outlineColor) {
		this.mapProvider = mapProvider;
		
		shapeMarker.setFillColor(Color.TRANSPARENT);
		shapeMarker.setOutlineThickness(2);
		shapeMarker.setOutlineColor(outlineColor);
	}

	protected abstract ILayerObject getLayerObject();
	protected float getWidth() {
		if( width==null )
			width = getLayerObject().getWidth();
		
		return width;
	}
	protected float getHeight() {
		if( height==null )
			height = getLayerObject().getHeight();
		
		return height;
	}
	
	@Override
	public void zoom(float factor) {
		zoom*=factor;
		getLayerObject().setDimensions(getWidth()*zoom, getHeight()*zoom);
	}

	@Override
	public void rotate(float degrees) {
		getLayerObject().setRotation(getLayerObject().getRotation()+degrees);
	}

	@Override
	public void resize(float widthDiff, float heightDiff) {
		width= Math.max(MIN_SIZE, getWidth()+widthDiff);
		height= Math.max(MIN_SIZE, getHeight()+heightDiff);
		getLayerObject().setDimensions(width*zoom, height*zoom);
	}

	@Override
	public Attributes getAttributes() {
		return getLayerObject().serialize();
	}

	@Override
	public abstract void setAttributes(Attributes attributes);

	@Override
	public boolean inside(Vector2f point) {
		return getLayerObject().inside(point);
	}

	@Override
	public void draw(RenderTarget renderTarget) {
		if( getLayerObject()!=null ) {
			shapeMarker.setSize(new Vector2f(getLayerObject().getWidth(), getLayerObject().getHeight()));
			shapeMarker.setOrigin(getLayerObject().getWidth()/2.f, getLayerObject().getHeight()/2.f);
			shapeMarker.setRotation(getLayerObject().getRotation());
			shapeMarker.setPosition(getLayerObject().getPosition());
			
			renderTarget.draw(shapeMarker);
		}
	}

	@Override
	public boolean isDragged() {
		return dragged;
	}

	@Override
	public void onDragged(Vector2f point) {
		dragged = true;
		dragOffset = Vector2f.sub(getLayerObject().getPosition(), point);
	}
	
	@Override
	public void onMouseMoved(Vector2f point) {
		if( dragged )
			getLayerObject().setPosition(Vector2f.add(point, dragOffset));
	}

	@Override
	public void onDragFinished(Vector2f point) {
		dragged = false;
	}

	@Override
	public void onDestroy() {
	}

}