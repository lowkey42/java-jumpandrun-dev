package de.secondsystem.game01.impl.map.physics;

import org.jsfml.system.Vector2f;

import de.secondsystem.game01.impl.map.IUpdateable;

public interface IPhysicalWorld extends IUpdateable {

	void init( Vector2f gravity );
	
	@Override
	void update(long frameTime);
	
	IPhysicsBody createBody( int gameWorldIdMask, float x, float y, float width, float height, float rotation, boolean isStatic, CollisionHandlerType type );
	
}
