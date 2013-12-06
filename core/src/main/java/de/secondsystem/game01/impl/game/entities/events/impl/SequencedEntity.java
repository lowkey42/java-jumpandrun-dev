package de.secondsystem.game01.impl.game.entities.events.impl;

import de.secondsystem.game01.impl.game.entities.IGameEntity;
import de.secondsystem.game01.model.IAnimated;
import de.secondsystem.game01.model.IAnimated.AnimationType;

public class SequencedEntity implements ISequencedEntity {
	
	private final IGameEntity entity;
	private boolean on = false;
	
	public SequencedEntity(IGameEntity entity) {
		this.entity = entity;
	}
	
	@Override
	public void onTurnOn() {
		on = true;
		((IAnimated) entity.getRepresentation()).play(AnimationType.USED, 1.f, true, true, false);
	}

	@Override
	public void onTurnOff() {
		on = false;
		((IAnimated) entity.getRepresentation()).play(AnimationType.IDLE, 1.f, true, true, false);
	}

	@Override
	public void onToggle() {
		if( !on )
			onTurnOn();
		else
			onTurnOff();
	}

}
