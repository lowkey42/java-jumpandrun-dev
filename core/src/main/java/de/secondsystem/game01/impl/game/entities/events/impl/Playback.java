package de.secondsystem.game01.impl.game.entities.events.impl;

import java.util.HashMap;
import java.util.UUID;

import org.json.simple.JSONObject;

import de.secondsystem.game01.impl.game.entities.IGameEntity;
import de.secondsystem.game01.impl.game.entities.IGameEntityManager;
import de.secondsystem.game01.impl.game.entities.events.IEntityEventHandler.EntityEventType;

public class Playback extends SequencedObject {

	public class PlaybackInputOption {
		public final HashMap<IGameEntity, IPlayedBack> playTriggers     = new HashMap<>();
		public final HashMap<IGameEntity, IPlayedBack> reverseTriggers  = new HashMap<>();
		public final HashMap<IGameEntity, IPlayedBack> stopTriggers     = new HashMap<>();
		public final HashMap<IGameEntity, IPlayedBack> pauseTriggers    = new HashMap<>();
		public final HashMap<IGameEntity, IPlayedBack> resumeTriggers   = new HashMap<>();
	}
	
	public final PlaybackInputOption inputOption = new PlaybackInputOption();
	
	public Playback(UUID uuid) {
		super(uuid);
	}
	
	@Override
	public Object handle(EntityEventType type, IGameEntity owner, Object... args) {
		super.handle(type, owner, args);
		
		if( inputOption.playTriggers.get(owner) != null ) 
			for( SequencedEntity target : targets )
				((IPlayedBack)target).onPlay();
		
		if( inputOption.reverseTriggers.get(owner) != null ) 	
			for( SequencedEntity target : targets )
				((IPlayedBack)target).onReverse();
		

		if( inputOption.stopTriggers.get(owner) != null ) 
			for( SequencedEntity target : targets )
				((IPlayedBack)target).onStop();
		
		if( inputOption.pauseTriggers.get(owner) != null ) 
			for( SequencedEntity target : targets )
				((IPlayedBack)target).onPause();
		
		if( inputOption.resumeTriggers.get(owner) != null ) 
			for( SequencedEntity target : targets )
				((IPlayedBack)target).onResume();

		return null;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public JSONObject serialize() {
		JSONObject obj = super.serialize();
		
		obj.put("playTriggers", serializeTriggers(inputOption.playTriggers));
		obj.put("reverseTriggers", serializeTriggers(inputOption.reverseTriggers));
		obj.put("stopTriggers", serializeTriggers(inputOption.stopTriggers));
		obj.put("pauseTriggers", serializeTriggers(inputOption.pauseTriggers));
		obj.put("resumeTriggers", serializeTriggers(inputOption.resumeTriggers));
		
		return obj;	
	}
	
	@Override
	public ISequencedObject deserialize(JSONObject obj, IGameEntityManager entityManager, SequenceManager sequenceManager) {
		ISequencedObject seqObj = super.deserialize(obj, entityManager, sequenceManager);
		if( seqObj != null )
			return seqObj;
		
		deserializeTriggers(inputOption.playTriggers, obj, entityManager, "playTriggers", sequenceManager);
		deserializeTriggers(inputOption.reverseTriggers, obj, entityManager, "reverseTriggers", sequenceManager);
		deserializeTriggers(inputOption.stopTriggers, obj, entityManager, "stopTriggers", sequenceManager);
		deserializeTriggers(inputOption.pauseTriggers, obj, entityManager, "pauseTriggers", sequenceManager);
		deserializeTriggers(inputOption.resumeTriggers, obj, entityManager, "resumeTriggers", sequenceManager);
		
		return null;
	}

}
