package de.secondsystem.game01.impl.game.entities;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.secondsystem.game01.impl.ResourceManager;
import de.secondsystem.game01.impl.game.entities.events.CollectionEntityEventHandler;
import de.secondsystem.game01.impl.game.entities.events.IEntityEventHandler;
import de.secondsystem.game01.impl.game.entities.events.IEntityEventHandler.EntityEventType;
import de.secondsystem.game01.impl.game.entities.events.ScriptEntityEventHandler;
import de.secondsystem.game01.impl.graphic.AnimatedSprite;
import de.secondsystem.game01.impl.graphic.SpriteWrappper;
import de.secondsystem.game01.impl.map.IGameMap;
import de.secondsystem.game01.impl.map.physics.IDynamicPhysicsBody;
import de.secondsystem.game01.impl.map.physics.IPhysicsWorld.DynamicPhysicsBodyFactory;
import de.secondsystem.game01.impl.map.physics.IPhysicsWorld.HumanoidPhysicsBodyFactory;
import de.secondsystem.game01.impl.map.physics.IPhysicsWorld.PhysicsBodyFactory;
import de.secondsystem.game01.impl.map.physics.PhysicsBodyShape;
import de.secondsystem.game01.model.Attributes;
import de.secondsystem.game01.model.IDrawable;

final class GameEntityHelper {
	
	public static enum RepresentationType {
		ANIMATION, 
		TEXTURE, 
		TILE;
	}
	
	public static IDrawable createRepresentation( Attributes attributes ) { // TODO
		IDrawable repr = null;
		try {
			RepresentationType type = RepresentationType.valueOf(attributes.getString("representationType"));
			float width = attributes.getFloat("width");
			float height = attributes.getFloat("height");
			String filename = attributes.getString("representation");
			
			switch( type ) {
			case ANIMATION:
				repr = new AnimatedSprite(ResourceManager.animation.get(filename), width, height);
				break;
			case TEXTURE:
				repr = new SpriteWrappper(width, height);
				((SpriteWrappper) repr).setTexture(ResourceManager.texture.get(filename));
				break;
			case TILE:
				repr = new SpriteWrappper(width, height);
				((SpriteWrappper) repr).setTexture(ResourceManager.texture_tiles.get(filename));
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
		
		Boolean kinematic = attributes.getBoolean("kinematic");
		if( kinematic!=null )
			factory.kinematic(kinematic);

		Boolean interactive = attributes.getBoolean("interactive");
		if( interactive!=null )
			factory.interactive(interactive);
		
		Boolean liftable = attributes.getBoolean("liftable");
		if( liftable!=null )
			factory.liftable(liftable);
		
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
	
	public static IEntityEventHandler createEventHandler(GameEntityManager entityManager, Attributes attributes) {
		CollectionEntityEventHandler eventHandler = new CollectionEntityEventHandler();
		Object eventsObj = attributes.getObject("events");		
		
		if( eventsObj instanceof Map ) {	
			@SuppressWarnings("unchecked")
			HashMap<String, String> events = (HashMap<String, String>) eventsObj;
			for( String eventType : events.keySet() ) {
				ScriptEntityEventHandler event;
				event = new ScriptEntityEventHandler(entityManager.map.getScriptEnv(), EntityEventType.valueOf(eventType), events.get(eventType));
				eventHandler.addEntityEventHandler(EntityEventType.valueOf(eventType), event);
			}
		}
		
		return eventHandler;
	}
	
	private GameEntityHelper() {}
}
