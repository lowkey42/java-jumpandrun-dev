package de.secondsystem.game01.impl.game.entities.events.impl;

import de.secondsystem.game01.impl.game.entities.IGameEntity;

public class SequencedEntity implements ISequencedEntity {
	
	protected boolean on = false;
	protected IGameEntity owner = null;
	protected ISequencedEntity parent = null;
	
	public SequencedEntity(IGameEntity owner, ISequencedEntity parent) {
		this.parent = parent;
		this.owner  = owner;
	}
	
	@Override
	public void onTurnOn() {
		on = true;
		if( parent != null )
			parent.onTurnOn();
	}

	@Override
	public void onTurnOff() {
		on = false;
		if( parent != null )
			parent.onTurnOff();
	}

	@Override
	public void onToggle() {
		if( !on )
			onTurnOn();
		else
			onTurnOff();
	}

	@Override
	public void setOwner(IGameEntity owner) {
		this.owner = owner;
	}
}
