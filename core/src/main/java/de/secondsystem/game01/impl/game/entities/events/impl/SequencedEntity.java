package de.secondsystem.game01.impl.game.entities.events.impl;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class SequencedEntity implements IToggled {
	
	protected boolean on = false;
	/**
	 * Extensions to the entity.
	 */
	public final List<SequencedEntity> linkedEntities = new ArrayList<>();
	
	@Override
	public void onTurnOn() {
		on = true;
		for(SequencedEntity e : linkedEntities)
			e.onTurnOn();
	}

	@Override
	public void onTurnOff() {
		on = false;
		for(SequencedEntity e : linkedEntities)
			e.onTurnOff();
	}

	@Override
	public void onToggle() {
		if( !on )
			onTurnOn();
		else
			onTurnOff();
	}
	
	@SuppressWarnings("unchecked")
	public JSONObject serialize() {
		JSONObject obj = new JSONObject();
		JSONArray jArray = new JSONArray();
		for(SequencedEntity e : linkedEntities)
			jArray.add(e.serialize());
		
		obj.put("linkedEntities", jArray);
		
		return obj;
	}
	
	public void deserialize(JSONObject obj) {
		JSONArray jArray = (JSONArray) obj.get("linkedEntities");
		
		for(Object e : jArray) {
			SequencedEntity linkedEntity = new SequencedEntity();
			linkedEntity.deserialize((JSONObject) e);
			this.linkedEntities.add(linkedEntity);
		}
	} 
	
}
