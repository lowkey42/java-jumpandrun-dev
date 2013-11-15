package de.secondsystem.game01.impl.game.entities;

import java.util.UUID;

public final class GameEntityManager {

	public IGameEntity create() {
		return new GameEntity(); // TODO
	}

	public void destroy( UUID eId ) {
		// TODO
	}

	public IGameEntity get( UUID eId ) {
		return null; // TODO
	}
	
}
