package de.secondsystem.game01.impl.game.entities.events.impl;

import java.util.ArrayList;
import java.util.List;

import de.secondsystem.game01.impl.game.entities.IGameEntity;
import de.secondsystem.game01.impl.game.entities.events.IEntityEventHandler;
import de.secondsystem.game01.impl.game.entities.events.IEntityEventHandler.EntityEventType;

public class Toggle extends SequencedObject {
	
	public class ToggleInputOption {
		public final List<SequencedEntity> on     = new ArrayList<>();
		public final List<SequencedEntity> off    = new ArrayList<>(); 
		public final List<SequencedEntity> toggle = new ArrayList<>();
	}
	
	public final ToggleInputOption inputOption = new ToggleInputOption();
	
	@Override
	public Object handle(EntityEventType type, IGameEntity owner) {
		super.handle(type, owner);
		
		for( SequencedEntity entity : inputOption.on ) {
			((IToggle) entity).onTurnOn();
			for( SequencedEntity target : targets )
				((IToggle) target).onTurnOn();
		}
		
		for( SequencedEntity entity : inputOption.off ) {
			((IToggle) entity).onTurnOff();
			for( SequencedEntity target : targets )
				((IToggle) target).onTurnOff();
		}
		
		for( SequencedEntity entity : inputOption.toggle ) {
			((IToggle) entity).onToggle();
			for( SequencedEntity target : targets )
				((IToggle) target).onToggle();
		}
		
		return null;
	}
	
}
