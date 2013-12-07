package de.secondsystem.game01.impl.game.entities.events.impl;

import de.secondsystem.game01.impl.game.entities.IGameEntity;

public interface ISequencedEntity extends IToggle {
	void setOwner(IGameEntity owner);
}
