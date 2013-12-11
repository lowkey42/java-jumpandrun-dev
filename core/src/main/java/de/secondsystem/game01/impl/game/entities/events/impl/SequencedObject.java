package de.secondsystem.game01.impl.game.entities.events.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import de.secondsystem.game01.impl.game.entities.IGameEntity;
import de.secondsystem.game01.impl.game.entities.events.IEntityEventHandler;
import de.secondsystem.game01.impl.game.entities.events.IEntityEventHandler.EntityEventType;
import de.secondsystem.game01.impl.map.IGameMap;

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
	protected static <T> JSONObject serializeTriggers(HashMap<IGameEntity, T> map) {
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
	protected static <T> void deserializeTriggers(HashMap<IGameEntity, T> hashMap, JSONObject obj, String inputOption, IGameMap map) {
		
		JSONObject triggers = (JSONObject) obj.get(inputOption);
		
		if( triggers == null )
			System.out.println("triggers is null");;
		
		for(Object o : triggers.keySet()) {
			IGameEntity entity = map.getEntityManager().get(UUID.fromString((String) o));
			JSONObject jSeqEntity = (JSONObject)triggers.get(o);
			SequencedEntity seqEntity = map.getSequenceManager().createSequencedEntity((String) jSeqEntity.get("class"));			
			SequencedEntity se = seqEntity.deserialize(jSeqEntity, map);
			hashMap.put(entity, se != null ? (T) se : (T) seqEntity);
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
	public ISequencedObject deserialize(JSONObject obj, IGameMap map) {
		UUID uuid = UUID.fromString( (String) obj.get("uuid") );
		ISequencedObject seqObj = map.getSequenceManager().getSequencedObject(uuid);
		if( seqObj != null )
			return seqObj;
		
		this.uuid = uuid;
		JSONArray targetArray = (JSONArray) obj.get("targets");
		
		if( targetArray == null )
			System.out.println("targetArray is null");
		
		for(Object o : targetArray) {	
			JSONObject jSeqEntity = (JSONObject) o;
			SequencedEntity target = map.getSequenceManager().createSequencedEntity((String) jSeqEntity.get("class"));			
			SequencedEntity t = target.deserialize(jSeqEntity, map);
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
		return uuid;
	}

}
