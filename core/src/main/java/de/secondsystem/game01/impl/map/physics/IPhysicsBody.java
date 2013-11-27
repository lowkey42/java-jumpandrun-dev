package de.secondsystem.game01.impl.map.physics;

import org.jsfml.system.Vector2f;

import de.secondsystem.game01.model.IMoveable;

public interface IPhysicsBody extends IMoveable {

	Object getOwner();
	void setOwner( Object owner );
	
	void setContactListener( ContactListener contactListener );
	
	void setGameWorldId(int id);
	
	CollisionHandlerType getCollisionHandlerType();
	boolean isStatic();
		
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
	
	void resetVelocity( boolean x, boolean y, boolean rotation );
	
	/**
	 * Binds to the other's body. Binds the body on top of the binding body if the mass is not too high.
	 * @return true if binding on top of the binding body is possible
	 */
	boolean bind(IPhysicsBody other, Vector2f anchor);
	void unbind(IPhysicsBody other);
	boolean isBound(IPhysicsBody other);
	
	
	public interface ContactListener {
		void beginContact( IPhysicsBody other );
		void endContact( IPhysicsBody other );
	}
	
}
