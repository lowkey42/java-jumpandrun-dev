package de.secondsystem.game01.impl.game.entities.events;

import de.secondsystem.game01.impl.map.IGameMap;
import de.secondsystem.game01.model.Attributes;
import de.secondsystem.game01.model.Attributes.Attribute;
import de.secondsystem.game01.model.Attributes.AttributeIfNotNull;

public class RepeatEventHandler implements IEventHandler {

	private final long intervalMs;
	
	private final IEventHandler sub;
	
	private long accu;
	
	public RepeatEventHandler(long intervalMs, IEventHandler sub) {
		this.intervalMs = intervalMs;
		this.sub = sub;
	}
	public RepeatEventHandler(IGameMap map, Attributes attributes) {
		this(attributes.getInteger("intervalMs"), EventUtils.createEventHandler(map, attributes.getObject("sub")));
	}

	@Override
	public Object handle(Object... args) {
		accu+=(long) args[1];
		
		if( accu>=intervalMs ) {
			accu%=intervalMs;
			return sub.handle(args);
		} else
			return null;
	}
	
	@Override
	public Attributes serialize() {
		return new Attributes(
				new Attribute(EventUtils.FACTORY, EventUtils.normalizeHandlerFactory(RepeatEHF.class.getName())),
				new AttributeIfNotNull("intervalMs", intervalMs),
				new AttributeIfNotNull("sub", 		 sub.serialize())
		);
	}
	
}

final class RepeatEHF implements IEventHandlerFactory {
	@Override
	public RepeatEventHandler create(IGameMap map, Attributes attributes) {
		return new RepeatEventHandler(map, attributes);
	}
}
