package de.secondsystem.game01.impl.game.entities.events;

import java.util.Collection;
import java.util.Set;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

import de.secondsystem.game01.impl.game.entities.IGameEntity;

public class CollectionEntityEventHandler implements IEntityEventHandler {

	private final ListMultimap<EntityEventType, IEntityEventHandler> handlers = ArrayListMultimap.create();
	
	public void addEntityEventHandler(EntityEventType type, IEntityEventHandler handler) {
		handlers.put(type, handler);
	}
	
	@Override
	public Object handle(EntityEventType type, IGameEntity owner,
			Object... args) {
		Collection<IEntityEventHandler> handler = handlers.get(type);
		
		Object returnValue = null;
		
		for( IEntityEventHandler h : handler ) {
			Object r = h.handle(type, owner, args);
			if( returnValue==null )
				returnValue = r;
		}
		
		return returnValue;
	}

	@Override
	public boolean isHandled(EntityEventType type) {
		return handlers.containsKey(type);
	}

	@Override
	public Set<EntityEventType> getHandled() {
		return handlers.keySet();
	}

	@Override
	public Object handle(EntityEventType type, Object... args) {
		Collection<IEntityEventHandler> handler = handlers.get(type);
		
		Object returnValue = null;
		
		for( IEntityEventHandler h : handler ) {
			Object r = h.handle(type, args);
			if( returnValue==null )
				returnValue = r;
		}
		
		return returnValue;
	}

}
