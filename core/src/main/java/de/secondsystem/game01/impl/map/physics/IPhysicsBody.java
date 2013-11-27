package de.secondsystem.game01.impl.map.physics;

import org.jsfml.system.Vector2f;

public interface IPhysicsBody {

	Object getOwner();
	void setOwner( Object owner );
	
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
	void setMaxThrowVelocity( float vel );
	
	byte move( float x, float y );
	
	void rotate( float angle );
	
	void forcePosition( float x, float y );
	
	void forceRotation( float angle );
	
	void resetVelocity( boolean x, boolean y, boolean rotation );
	
	boolean isClimbing();

	void climb(boolean climb);
	
	/**
	 * Binds to the other's body. Binds the body on top of the binding body if the mass is not too high.
	 * @return true if binding on top of the binding body is possible
	 */
	boolean bind(IPhysicsBody other, Vector2f anchor);
	void unbind();
	boolean isBound();

	void throwBoundBody(float x, float y);
	
	IPhysicsBody getTouchingBodyRight();
	IPhysicsBody getTouchingBodyLeft();
	
	boolean isWorldSwitchPossible();
	
	boolean isStatic();
	
	public interface ContactListener {
		void beginContact( IPhysicsBody other );
		void endContact( IPhysicsBody other );
	}
	
}
