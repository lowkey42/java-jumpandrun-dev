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
	
	void setFollowWorldSwitch( boolean followWorldSwitch );
	boolean getFollowWorldSwitch();
	
}
