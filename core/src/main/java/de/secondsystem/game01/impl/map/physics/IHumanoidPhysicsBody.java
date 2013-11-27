package de.secondsystem.game01.impl.map.physics;

import java.util.List;

public interface IHumanoidPhysicsBody extends IDynamicPhysicsBody {

	void setMaxThrowVelocity( float vel );
	void setMaxLiftWeight( float weight );
	
	boolean isClimbing();
	boolean tryClimbing();
	void stopClimbing();

	boolean liftBody(IPhysicsBody other);
	boolean throwLiftedBody(float strength);
	
	/**
	 * @return all reachable bodies that are usable or liftable
	 */
	List<IPhysicsBody> listInteractiveBodies();
	
	/**
	 * @return the nearest reachable body that is usable or liftable (or NULL)
	 */
	IPhysicsBody getNearestInteractiveBody();
	
}
