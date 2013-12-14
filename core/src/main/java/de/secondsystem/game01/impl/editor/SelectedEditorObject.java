package de.secondsystem.game01.impl.editor;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.RectangleShape;
import org.jsfml.graphics.RenderTarget;
import org.jsfml.system.Vector2f;
import org.jsfml.system.Vector2i;

import de.secondsystem.game01.impl.map.GameMap;
import de.secondsystem.game01.impl.map.ILayerObject;
import de.secondsystem.game01.impl.map.LayerType;
import de.secondsystem.game01.util.Tools;

public class SelectedEditorObject extends EditorObject {
	private RectangleShape marker = new RectangleShape(new Vector2f(1.f,1.f));
	private RectangleShape scaleWidthMarker  = new RectangleShape(new Vector2f(1.f,1.f));
	private RectangleShape scaleHeightMarker = new RectangleShape(new Vector2f(1.f,1.f));
	private boolean scaleWidthAllowed  = false;
	private boolean scaleHeightAllowed = false;
	protected Vector2f lastMappedMousePos;
	private float lastWidth, lastHeight;
	
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
		return Tools.isInside(shape, mappedMousePos);
	}
	
	@Override
	public void update(boolean movedObj, RenderTarget rt, int mousePosX, int mousePosY, float zoom) {
		mappedMousePos = rt.mapPixelToCoords(new Vector2i(mousePosX, mousePosY));
		
		if (movedObj) 
			setPosition(mappedMousePos);
		
		Vector2f newSize = new Vector2f(layerObject.getWidth(), layerObject.getHeight());
		Tools.setRectangleShape(marker, newSize, new Vector2f(newSize.x / 2f, newSize.y / 2f), layerObject.getPosition(), layerObject.getRotation());

		Tools.setRectangleShape(scaleHeightMarker, new Vector2f(newSize.x, 8.f), new Vector2f(newSize.x / 2f, newSize.y / 2f), 
				layerObject.getPosition(), layerObject.getRotation());

		Tools.setRectangleShape(scaleWidthMarker, new Vector2f(8.f, newSize.y), new Vector2f(newSize.x / 2f, newSize.y / 2f),
				layerObject.getPosition(), layerObject.getRotation());
		
		if( scaleWidthAllowed ) {
			width = lastWidth;
			width += (lastMappedMousePos.x - mappedMousePos.x) * 2.0f;
		}
		else
			lastWidth = width;

		if( scaleHeightAllowed ) {
			height = lastHeight;
			height += (lastMappedMousePos.y - mappedMousePos.y) * 2.0f;
		}
		else
			lastHeight = height;
		
		if( width < 10.f )
			width = 10.f;
		if( height < 10.f )
			height = 10.f;
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
	
	public void resetScalingPermission() {
		scaleWidthAllowed  = false;
		scaleHeightAllowed = false;
	}
	
	public void checkScaleMarkers(Vector2f v) {
		if ( Tools.isInside(scaleWidthMarker, v) )
			scaleWidthAllowed = true;

		if ( Tools.isInside(scaleHeightMarker, v) )
			scaleHeightAllowed = true;
	}
	
	public void removeFromMap(GameMap map, LayerType currentLayer) {
		map.remove(currentLayer, layerObject);
		layerObject = null;
	}
	
	public void setLastMappedMousePos(Vector2f pos) {
		lastMappedMousePos = pos;
	}

}
