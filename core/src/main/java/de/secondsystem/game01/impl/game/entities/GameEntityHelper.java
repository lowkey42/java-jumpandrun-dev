package de.secondsystem.game01.impl.game.entities;

import java.io.IOException;

import de.secondsystem.game01.impl.ResourceManager;
import de.secondsystem.game01.impl.game.entities.events.EntityEventHandler;
import de.secondsystem.game01.impl.graphic.AnimatedSprite;
import de.secondsystem.game01.impl.map.IGameMap;
import de.secondsystem.game01.impl.map.physics.IDynamicPhysicsBody;
import de.secondsystem.game01.impl.map.physics.IPhysicsWorld.DynamicPhysicsBodyFactory;
import de.secondsystem.game01.impl.map.physics.IPhysicsWorld.HumanoidPhysicsBodyFactory;
import de.secondsystem.game01.impl.map.physics.IPhysicsWorld.PhysicsBodyFactory;
import de.secondsystem.game01.impl.map.physics.PhysicsBodyShape;
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
				.worldMask(attributes.getInteger("worldId", map.getActiveWorldId().id))
				.position(attributes.getFloat("x"), attributes.getFloat("y"))
				.dimension(attributes.getFloat("width"), attributes.getFloat("height"));
		
		Float rotation = attributes.getFloat("rotation");
		if( rotation!=null )
			factory.rotation(rotation);

		Float density = attributes.getFloat("density");
		if( density!=null )
			factory.density(density);

		Float weight = attributes.getFloat("weight");
		if( weight!=null )
			factory.weight(weight);

		Float friction = attributes.getFloat("friction");
		if( friction!=null )
			factory.friction(friction);

		Float restitution = attributes.getFloat("restitution");
		if( restitution!=null )
			factory.restitution(restitution);

		PhysicsBodyShape shape = PhysicsBodyShape.valueOf(attributes.getString("shape"));
		final DynamicPhysicsBodyFactory bodyFactory;
		
		if( shape==null || shape==PhysicsBodyShape.HUMANOID ) {
			HumanoidPhysicsBodyFactory hBodyFactory = factory.humanoidBody();
			
			Float maxLiftWeight = attributes.getFloat("maxLiftWeight");
			if( maxLiftWeight!=null )
				hBodyFactory.maxLiftWeight(maxLiftWeight);
			
			Float maxThrowSpeed = attributes.getFloat("maxThrowSpeed");
			if( maxThrowSpeed!=null )
				hBodyFactory.maxThrowSpeed(maxThrowSpeed);
			
			Float maxReach = attributes.getFloat("maxReach");
			if( maxReach!=null )
				hBodyFactory.maxReach(maxReach);
			
			Float maxSlope = attributes.getFloat("maxSlope");
			if( maxSlope!=null )
				hBodyFactory.maxSlope(maxSlope);
						
			bodyFactory = hBodyFactory;
			
		} else {
			bodyFactory = factory.dynamicBody(shape);
			Boolean jumpAllowed = attributes.getBoolean("jumpAllowed");
			if( jumpAllowed!=null )
				bodyFactory.stableCheck(jumpAllowed);
				
			Boolean worldSwitchAllowed = attributes.getBoolean("worldSwitchAllowed");
			if( worldSwitchAllowed!=null )
				bodyFactory.worldSwitch(worldSwitchAllowed);
		}

		Float maxMoveSpeed = attributes.getFloat("maxMoveSpeed");
		if( maxMoveSpeed!=null )
			bodyFactory.maxXSpeed(maxMoveSpeed);
		
		Float maxJumpSpeed = attributes.getFloat("maxJumpSpeed");
		if( maxJumpSpeed!=null )
			bodyFactory.maxYSpeed(maxJumpSpeed);
		
		
		return bodyFactory.create();
	}
	
	public static EntityEventHandler createEventHandler(GameEntityManager entityManager, Attributes attributes) {
		return null;	// TODO
	}
	
	private GameEntityHelper() {}
}
