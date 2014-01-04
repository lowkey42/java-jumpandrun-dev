package de.secondsystem.game01.impl.game.entities.events.impl;

import java.util.HashMap;
import java.util.UUID;

import org.json.simple.JSONObject;

import de.secondsystem.game01.impl.game.entities.IGameEntity;
import de.secondsystem.game01.impl.game.entities.events.EventType;
import de.secondsystem.game01.impl.map.IGameMap;

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
	
	public Playback() {
	}
	
	@Override
	public Object handle(EventType type, IGameEntity owner, Object... args) {
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
		obj.put("class", "Playback");
		
		return obj;	
	}
	
	@Override
	public ISequencedObject deserialize(JSONObject obj, IGameMap map) {
		ISequencedObject seqObj = super.deserialize(obj, map);
		if( seqObj != null )
			return seqObj;
		
		deserializeTriggers(inputOption.playTriggers, obj, "playTriggers", map);
		deserializeTriggers(inputOption.reverseTriggers, obj, "reverseTriggers", map);
		deserializeTriggers(inputOption.stopTriggers, obj, "stopTriggers", map);
		deserializeTriggers(inputOption.pauseTriggers, obj, "pauseTriggers", map);
		deserializeTriggers(inputOption.resumeTriggers, obj, "resumeTriggers", map);
		
		return null;
	}

}
