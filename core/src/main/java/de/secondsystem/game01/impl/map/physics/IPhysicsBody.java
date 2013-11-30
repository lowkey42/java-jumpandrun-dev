package de.secondsystem.game01.impl.map.physics;

import org.jsfml.system.Vector2f;

import de.secondsystem.game01.model.IDimensioned;
import de.secondsystem.game01.model.IMoveable;

public interface IPhysicsBody extends IMoveable, IDimensioned {

	Object getOwner();
	void setOwner( Object owner );
	IPhysicsWorld getParent();
	
	void setContactListener( PhysicsContactListener contactListener );
	
	CollisionHandlerType getCollisionHandlerType();
	boolean isStatic();
	
	float getWeight();

	/**
	 * Binds to the other's body. Binds the body on top of the binding body if the mass is not too high.
	 * @return true if binding on top of the binding body is possible
	 */
	boolean bind(IPhysicsBody other, Vector2f anchor);
	void unbind(IPhysicsBody other);
	boolean isBound(IPhysicsBody other);
}
