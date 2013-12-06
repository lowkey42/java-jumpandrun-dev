package de.secondsystem.game01.impl.game.entities.events.impl;

import de.secondsystem.game01.impl.game.entities.IGameEntity;

public class BaseSequencedEntity extends SequencedEntity {
	
	public BaseSequencedEntity(IGameEntity owner, ISequencedEntity parent) {
		this.parent = parent;
		this.owner  = owner;
	}
	
	@Override
	public void onTurnOn() {
		if( parent == null )
			on = true;
		else
			parent.onTurnOff();
	}

	@Override
	public void onTurnOff() {
		if( parent == null )
			on = false;
		else
			parent.onTurnOff();
	}

	@Override
	public void onToggle() {
		if( !on )
			onTurnOn();
		else
			onTurnOff();
	}
}
