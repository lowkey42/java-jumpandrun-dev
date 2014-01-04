package de.secondsystem.game01.impl.game.entities.events;

import de.secondsystem.game01.model.Attributes;

public interface IEventHandlerCollection {

	Object notify(EventType type, Object... args);

	Attributes serialize();

	void removeEventHandler(EventType type);

	void setEventHandler(EventType type, IEventHandler handler);

	void addEventHandler(EventType type, IEventHandler handler);

}