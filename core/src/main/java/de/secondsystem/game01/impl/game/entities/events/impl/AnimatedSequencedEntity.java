package de.secondsystem.game01.impl.game.entities.events.impl;

import de.secondsystem.game01.impl.game.entities.IGameEntity;
import de.secondsystem.game01.model.IAnimated;
import de.secondsystem.game01.model.IAnimated.AnimationType;

public class AnimatedSequencedEntity extends SequencedEntity {

	public AnimatedSequencedEntity(IGameEntity owner, ISequencedEntity parent) {
		super(owner, parent);
	}

	@Override
	public void onTurnOn() {
		super.onTurnOn();
		if( owner != null )
			((IAnimated) owner.getRepresentation()).play(AnimationType.USED, 1.f, true, true, false);
	}

	@Override
	public void onTurnOff() {
		super.onTurnOff();
		if( owner != null )
			((IAnimated) owner.getRepresentation()).play(AnimationType.IDLE, 1.f, true, true, false);
	}
}
