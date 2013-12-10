package de.secondsystem.game01.impl.game.entities.events.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import de.secondsystem.game01.impl.game.entities.GameEntityManager;
import de.secondsystem.game01.impl.game.entities.IGameEntity;
import de.secondsystem.game01.impl.game.entities.IGameEntityManager;
import de.secondsystem.game01.impl.game.entities.events.IEntityEventHandler;
import de.secondsystem.game01.impl.game.entities.events.IEntityEventHandler.EntityEventType;

public class SequencedObject implements ISequencedObject {
	
	protected final List<SequencedEntity> targets   = new ArrayList<>();
	protected final List<IEntityEventHandler> events = new ArrayList<>(); 
	
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
	
	@SuppressWarnings("unchecked")
	protected <T> void deserializeTriggers(HashMap<IGameEntity, T> map, JSONObject obj, IGameEntityManager entityManager, String inputOption) {
		JSONObject triggers = (JSONObject) obj.get(inputOption);
		for(Object o : triggers.keySet()) {
			IGameEntity entity = entityManager.get((UUID) o);
			SequencedEntity seqEntity = new SequencedEntity();
			seqEntity.deserialize((JSONObject)triggers.get(o));
			map.put(entity, (T) seqEntity);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public JSONObject serialize() {
		JSONObject obj = new JSONObject();
		
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
	public void deserialize(JSONObject obj, IGameEntityManager entityManager) {	
		JSONArray targetArray = (JSONArray) obj.get("targets");
		for(Object o : targetArray) {		
			SequencedEntity target = new SequencedEntity();
			target.deserialize((JSONObject) o);
			this.addTarget(target);
		}
		
//		JSONArray eventArray = (JSONArray) obj.get("targets");
//		for(Object o : eventArray) {
//			IEntityEventHandler event = new SingleEntityEventHandler();
//			this.addEvent(event.deserialize(o));
//		}
		
	}

}
