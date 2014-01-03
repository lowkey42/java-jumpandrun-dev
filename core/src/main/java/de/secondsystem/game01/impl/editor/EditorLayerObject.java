package de.secondsystem.game01.impl.editor;

import org.jsfml.graphics.RenderTarget;
import org.jsfml.system.Vector2f;
import de.secondsystem.game01.impl.map.ILayerObject;

public class EditorLayerObject extends AbstractEditorObject {	
	protected ILayerObject layerObject = null;
	
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
	public void update(boolean movedObj, RenderTarget rt, int mousePosX, int mousePosY, float zoom, long frameTimeMs) {	
		layerObject.setPosition(pos);
	}

}
