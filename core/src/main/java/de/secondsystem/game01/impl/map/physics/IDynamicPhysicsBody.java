package de.secondsystem.game01.impl.map.physics;

import org.jsfml.system.Vector2f;

public interface IDynamicPhysicsBody extends IPhysicsBody {

	/**
	 * Indicates if the body stands stable (e.g. on a static body)
	 * Might be useful for isJumpAllowed or something like this ;-)
	 * @return true if the body is stable, currently
	 */
	boolean isStable();
	
	boolean tryWorldSwitch(int id);

	Vector2f getVelocity();
	
	void setMaxVelocityX( float x );
	void setMaxVelocityY( float y );
	
	byte move( float x, float y );
	void rotate( float angle );
	
	void resetVelocity( boolean x, boolean y, boolean rotation );
	
}
