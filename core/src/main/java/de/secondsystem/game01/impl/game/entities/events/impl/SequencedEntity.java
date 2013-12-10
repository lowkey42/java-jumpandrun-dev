package de.secondsystem.game01.impl.game.entities.events.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import de.secondsystem.game01.impl.game.entities.IGameEntityManager;

public class SequencedEntity implements IToggled {
	
	protected boolean on = false;
	protected UUID uuid;
	
	/**
	 * Extensions to the entity.
	 */
	public final List<SequencedEntity> linkedEntities = new ArrayList<>();
	
	
	public SequencedEntity(UUID uuid) {
		this.uuid = uuid;
	}
	
	
	public SequencedEntity() {
	}
	
	
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
	
	UUID uuid() {
		return uuid;
	}
	
	@SuppressWarnings("unchecked")
	public JSONObject serialize() {
		JSONObject obj = new JSONObject();
		obj.put("uuid", uuid.toString());
		JSONArray jArray = new JSONArray();
		for(SequencedEntity e : linkedEntities)
			jArray.add(e.serialize());
		
		obj.put("linkedEntities", jArray);
		
		return obj;
	}
	
	/**
	 * @return Null if this SequencedEntity does not exist, returns the existing SequencedEntity otherwise.
	 */
	public SequencedEntity deserialize(JSONObject obj, IGameEntityManager entityManager, SequenceManager sequenceManager) {
		UUID uuid = UUID.fromString( (String) obj.get("uuid") );
		if( sequenceManager.getSequencedEntity(uuid) != null )
			return sequenceManager.getSequencedEntity(uuid);
		
		this.uuid = uuid;
		JSONArray jArray = (JSONArray) obj.get("linkedEntities");
		
		if( jArray == null )
			return null;
		
		for(Object e : jArray) {
			SequencedEntity linkedEntity = new SequencedEntity(null);
			SequencedEntity le = linkedEntity.deserialize((JSONObject) e, entityManager, sequenceManager);
			this.linkedEntities.add(le != null ? le : linkedEntity);
		}
		
		return null;
	} 
	
}
