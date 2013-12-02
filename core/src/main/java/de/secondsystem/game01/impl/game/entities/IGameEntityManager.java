package de.secondsystem.game01.impl.game.entities;

import java.util.Map;
import java.util.UUID;

import de.secondsystem.game01.model.IDrawable;
import de.secondsystem.game01.model.IUpdateable;

public interface IGameEntityManager extends IDrawable, IUpdateable {

	IControllableGameEntity createControllable( String type, Map<String, Object> args );
	
	void destroy(UUID eId);

	IGameEntity get(UUID eId);

}