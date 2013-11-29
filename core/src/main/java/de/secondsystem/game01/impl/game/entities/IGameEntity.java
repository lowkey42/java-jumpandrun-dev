package de.secondsystem.game01.impl.game.entities;

import java.util.UUID;

import de.secondsystem.game01.impl.map.ICameraController;
import de.secondsystem.game01.model.IDrawable;
import de.secondsystem.game01.model.IUpdateable;

/**
 * All objects in a map (player, enemies, triggers)
 * @author lowkey
 *
 */
public interface IGameEntity extends IUpdateable, IDrawable, ICameraController {

	UUID uuid();
	
	int getWorldId();
	void setWorldId(int newWorldId);

	void onUsed();
	float onUsedDraged(float force);
	void onLifted(IGameEntity liftingEntity);
	void onUnlifted(IGameEntity unliftingEntity);
	void onViewed();
	void onUnviewed();
}
