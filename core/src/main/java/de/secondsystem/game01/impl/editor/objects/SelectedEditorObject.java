package de.secondsystem.game01.impl.editor.objects;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.RenderTarget;
import org.jsfml.system.Vector2f;
import org.jsfml.system.Vector2i;

import de.secondsystem.game01.impl.map.GameMap;
import de.secondsystem.game01.impl.map.IGameMap;
import de.secondsystem.game01.impl.map.ILayerObject;
import de.secondsystem.game01.impl.map.LayerType;
import de.secondsystem.game01.util.Tools;

public class SelectedEditorObject extends EditorLayerObject implements ISelectedEditorObject {
	private EditorMarker marker;
	private EditorMarker [] scaleMarkers  = new EditorMarker[4];
	private Vector2f mappedMousePos;
	private Vector2f lastMappedMousePos = new Vector2f(0.f, 0.f);
	private float lastWidth, lastHeight;
	private Vector2f lastPos = new Vector2f(0.f, 0.f);
	private int scalingX = 0;
	private int scalingY = 0;	
	private boolean scaling = false;
	protected IGameMap map;
	
	public SelectedEditorObject(Color outlineColor, float outlineThickness, Color fillColor, IGameMap map) {
		marker = new EditorMarker(outlineColor, outlineThickness, fillColor);
		
		for(int i=0; i<4; i++)
			scaleMarkers[i] = new EditorMarker(Color.TRANSPARENT, 0.0f, new Color(255, 100, 100, 150));
		
		this.map = map;
	}
	
	public void setLayerObject(ILayerObject layerObject) {
		this.layerObject = layerObject;
		
		if( layerObject != null ) {
			rotation = layerObject.getRotation();
			zoom = 1.0f;
			height = layerObject.getHeight();
			width = layerObject.getWidth();
			setPosition(layerObject.getPosition());
			lastPos = pos;
			lastWidth = width;
			lastHeight = height;
		}
	}
	
	public ILayerObject getLayerObject() {
		return layerObject;
	}
	
	public void removeFromMap(GameMap map, LayerType currentLayer) {
		map.remove(currentLayer, layerObject);
		layerObject = null;
	}
	
	public void draw(RenderTarget rt) {	
		super.draw(rt);
		marker.draw(rt);
		
		if( mappedMousePos != null )
			for(int i=0; i<4; i++) {
				if( scaleMarkers[i].isMouseOver(mappedMousePos) )
					scaleMarkers[i].draw(rt);
		}
	}
	
	@Override
	public void update(boolean movedObj, RenderTarget rt, int mousePosX, int mousePosY, float zoom, long frameTimeMs) {
		super.update(movedObj, rt, mousePosX, mousePosY, zoom, frameTimeMs);
		
		mappedMousePos = rt.mapPixelToCoords(new Vector2i(mousePosX, mousePosY));
		
		if (movedObj) 
			setPosition(mappedMousePos);
		
		marker.update(this);	
		
		EditorMarker em = scaleMarkers[2];
		em.setRelativePos(width - em.getWidth(), height - em.getHeight());
		em = scaleMarkers[3];
		em.setRelativePos(width - em.getWidth(), height - em.getHeight());
		
		for(int i=0; i<4; i++) {
			scaleMarkers[i].update(this);
			scaleMarkers[i].setSize( new Vector2f(i%2 == 1 ? scaleMarkers[i].getWidth() : 8.f, i%2 == 0 ? scaleMarkers[i].getHeight() : 8.f));
		}
		
		mouseScaling();
	}
	
	@Override
	public void resetScalingDirection() {
		scalingX = 0;
		scalingY = 0;
	}
	
	@Override
	public void checkScaleMarkers(Vector2f p) {
		float r = Tools.clampedRotation(scaleMarkers[0].getShape().getRotation());
		if( r >= 90 && r <= 270 ) {
			scalingX = scaleMarkers[0].isInside(p) ? 1 : scaleMarkers[2].isInside(p) ? -1 : 0;		
			scalingY = scaleMarkers[1].isInside(p) ? 1 : scaleMarkers[3].isInside(p) ? -1 : 0;
		}
		else
		{
			scalingX = scaleMarkers[0].isInside(p) ? -1 : scaleMarkers[2].isInside(p) ? 1 : 0;		
			scalingY = scaleMarkers[1].isInside(p) ? -1 : scaleMarkers[3].isInside(p) ? 1 : 0;
		}
	}
	
	public void setScaling(boolean scaling) {
		this.scaling = scaling;
	}
	
	public boolean isScaling() {
		return scaling;
	}
	
	@Override
	public void setLastMappedMousePos(Vector2f pos) {
		lastMappedMousePos = pos;
	}
	
	private void mouseScaling() {
		Vector2f dir = new Vector2f(0.f, 0.f);
	
		if( scalingX != 0 ) {
			if( scalingX == -1 )
				dir = Vector2f.sub( Tools.distanceVector(scaleMarkers[0].getShape(), 3, 0, mappedMousePos), 
							Tools.distanceVector(scaleMarkers[0].getShape(), 3, 0, lastMappedMousePos) );
			else
				dir = Vector2f.sub( Tools.distanceVector(scaleMarkers[2].getShape(), 1, 2, mappedMousePos), 
							Tools.distanceVector(scaleMarkers[2].getShape(), 1, 2, lastMappedMousePos) );
			
			float d = Tools.vectorLength(dir);
			setWidth( lastWidth + ( d * (scalingX == -1 ? 1 : -1) ) * ( dir.x < 0 ? -1 : 1 ) );
			Vector2f v = Vector2f.sub( lastPos, Vector2f.div(dir, 2.f) );
			setPosition(v);
		}
		else 
			lastWidth  = width;
		
		if( scalingY != 0 ) {		
			if( scalingY == -1 ) 
				dir = Vector2f.sub( Tools.distanceVector(scaleMarkers[1].getShape(), 0, 1, mappedMousePos), 
						Tools.distanceVector(scaleMarkers[1].getShape(), 0, 1, lastMappedMousePos) );
			else 
				dir = Vector2f.sub( Tools.distanceVector(scaleMarkers[3].getShape(), 2, 3, mappedMousePos), 
							Tools.distanceVector(scaleMarkers[3].getShape(), 2, 3, lastMappedMousePos) );
			
			float d = Tools.vectorLength(dir);
			setHeight( lastHeight + ( d * (scalingY == -1 ? 1 : -1) ) * ( dir.y < 0 ? -1 : 1 ) );
			Vector2f v = Vector2f.sub( lastPos, Vector2f.div(dir, 2.f) );
			setPosition(v);
		}
		else 
			lastHeight = height;
		
		if( scalingX == 0 && scalingY == 0 )
			lastPos = pos;
		else
			scaling = true;
		
		if( width < 10.f )
			setWidth( 10.f );
		
		if( height < 10.f )
			setHeight( 10.f );
	}

	@Override
	public void deselect(IGameMap map) {
		// ignored for now
	}

}
