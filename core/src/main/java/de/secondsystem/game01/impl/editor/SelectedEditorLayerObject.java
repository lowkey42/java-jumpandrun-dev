package de.secondsystem.game01.impl.editor;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.RenderTarget;
import org.jsfml.system.Vector2f;
import org.jsfml.system.Vector2i;

import de.secondsystem.game01.impl.map.GameMap;
import de.secondsystem.game01.impl.map.ILayerObject;
import de.secondsystem.game01.impl.map.LayerType;

public class SelectedEditorLayerObject extends EditorLayerObject {
	private EditorMarker marker;
	private EditorMarker [] scaleMarkers  = new EditorMarker[2];
	private boolean scaleWidthAllowed  = false;
	private boolean scaleHeightAllowed = false;
	protected Vector2f lastMappedMousePos;
	private float lastWidth, lastHeight;
	private float lastX = 0.f, lastY = 0.f;
	
	public SelectedEditorLayerObject(Color outlineColor, float outlineThickness, Color fillColor) {
		marker = new EditorMarker(outlineColor, outlineThickness, fillColor);
		
		for(int i=0; i<2; i++)
			scaleMarkers[i] = new EditorMarker(Color.TRANSPARENT, 0.0f, new Color(255, 100, 100, 150));
	}
	
	@Override
	public void draw(RenderTarget rt) {
		super.draw(rt);
		
		marker.draw(rt);
		
		for(int i=0; i<2; i++) {
			if( scaleMarkers[i].isMouseOver(mappedMousePos) )
				scaleMarkers[i].draw(rt);
		}
	}
	

	
	@Override
	public void update(boolean movedObj, RenderTarget rt, int mousePosX, int mousePosY, float zoom) {
		mappedMousePos = rt.mapPixelToCoords(new Vector2i(mousePosX, mousePosY));
		
		if (movedObj) 
			setPosition(mappedMousePos);
		
		marker.update(layerObject);
		
		for(int i=0; i<2; i++) {
			scaleMarkers[i].update(layerObject);
			scaleMarkers[i].setSize( new Vector2f(i==1 ? scaleMarkers[i].getWidth() : 8.f, i==0 ? scaleMarkers[i].getHeight() : 8.f));
		}
		
		// scaling with mouse
		if( scaleWidthAllowed ) {
			width = lastWidth + lastMappedMousePos.x - mappedMousePos.x;
			Vector2f v = new Vector2f(lastX - (lastMappedMousePos.x - mappedMousePos.x)/2.f, layerObject.getPosition().y);
			layerObject.setPosition(v);
		}
		else {
			lastWidth = width;
			lastX = layerObject.getPosition().x;
		}

		if( scaleHeightAllowed ) {
			height = lastHeight + lastMappedMousePos.y - mappedMousePos.y;
			Vector2f v = new Vector2f(layerObject.getPosition().x , lastY - (lastMappedMousePos.y - mappedMousePos.y)/2.f);
			layerObject.setPosition(v);
		}
		else {
			lastHeight = height;
			lastY = layerObject.getPosition().y;
		}
		
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
	
	public void checkScaleMarkers(Vector2f p) {
		if ( scaleMarkers[0].isInside(p) )
			scaleWidthAllowed = true;

		if ( scaleMarkers[1].isInside(p) )
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
