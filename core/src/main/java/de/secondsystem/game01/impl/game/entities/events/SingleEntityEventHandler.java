package de.secondsystem.game01.impl.game.entities.events;

import java.util.Collections;
import java.util.Set;

import org.json.simple.JSONObject;

import de.secondsystem.game01.impl.map.IGameMap;

public abstract class SingleEntityEventHandler implements IEntityEventHandler {

	protected EntityEventType eventType;
	
	public SingleEntityEventHandler(EntityEventType eventType) {
		this.eventType = eventType;
	}
	
	public SingleEntityEventHandler() {
	}
	
	@Override
	public final boolean isHandled(EntityEventType type) {
		return eventType==type;
	}

	@Override
	public final Set<EntityEventType> getHandled() {
		return Collections.singleton(eventType);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public JSONObject serialize() {
		JSONObject obj = new JSONObject();
		obj.put("eventType", eventType.toString());
		
		return obj;
	}
	
	@Override
	public void deserialize(JSONObject obj, IGameMap map) {
		this.eventType = EntityEventType.valueOf( (String) obj.get("eventType") );
	}

}
