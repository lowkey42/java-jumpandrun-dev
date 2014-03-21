package de.secondsystem.game01.impl.game.entities.events;

import de.secondsystem.game01.impl.game.entities.IGameEntity;
import de.secondsystem.game01.impl.game.entities.IGameEntityManager;
import de.secondsystem.game01.impl.map.IGameMap;
import de.secondsystem.game01.model.Attributes;
import de.secondsystem.game01.model.Attributes.Attribute;

public class DeleteEventHandler implements IEventHandler {

	private final IGameEntityManager em;
	
	public DeleteEventHandler(IGameEntityManager em) {
		this.em = em;
	}
	public DeleteEventHandler(IGameMap map, Attributes attributes) {
		this(map.getEntityManager());
	}

	@Override
	public Object handle(Object... args) {
		em.destroy(((IGameEntity)args[0]).uuid());
		
		return null;
	}
	
	@Override
	public Attributes serialize() {
		return new Attributes(
				new Attribute(EventUtils.FACTORY, EventUtils.normalizeHandlerFactory(RepeatEHF.class.getName()))
		);
	}
	
}

final class DeleteEHF implements IEventHandlerFactory {
	@Override
	public DeleteEventHandler create(IGameMap map, Attributes attributes) {
		return new DeleteEventHandler(map, attributes);
	}
}
