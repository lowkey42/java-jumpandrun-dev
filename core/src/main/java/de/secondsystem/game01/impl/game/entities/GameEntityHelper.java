package de.secondsystem.game01.impl.game.entities;

import java.io.IOException;

import de.secondsystem.game01.impl.ResourceManager;
import de.secondsystem.game01.impl.game.entities.events.EntityEventHandler;
import de.secondsystem.game01.impl.graphic.AnimatedSprite;
import de.secondsystem.game01.impl.map.IGameMap;
import de.secondsystem.game01.impl.map.physics.CollisionHandlerType;
import de.secondsystem.game01.impl.map.physics.IPhysicsBody;
import de.secondsystem.game01.impl.map.physics.PhysicalBodyFeatures;
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
			canPickUpObjects, boolean createTestFixture, Attributes attributes ) {
		return map.getPhysicalWorld().createDynamicBody(attributes.getInteger("worldId", map.getActiveWorldId()),
				attributes.getFloat("x"), 
				attributes.getFloat("y"), 
				attributes.getFloat("width"), 
				attributes.getFloat("height"), 
				attributes.getFloat("rotation", 0), 
				CollisionHandlerType.SOLID, 
				i(jumper, PhysicalBodyFeatures.SIDE_CONTACT_CHECK)|
				i(canPickUpObjects,PhysicalBodyFeatures.STABLE_CHECK)|
				i(createTestFixture, PhysicalBodyFeatures.WORLD_SWITCH_CHECK) );
	}
	
	public static EntityEventHandler createEventHandler(GameEntityManager entityManager, Attributes attributes) {
		return null;	// TODO
	}
	
	private static int i( boolean cond, int f ) {
		return cond ? f : 0;
	}
	
	private GameEntityHelper() {}
}
