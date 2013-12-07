package de.secondsystem.game01.impl.game.entities.events.impl;

import de.secondsystem.game01.impl.game.entities.IGameEntity;

public class SequencedEntity implements ISequencedEntity {
	
	protected boolean on = false;
	protected IGameEntity owner = null;
	protected ISequencedEntity linkedEntity = null;
	
	public SequencedEntity(IGameEntity owner, ISequencedEntity linkedEntity) {
		this.linkedEntity = linkedEntity;
		this.owner  = owner;
	}
	
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

	@Override
	public void setOwner(IGameEntity owner) {
		this.owner = owner;
	}

	@Override
	public void onPlay() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onReverse() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		
	}
}
