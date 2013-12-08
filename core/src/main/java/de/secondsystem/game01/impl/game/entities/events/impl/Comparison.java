package de.secondsystem.game01.impl.game.entities.events.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.secondsystem.game01.impl.game.entities.IGameEntity;
import de.secondsystem.game01.impl.game.entities.events.IEntityEventHandler.EntityEventType;
import de.secondsystem.game01.impl.map.physics.IHumanoidPhysicsBody;
import de.secondsystem.game01.impl.map.physics.IPhysicsBody;

public class Comparison implements ISequencedObject {
	public class ComparisonOutputOption {
		public final List<ISequencedObject> isOtherHumanoid  = new ArrayList<>();
		public final List<ISequencedObject> isOtherStatic    = new ArrayList<>();
		public final List<ISequencedObject> isOtherKinematic = new ArrayList<>();
		
		public final List<ISequencedObject> isOwnerHumanoid  = new ArrayList<>();
		public final List<ISequencedObject> isOwnerStatic    = new ArrayList<>();
		public final List<ISequencedObject> isOwnerKinematic = new ArrayList<>();
	}
	
	public final ComparisonOutputOption outputOption = new ComparisonOutputOption();
	public final HashMap<IGameEntity, AbstractSequencedEntity> inTrigger = new HashMap<>();
	
	@Override
	public Object handle(EntityEventType type, IGameEntity owner, Object... args) {
		if( !inTrigger.containsKey(owner) )
			return null;
		
		IPhysicsBody other = null;
		if( args.length > 0 && args[0] instanceof IPhysicsBody )
			other = (IPhysicsBody)args[0];
		
		if( other != null && outputOption.isOtherHumanoid.size() > 0 && other instanceof IHumanoidPhysicsBody )
			for( ISequencedObject obj : outputOption.isOtherHumanoid )
				obj.handle(type, owner, args);
	
		if( other != null && outputOption.isOtherStatic.size() > 0 && other.isStatic() )
			for( ISequencedObject obj : outputOption.isOtherStatic )
				obj.handle(type, owner, args);
		
		if( other != null && outputOption.isOtherKinematic.size() > 0 && other.isStatic() )
			for( ISequencedObject obj : outputOption.isOtherKinematic )
				obj.handle(type, owner, args);
		
		if( outputOption.isOwnerHumanoid.size() > 0 && owner.getPhysicsBody() instanceof IHumanoidPhysicsBody )
			for( ISequencedObject obj : outputOption.isOwnerHumanoid )
				obj.handle(type, owner, args);
		
		if( outputOption.isOwnerStatic.size() > 0 && owner.getPhysicsBody().isStatic() )
			for( ISequencedObject obj : outputOption.isOwnerStatic )
				obj.handle(type, owner, args);
		
		if( outputOption.isOwnerKinematic.size() > 0 && owner.getPhysicsBody().isKinematic() )
			for( ISequencedObject obj : outputOption.isOwnerKinematic )
				obj.handle(type, owner, args);
		
		return null;	
	}
	
	@SuppressWarnings("unchecked")
	public <T> void add(SequencedObject obj, List<ISequencedObject> outputList,HashMap<IGameEntity, T> inputMap) {
		inputMap.putAll((Map<? extends IGameEntity, ? extends T>) inTrigger);
		outputList.add(obj);
	}
}
