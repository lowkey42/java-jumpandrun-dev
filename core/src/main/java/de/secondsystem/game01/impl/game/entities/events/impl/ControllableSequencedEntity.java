package de.secondsystem.game01.impl.game.entities.events.impl;

import java.util.UUID;

import org.json.simple.JSONObject;

import de.secondsystem.game01.impl.game.controller.PatrollingController;
import de.secondsystem.game01.impl.map.IGameMap;

public class ControllableSequencedEntity extends SequencedEntity implements IPlayedBack {
	
	private PatrollingController controller;
	
	public ControllableSequencedEntity(UUID uuid, PatrollingController controller) {	
		super(uuid);
		
		this.controller = controller;
	}
	
	public ControllableSequencedEntity() {		
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
		obj.put("class", "ControllableSequencedEntity");
		
		return obj;
	}
	
	@Override
	public SequencedEntity deserialize(JSONObject obj, IGameMap map) {
		SequencedEntity seqEntity = super.deserialize(obj, map);
		if( seqEntity != null )
			return seqEntity;
		
		controller = new PatrollingController();
		controller.deserialize((JSONObject) obj.get("controller"), map);
		
		return null;
	} 
}
