package de.secondsystem.game01.impl.game.entities.events.impl;

import java.util.ArrayList;
import java.util.List;

import de.secondsystem.game01.impl.game.entities.IGameEntity;
import de.secondsystem.game01.impl.game.entities.events.IEntityEventHandler;
import de.secondsystem.game01.impl.game.entities.events.IEntityEventHandler.EntityEventType;

public class Toggle extends SequencedObject {
	
	public class ToggleInputOption {
		public final List<ISequencedEntity> on     = new ArrayList<>();
		public final List<ISequencedEntity> off    = new ArrayList<>(); 
		public final List<ISequencedEntity> toggle = new ArrayList<>();
	}
	
	public final ToggleInputOption inputOption = new ToggleInputOption();
	
	@Override
	public Object handle(EntityEventType type, IGameEntity owner) {
		super.handle(type, owner);
		
		for( ISequencedEntity entity : inputOption.on ) {
			entity.onTurnOn();
			for( ISequencedEntity target : targets )
				target.onTurnOn();
		}
		
		for( ISequencedEntity entity : inputOption.off ) {
			entity.onTurnOff();
			for( ISequencedEntity target : targets )
				target.onTurnOff();
		}
		
		for( ISequencedEntity entity : inputOption.toggle ) {
			entity.onToggle();
			for( ISequencedEntity target : targets )
				target.onToggle();
		}
		
		return null;
	}
	
}
