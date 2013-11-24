package de.secondsystem.game01.impl.map.physics;

import org.jsfml.system.Vector2f;

import de.secondsystem.game01.model.IUpdateable;

public interface IPhysicalWorld extends IUpdateable {

	void init( Vector2f gravity );
	
	@Override
	void update(long frameTime);

	IPhysicsBody createStaticBody( int gameWorldIdMask, float x, float y, float width, 
			float height, float rotation, CollisionHandlerType type );
			
	IPhysicsBody createDynamicBody( int gameWorldIdMask, float x, float y, float width, 
			float height, float rotation, CollisionHandlerType type, 
			int features );
	
}
