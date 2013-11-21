package de.secondsystem.game01.impl.map.physics;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.joints.RevoluteJoint;
import org.jsfml.system.Vector2f;

public interface IPhysicsBody {

	void setContactListener( ContactListener contactListener );
	
	void setGameWorldId(int id);
	
	CollisionHandlerType getCollisionHandlerType();
	
	Vector2f getPosition();
	
	/**
	 * Get the rotation of the body
	 * @return The normalized (0-359) rotation in degrees
	 */
	float getRotation();
	
	/**
	 * Indicates if the body stands stable (e.g. on a static body)
	 * Might be useful for isJumpAllowed or something like this ;-)
	 * @return true if the body is stable, currently
	 */
	boolean isStable();
	
	Vector2f getVelocity();
	
	void setMaxVelocityX( float x );
	void setMaxVelocityY( float y );
	
	byte move( float x, float y );
	
	void rotate( float angle );
	
	void forcePosition( float x, float y );
	
	void forceRotation( float angle );
	
	void resetVelocity( boolean x, boolean y, boolean rotation );
	
	boolean isAffectedByGravity();

	void useObject(boolean use); // e.g. ladders, switches... with w or upkey 
	
	boolean isUsingObject();
	
	RevoluteJoint bind(IPhysicsBody other, Vec2 anchor);
	void unbind();
	
	void setTouchingBody(IPhysicsBody body);
	IPhysicsBody getTouchingBody();
	
	Body getBody();
	boolean hasJoint();
	boolean isLiftable();
	
	interface ContactListener {
		void beginContact( IPhysicsBody other );
		void endContact( IPhysicsBody other );
	}
	
}
