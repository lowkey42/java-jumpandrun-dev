package de.secondsystem.game01.impl.game.entities.events.impl;

import java.util.ArrayList;
import java.util.List;

import de.secondsystem.game01.impl.game.entities.IGameEntity;
import de.secondsystem.game01.impl.game.entities.events.IEntityEventHandler;
import de.secondsystem.game01.impl.game.entities.events.IEntityEventHandler.EntityEventType;

public class Toggle extends SequencedObject implements IToggled {
	
	public class ToggleInputOption {
		public final List<IToggled> on     = new ArrayList<>();
		public final List<IToggled> off    = new ArrayList<>(); 
		public final List<IToggled> toggle = new ArrayList<>();
	}
	
	public final ToggleInputOption inputOption = new ToggleInputOption();
	
	@Override
	public Object handle(EntityEventType type, IGameEntity owner) {
		super.handle(type, owner);
		
		if( inputOption.on.size() > 0 )
			onTurnOn();
		
		if( inputOption.off.size() > 0 )
			onTurnOff();
		
		if( inputOption.toggle.size() > 0 )
			onToggle();
		
		return null;
	}

	@Override
	public void onTurnOn() {
		for( IToggled toggled : inputOption.on ) 
			toggled.onTurnOn();
		
		for( IToggled target : targets )
			target.onTurnOn();
	}

	@Override
	public void onTurnOff() {
		for( IToggled toggled : inputOption.off )
			toggled.onTurnOff();
		
		for( IToggled target : targets )
			target.onTurnOff();
	}

	@Override
	public void onToggle() {
		for( IToggled toggled : inputOption.toggle ) 
			toggled.onToggle();
		
		for( IToggled target : targets )
			target.onToggle();
	}
	
}
