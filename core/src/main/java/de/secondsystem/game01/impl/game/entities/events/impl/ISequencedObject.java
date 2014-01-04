package de.secondsystem.game01.impl.game.entities.events.impl;

import java.util.UUID;

import de.secondsystem.game01.impl.game.entities.IGameEntity;
import de.secondsystem.game01.impl.map.IGameMap;
import de.secondsystem.game01.model.Attributes;

public interface ISequencedObject {
	Object handle(IGameEntity owner, Object... args);
	Attributes serialize();

	/**
	 * @return Null if this ISequencedObject does not exist, returns the existing ISequencedObject otherwise.
	 */
	ISequencedObject deserialize(Attributes jSeqObject, IGameMap map);
	
	UUID uuid();
}
