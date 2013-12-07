package de.secondsystem.game01.impl.game.entities.events.impl;

import de.secondsystem.game01.impl.game.entities.IGameEntity;

public class ControllableSequencedEntity extends SequencedEntity {

	public ControllableSequencedEntity(IGameEntity owner, ISequencedEntity linkedEntity) {
		super(owner, linkedEntity);
	}

	@Override
	public void onTurnOn() {
		super.onTurnOn();
		// TODO
	}

	@Override
	public void onTurnOff() {
		super.onTurnOff();
		// TODO
	}
}
