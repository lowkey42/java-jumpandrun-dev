package de.secondsystem.game01.impl.game.entities.events.impl;

import java.util.List;

import de.secondsystem.game01.impl.game.entities.IGameEntity;
import de.secondsystem.game01.impl.game.entities.events.IEntityEventHandler;
import de.secondsystem.game01.impl.game.entities.events.IEntityEventHandler.EntityEventType;

public class SequencedObject implements ISequencedObject {

	@Override
	public Object handle(EntityEventType type, IGameEntity owner, List<ISequencedEntity> targets, List<IEntityEventHandler> events) {
		for( IEntityEventHandler event : events )
			if( event.isHandled(type) )
				event.handle(type, null);
		
		return null;
	}

}
