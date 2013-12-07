package de.secondsystem.game01.impl.game.entities.events.impl;

import de.secondsystem.game01.impl.game.entities.IGameEntity;
import de.secondsystem.game01.impl.game.entities.events.IEntityEventHandler.EntityEventType;

public interface ISequencedObject {
	Object handle(EntityEventType type, IGameEntity owner);
}
