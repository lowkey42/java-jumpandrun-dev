package de.secondsystem.game01.impl.map.physics;

import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.dynamics.contacts.Contact;

import de.secondsystem.game01.impl.map.objects.TestCharacter;

public class PhysicsContactListener implements ContactListener {

	@Override
	public void beginContact(Contact arg0) {
		PhysicsBody body1 = (PhysicsBody) arg0.getFixtureA().getBody().getUserData();
		PhysicsBody body2 = (PhysicsBody) arg0.getFixtureB().getBody().getUserData();
		body1.beginContact(body2);
		body2.beginContact(body1);
	}

	@Override
	public void endContact(Contact arg0) {
		PhysicsBody body1 = (PhysicsBody) arg0.getFixtureA().getBody().getUserData();
		PhysicsBody body2 = (PhysicsBody) arg0.getFixtureB().getBody().getUserData();
		body1.endContact(body2);
		body2.endContact(body1);
	}

	@Override
	public void postSolve(Contact arg0, ContactImpulse arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void preSolve(Contact arg0, Manifold arg1) {
		// TODO Auto-generated method stub
		
	}

}
