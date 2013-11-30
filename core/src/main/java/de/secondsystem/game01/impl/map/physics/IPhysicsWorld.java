package de.secondsystem.game01.impl.map.physics;

import org.jsfml.system.Vector2f;

import de.secondsystem.game01.impl.map.IGameMap.WorldId;
import de.secondsystem.game01.model.IUpdateable;

public interface IPhysicsWorld extends IUpdateable {

	void init(Vector2f gravity);

	@Override
	void update(long frameTime);

	PhysicsBodyFactory factory();
	
	
	public interface PhysicsBodyFactory {
		PhysicsBodyFactory inWorld(WorldId worldId);
		PhysicsBodyFactory worldMask(int worldMask);

		PhysicsBodyFactory position(float x, float y);
		PhysicsBodyFactory rotation(float rotation);
		PhysicsBodyFactory dimension(float width, float height);
		
		PhysicsBodyFactory weight(float weight);
		PhysicsBodyFactory density(float density);
		
		PhysicsBodyFactory friction(float friction);
		PhysicsBodyFactory restitution(float restitution); ///< elasticity [0,1]

		PhysicsBodyFactory type(CollisionHandlerType type);
		
		StaticPhysicsBodyFactory staticBody(PhysicsBodyShape shape);
		DynamicPhysicsBodyFactory dynamicBody(PhysicsBodyShape shape);
		HumanoidPhysicsBodyFactory humanoidBody();
	}
	public interface StaticPhysicsBodyFactory {
		IPhysicsBody create();
	}
	public interface DynamicPhysicsBodyFactory extends StaticPhysicsBodyFactory {
		IDynamicPhysicsBody create();
		
		DynamicPhysicsBodyFactory maxXSpeed(float speed);
		DynamicPhysicsBodyFactory maxYSpeed(float speed);
		
		DynamicPhysicsBodyFactory stableCheck(boolean enable);
		DynamicPhysicsBodyFactory worldSwitch(boolean allowed);
	}
	public interface HumanoidPhysicsBodyFactory extends DynamicPhysicsBodyFactory {
		HumanoidPhysicsBodyFactory maxXSpeed(float speed);
		HumanoidPhysicsBodyFactory maxYSpeed(float speed);
		
		HumanoidPhysicsBodyFactory maxSlope(float degree);
		HumanoidPhysicsBodyFactory maxReach(float px);
		
		HumanoidPhysicsBodyFactory maxThrowSpeed(float speed);
		HumanoidPhysicsBodyFactory maxLiftWeight(float weight);
		
		IHumanoidPhysicsBody create();
	}

}
