package de.secondsystem.game01.impl.game.entities.events.impl;

import java.util.List;

import de.secondsystem.game01.impl.game.entities.IGameEntity;
import de.secondsystem.game01.impl.game.entities.events.IEntityEventHandler;
import de.secondsystem.game01.impl.game.entities.events.IEntityEventHandler.EntityEventType;

public class Toggle implements ISequencedObject {
	
	public enum ToggleInputOption {
		ON,
		OFF,
		TOGGLE
	}
	
	private final ToggleInputOption inputOption;
	private final ISequencedEntity owner;;
	
	public Toggle(ToggleInputOption inputOption, IGameEntity owner) {
		this.inputOption = inputOption;	
		this.owner = new SequencedEntity(owner);
	}

	@Override
	public Object handle(EntityEventType type, List<ISequencedEntity> targets, List<IEntityEventHandler> events) {
		for( IEntityEventHandler event : events )
			if( event.isHandled(type) )
				event.handle(type);
		
		switch( inputOption ) {
		case ON:
			owner.onTurnOn();
			for( ISequencedEntity target : targets )
				target.onTurnOn();
			break;
		case OFF:
			owner.onTurnOff();
			for( ISequencedEntity target : targets )
				target.onTurnOff();
			break;
		case TOGGLE:
			owner.onToggle();
			for( ISequencedEntity target : targets )
				target.onToggle();
			break;
		}
		
		return null;
	}
	
}
