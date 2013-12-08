package de.secondsystem.game01.impl.game.entities.events.impl;

import java.util.HashMap;

import de.secondsystem.game01.impl.game.entities.IGameEntity;
import de.secondsystem.game01.impl.game.entities.events.IEntityEventHandler.EntityEventType;

public class Toggle extends SequencedObject {
	
	public class ToggleInputOption {
		public final HashMap<IGameEntity, IToggled> onTrigger     = new HashMap<>();
		public final HashMap<IGameEntity, IToggled> offTrigger    = new HashMap<>(); 
		public final HashMap<IGameEntity, IToggled> toggleTrigger = new HashMap<>();
	}
	
	public final ToggleInputOption inputOption = new ToggleInputOption();
	public HashMap<IGameEntity, IToggled> map = new HashMap<>();
	
	@Override
	public Object handle(EntityEventType type, IGameEntity owner, Object... args) {
		super.handle(type, owner, args);
		
		if( inputOption.onTrigger.get(owner) != null ) 
			for( IToggled target : targets )
				target.onTurnOn();
		
		if( inputOption.offTrigger.get(owner) != null ) 
			for( IToggled target : targets )
				target.onTurnOff();
		
		if( inputOption.toggleTrigger.get(owner) != null ) 
			for( IToggled target : targets )
				target.onToggle();
		
		return null;
	}
}
