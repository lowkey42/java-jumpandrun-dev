package de.secondsystem.game01.impl.game.entities.events.impl;

import java.util.ArrayList;
import java.util.List;

import de.secondsystem.game01.impl.game.entities.IGameEntity;
import de.secondsystem.game01.impl.game.entities.events.IEntityEventHandler;
import de.secondsystem.game01.impl.game.entities.events.IEntityEventHandler.EntityEventType;

public class SequencedObject implements ISequencedObject {
	
	protected final List<AbstractSequencedEntity> targets   = new ArrayList<>();
	protected final List<IEntityEventHandler> events = new ArrayList<>(); 
	
	@Override
	public Object handle(EntityEventType type, IGameEntity owner) {
		for( IEntityEventHandler event : events )
			if( event.isHandled(type) )
				event.handle(type, null);
		
		return null;
	}
	
	public void addTarget(AbstractSequencedEntity target) {
		targets.add(target);
	}	
	
	public void removeTarget(AbstractSequencedEntity target) {
		targets.remove(target);
	}
	
	public void addEvent(IEntityEventHandler event) {
		events.add(event);
	}
	
	public void removeEvent(IEntityEventHandler event) {
		events.remove(event);
	}

}
