package de.secondsystem.game01.impl.game.entities.events;

import de.secondsystem.game01.model.Attributes;

public interface IEventHandlerCollection {

	Object notify(EventType type, Object... args);

	Attributes serialize();

}