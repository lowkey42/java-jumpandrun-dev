package de.secondsystem.game01.impl.game.entities.events.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import de.secondsystem.game01.impl.game.entities.IGameEntity;
import de.secondsystem.game01.impl.game.entities.events.IEntityEventHandler.EntityEventType;
import de.secondsystem.game01.impl.map.IGameMap;
import de.secondsystem.game01.impl.map.physics.IHumanoidPhysicsBody;
import de.secondsystem.game01.impl.map.physics.IPhysicsBody;

public class Condition implements ISequencedObject {
	public class ComparisonOutputOption {
		public final List<ISequencedObject> isOtherHumanoid  = new ArrayList<>();
		public final List<ISequencedObject> isOtherStatic    = new ArrayList<>();
		public final List<ISequencedObject> isOtherKinematic = new ArrayList<>();
		
		public final List<ISequencedObject> isOwnerHumanoid  = new ArrayList<>();
		public final List<ISequencedObject> isOwnerStatic    = new ArrayList<>();
		public final List<ISequencedObject> isOwnerKinematic = new ArrayList<>();
	}
	
	public final ComparisonOutputOption outputOption = new ComparisonOutputOption();
	public final HashMap<IGameEntity, SequencedEntity> inTriggers = new HashMap<>();
	protected UUID uuid;
	
	public Condition(UUID uuid) {
		this.uuid = uuid;
	}
	
	public Condition() {
	}
	
	@Override
	public Object handle(EntityEventType type, IGameEntity owner, Object... args) {
		if( !inTriggers.containsKey(owner) )
			return null;
		
		IPhysicsBody other = null;
		if( args.length > 0 && args[0] instanceof IPhysicsBody )
			other = (IPhysicsBody)args[0];
		
		handleCondition(outputOption.isOtherHumanoid, other instanceof IHumanoidPhysicsBody, type, owner, args);
		handleCondition(outputOption.isOtherStatic, other != null && other.isStatic(), type, owner, args);
		handleCondition(outputOption.isOtherKinematic, other != null && other.isKinematic(), type, owner, args);
	
		handleCondition(outputOption.isOwnerHumanoid, owner.getPhysicsBody() instanceof IHumanoidPhysicsBody, type, owner, args);
		handleCondition(outputOption.isOwnerStatic, owner.getPhysicsBody().isStatic(), type, owner, args);		
		handleCondition(outputOption.isOwnerKinematic, owner.getPhysicsBody().isKinematic(), type, owner, args);
		
		return null;	
	}
	
	private void handleCondition(List<ISequencedObject> outputOptionLinks, boolean condition, EntityEventType type, IGameEntity owner, Object... args) {
		if( condition )
			for( ISequencedObject obj : outputOptionLinks )
				obj.handle(type, owner, args);
	}
	
	@SuppressWarnings("unchecked")
	public <T> void add(SequencedObject obj, List<ISequencedObject> outputList, HashMap<IGameEntity, T> inputMap) {
		inputMap.putAll((Map<? extends IGameEntity, ? extends T>) inTriggers);
		outputList.add(obj);
	}
	
	@SuppressWarnings("unchecked")
	private JSONArray serializeOutputOptionLinks(List<ISequencedObject> outputOption) {
		JSONArray outputLinks = new JSONArray();
		for(ISequencedObject seqObj : outputOption)
			outputLinks.add(seqObj.serialize());
		
		return outputLinks;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public JSONObject serialize() {
		JSONObject obj = new JSONObject();
		obj.put("uuid", uuid.toString());
		
		JSONObject inTriggers = new JSONObject();
		for( IGameEntity entity : this.inTriggers.keySet() )
			inTriggers.put(entity.uuid(), ((SequencedEntity)this.inTriggers.get(entity)).serialize());
		obj.put("inTriggers", inTriggers);
		
		obj.put("isOtherHumanoid", serializeOutputOptionLinks(outputOption.isOtherHumanoid));
		obj.put("isOtherKinematic", serializeOutputOptionLinks(outputOption.isOtherKinematic));
		obj.put("isOtherStatic", serializeOutputOptionLinks(outputOption.isOtherStatic));
		
		obj.put("isOwnerHumanoid", serializeOutputOptionLinks(outputOption.isOwnerHumanoid));
		obj.put("isOwnerKinematic", serializeOutputOptionLinks(outputOption.isOwnerKinematic));
		obj.put("isOwnerStatic", serializeOutputOptionLinks(outputOption.isOwnerStatic));
		
		obj.put("class", "Condition");
		
		return obj;
	}

	@Override
	public ISequencedObject deserialize(JSONObject obj, IGameMap map) {
		UUID uuid = UUID.fromString( (String) obj.get("uuid") );
		ISequencedObject seqObj = map.getSequenceManager().getSequencedObject(uuid);
		if( seqObj != null )
			return seqObj;
		
		this.uuid = uuid;
		SequencedObject.deserializeTriggers(this.inTriggers, obj, "inTriggers", map);
		
		deserializeOutputOptionLinks("isOtherHumanoid", outputOption.isOtherHumanoid, obj, map);
		deserializeOutputOptionLinks("isOtherKinematic", outputOption.isOtherKinematic, obj, map);
		deserializeOutputOptionLinks("isOtherStatic", outputOption.isOtherStatic, obj, map);
		
		deserializeOutputOptionLinks("isOwnerHumanoid", outputOption.isOwnerHumanoid, obj, map);
		deserializeOutputOptionLinks("isOwnerKinematic", outputOption.isOwnerKinematic, obj, map);
		deserializeOutputOptionLinks("isOwnerStatic", outputOption.isOwnerStatic, obj, map);
		
		return null;
	}
	
	private void deserializeOutputOptionLinks(String outputOption, List<ISequencedObject> outputOptionLinks, JSONObject obj, IGameMap map) {
		
		JSONArray jArray = (JSONArray) obj.get(outputOption);
		
		if( jArray == null )
			System.out.println("outputOption " + outputOption + " is null");
		
		for(Object e : jArray) {
			JSONObject jSeqObject = (JSONObject) e;
			ISequencedObject linkedOutputObject = map.getSequenceManager().createSequencedObject((String) jSeqObject.get("class"));	
			ISequencedObject lo = linkedOutputObject.deserialize(jSeqObject, map);
			outputOptionLinks.add(lo != null ? lo : linkedOutputObject);
		}
	}

	@Override
	public UUID uuid() {
		return uuid;
	}
	
}
