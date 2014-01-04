package de.secondsystem.game01.impl.game.entities.events;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.secondsystem.game01.impl.map.IGameMap;
import de.secondsystem.game01.model.Attributes;
import de.secondsystem.game01.model.Attributes.Attribute;

public final class PingPongEventHandler implements IEventHandler {

	private final EventType out;
	
	public PingPongEventHandler(EventType out) {
		this.out = out;
	}
	
	public PingPongEventHandler(IGameMap map, Attributes attributes) {
		this.out = EventType.valueOf(attributes.getString("out"));
	}
	
	@Override
	public Object handle(Object... args) {
		List<Object> newArgs = new ArrayList<>(Arrays.asList(args));
		newArgs.set(0, args[1]);
		newArgs.set(1, args[0]);
		
		return ((IEventHandlerCollection)args[1]).notify(out, newArgs.toArray());
	}
	
	@Override
	public Attributes serialize() {
		return new Attributes(
				new Attribute(EventUtils.FACTORY, EventUtils.normalizeHandlerFactory(PPEHF.class.getName())), 
				new Attribute("out", out.name()));
	}
	
}

final class PPEHF implements IEventHandlerFactory {
	@Override
	public PingPongEventHandler create(IGameMap map, Attributes attributes) {
		return new PingPongEventHandler(map, attributes);
	}
}
