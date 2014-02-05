package de.secondsystem.game01.impl.game.entities.events;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Collections2;
import com.google.common.collect.ListMultimap;

import de.secondsystem.game01.impl.map.IGameMap;
import de.secondsystem.game01.model.Attributes;
import de.secondsystem.game01.model.Tuple;

public class EventHandlerCollection implements IEventHandlerCollection {

	private static final String EVENT_PREFIX = "on";
	
	private final ListMultimap<EventType, IEventHandler> handlers = ArrayListMultimap.create();
	
	private final Set<Tuple<EventType, IEventHandler>> deleteRequests = new HashSet<>();
	
	@SuppressWarnings("unchecked")
	public EventHandlerCollection(IGameMap map, Attributes attributes) {
		if( attributes!=null ) {
			for( EventType type : EventType.values() ) {
				final Object h = attributes.get(EVENT_PREFIX+type.name());
				if( h instanceof List )
					for( Object e : (List<?>) h )
						handlers.put(type, EventUtils.createEventHandler(map, new Attributes((Map<String, Object>)e )));
				
				else if( h instanceof Map )
					handlers.put(type, EventUtils.createEventHandler(map, new Attributes((Map<String, Object>)h )));
			}
		}
	}
	
	@Override
	public Object notify(EventType type, Object... args) {
		for( Tuple<EventType, IEventHandler> r : deleteRequests ) {
			if( r.b==null )
				handlers.removeAll(r.a);
			else
				handlers.remove(type, r.b);
		}
		
		Collection<IEventHandler> handlers = this.handlers.get(type);
		
		Object returnValue = null;
		
		for( IEventHandler h : handlers ) {
			Object r = h.handle(args);
			if( returnValue==null )
				returnValue = r; // returns only the first return value (questionable)
		}
		
		return returnValue;
	}

	@Override
	public void addEventHandler(EventType type, IEventHandler handler) {
		handlers.put(type, handler);
	}

	@Override
	public void setEventHandler(EventType type, IEventHandler handler) {
		handlers.removeAll(type);
		addEventHandler(type, handler);
	}

	@Override
	public void removeEventHandler(EventType type) {
		deleteRequests.add(new Tuple<EventType, IEventHandler>(type, null));
	}

	@Override
	public void removeEventHandler(EventType type, IEventHandler handler) {
		deleteRequests.add(new Tuple<EventType, IEventHandler>(type, handler));
	}

	/* (non-Javadoc)
	 * @see de.secondsystem.game01.impl.game.entities.events.IEventHandlerCollection#serialize()
	 */
	@Override
	public Attributes serialize() {
		Attributes attr = new Attributes();
		
		for( Entry<EventType, Collection<IEventHandler>> handler : handlers.asMap().entrySet() ) {
			attr.put( handler.getKey().name(), Collections2.transform(handler.getValue(), EventUtils.HANDLER_SERIALIZER) );
		}
		
		return attr;
	}
	
}
