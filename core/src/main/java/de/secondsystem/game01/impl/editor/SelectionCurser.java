package de.secondsystem.game01.impl.editor;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.RectangleShape;
import org.jsfml.graphics.RenderTarget;
import org.jsfml.system.Vector2f;

import de.secondsystem.game01.impl.map.ILayerObject;
import de.secondsystem.game01.model.Attributes;

class SelectionCurser implements IEditorCurser {
	
	private static final int MIN_SIZE = 5;
	
	protected final IMapProvider mapProvider;
	
	protected float width;
	
	protected float height;
	
	protected float zoom = 1.f;
	
	protected ILayerObject layerObject;
	
	private boolean dragged = false;
	
	private final RectangleShape shapeMarker  = new RectangleShape();
	private final RectangleShape resizeMarkers[] = new RectangleShape[] {
			new RectangleShape(),
			new RectangleShape(),
			new RectangleShape(),
			new RectangleShape()
	};
	
	public SelectionCurser( IMapProvider mapProvider, ILayerObject layerObject ) {
		this.mapProvider = mapProvider;
		this.layerObject = layerObject;
		
		width = layerObject.getWidth();
		height = layerObject.getHeight();
		
		shapeMarker.setFillColor(Color.TRANSPARENT);
		shapeMarker.setOutlineThickness(2);
		shapeMarker.setOutlineColor(Color.BLUE);
		
	}
	
	/* (non-Javadoc)
	 * @see de.secondsystem.game01.impl.editor.EditorCurser#zoom(float)
	 */
	@Override
	public void zoom(float factor) {
		zoom*=factor;
		layerObject.setDimensions(width*zoom, height*zoom);
	}
	
	/* (non-Javadoc)
	 * @see de.secondsystem.game01.impl.editor.EditorCurser#rotate(float)
	 */
	@Override
	public void rotate(float degrees) {
		layerObject.setRotation(layerObject.getRotation()+degrees);
	}
	
	/* (non-Javadoc)
	 * @see de.secondsystem.game01.impl.editor.EditorCurser#resize(float, float)
	 */
	@Override
	public void resize(float widthDiff, float heightDiff) {
		width= Math.max(MIN_SIZE, width+widthDiff);
		height= Math.max(MIN_SIZE, height+heightDiff);
		layerObject.setDimensions(width*zoom, height*zoom);
	}
	
	/* (non-Javadoc)
	 * @see de.secondsystem.game01.impl.editor.EditorCurser#getAttributes()
	 */
	@Override
	public Attributes getAttributes() {
		return layerObject.serialize();
	}
	/* (non-Javadoc)
	 * @see de.secondsystem.game01.impl.editor.EditorCurser#setAttributes(de.secondsystem.game01.model.Attributes)
	 */
	@Override
	public void setAttributes(Attributes attributes) {
		mapProvider.getMap().updateNode(layerObject, attributes);
	}

	/* (non-Javadoc)
	 * @see de.secondsystem.game01.impl.editor.EditorCurser#inside(org.jsfml.system.Vector2f)
	 */
	@Override
	public boolean inside(Vector2f point) {
		return layerObject.inside(point);
	}

	/* (non-Javadoc)
	 * @see de.secondsystem.game01.impl.editor.EditorCurser#draw(org.jsfml.graphics.RenderTarget)
	 */
	@Override
	public void draw(RenderTarget renderTarget) {
		if( layerObject!=null ) {
			// TODO
			shapeMarker.setSize(new Vector2f(layerObject.getWidth(), layerObject.getHeight()));
			shapeMarker.setOrigin(layerObject.getWidth()/2.f, layerObject.getHeight()/2.f);
			shapeMarker.setRotation(layerObject.getRotation());
			shapeMarker.setPosition(layerObject.getPosition());
		}
	}
	
	/* (non-Javadoc)
	 * @see de.secondsystem.game01.impl.editor.EditorCurser#isDragged()
	 */
	@Override
	public boolean isDragged() {
		return dragged;
	}
	
	/* (non-Javadoc)
	 * @see de.secondsystem.game01.impl.editor.EditorCurser#onDragStarted(org.jsfml.system.Vector2f)
	 */
	@Override
	public void onDragged(Vector2f point) {
		dragged = true;
		layerObject.setPosition(point);
	}
	
	/* (non-Javadoc)
	 * @see de.secondsystem.game01.impl.editor.EditorCurser#onDragFinished(org.jsfml.system.Vector2f)
	 */
	@Override
	public void onDragFinished(Vector2f point) {
		dragged = false;
	}

	@Override
	public void preDestroy() {
	}
	
}
