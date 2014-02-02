package de.secondsystem.game01.impl.game.entities.events;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import de.secondsystem.game01.impl.game.entities.IWeakGameEntityRef;
import de.secondsystem.game01.impl.map.IGameMap;
import de.secondsystem.game01.model.Attributes;
import de.secondsystem.game01.model.Attributes.Attribute;
import de.secondsystem.game01.model.Attributes.AttributeIfNotNull;

/**
 * Redirects an event to another slot and entity
 * The redirected event targets the configured entity or the source-entity (args[1])
 * @author lowkey
 *
 */
public final class PingPongEventHandler implements IEventHandler {

	private final EventType out;
	
	private final IEventHandler handler;
	
	private final IWeakGameEntityRef targetEntity;

	public PingPongEventHandler(EventType out) {
		this(out, null);
	}
	public PingPongEventHandler(EventType out, IWeakGameEntityRef targetEntity) {
		this.out = out;
		this.handler = null;
		this.targetEntity = targetEntity;
	}
	public PingPongEventHandler(IEventHandler handler) {
		this(handler, null);
	}
	public PingPongEventHandler(IEventHandler handler, IWeakGameEntityRef targetEntity) {
		this.out = null;
		this.handler = handler;
		this.targetEntity = targetEntity;
	}
	
	public PingPongEventHandler(IGameMap map, Attributes attributes) {
		this.out = attributes.getString("out")==null ? null : EventType.valueOf(attributes.getString("out"));
		
		Attributes handlerAttributes = attributes.getObject("sub");
		this.handler = handlerAttributes==null ? null : EventUtils.createEventHandler(map, handlerAttributes);
		
		final String targetUuid = attributes.getString("target");
		this.targetEntity = targetUuid!=null ? map.getEntityManager().getRef(UUID.fromString(targetUuid)) : null;
	}
	
	@Override
	public Object handle(Object... args) {
		List<Object> newArgs = new ArrayList<>(Arrays.asList(args));
		Object target = targetEntity==null ? args[1] : targetEntity.get();

		newArgs.set(0, target);
		
		if( targetEntity==null )
			newArgs.set(1, args[0]);		
		
		
		if( handler!=null )
			return handler.handle(newArgs.toArray());
		
		else
			return ((IEventHandlerCollection)target).notify(out, newArgs.toArray());
	}
	
	@Override
	public Attributes serialize() {
		return new Attributes(
				new Attribute(EventUtils.FACTORY, EventUtils.normalizeHandlerFactory(PPEHF.class.getName())), 
				new AttributeIfNotNull("out", 		out==null ? null : out.name()),
				new AttributeIfNotNull("sub", 		handler==null ? null : handler.serialize()),
				new AttributeIfNotNull("target", 	targetEntity==null ? null : targetEntity.uuid().toString())
		);
	}
	
}

final class PPEHF implements IEventHandlerFactory {
	@Override
	public PingPongEventHandler create(IGameMap map, Attributes attributes) {
		return new PingPongEventHandler(map, attributes);
	}
}
