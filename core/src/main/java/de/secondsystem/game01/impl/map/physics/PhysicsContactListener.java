package de.secondsystem.game01.impl.map.physics;

import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.contacts.Contact;

class PhysicsContactListener implements ContactListener {

	@Override
	public void beginContact(Contact contact) {
		Fixture fixtureA = contact.getFixtureA();
		Fixture fixtureB = contact.getFixtureB();
		Box2dPhysicsBody body1 = (Box2dPhysicsBody) fixtureA.getBody().getUserData();
		Box2dPhysicsBody body2 = (Box2dPhysicsBody) fixtureB.getBody().getUserData();
		
		if( contact.isEnabled() && body2.isInWorld(body1.getWorldIdMask()) ) {
			body1.onBeginContact(contact, body2, fixtureA);
			body2.onBeginContact(contact, body1, fixtureB);
		}
	}

	@Override
	public void endContact(Contact contact) {
		Fixture fixtureA = contact.getFixtureA();
		Fixture fixtureB = contact.getFixtureB();
		Box2dPhysicsBody body1 = (Box2dPhysicsBody) contact.getFixtureA().getBody().getUserData();
		Box2dPhysicsBody body2 = (Box2dPhysicsBody) contact.getFixtureB().getBody().getUserData();
		
		body1.onEndContact(contact, body2, fixtureA);	
		body2.onEndContact(contact, body1, fixtureB);
	}

	@Override
	public void postSolve(Contact contact, ContactImpulse arg1) {
		// not used currently
	}

	@Override
	public void preSolve(Contact contact, Manifold arg1) {
        Box2dPhysicsBody bodyA = (Box2dPhysicsBody) contact.m_fixtureA.getBody().getUserData();
        Box2dPhysicsBody bodyB = (Box2dPhysicsBody) contact.m_fixtureB.getBody().getUserData();
		
        contact.setEnabled(!isFiltered(contact, bodyA, bodyB));   
	}
	
	protected boolean isFiltered(Contact contact, Box2dPhysicsBody bodyA, Box2dPhysicsBody bodyB) {
        return !bodyB.isInWorld(bodyA.getWorldIdMask())
        	|| bodyA.isContactFiltered(contact, bodyB, contact.m_fixtureA)
        	|| bodyB.isContactFiltered(contact, bodyA, contact.m_fixtureB);
	}

}
