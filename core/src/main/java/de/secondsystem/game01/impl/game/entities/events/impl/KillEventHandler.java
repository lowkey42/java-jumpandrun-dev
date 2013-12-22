package de.secondsystem.game01.impl.game.entities.events.impl;

import java.util.UUID;

import de.secondsystem.game01.impl.game.entities.IControllableGameEntity;
import de.secondsystem.game01.impl.game.entities.IGameEntity;
import de.secondsystem.game01.impl.game.entities.events.SingleEntityEventHandler;
import de.secondsystem.game01.impl.map.IGameMap.WorldId;

public class KillEventHandler extends SingleEntityEventHandler {

	public KillEventHandler(UUID uuid, EntityEventType eventType) {
		super(uuid, eventType);
	}
	
	@Override
	public Object handle(EntityEventType type, IGameEntity owner,
			Object... args) {
		switch( owner.getWorldId() ) {
			case MAIN:
				inMainWorld(owner, (IGameEntity) args[0]);
				break;
				
			case OTHER:
				inOtherWorld(owner, (IGameEntity) args[0]);
				break;
		}
		
		return null;
	}

	protected void killEntity(IGameEntity entity) {
		entity.manager().destroy(entity.uuid());
	}
	
	protected void inMainWorld(IGameEntity entity, IGameEntity other) {
		if( entity instanceof IControllableGameEntity )
			if( !entity.setWorld(WorldId.OTHER) )
				killEntity(entity);
		
		entity.setDead(true);
	}

	protected void inOtherWorld(IGameEntity entity, IGameEntity other) {
		entity.setDead(true);
		killEntity(entity);
	}
	
}
