package de.secondsystem.game01.impl.game.entities.events;

import java.util.ArrayList;
import java.util.List;

import de.secondsystem.game01.impl.game.entities.IGameEntity;
import de.secondsystem.game01.impl.game.entities.events.impl.ISequencedEntity;
import de.secondsystem.game01.impl.game.entities.events.impl.ISequencedObject;

public class SequencedEntityEventHandler extends SingleEntityEventHandler {
	
	private final ISequencedObject sequencedObject;
	
	private final List<ISequencedEntity> targets   = new ArrayList<>();
	private final List<IEntityEventHandler> events = new ArrayList<>(); 
	
	public SequencedEntityEventHandler(EntityEventType eventType, ISequencedObject sequencedObject) {
		super(eventType);
		
		this.sequencedObject = sequencedObject;
	}

	public void addTarget(ISequencedEntity target) {
		targets.add(target);
	}	
	
	public void removeTarget(ISequencedEntity target) {
		targets.remove(target);
	}
	
	public void addEvent(IEntityEventHandler event) {
		events.add(event);
	}
	
	public void removeEvent(IEntityEventHandler event) {
		events.remove(event);
	}
	
	@Override
	public Object handle(EntityEventType type, IGameEntity owner,
			Object... args) {
		return sequencedObject.handle(type, targets, events);
	}

	@Override
	public Object handle(EntityEventType type, Object... args) {
		return sequencedObject.handle(type, targets, events);
	}

}
