package de.secondsystem.game01.impl.map.physics;

import org.jsfml.system.Vector2f;

import de.secondsystem.game01.impl.map.IGameMap.WorldId;
import de.secondsystem.game01.model.IDimensioned;
import de.secondsystem.game01.model.IMoveable;

public interface IPhysicsBody extends IMoveable, IDimensioned {

	boolean isInWorld(WorldId worldId);
	void setWorld(WorldId worldId, boolean exists);
	
	Object getOwner();
	void setOwner( Object owner );
	IPhysicsWorld getParent();
	
	void setContactListener( PhysicsContactListener contactListener );
	
	CollisionHandlerType getCollisionHandlerType();
	boolean isStatic();
	boolean isKinematic();
	
	float getWeight();
	boolean isInteractive();
	boolean isLiftable();
	void setIdle(boolean idle);
	
	/**
	 * Binds to the other's body. Binds the body on top of the binding body if the mass is not too high.
	 * @return true if binding on top of the binding body is possible
	 */
	boolean bind(IPhysicsBody other, Vector2f anchor);
	void unbind(IPhysicsBody other);
	boolean isBound(IPhysicsBody other);
	boolean isBound();
}
