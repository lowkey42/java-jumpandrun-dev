package de.secondsystem.game01.impl.editor.objects;

import org.jsfml.graphics.RenderTarget;
import org.jsfml.system.Vector2f;

import de.secondsystem.game01.impl.map.GameMap;
import de.secondsystem.game01.impl.map.IGameMap;
import de.secondsystem.game01.impl.map.ILayerObject;
import de.secondsystem.game01.impl.map.LayerType;
import de.secondsystem.game01.model.IDimensioned;
import de.secondsystem.game01.model.IDrawable;
import de.secondsystem.game01.model.IInsideCheck;
import de.secondsystem.game01.model.IMoveable;

public interface IEditorLayerObject extends IDrawable, IDimensioned, IMoveable, IInsideCheck {
	void setWidth(float width);
	void setHeight(float height);
	void rotate(float rotation);
	void zoom(float factor);
	void refresh();
	void zoom(int mouseWheelOffset, float mouseWheelDelta);
	void update(boolean movedObj, RenderTarget rt, int mousePosX, int mousePosY, float zoom, long frameTimeMs);
	void deselect();
	void setMouseState(boolean mouseState);
	boolean inMouseState();
	
	void setScaling(boolean scaling);
	boolean isScaling();
	
	ILayerObject getLayerObject();
	void setLayerObject(ILayerObject layerObject);
	
	void removeFromMap(GameMap map, LayerType currentLayer);
	
	// used in mouse state
	void create(IGameMap map);
	void changeSelection(int offset);
	void addToMap(LayerType currentLayer);
	
	// used in selected state
	void setLastMappedMousePos(Vector2f pos);
	void resetScalingDirection();
	void checkScaleMarkers(Vector2f p);
}
