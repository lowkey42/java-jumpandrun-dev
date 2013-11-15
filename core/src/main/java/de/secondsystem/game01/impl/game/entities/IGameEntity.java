package de.secondsystem.game01.impl.game.entities;

import java.util.UUID;

import org.jsfml.graphics.RenderTarget;

/**
 * All objects in a map (player, enemies, triggers)
 * @author lowkey
 *
 */
public interface IGameEntity extends IControllable {

	UUID uuid();
	
	void onFrame( RenderTarget target );
	
	// TODO: position, sprite, physics, linked-GEs, hidden, ???
	
}
