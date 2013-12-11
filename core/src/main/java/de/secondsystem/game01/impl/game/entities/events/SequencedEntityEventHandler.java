package de.secondsystem.game01.impl.game.entities.events;

import java.util.UUID;

import org.json.simple.JSONObject;

import de.secondsystem.game01.impl.game.entities.IGameEntity;
import de.secondsystem.game01.impl.game.entities.events.impl.ISequencedObject;
import de.secondsystem.game01.impl.map.IGameMap;

public class SequencedEntityEventHandler extends SingleEntityEventHandler {
	
	private ISequencedObject sequencedObject;
	
	public SequencedEntityEventHandler(UUID uuid, EntityEventType eventType, ISequencedObject sequencedObject) {
		super(uuid, eventType);
		
		this.sequencedObject = sequencedObject;
	}
	
	public SequencedEntityEventHandler() {
	}
	
	@Override
	public Object handle(EntityEventType type, IGameEntity owner, Object... args) {
		return sequencedObject.handle(type, owner, args);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public JSONObject serialize() {
		JSONObject obj = super.serialize();
		
		obj.put("sequencedObject", sequencedObject.serialize());
		obj.put("class", "SequencedEntityEventHandler");
		
		return obj;
	}
	
	@Override
	public IEntityEventHandler deserialize(JSONObject obj, IGameMap map) {
		IEntityEventHandler eventHandler = super.deserialize(obj, map);
		if( eventHandler != null )
			return eventHandler;
		
		JSONObject jSeqObject = (JSONObject) obj.get("sequencedObject");
		ISequencedObject seqObj = map.getSequenceManager().createSequencedObject((String) jSeqObject.get("class"));	
		ISequencedObject so = seqObj.deserialize(jSeqObject, map);
		this.sequencedObject = so != null ? so : seqObj;
		
		map.getEventManager().add(this);
		
		return null;
	}
}
