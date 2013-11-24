package de.secondsystem.game01.impl.game.entities;

import java.io.IOException;

import de.secondsystem.game01.impl.ResourceManager;
import de.secondsystem.game01.impl.graphic.AnimatedSprite;
import de.secondsystem.game01.impl.map.IGameMap;
import de.secondsystem.game01.impl.map.physics.CollisionHandlerType;
import de.secondsystem.game01.impl.map.physics.IPhysicsBody;
import de.secondsystem.game01.model.Attributes;
import de.secondsystem.game01.model.IDrawable;

final class GameEntityHelper {

	public static IDrawable createRepresentation( Attributes attributes ) { // TODO
		AnimatedSprite repr;
		try {
			repr = new AnimatedSprite(ResourceManager.animation.get("dude.anim"), 50.f, 55.f);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return repr;
	}
	public static IPhysicsBody createPhysicsBody( IGameMap map, boolean jumper, boolean 
			canPickUpObjects, boolean liftable, boolean createTestFixture, Attributes attributes ) {
		return map.getPhysicalWorld().createBody(attributes.getInteger("worldId", map.getActiveGameWorldId()),
				attributes.getFloat("x"), 
				attributes.getFloat("y"), 
				attributes.getFloat("width"), 
				attributes.getFloat("height"), 
				attributes.getFloat("rotation", 0), 
				false, CollisionHandlerType.SOLID, jumper, canPickUpObjects, createTestFixture, liftable);
	}
	
	private GameEntityHelper() {}
}
