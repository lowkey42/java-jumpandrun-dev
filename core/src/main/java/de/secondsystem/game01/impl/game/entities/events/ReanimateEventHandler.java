package de.secondsystem.game01.impl.game.entities.events;

import de.secondsystem.game01.impl.game.entities.IGameEntity;
import de.secondsystem.game01.impl.map.IGameMap;
import de.secondsystem.game01.impl.map.IGameMap.WorldId;
import de.secondsystem.game01.model.Attributes;
import de.secondsystem.game01.model.Attributes.Attribute;

public class ReanimateEventHandler implements IEventHandler {

	public ReanimateEventHandler() {
	}
	
	@Override
	public Object handle(Object... args) {
		final IGameEntity owner = (IGameEntity) args[0];
		
		if( owner.isDead() ) {
			owner.setDead(false);
			owner.setWorld(WorldId.MAIN);
		}
				
		return null;
	}

	@Override
	public Attributes serialize() {
		return new Attributes(new Attribute(EventUtils.FACTORY, EventUtils.normalizeHandlerFactory(ReanEHF.class.getName())));
	}

}

final class ReanEHF implements IEventHandlerFactory {
	@Override
	public ReanimateEventHandler create(IGameMap map, Attributes attributes) {
		return new ReanimateEventHandler();
	}
}
