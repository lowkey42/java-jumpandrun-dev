package de.secondsystem.game01.impl.game.entities.events;

import org.json.simple.JSONObject;

import de.secondsystem.game01.impl.game.entities.IGameEntity;
import de.secondsystem.game01.impl.game.entities.events.IEntityEventHandler.EntityEventType;
import de.secondsystem.game01.impl.game.entities.events.impl.ISequencedObject;
import de.secondsystem.game01.impl.game.entities.events.impl.SequencedObject;
import de.secondsystem.game01.impl.map.IGameMap;

public class SequencedEntityEventHandler extends SingleEntityEventHandler {
	
	private ISequencedObject sequencedObject;
	
	public SequencedEntityEventHandler(EntityEventType eventType, ISequencedObject sequencedObject) {
		super(eventType);
		
		this.sequencedObject = sequencedObject;
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
		
		return obj;
	}
	
	@Override
	public void deserialize(JSONObject obj, IGameMap map) {
		super.deserialize(obj, map);
		
		ISequencedObject seqObj = new SequencedObject();
		seqObj.deserialize(obj, map.getEntityManager(), map.getSequenceManager());
		this.sequencedObject = seqObj;
	}
}
