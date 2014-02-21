package de.secondsystem.game01.impl.editor.objects;

import org.jsfml.system.Vector2f;

import de.secondsystem.game01.impl.map.IGameMap;

public interface ISelectedEditorObject {
	void setLastMappedMousePos(Vector2f pos);
	void resetScalingDirection();
	void checkScaleMarkers(Vector2f p);
	void deselect(IGameMap map);
}
