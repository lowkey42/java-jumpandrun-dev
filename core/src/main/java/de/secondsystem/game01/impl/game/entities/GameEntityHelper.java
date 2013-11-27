package de.secondsystem.game01.impl.game.entities;

import java.io.IOException;

import de.secondsystem.game01.impl.ResourceManager;
import de.secondsystem.game01.impl.game.entities.events.EntityEventHandler;
import de.secondsystem.game01.impl.graphic.AnimatedSprite;
import de.secondsystem.game01.impl.map.IGameMap;
import de.secondsystem.game01.impl.map.physics.CollisionHandlerType;
import de.secondsystem.game01.impl.map.physics.IDynamicPhysicsBody;
import de.secondsystem.game01.impl.map.physics.IPhysicalWorld.DynamicPhysicsBodyFactory;
import de.secondsystem.game01.impl.map.physics.IPhysicalWorld.PhysicsBodyFactory;
import de.secondsystem.game01.impl.map.physics.IPhysicalWorld.StaticPhysicsBodyFactory;
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
	public static IDynamicPhysicsBody createPhysicsBody( IGameMap map, boolean jumper, boolean 
			canPickUpObjects, boolean createTestFixture, Attributes attributes ) {
		PhysicsBodyFactory factory = map.getPhysicalWorld().factory()
				.inWorld(attributes.getInteger("worldId", map.getActiveWorldId()));
		
		Float x = attributes.getFloat("x");
		Float y = attributes.getFloat("y");
		if( x!=null && y!=null )
			factory.position(x, y);
		
		Float width = attributes.getFloat("width");
		Float height = attributes.getFloat("height");
		if( width!=null && height!=null )
			factory.dimension(width, height);

		Float rotation = attributes.getFloat("rotation");
		if( rotation!=null )
			factory.rotation(rotation);
		
		
		DynamicPhysicsBodyFactory bodyFactory = factory.dynamicBody();
		
		// TODO
		
		return bodyFactory.create();
	}
	
	public static EntityEventHandler createEventHandler(GameEntityManager entityManager, Attributes attributes) {
		return null;	// TODO
	}
	
	private GameEntityHelper() {}
}
