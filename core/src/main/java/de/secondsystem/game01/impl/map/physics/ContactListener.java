package de.secondsystem.game01.impl.map.physics;

public interface ContactListener {
	void beginContact( IPhysicsBody other );
	void endContact( IPhysicsBody other );
}