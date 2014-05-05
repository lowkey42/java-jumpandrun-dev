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
		final boolean cancelNormalHandlers;
		final boolean mainBody;
		FixtureData(){
			this(false, false, true, null);
		}
		FixtureData(boolean multiverse, FixtureContactListener overrideListener){
			this(multiverse, true, true, overrideListener);
		}
		FixtureData(boolean multiverse, boolean cancelNormalHandlers, FixtureContactListener overrideListener){
			this(multiverse, cancelNormalHandlers, true, overrideListener);
		}
		FixtureData(boolean multiverse, boolean cancelNormalHandlers, boolean mainBody, FixtureContactListener overrideListener){
			this.multiverse = multiverse;
			this.overrideListener = overrideListener;
			this.cancelNormalHandlers = cancelNormalHandlers;
			this.mainBody = mainBody;
		}
	}
	
	private static boolean isMainBody(Fixture fixture) {
		return fixture.getUserData()==null || ((FixtureData)fixture.getUserData()).overrideListener==null || ((FixtureData)fixture.getUserData()).mainBody;
	}
	
	private static void callBeginListener(Contact contact, Box2dPhysicsBody otherBody, Fixture fixture, FixtureContactListener def) {
		if( fixture.getUserData()!=null && ((FixtureData)fixture.getUserData()).overrideListener!=null ) {
			((FixtureData)fixture.getUserData()).overrideListener.onBeginContact(contact, otherBody, fixture);
			
			if( ((FixtureData)fixture.getUserData()).cancelNormalHandlers )
				return;
		}
		
		def.onBeginContact(contact, otherBody, fixture);
	}
	
	private static void callEndListener(Contact contact, Box2dPhysicsBody otherBody, Fixture fixture, FixtureContactListener def) {
		if( fixture.getUserData()!=null && ((FixtureData)fixture.getUserData()).overrideListener!=null ) {
			((FixtureData)fixture.getUserData()).overrideListener.onEndContact(contact, otherBody, fixture);
			
			if( ((FixtureData)fixture.getUserData()).cancelNormalHandlers )
				return;
		}
		
		def.onEndContact(contact, otherBody, fixture);
	}
	
	@Override
	public void beginContact(Contact contact) {
		Fixture fixtureA = contact.getFixtureA();
		Fixture fixtureB = contact.getFixtureB();
		Box2dPhysicsBody body1 = (Box2dPhysicsBody) fixtureA.getBody().getUserData();
		Box2dPhysicsBody body2 = (Box2dPhysicsBody) fixtureB.getBody().getUserData();
		
		if( contact.isEnabled() && isWorldShared(body1, body2, fixtureA, fixtureB) ) {
			if( isMainBody(fixtureB) )
				callBeginListener(contact, body2, fixtureA, body1);
			
			if( isMainBody(fixtureA) )	
				callBeginListener(contact, body1, fixtureB, body2);
		}
	}

	@Override
	public void endContact(Contact contact) {
		Fixture fixtureA = contact.getFixtureA();
		Fixture fixtureB = contact.getFixtureB();
		Box2dPhysicsBody body1 = (Box2dPhysicsBody) contact.getFixtureA().getBody().getUserData();
		Box2dPhysicsBody body2 = (Box2dPhysicsBody) contact.getFixtureB().getBody().getUserData();

		if( isMainBody(fixtureB) )
			callEndListener(contact, body2, fixtureA, body1);
		
		if( isMainBody(fixtureA) )	
			callEndListener(contact, body1, fixtureB, body2);
	}

	@Override
	public void postSolve(Contact contact, ContactImpulse arg1) {
        Box2dPhysicsBody bodyA = (Box2dPhysicsBody) contact.m_fixtureA.getBody().getUserData();
        Box2dPhysicsBody bodyB = (Box2dPhysicsBody) contact.m_fixtureB.getBody().getUserData();
        
		if( arg1.normalImpulses[0]>=1.0 )
			bodyA.onPressed(arg1.normalImpulses[0]);

		if( arg1.normalImpulses.length>1 && arg1.normalImpulses[1]>=1.0 )
			bodyB.onPressed(arg1.normalImpulses[1]);
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
