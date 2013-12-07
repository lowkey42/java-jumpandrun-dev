package de.secondsystem.game01.impl.game.entities.events;

import java.util.ArrayList;
import java.util.List;

import de.secondsystem.game01.impl.game.entities.IGameEntity;
import de.secondsystem.game01.impl.game.entities.events.impl.ISequencedEntity;
import de.secondsystem.game01.impl.game.entities.events.impl.ISequencedObject;

public class SequencedEntityEventHandler extends SingleEntityEventHandler {
	
	private final ISequencedObject sequencedObject;
	
	public SequencedEntityEventHandler(EntityEventType eventType, ISequencedObject sequencedObject) {
		super(eventType);
		
		this.sequencedObject = sequencedObject;
	}
	
	@Override
	public Object handle(EntityEventType type, IGameEntity owner,
			Object... args) {
		return sequencedObject.handle(type, owner);
	}

}
