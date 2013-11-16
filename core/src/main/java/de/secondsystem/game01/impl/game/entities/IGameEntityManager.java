package de.secondsystem.game01.impl.game.entities;

import java.util.UUID;

import de.secondsystem.game01.model.IDrawable;
import de.secondsystem.game01.model.IUpdateable;

public interface IGameEntityManager extends IDrawable, IUpdateable {

	IControllableGameEntity createPlayer(float x, float y);

	void destroy(UUID eId);

	IGameEntity get(UUID eId);

}