package de.secondsystem.game01.impl.map.physics;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.joints.Joint;
import org.jbox2d.dynamics.joints.RevoluteJoint;
import org.jsfml.system.Vector2f;

import de.secondsystem.game01.model.IUpdateable;

public interface IPhysicalWorld extends IUpdateable {

	void init( Vector2f gravity );
	
	@Override
	void update(long frameTime);
	
	IPhysicsBody createBody( int gameWorldIdMask, float x, float y, float width, 
			float height, float rotation, boolean isStatic, CollisionHandlerType type, boolean createFoot, boolean createHand, boolean liftable);
	
	RevoluteJoint createRevoluteJoint(Body body1, Body body2, Vec2 anchor);
	void destroyJoint(Joint joint);
	
}
