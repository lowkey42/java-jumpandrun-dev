package de.secondsystem.game01.impl.editor.objects;

import org.jsfml.graphics.RenderTarget;

import de.secondsystem.game01.impl.map.IGameMap;
import de.secondsystem.game01.impl.map.LayerType;

public interface IMouseEditorObject extends IEditorObject{
	void create(IGameMap map);
	void changeSelection(int offset);
	void addToMap(LayerType currentLayer);
	void update(boolean movedObj, RenderTarget rt, int mousePosX, int mousePosY, float zoom, long frameTimeMs);
}
