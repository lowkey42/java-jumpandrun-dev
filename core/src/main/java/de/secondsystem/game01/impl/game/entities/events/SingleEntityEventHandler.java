package de.secondsystem.game01.impl.game.entities.events;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;

import org.json.simple.JSONObject;

import de.secondsystem.game01.impl.map.IGameMap;

public abstract class SingleEntityEventHandler implements IEntityEventHandler {

	protected EntityEventType eventType;
	
	protected UUID uuid;
	
	public SingleEntityEventHandler(UUID uuid, EntityEventType eventType) {
		this.eventType = eventType;
		this.uuid = uuid;
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
		obj.put("uuid", uuid.toString());
		
		return obj;
	}
	
	@Override
	public IEntityEventHandler deserialize(JSONObject obj, IGameMap map) {
		uuid = UUID.fromString( (String) obj.get("uuid"));
		IEntityEventHandler eventHandler = map.getEventManager().get(uuid);
		if( eventHandler != null )
			return eventHandler;
		
		this.eventType = EntityEventType.valueOf( (String) obj.get("eventType") );
		
		return null;
	}
	
	@Override
	public UUID uuid() {
		return uuid;
	}
	
	public EntityEventType getType() {
		return eventType;
	}

}
