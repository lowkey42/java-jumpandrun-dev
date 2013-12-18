package de.secondsystem.game01.impl.game.entities;

import java.util.UUID;

public interface IWeakGameEntityRef {

	UUID uuid();
	IGameEntityManager manager();
	
	IGameEntity get();
	
}
