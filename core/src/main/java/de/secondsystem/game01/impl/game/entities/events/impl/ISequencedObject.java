package de.secondsystem.game01.impl.game.entities.events.impl;

import java.util.List;

import de.secondsystem.game01.impl.game.entities.IGameEntity;
import de.secondsystem.game01.impl.game.entities.events.IEntityEventHandler;
import de.secondsystem.game01.impl.game.entities.events.IEntityEventHandler.EntityEventType;

public interface ISequencedObject {
	Object handle(EntityEventType type, IGameEntity owner, List<ISequencedEntity> targets, List<IEntityEventHandler> events);
}
