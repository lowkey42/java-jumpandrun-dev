package de.secondsystem.game01.impl.game.entities.events;

import de.secondsystem.game01.impl.game.entities.IGameEntity;
import de.secondsystem.game01.impl.map.IGameMap;
import de.secondsystem.game01.model.Attributes;
import de.secondsystem.game01.model.Attributes.Attribute;

public class ReplacePhysicsBodyEventHandler implements IEventHandler {

	private final Attributes bodyArgs;
	
	public ReplacePhysicsBodyEventHandler(IGameMap map, Attributes attributes) {
		bodyArgs = attributes.getObject("body");
	}

	@Override
	public Object handle(Object... args) {
		if( bodyArgs==null )
			((IGameEntity)args[0]).setPhysicsBody(null);
		else
			((IGameEntity)args[0]).setPhysicsBodyFromAttributes(bodyArgs);
		
		return true;
	}
	
	@Override
	public Attributes serialize() {
		return new Attributes(
				new Attribute(EventUtils.FACTORY, EventUtils.normalizeHandlerFactory(ReplacePhysicsBodyEHF.class.getName())),
				new Attribute("body", bodyArgs)
		);
	}
	
}

final class ReplacePhysicsBodyEHF implements IEventHandlerFactory {
	@Override
	public ReplacePhysicsBodyEventHandler create(IGameMap map, Attributes attributes) {
		return new ReplacePhysicsBodyEventHandler(map, attributes);
	}
}
