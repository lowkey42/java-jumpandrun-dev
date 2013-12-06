package de.secondsystem.game01.impl.map.physics;

import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.contacts.Contact;

class Box2dContactListener implements ContactListener {

	public static interface FixtureContactListener {
		void onBeginContact(Contact contact, Box2dPhysicsBody other, Fixture fixture);
		void onEndContact(Contact contact, Box2dPhysicsBody other, Fixture fixture);
	}
	public static class FixtureData {
		final boolean multiverse; ///< collides with objects in any part of our complex multi-world map
		final FixtureContactListener overrideListener;
		FixtureData(){
			multiverse = false;
			overrideListener = null;}
		FixtureData(boolean multiverse, FixtureContactListener overrideListener){
			this.multiverse = multiverse;
			this.overrideListener = overrideListener;
		}
	}
	
	private static FixtureContactListener getListener(Fixture fixture, FixtureContactListener def) {
		if( fixture.getUserData()!=null && ((FixtureData)fixture.getUserData()).overrideListener!=null )
			return ((FixtureData)fixture.getUserData()).overrideListener;
		else
			return def;
	}
	
	@Override
	public void beginContact(Contact contact) {
		Fixture fixtureA = contact.getFixtureA();
		Fixture fixtureB = contact.getFixtureB();
		Box2dPhysicsBody body1 = (Box2dPhysicsBody) fixtureA.getBody().getUserData();
		Box2dPhysicsBody body2 = (Box2dPhysicsBody) fixtureB.getBody().getUserData();
		
		if( contact.isEnabled() && isWorldShared(body1, body2, fixtureA, fixtureB) ) {
			getListener(fixtureA, body1).onBeginContact(contact, body2, fixtureA);
			getListener(fixtureB, body2).onBeginContact(contact, body1, fixtureB);
		}
	}

	@Override
	public void endContact(Contact contact) {
		Fixture fixtureA = contact.getFixtureA();
		Fixture fixtureB = contact.getFixtureB();
		Box2dPhysicsBody body1 = (Box2dPhysicsBody) contact.getFixtureA().getBody().getUserData();
		Box2dPhysicsBody body2 = (Box2dPhysicsBody) contact.getFixtureB().getBody().getUserData();
		
		getListener(fixtureA, body1).onEndContact(contact, body2, fixtureA);	
		getListener(fixtureB, body2).onEndContact(contact, body1, fixtureB);
	}

	@Override
	public void postSolve(Contact contact, ContactImpulse arg1) {
		// not used currently
	}

	@Override
	public void preSolve(Contact contact, Manifold arg1) {
		
        Box2dPhysicsBody bodyA = (Box2dPhysicsBody) contact.m_fixtureA.getBody().getUserData();
        Box2dPhysicsBody bodyB = (Box2dPhysicsBody) contact.m_fixtureB.getBody().getUserData();
		
        contact.setEnabled(
        		isWorldShared(bodyA, bodyB, contact.m_fixtureA, contact.m_fixtureB)
        		&& !bodyA.isContactFiltered(contact, bodyB, contact.m_fixtureA, contact.m_fixtureB)
        		&& !bodyB.isContactFiltered(contact, bodyA, contact.m_fixtureB, contact.m_fixtureA)
        );   
	}
	
	protected boolean isWorldShared(Box2dPhysicsBody bodyA, Box2dPhysicsBody bodyB, Fixture fixtureA, Fixture fixtureB) {
		if( fixtureA.getUserData()!=null && ((FixtureData)fixtureA.getUserData()).multiverse )
			return true;
		if( fixtureB.getUserData()!=null && ((FixtureData)fixtureB.getUserData()).multiverse )
			return true;
		
		return bodyB.isInWorld(bodyA.getWorldIdMask());
	}

}
