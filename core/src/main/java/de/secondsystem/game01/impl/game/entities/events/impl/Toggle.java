package de.secondsystem.game01.impl.game.entities.events.impl;

import java.util.HashMap;
import java.util.UUID;

import org.json.simple.JSONObject;

import de.secondsystem.game01.impl.game.entities.IGameEntity;
import de.secondsystem.game01.impl.game.entities.events.EventType;
import de.secondsystem.game01.impl.map.IGameMap;

public class Toggle extends SequencedObject {

	public class ToggleInputOption {
		public final HashMap<IGameEntity, IToggled> onTriggers     = new HashMap<>();
		public final HashMap<IGameEntity, IToggled> offTriggers    = new HashMap<>(); 
		public final HashMap<IGameEntity, IToggled> toggleTriggers = new HashMap<>();
	}
	
	public final ToggleInputOption inputOption = new ToggleInputOption();	
	
	public Toggle(UUID uuid) {
		super(uuid);
	}
	
	public Toggle() {
	}
	
	@Override
	public Object handle(EventType type, IGameEntity owner, Object... args) {
		super.handle(type, owner, args);
		
		if( inputOption.onTriggers.get(owner) != null ) 
			for( IToggled target : targets )
				target.onTurnOn();
		
		if( inputOption.offTriggers.get(owner) != null ) 
			for( IToggled target : targets )
				target.onTurnOff();
		
		if( inputOption.toggleTriggers.get(owner) != null ) 
			for( IToggled target : targets )
				target.onToggle();
		
		return null;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public JSONObject serialize() {
		JSONObject obj = super.serialize();
		
		obj.put("onTriggers", serializeTriggers(inputOption.onTriggers));
		obj.put("offTriggers", serializeTriggers(inputOption.offTriggers));
		obj.put("toggleTriggers", serializeTriggers(inputOption.toggleTriggers));
		obj.put("class", "Toggle");
		
		return obj;
	}
	
	@Override
	public ISequencedObject deserialize(JSONObject obj, IGameMap map) {
		ISequencedObject seqObj = super.deserialize(obj, map);
		if( seqObj != null )
			return seqObj;
		
		deserializeTriggers(inputOption.onTriggers, obj, "onTriggers", map);
		deserializeTriggers(inputOption.offTriggers, obj, "offTriggers", map);
		deserializeTriggers(inputOption.toggleTriggers, obj, "toggleTriggers", map);
		
		return null;
	}
}
