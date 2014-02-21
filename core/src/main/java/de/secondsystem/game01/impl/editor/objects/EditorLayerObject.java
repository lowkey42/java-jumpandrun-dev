package de.secondsystem.game01.impl.editor.objects;

import org.jsfml.graphics.RenderTarget;
import org.jsfml.system.Vector2f;
import de.secondsystem.game01.impl.map.ILayerObject;
import de.secondsystem.game01.model.IInsideCheck;

public class EditorLayerObject extends AbstractEditorObject implements IInsideCheck {	
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
	public void update(boolean movedObj, RenderTarget rt, int mousePosX, int mousePosY, float zoom, long frameTimeMs) {	
		layerObject.setPosition(pos);
	}

	@Override
	public boolean inside(Vector2f point) {
		return layerObject != null && layerObject.inside(point);
	}

}
