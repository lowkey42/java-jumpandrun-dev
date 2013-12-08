package de.secondsystem.game01.impl.game.entities.events.impl;

import java.util.HashMap;

import de.secondsystem.game01.impl.game.entities.IGameEntity;
import de.secondsystem.game01.impl.game.entities.events.IEntityEventHandler.EntityEventType;

public class Toggle extends SequencedObject {
	
	public class ToggleInputOption {
		public final HashMap<IGameEntity, IToggled> on     = new HashMap<>();
		public final HashMap<IGameEntity, IToggled> off    = new HashMap<>(); 
		public final HashMap<IGameEntity, IToggled> toggle = new HashMap<>();
	}
	
	public final ToggleInputOption inputOption = new ToggleInputOption();
	public HashMap<IGameEntity, IToggled> map = new HashMap<>();
	
	@Override
	public Object handle(EntityEventType type, IGameEntity owner, Object... args) {
		super.handle(type, owner, args);
		
		if( inputOption.on.get(owner) != null ) {
			inputOption.on.get(owner).onTurnOn();
		
			for( IToggled target : targets )
				target.onTurnOn();
		}
		
		if( inputOption.off.get(owner) != null ) {
			inputOption.off.get(owner).onTurnOff();
		
			for( IToggled target : targets )
				target.onTurnOff();
		}
		
		if( inputOption.toggle.get(owner) != null ) {
			inputOption.toggle.get(owner).onToggle();
		
			for( IToggled target : targets )
				target.onToggle();
		}
		
		return null;
	}
	
}
