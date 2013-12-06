package de.secondsystem.game01.impl.game.entities.events.impl;

import de.secondsystem.game01.impl.game.entities.IGameEntity;

public abstract class SequencedEntity implements ISequencedEntity {
	
	protected boolean on = false;
	protected IGameEntity owner = null;
	protected ISequencedEntity parent = null;
	
	@Override
	public abstract void onTurnOn();
	
	@Override
	public abstract void onTurnOff();
	
	@Override
	public abstract void onToggle();
}
