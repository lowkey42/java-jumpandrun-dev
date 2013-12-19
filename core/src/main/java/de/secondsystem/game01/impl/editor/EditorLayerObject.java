package de.secondsystem.game01.impl.editor;

import org.jsfml.graphics.RenderTarget;
import org.jsfml.system.Vector2f;
import de.secondsystem.game01.impl.map.ILayerObject;

public class EditorLayerObject extends AbstractEditorObject {	
	protected ILayerObject layerObject = null;
	protected Vector2f mappedMousePos;	
	
	@Override
	public void refresh() {
		layerObject.setRotation(rotation);
		layerObject.setDimensions(width * zoom, height * zoom);
	}
	
	@Override
	public void draw(RenderTarget rt) {
		layerObject.draw(rt);
	}
	
	@Override
	public boolean isPointInside(Vector2f p) {
		return layerObject != null && layerObject.inside(p);
	}
	
	@Override
	public void setPosition(Vector2f pos) {
		super.setPosition(pos);
		layerObject.setPosition(pos);
	}

}
