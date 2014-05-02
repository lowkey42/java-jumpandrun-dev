package de.secondsystem.game01.impl.game.entities.events;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import de.secondsystem.game01.impl.map.IGameMap;
import de.secondsystem.game01.model.Attributes;
import de.secondsystem.game01.model.Tuple;

public class EventHandlerCollection implements IEventHandlerCollection {

	private static final String EVENT_PREFIX = "on";
	
	private final Map<EventType, List<IEventHandler>> handlers = new HashMap<>();
	
	private final Set<Tuple<EventType, IEventHandler>> deleteRequests = new HashSet<>();
	
	@SuppressWarnings("unchecked")
	public EventHandlerCollection(IGameMap map, Attributes attributes) {
		if( attributes!=null ) {
			for( EventType type : EventType.values() ) {
				List<IEventHandler> handlerList;
				handlers.put(type, handlerList=new ArrayList<>());
				
				final Object h = attributes.get(EVENT_PREFIX+type.name());
				if( h instanceof List )
					for( Object e : (List<?>) h )
						handlerList.add(EventUtils.createEventHandler(map, new Attributes((Map<String, Object>)e )));
				
				else if( h instanceof Map )
					handlerList.add(EventUtils.createEventHandler(map, new Attributes((Map<String, Object>)h )));
			}
		}
	}
	
	@Override
	public Object notify(EventType type, Object... args) {
		for( Tuple<EventType, IEventHandler> r : deleteRequests ) {
			if( r.b==null )
				handlers.remove(r.a);
			else {
				List<IEventHandler> h = handlers.get(type);
				
				if( h!=null )
					h.remove(r.b);
			}
		}
		
		Collection<IEventHandler> handlers = this.handlers.get(type);
		
		Object returnValue = null;
		
		if( handlers!=null )
			for( IEventHandler h : handlers ) {
				Object r = h.handle(args);
				if( returnValue==null )
					returnValue = r; // returns only the first return value (questionable)
			}
		
		return returnValue;
	}

	@Override
	public void addEventHandler(EventType type, IEventHandler handler) {
		List<IEventHandler> h = handlers.get(type);
		if( h==null )
			handlers.put(type, h=new ArrayList<>(1));
			
		h.add(handler);
	}

	@Override
	public void setEventHandler(EventType type, IEventHandler handler) {
		handlers.remove(type);
		addEventHandler(type, handler);
	}

	@Override
	public void clearEventHandlers() {
		handlers.clear();
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
		
		for( Entry<EventType, List<IEventHandler>> handler : handlers.entrySet() ) {
			List<Attributes> sub = new ArrayList<>(handler.getValue().size());
			for( IEventHandler h : handler.getValue() )
				sub.add(h.serialize());
				
			attr.put( handler.getKey().name(), sub );
		}
		
		return attr;
	}
	
}
