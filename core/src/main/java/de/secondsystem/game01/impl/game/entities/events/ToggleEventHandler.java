package de.secondsystem.game01.impl.game.entities.events;

import de.secondsystem.game01.impl.map.IGameMap;
import de.secondsystem.game01.model.Attributes;
import de.secondsystem.game01.model.Attributes.Attribute;

public class ToggleEventHandler implements IEventHandler {

	private boolean enabled;
	
	public ToggleEventHandler(boolean initialState) {
		enabled = initialState;
	}
	public ToggleEventHandler(IGameMap map, Attributes attributes) {
		this( attributes.getBoolean("enabled", false) );
	}
	
	@Override
	public Object handle(Object... args) {
		final IEventHandlerCollection target = (IEventHandlerCollection) args[0];
		
		enabled = !enabled;
		
		return target.notify( enabled ? EventType.ENABLED : EventType.DISABLED, args);
	}

	@Override
	public Attributes serialize() {
		return new Attributes(
				new Attribute(EventUtils.FACTORY, EventUtils.normalizeHandlerFactory(ToggleEHF.class.getName())),
				new Attribute("enabled", enabled)
		);
	}
	
}

final class ToggleEHF implements IEventHandlerFactory {
	@Override
	public ToggleEventHandler create(IGameMap map, Attributes attributes) {
		return new ToggleEventHandler(map, attributes);
	}
}
