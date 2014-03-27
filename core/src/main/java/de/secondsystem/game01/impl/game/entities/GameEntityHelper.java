package de.secondsystem.game01.impl.game.entities;

import java.io.IOException;

import de.secondsystem.game01.impl.ResourceManager;
import de.secondsystem.game01.impl.graphic.AnimatedSprite;
import de.secondsystem.game01.impl.graphic.SpriteWrappper;
import de.secondsystem.game01.impl.map.IGameMap;
import de.secondsystem.game01.impl.map.physics.IPhysicsBody;
import de.secondsystem.game01.impl.map.physics.IPhysicsWorld.DynamicPhysicsBodyFactory;
import de.secondsystem.game01.impl.map.physics.IPhysicsWorld.HumanoidPhysicsBodyFactory;
import de.secondsystem.game01.impl.map.physics.IPhysicsWorld.PhysicsBodyFactory;
import de.secondsystem.game01.impl.map.physics.IPhysicsWorld.StaticPhysicsBodyFactory;
import de.secondsystem.game01.impl.map.physics.PhysicsBodyShape;
import de.secondsystem.game01.model.Attributes;
import de.secondsystem.game01.model.IDrawable;

final class GameEntityHelper {
	
	public static enum RepresentationType {
		ANIMATION, 
		TEXTURE, 
		TILE,
		LIGHT;
	}
	
	public static enum PhysicsType {
		STATIC,
		DYNAMIC,
		KINEMATIC,
		FLYING
	}
	
	public static IDrawable createRepresentation( IGameMap map, Attributes attributes ) {
		IDrawable repr = null;
		try {
			final String typeStr = attributes.getString("representationType");
			if( typeStr==null )
				return null;
			
			final RepresentationType type = RepresentationType.valueOf(typeStr);
			
			
			float width = attributes.getFloat("width");
			float height = attributes.getFloat("height");
			String filename = attributes.getString("representation");
			
			switch( type ) {
			case ANIMATION:
				repr = new AnimatedSprite(ResourceManager.animation.get(filename), width, height);
				((AnimatedSprite) repr).setFlip(attributes.getBoolean("flipped", false));
				break;
			case TEXTURE:
				repr = new SpriteWrappper(width, height);
				((SpriteWrappper) repr).setTexture(ResourceManager.texture.get(filename));
				((SpriteWrappper) repr).setFlip(attributes.getBoolean("flipped", false));
				break;
			case TILE:
				repr = new SpriteWrappper(width, height);
				((SpriteWrappper) repr).setTexture(ResourceManager.texture_tiles.get(filename));
				((SpriteWrappper) repr).setFlip(attributes.getBoolean("flipped", false));
				break;
				
			default:
				System.out.println("Representation unknown.");
				break;
			}
			
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return repr;
	}
	
	public static IPhysicsBody createPhysicsBody( IGameMap map, boolean jumper, boolean 
			canPickUpObjects, boolean createTestFixture, Attributes attributes ) {
		PhysicsBodyFactory factory = map.getPhysicalWorld().factory()
				.worldMask(attributes.getInteger("worldId", map.getActiveWorldId().id))
				.position(attributes.getFloat("x"), attributes.getFloat("y"))
				.dimension(
						attributes.getFloat("physicsWidth", attributes.getFloat("width")), 
						attributes.getFloat("physicsHeight", attributes.getFloat("height")) );
		
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

		Boolean interactive = attributes.getBoolean("interactive");
		if( interactive!=null )
			factory.interactive(interactive);
		
		Boolean liftable = attributes.getBoolean("liftable");
		if( liftable!=null )
			factory.liftable(liftable);
		
		Boolean sensor = attributes.getBoolean("sensor");
		if( sensor!=null )
			factory.sensor(sensor);
		
		PhysicsType type = PhysicsType.valueOf(attributes.getString("physicsType", "DYNAMIC"));
		if( type==null )
			type = PhysicsType.DYNAMIC;
		else if( type==PhysicsType.FLYING )
			factory.flying(true);
		
		PhysicsBodyShape shape = PhysicsBodyShape.valueOf(attributes.getString("shape"));
		StaticPhysicsBodyFactory bodyFactory = null;
		
		if( shape==null || shape==PhysicsBodyShape.HUMANOID ) {
			HumanoidPhysicsBodyFactory hBodyFactory = factory.humanoidBody();
			
			Float maxLiftWeight = attributes.getFloat("maxLiftWeight");
			if( maxLiftWeight!=null )
				hBodyFactory.maxLiftWeight(maxLiftWeight);
			
			Float maxThrowSpeed = attributes.getFloat("maxThrowSpeed");
			if( maxThrowSpeed!=null )
				hBodyFactory.maxThrowSpeed(maxThrowSpeed);
			
			Float maxLiftForce = attributes.getFloat("maxLiftForce");
			if( maxLiftForce!=null )
				hBodyFactory.maxLiftForce(maxLiftForce);
			
			Float maxReach = attributes.getFloat("maxReach");
			if( maxReach!=null )
				hBodyFactory.maxReach(maxReach);
			
			Float maxSlope = attributes.getFloat("maxSlope");
			if( maxSlope!=null )
				hBodyFactory.maxSlope(maxSlope);
						
			bodyFactory = hBodyFactory;
		}

		switch (type) {
			case STATIC:
				if( bodyFactory==null )
					bodyFactory = factory.staticBody(shape);
				break;
				
			case KINEMATIC:
				if( bodyFactory==null )
					bodyFactory = factory.kinematicBody(shape);
	
			case FLYING:
			case DYNAMIC:
				if( bodyFactory==null )
					bodyFactory = factory.dynamicBody(shape);

				Float maxMoveSpeed = attributes.getFloat("maxMoveSpeed");
				if( maxMoveSpeed!=null )
					((DynamicPhysicsBodyFactory)bodyFactory).maxXSpeed(maxMoveSpeed);
				
				Float maxJumpSpeed = attributes.getFloat("maxJumpSpeed");
				if( maxJumpSpeed!=null )
					((DynamicPhysicsBodyFactory)bodyFactory).maxYSpeed(maxJumpSpeed);
	
				Float initialRelXSpeed = attributes.getFloat("initialRelXSpeed");
				Float initialRelYSpeed = attributes.getFloat("initialRelYSpeed");
				if( initialRelXSpeed!=null && initialRelYSpeed!=null )
					((DynamicPhysicsBodyFactory)bodyFactory).initialRelativeSpeed(initialRelXSpeed, initialRelYSpeed);

				Boolean jumpAllowed = attributes.getBoolean("jumpAllowed");
				if( jumpAllowed!=null )
					((DynamicPhysicsBodyFactory)bodyFactory).stableCheck(jumpAllowed);
					
				Boolean worldSwitchAllowed = attributes.getBoolean("worldSwitchAllowed");
				if( worldSwitchAllowed!=null )
					((DynamicPhysicsBodyFactory)bodyFactory).worldSwitch(worldSwitchAllowed);
				break;
		}
		
		return bodyFactory.create();
	}
	
	private GameEntityHelper() {}
}
