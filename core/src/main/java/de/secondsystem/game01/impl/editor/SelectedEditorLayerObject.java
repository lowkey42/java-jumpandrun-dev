package de.secondsystem.game01.impl.editor;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.RenderTarget;
import org.jsfml.system.Vector2f;

import de.secondsystem.game01.impl.map.GameMap;
import de.secondsystem.game01.impl.map.ILayerObject;
import de.secondsystem.game01.impl.map.LayerType;

public class SelectedEditorLayerObject extends EditorLayerObject implements ISelectedEditorObject {
	private SelectedEditorObject selectedObject = new SelectedEditorObject(Color.BLUE, 2.0f, Color.TRANSPARENT);
	
	public void setLayerObject(ILayerObject layerObject) {
		this.layerObject = layerObject;
		
		if( layerObject != null ) {
			rotation = layerObject.getRotation();
			zoom = 1.0f;
			height = layerObject.getHeight();
			width = layerObject.getWidth();
			setPosition(layerObject.getPosition());
		}
	}
	
	public ILayerObject getLayerObject() {
		return layerObject;
	}
	
	public void removeFromMap(GameMap map, LayerType currentLayer) {
		map.remove(currentLayer, layerObject);
		layerObject = null;
	}

	@Override
	public void setLastMappedMousePos(Vector2f pos) {
		selectedObject.setLastMappedMousePos(pos);
	}

	@Override
	public void resetScalingDirection() {
		selectedObject.resetScalingDirection();
	}

	@Override
	public void checkScaleMarkers(Vector2f p) {
		selectedObject.checkScaleMarkers(p);
	}
	
	@Override
	public void draw(RenderTarget rt) {
		super.draw(rt);
		
		selectedObject.draw(rt);
	}
	
	@Override
	public void update(boolean movedObj, RenderTarget rt, int mousePosX, int mousePosY, float zoom, long frameTimeMs) {
		super.update(movedObj, rt, mousePosX, mousePosY, zoom, frameTimeMs);
		
		selectedObject.update(movedObj, rt, mousePosX, mousePosY, this);
	}
}
