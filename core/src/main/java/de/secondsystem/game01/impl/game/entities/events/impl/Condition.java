package de.secondsystem.game01.impl.game.entities.events.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import de.secondsystem.game01.impl.game.entities.IGameEntity;
import de.secondsystem.game01.impl.game.entities.IGameEntityManager;
import de.secondsystem.game01.impl.game.entities.events.IEntityEventHandler.EntityEventType;
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
		
		System.out.println(obj);
		
		return obj;
	}

	@Override
	public void deserialize(JSONObject obj, IGameEntityManager entityManager) {
		JSONObject inTriggers = (JSONObject) obj.get("inTriggers");
		for(Object o : inTriggers.keySet()) {
			IGameEntity entity = entityManager.get((UUID) o);
			SequencedEntity seqEntity = new SequencedEntity();
			seqEntity.deserialize((JSONObject)inTriggers.get(o));
			this.inTriggers.put(entity, seqEntity);
		}
		
		deserializeOutputOptionLinks("isOtherHumanoid", outputOption.isOtherHumanoid, obj, entityManager);
		deserializeOutputOptionLinks("isOtherKinematic", outputOption.isOtherKinematic, obj, entityManager);
		deserializeOutputOptionLinks("isOtherStatic", outputOption.isOtherStatic, obj, entityManager);
		
		deserializeOutputOptionLinks("isOwnerHumanoid", outputOption.isOwnerHumanoid, obj, entityManager);
		deserializeOutputOptionLinks("isOwnerKinematic", outputOption.isOwnerKinematic, obj, entityManager);
		deserializeOutputOptionLinks("isOwnerStatic", outputOption.isOwnerStatic, obj, entityManager);
	}
	
	private void deserializeOutputOptionLinks(String outputOption, List<ISequencedObject> outputOptionLinks, 
			JSONObject obj, IGameEntityManager entityManager) {
		
		JSONArray jArray = (JSONArray) obj.get(outputOption);
		
		for(Object e : jArray) {
			ISequencedObject linkedOutputObject = new SequencedObject();
			linkedOutputObject.deserialize((JSONObject) e, entityManager);
			outputOptionLinks.add(linkedOutputObject);
		}
	}
	
}
