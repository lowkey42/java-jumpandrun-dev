package de.secondsystem.game01.impl.editor;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.RectangleShape;
import org.jsfml.graphics.RenderTarget;
import org.jsfml.system.Vector2f;
import org.jsfml.system.Vector2i;

import de.secondsystem.game01.impl.map.GameMap;
import de.secondsystem.game01.impl.map.ILayerObject;
import de.secondsystem.game01.impl.map.LayerType;

public class SelectedEditorObject extends EditorObject {
	private RectangleShape marker = new RectangleShape(new Vector2f(1.f,1.f));
	private RectangleShape scaleWidthMarker  = new RectangleShape(new Vector2f(1.f,1.f));
	private RectangleShape scaleHeightMarker = new RectangleShape(new Vector2f(1.f,1.f));
	private boolean scaleWidth  = false;
	private boolean scaleHeight = false;
	protected Vector2f lastMousePos;
	
	public SelectedEditorObject(Color outlineColor, float outlineThickness, Color fillColor) {
		setupMarker(marker, outlineColor, outlineThickness, fillColor);
		setupMarker(scaleWidthMarker,  Color.TRANSPARENT, 0.0f, new Color(255, 100, 100, 150));
		setupMarker(scaleHeightMarker, Color.TRANSPARENT, 0.0f, new Color(255, 100, 100, 150));
	}
	
	public void setupMarker(RectangleShape marker, Color outlineColor, float outlineThickness, Color fillColor) {
		marker.setOutlineColor(outlineColor);
		marker.setOutlineThickness(outlineThickness);
		marker.setFillColor(fillColor);
	}
	
	@Override
	public void draw(RenderTarget rt) {
		super.draw(rt);
		
		rt.draw(marker);

		if( isMouseOver(scaleHeightMarker) )
			rt.draw(scaleHeightMarker);
	
		if ( isMouseOver(scaleWidthMarker) )
			rt.draw(scaleWidthMarker);
	}
	
	private boolean isMouseOver(RectangleShape shape) {
		return shape.getGlobalBounds().contains(mapMousePos);
	}
	
	@Override
	public void update(boolean movedObj, RenderTarget rt, int mousePosX, int mousePosY, float zoom) {
		mapMousePos = rt.mapPixelToCoords(new Vector2i(mousePosX, mousePosY));
		
		if (movedObj) 
			setPosition(mapMousePos);
		
		Vector2f newSize = new Vector2f(layerObject.getWidth(), layerObject.getHeight());
		setupMarker(marker, newSize, new Vector2f(newSize.x / 2f, newSize.y / 2f), layerObject.getPosition(), layerObject.getRotation());

		setupMarker(scaleHeightMarker, new Vector2f(newSize.x, 8.f), new Vector2f(newSize.x / 2f, newSize.y / 2f), 
				layerObject.getPosition(), layerObject.getRotation());

		setupMarker(scaleWidthMarker, new Vector2f(8.f, newSize.y), new Vector2f(newSize.x / 2f, newSize.y / 2f),
				layerObject.getPosition(), layerObject.getRotation());

		if (scaleWidth)
			width  += (lastMousePos.x - mousePosX) * 2.f / zoom;

		if (scaleHeight)
			height += (lastMousePos.y - mousePosY) * 2.f / zoom;
		
		lastMousePos = new Vector2f(mousePosX, mousePosY);
	}
	
	private void setupMarker(RectangleShape marker, Vector2f size, Vector2f origin, Vector2f pos, float rotation) {
		marker.setSize(size);
		marker.setOrigin(origin);
		marker.setPosition(pos);
		marker.setRotation(rotation);
	}
	
	public void setLayerObject(ILayerObject layerObject) {
		this.layerObject = layerObject;
		
		if( layerObject != null ) {
			rotation = layerObject.getRotation();
			zoom = 1.0f;
			height = layerObject.getHeight();
			width = layerObject.getWidth();
		}
	}
	
	public ILayerObject getLayerObject() {
		return layerObject;
	}
	
	public void resetScales() {
		scaleWidth  = false;
		scaleHeight = false;
	}
	
	public void checkScaleMarkers(Vector2f v) {
		if (scaleWidthMarker.getGlobalBounds().contains(v))
			scaleWidth = true;

		if (scaleHeightMarker.getGlobalBounds().contains(v))
			scaleHeight = true;
	}
	
	public void removeFromMap(GameMap map, LayerType currentLayer) {
		map.remove(currentLayer, layerObject);
		layerObject = null;
	}
	
	public void setLastMousePos(Vector2f pos) {
		lastMousePos = pos;
	}
}
