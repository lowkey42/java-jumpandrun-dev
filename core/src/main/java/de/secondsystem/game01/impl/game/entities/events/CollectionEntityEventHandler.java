package de.secondsystem.game01.impl.game.entities.events;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import de.secondsystem.game01.impl.game.entities.IGameEntity;

public class CollectionEntityEventHandler implements IEntityEventHandler {

	private final Map<EntityEventType, IEntityEventHandler> handlers = new HashMap<>();
	
	public void addEntityEventHandler(EntityEventType type, IEntityEventHandler handler) {
		handlers.put(type, handler);
	}
	
	@Override
	public Object handle(EntityEventType type, IGameEntity owner,
			Object... args) {
		IEntityEventHandler handler = handlers.get(type);
		
		return handler!=null ? handler.handle(type, owner, args) : null;
	}

	@Override
	public boolean isHandled(EntityEventType type) {
		return handlers.containsKey(type);
	}

	@Override
	public Set<EntityEventType> getHandled() {
		return handlers.keySet();
	}

}
