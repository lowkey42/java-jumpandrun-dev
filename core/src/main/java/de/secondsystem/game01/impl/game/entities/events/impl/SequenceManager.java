package de.secondsystem.game01.impl.game.entities.events.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import de.secondsystem.game01.impl.game.controller.PatrollingController;
import de.secondsystem.game01.impl.game.entities.IGameEntity;
import de.secondsystem.game01.impl.map.IGameMap;

public final class SequenceManager {	
	private final Map<UUID, ISequencedObject> sequencedObjects = new HashMap<>();
	private final Map<UUID, SequencedEntity>  sequencedEntities = new HashMap<>();
	
	public SequenceManager() {
		
	}
	
	public ISequencedObject getSequencedObject(UUID uuid) {
		return sequencedObjects.get(uuid);
	}
	
	public SequencedEntity getSequencedEntity(UUID uuid) {
		return sequencedEntities.get(uuid);
	}
	
	public ISequencedObject createSequencedObject(String className) {
		if( className.equals("Toggle") )
			return new Toggle();
		
		if( className.equals("Playback") )
			return new Playback();
		
		if( className.equals("Condition") )
			return new Condition();
		
		System.out.println("className " + className + " unknown");
		return null;
	}
	
	public Toggle createToggle() {
		Toggle toggle = new Toggle(UUID.randomUUID());
		sequencedObjects.put(toggle.uuid(), toggle);
		
		return toggle;
	}
	
	public Playback createPlayback() {
		Playback playback = new Playback(UUID.randomUUID());
		sequencedObjects.put(playback.uuid(), playback);
		
		return playback;
	}
	
	public Condition createCondition() {
		Condition condition = new Condition(UUID.randomUUID());
		sequencedObjects.put(condition.uuid(), condition);
		
		return condition;
	}
	
	public SequencedEntity createSequencedEntity(String className) {
		if( className.compareTo("AnimatedSequencedEntity") == 0 )
			return new AnimatedSequencedEntity();
		
		if( className.compareTo("ControllableSequencedEntity") == 0 )
			return new ControllableSequencedEntity();
		
		System.out.println("className " + className + " unknown");
		return null;
	}
	
	public AnimatedSequencedEntity createAnimatedSequencedEntity(IGameEntity owner) {
		AnimatedSequencedEntity e = new AnimatedSequencedEntity(UUID.randomUUID(), owner);
		sequencedEntities.put(e.uuid(), e);
		
		return e;
	}
	
	public ControllableSequencedEntity createControllableSequencedEntity(PatrollingController controller) {
		ControllableSequencedEntity e = new ControllableSequencedEntity(UUID.randomUUID(), controller);
		sequencedEntities.put(e.uuid(), e);
		
		return e;
	}
	
	@SuppressWarnings("unchecked")
	public JSONObject serialize() {
		JSONObject obj = new JSONObject();
		
		JSONArray seqObjects = new JSONArray();
		for(ISequencedObject seqObj : sequencedObjects.values()) 
			seqObjects.add(seqObj.serialize());
		
		JSONArray seqEntities = new JSONArray();
		for(SequencedEntity seqObj : sequencedEntities.values()) 
			seqEntities.add(seqObj.serialize());
		
		obj.put("sequencedObjects", seqObjects);
		obj.put("sequencedEntities", seqEntities);
		
		return obj;
	}
	
	public void deserialize(IGameMap map, JSONObject obj) {
		if( obj == null )
			return;
		
		JSONArray seqObjects = (JSONArray) obj.get("sequencedObjects");
		for(Object o : seqObjects) {
			JSONObject jSeqObject = (JSONObject) o;
			ISequencedObject seqObj = createSequencedObject((String) jSeqObject.get("class"));
			ISequencedObject so = seqObj.deserialize(jSeqObject, map);
			sequencedObjects.put(seqObj.uuid(), so != null ? so : seqObj);
		}
		
		JSONArray seqEntities = (JSONArray) obj.get("sequencedEntities");
		for(Object o : seqEntities) {
			JSONObject jSeqEntity = (JSONObject) o;
			SequencedEntity seqEntity = createSequencedEntity((String) jSeqEntity.get("class"));
			SequencedEntity se = seqEntity.deserialize(jSeqEntity, map);
			sequencedEntities.put(seqEntity.uuid(), se != null ? se : seqEntity);
		}
	}
}
