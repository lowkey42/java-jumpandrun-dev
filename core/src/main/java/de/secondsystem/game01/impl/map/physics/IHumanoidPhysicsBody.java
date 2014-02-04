package de.secondsystem.game01.impl.map.physics;

import java.util.List;

import org.jsfml.system.Vector2f;

public interface IHumanoidPhysicsBody extends IDynamicPhysicsBody {

	void setMaxThrowVelocity( float vel );
	void setMaxLiftWeight( float weight );
	
	boolean isClimbing();
	boolean tryClimbing();
	void stopClimbing();

	boolean liftBody(IPhysicsBody other);
	boolean isLiftingSomething();
	IPhysicsBody throwLiftedBody(float strength, Vector2f direction);
	
	/**
	 * @return all reachable bodies
	 */
	List<IPhysicsBody> listNearBodies(Vector2f direction, boolean checkBack, BodyFilter filter);
	
	/**
	 * @return the nearest reachable body
	 */
	IPhysicsBody getNearestBody(Vector2f direction, BodyFilter filter);

	public interface BodyFilter {
		boolean accept( IPhysicsBody body );
	}
	public static final BodyFilter BF_LIFTABLE = new BodyFilter() {
		@Override public boolean accept(IPhysicsBody body) {
			return body.isLiftable();
		}
	};
	public static final BodyFilter BF_INTERACTIVE = new BodyFilter() {
		@Override public boolean accept(IPhysicsBody body) {
			return body.isInteractive();
		}
	};

}
