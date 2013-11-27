package de.secondsystem.game01.impl.map.physics;

import org.jsfml.system.Vector2f;

import de.secondsystem.game01.model.IUpdateable;

public interface IPhysicalWorld extends IUpdateable {

	void init(Vector2f gravity);

	@Override
	void update(long frameTime);

	PhysicsBodyFactory factory();
	
	
	public interface PhysicsBodyFactory {
		PhysicsBodyFactory inWorld(int worldId);

		PhysicsBodyFactory position(float x, float y);
		PhysicsBodyFactory rotation(float rotation);
		PhysicsBodyFactory dimension(float width, float height);

		PhysicsBodyFactory type(CollisionHandlerType type);
		
		StaticPhysicsBodyFactory staticBody();
		DynamicPhysicsBodyFactory dynamicBody();
		HumanoidPhysicsBodyFactory humanoidBody();
	}
	public interface StaticPhysicsBodyFactory {
		IPhysicsBody create();
	}
	public interface DynamicPhysicsBodyFactory extends StaticPhysicsBodyFactory {
		IPhysicsBody create();
		
		DynamicPhysicsBodyFactory maxXSpeed(float speed);
		DynamicPhysicsBodyFactory maxYSpeed(float speed);
		
		DynamicPhysicsBodyFactory stableCheck(boolean enable);
		DynamicPhysicsBodyFactory worldSwitch(boolean allowed);
	}
	public interface HumanoidPhysicsBodyFactory extends DynamicPhysicsBodyFactory {
		HumanoidPhysicsBodyFactory maxXSpeed(float speed);
		HumanoidPhysicsBodyFactory maxYSpeed(float speed);
		
		HumanoidPhysicsBodyFactory maxSlope( float degree );
		HumanoidPhysicsBodyFactory maxReach( float px );
		
		IHumanoidPhysicsBody create();
	}

}
