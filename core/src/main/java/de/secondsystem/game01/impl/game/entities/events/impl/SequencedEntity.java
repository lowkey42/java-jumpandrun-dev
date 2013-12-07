package de.secondsystem.game01.impl.game.entities.events.impl;

import de.secondsystem.game01.impl.game.entities.IGameEntity;

public class SequencedEntity implements ISequencedEntity {
	
	protected boolean on = false;
	protected ISequencedEntity linkedEntity = null;
	
	@Override
	public void onTurnOn() {
		on = true;
		if( linkedEntity != null )
			linkedEntity.onTurnOn();
	}

	@Override
	public void onTurnOff() {
		on = false;
		if( linkedEntity != null )
			linkedEntity.onTurnOff();
	}

	@Override
	public void onToggle() {
		if( !on )
			onTurnOn();
		else
			onTurnOff();
	}
}
