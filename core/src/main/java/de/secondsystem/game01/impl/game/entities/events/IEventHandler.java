package de.secondsystem.game01.impl.game.entities.events;

import de.secondsystem.game01.impl.map.IGameMap;
import de.secondsystem.game01.model.Attributes;
import de.secondsystem.game01.model.ISerializable;

public interface IEventHandler extends ISerializable {

	Object handle( Object... args );
	
}

interface IEventHandlerFactory {
	IEventHandler create(IGameMap map, Attributes attributes);
}