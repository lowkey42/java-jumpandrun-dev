package de.secondsystem.game01.impl.game.entities.events.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import de.secondsystem.game01.impl.game.entities.IGameEntity;
import de.secondsystem.game01.impl.game.entities.IGameEntityManager;
import de.secondsystem.game01.impl.game.entities.events.IEntityEventHandler;
import de.secondsystem.game01.impl.game.entities.events.IEntityEventHandler.EntityEventType;

public class SequencedObject implements ISequencedObject {
	
	protected final List<SequencedEntity> targets   = new ArrayList<>();
	protected final List<IEntityEventHandler> events = new ArrayList<>(); 
	protected UUID uuid;
	
	public SequencedObject(UUID uuid) {
		this.uuid = uuid;
	}
	
	public SequencedObject() {
		
	}
	
	@Override
	public Object handle(EntityEventType type, IGameEntity owner, Object... args) {
		for( IEntityEventHandler event : events )
			if( event.isHandled(type) )
				event.handle(type, null);
		
		return null;
	}
	
	public void addTarget(SequencedEntity target) {
		targets.add(target);
	}	
	
	public void addTargets(List<SequencedEntity> targets) {
		this.targets.addAll(targets);
	}
	
	public void removeTarget(SequencedEntity target) {
		targets.remove(target);
	}
	
	public void addEvent(IEntityEventHandler event) {
		events.add(event);
	}
	
	public void removeEvent(IEntityEventHandler event) {
		events.remove(event);
	}
	
	@SuppressWarnings("unchecked")
	protected <T> JSONObject serializeTriggers(HashMap<IGameEntity, T> map) {
		JSONObject triggers = new JSONObject();
		for( IGameEntity entity : map.keySet() )
			triggers.put(entity.uuid(), ((SequencedEntity)map.get(entity)).serialize());

		return triggers;
	}
	
	/**
	 * 
	 * @param map
	 * @param obj
	 * @param entityManager
	 * @param inputOption
	 * @param sequenceManager
	 * @return False if this trigger already exists.
	 */
	@SuppressWarnings("unchecked")
	protected <T> void deserializeTriggers(HashMap<IGameEntity, T> map, JSONObject obj, 
			IGameEntityManager entityManager, String inputOption, SequenceManager sequenceManager) {
		
		JSONObject triggers = (JSONObject) obj.get(inputOption);
		
		if( triggers == null )
			return;
		
		for(Object o : triggers.keySet()) {
			IGameEntity entity = entityManager.get((UUID) o);
			SequencedEntity seqEntity = new SequencedEntity(null);
			SequencedEntity se = seqEntity.deserialize((JSONObject)triggers.get(o), entityManager, sequenceManager);
			map.put(entity, se != null ? (T) se : (T) seqEntity);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public JSONObject serialize() {
		JSONObject obj = new JSONObject();
		obj.put("uuid", uuid.toString());
		
		JSONArray targetArray = new JSONArray();
		for(SequencedEntity target : targets) 
			targetArray.add(target.serialize());
		
		JSONArray eventArray = new JSONArray();
//		for(IEntityEventHandler event : events) 
//			eventArray.add(event.serialize());
		
		obj.put("targets", targetArray);
		obj.put("events", eventArray);
		
		return obj;	
	}

	@Override
	public ISequencedObject deserialize(JSONObject obj, IGameEntityManager entityManager, SequenceManager sequenceManager) {
		UUID uuid = UUID.fromString( (String) obj.get("uuid") );
		ISequencedObject seqObj = sequenceManager.getSequencedObject(uuid);
		if( seqObj != null )
			return seqObj;
		
		this.uuid = uuid;
		JSONArray targetArray = (JSONArray) obj.get("targets");
		
		if( targetArray == null )
			return null;
		
		for(Object o : targetArray) {		
			SequencedEntity target = new SequencedEntity(null);
			SequencedEntity t = target.deserialize((JSONObject) o, entityManager, sequenceManager);
			this.addTarget(t != null ? t : target);
		}
		

//		JSONArray eventArray = (JSONArray) obj.get("targets");
//		if( eventArray == null )
//			return null;
//		for(Object o : eventArray) {
//			IEntityEventHandler event = new SingleEntityEventHandler();
//			this.addEvent(event.deserialize(o));
//		}
		
		return null;
	}

	@Override
	public UUID uuid() {
		// TODO Auto-generated method stub
		return null;
	}

}
