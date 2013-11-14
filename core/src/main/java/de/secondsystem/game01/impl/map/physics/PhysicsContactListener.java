package de.secondsystem.game01.impl.map.physics;

import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.dynamics.contacts.Contact;

class PhysicsContactListener implements ContactListener {

	@Override
	public void beginContact(Contact arg0) {
		Box2dPhysicsBody body1 = (Box2dPhysicsBody) arg0.getFixtureA().getBody().getUserData();
		Box2dPhysicsBody body2 = (Box2dPhysicsBody) arg0.getFixtureB().getBody().getUserData();
		body1.beginContact(body2);
		body2.beginContact(body1);
	}

	@Override
	public void endContact(Contact arg0) {
		Box2dPhysicsBody body1 = (Box2dPhysicsBody) arg0.getFixtureA().getBody().getUserData();
		Box2dPhysicsBody body2 = (Box2dPhysicsBody) arg0.getFixtureB().getBody().getUserData();
		body1.endContact(body2);
		body2.endContact(body1);
	}

	@Override
	public void postSolve(Contact arg0, ContactImpulse arg1) {
		// not used currently
	}

	@Override
	public void preSolve(Contact arg0, Manifold arg1) {
		// not used currently
	}

}
