package de.secondsystem.game01.impl.game.entities.events.impl;

import java.util.UUID;

import de.secondsystem.game01.impl.game.entities.IGameEntity;
import de.secondsystem.game01.impl.game.entities.events.SingleEntityEventHandler;
import de.secondsystem.game01.impl.map.IGameMap.WorldId;

public class ReanimateEventHandler extends SingleEntityEventHandler {

	public ReanimateEventHandler(UUID uuid, EntityEventType eventType) {
		super(uuid, eventType);
	}
	
	@Override
	public Object handle(EntityEventType type, IGameEntity owner,
			Object... args) {
		if( owner.isDead() ) {
			owner.setDead(false);
			owner.setWorld(WorldId.MAIN);
		}
				
		return null;
	}

}
