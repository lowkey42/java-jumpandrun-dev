package de.secondsystem.game01.impl.game.entities.events.impl;

import de.secondsystem.game01.impl.game.controller.PatrollingController;
import de.secondsystem.game01.impl.game.entities.IGameEntity;

public class ControllableSequencedEntity extends SequencedEntity implements IPlayback {
	
	private PatrollingController controller;
	
	public ControllableSequencedEntity(IGameEntity owner, ISequencedEntity linkedEntity, PatrollingController controller) {
		this.linkedEntity = linkedEntity;
		
		this.controller = controller;
	}

	@Override
	public void onTurnOn() {
		super.onTurnOn();
		
		controller.play();
	}

	@Override
	public void onTurnOff() {
		super.onTurnOff();
		
		controller.reverse();
	}
	
	@Override
	public void onPlay() {
		controller.play();
	}

	@Override
	public void onReverse() {
		controller.reverse();
	}

	@Override
	public void onStop() {
		controller.stop();
	}

	@Override
	public void onPause() {
		controller.pause();
	}

	@Override
	public void onResume() {
		controller.play();
	}
}
