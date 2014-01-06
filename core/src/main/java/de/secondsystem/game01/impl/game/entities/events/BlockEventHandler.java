package de.secondsystem.game01.impl.game.entities.events;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Collections2;

import de.secondsystem.game01.impl.map.IGameMap;
import de.secondsystem.game01.model.Attributes;
import de.secondsystem.game01.model.Attributes.Attribute;
import de.secondsystem.game01.model.Attributes.AttributeIfNotNull;

public final class BlockEventHandler implements IEventHandler {
	
	private final List<IEventHandler> handlers;

	public BlockEventHandler(List<IEventHandler> handlers) {
		this.handlers = handlers;
	}
	public BlockEventHandler(IGameMap map, Attributes attributes) {
		List<Attributes> handlerAttributes = attributes.getObjectList("subs");
		
		handlers = new ArrayList<>(handlerAttributes.size());
		
		for( Attributes attr : handlerAttributes )
			handlers.add( EventUtils.createEventHandler(map, attr) );
	}
	
	@Override
	public Object handle(Object... args) {
		Object returnValue = null;
		
		for( IEventHandler h : handlers ) {
			Object r = h.handle(args);
			if( returnValue==null )
				returnValue = r;
		}
		
		return returnValue;
	}
	
	@Override
	public Attributes serialize() {
		return new Attributes(
				new Attribute(EventUtils.FACTORY, EventUtils.normalizeHandlerFactory(BlockEHF.class.getName())), 
				new AttributeIfNotNull("subs", Collections2.transform(handlers, EventUtils.HANDLER_SERIALIZER))
		);
	}
	
}

final class BlockEHF implements IEventHandlerFactory {
	@Override
	public BlockEventHandler create(IGameMap map, Attributes attributes) {
		return new BlockEventHandler(map, attributes);
	}
}
