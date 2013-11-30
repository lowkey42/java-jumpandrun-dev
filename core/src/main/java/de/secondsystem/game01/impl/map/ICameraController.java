package de.secondsystem.game01.impl.map;

import org.jsfml.system.Vector2f;

import de.secondsystem.game01.impl.map.IGameMap.WorldId;

public interface ICameraController {

	Vector2f getPosition();
	
	WorldId getWorldId();
	
}
