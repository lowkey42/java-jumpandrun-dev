package de.secondsystem.game01.impl.map.physics;

public interface PhysicsContactListener {
	void beginContact( IPhysicsBody other );
	void endContact( IPhysicsBody other );
	void pressed( float pressure );
}