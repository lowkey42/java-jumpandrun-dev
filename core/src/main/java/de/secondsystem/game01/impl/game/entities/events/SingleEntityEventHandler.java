package de.secondsystem.game01.impl.game.entities.events;

import java.util.Collections;
import java.util.Set;

import de.secondsystem.game01.impl.game.entities.IGameEntity;

public abstract class SingleEntityEventHandler implements EntityEventHandler {

	private final EntityEventType eventType;
	
	public SingleEntityEventHandler(EntityEventType eventType) {
		this.eventType = eventType;
	}

	@Override
	public abstract Object handle(EntityEventType type, IGameEntity owner, Object... args);

	@Override
	public final boolean isHandled(EntityEventType type) {
		return eventType==type;
	}

	@Override
	public final Set<EntityEventType> getHandled() {
		return Collections.singleton(eventType);
	}

}
