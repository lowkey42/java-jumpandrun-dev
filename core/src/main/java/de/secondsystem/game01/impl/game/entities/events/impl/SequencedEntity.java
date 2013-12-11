package de.secondsystem.game01.impl.game.entities.events.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import de.secondsystem.game01.impl.map.IGameMap;

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
	public SequencedEntity deserialize(JSONObject obj, IGameMap map) {
		UUID uuid = UUID.fromString( (String) obj.get("uuid") );
		if( map.getSequenceManager().getSequencedEntity(uuid) != null )
			return map.getSequenceManager().getSequencedEntity(uuid);
		
		this.uuid = uuid;
		JSONArray jArray = (JSONArray) obj.get("linkedEntities");
		
		if( jArray == null )
			System.out.println("linkedEntities array is null");
		
		for(Object e : jArray) {
			JSONObject jSeqEntity = (JSONObject) e;
			SequencedEntity linkedEntity = map.getSequenceManager().createSequencedEntity((String) jSeqEntity.get("class"));
			SequencedEntity le = linkedEntity.deserialize(jSeqEntity, map);
			this.linkedEntities.add(le != null ? le : linkedEntity);
		}
		
		return null;
	} 
	
}
