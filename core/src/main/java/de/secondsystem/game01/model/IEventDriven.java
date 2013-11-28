package de.secondsystem.game01.model;

import de.secondsystem.game01.impl.game.entities.IGameEntity;

public interface IEventDriven {
	void  onUsed();
	float onUsedDraged(float force);
	void  onLifted(IGameEntity liftingEntity);
	void  onUnlifted(IGameEntity unliftingEntity);
	void  onViewed();
	void  onUnviewed();
}
