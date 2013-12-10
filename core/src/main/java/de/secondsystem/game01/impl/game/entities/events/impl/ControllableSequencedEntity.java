package de.secondsystem.game01.impl.game.entities.events.impl;

import org.json.simple.JSONObject;

import de.secondsystem.game01.impl.game.controller.PatrollingController;
import de.secondsystem.game01.impl.game.entities.IGameEntity;

public class ControllableSequencedEntity extends SequencedEntity implements IPlayedBack {
	
	private PatrollingController controller;
	
	public ControllableSequencedEntity(IGameEntity owner, PatrollingController controller) {	
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
	
	@SuppressWarnings("unchecked")
	@Override
	public JSONObject serialize() {
		JSONObject obj = super.serialize();
		obj.put("controller", controller.serialize());
		
		return obj;
	}
}
