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
		
		if( body1.getGameWorldId() == body2.getGameWorldId() )
		{
			body1.beginContact(contact, body2, fixtureA);
			body2.beginContact(contact, body1, fixtureB);
		}
		else
			if( fixtureA.getUserData() != null && ((String)fixtureA.getUserData()).compareTo("testFixture") == 0 
			&& body2.getCollisionHandlerType() != CollisionHandlerType.NO_GRAV && body2.getCollisionHandlerType() != CollisionHandlerType.ONE_WAY)
				body1.addCollisionsWithTestFixture(1);
		
			if( fixtureB.getUserData() != null && ((String)fixtureB.getUserData()).compareTo("testFixture") == 0 
			&& body1.getCollisionHandlerType() != CollisionHandlerType.NO_GRAV && body1.getCollisionHandlerType() != CollisionHandlerType.ONE_WAY)
				body2.addCollisionsWithTestFixture(1);
	}

	@Override
	public void endContact(Contact contact) {
		Fixture fixtureA = contact.getFixtureA();
		Fixture fixtureB = contact.getFixtureB();
		Box2dPhysicsBody body1 = (Box2dPhysicsBody) contact.getFixtureA().getBody().getUserData();
		Box2dPhysicsBody body2 = (Box2dPhysicsBody) contact.getFixtureB().getBody().getUserData();
		
		body1.endContact(contact, body2, fixtureA);	
		body2.endContact(contact, body1, fixtureB);
		
		if( fixtureA.getUserData() != null && ((String)fixtureA.getUserData()).compareTo("testFixture") == 0 
		&& body2.getCollisionHandlerType() != CollisionHandlerType.NO_GRAV && body2.getCollisionHandlerType() != CollisionHandlerType.ONE_WAY)
			body1.addCollisionsWithTestFixture(-1);

		if( fixtureB.getUserData() != null && ((String)fixtureB.getUserData()).compareTo("testFixture") == 0 
		&& body1.getCollisionHandlerType() != CollisionHandlerType.NO_GRAV && body1.getCollisionHandlerType() != CollisionHandlerType.ONE_WAY)
			body2.addCollisionsWithTestFixture(-1);
	}

	@Override
	public void postSolve(Contact contact, ContactImpulse arg1) {
		// not used currently
	}

	@Override
	public void preSolve(Contact contact, Manifold arg1) {
        Box2dPhysicsBody body1 = (Box2dPhysicsBody) contact.m_fixtureA.getBody().getUserData();
        Box2dPhysicsBody body2 = (Box2dPhysicsBody) contact.m_fixtureB.getBody().getUserData();
		
        // TODO: call endContact after world-switch if the object doesn't exist in the new world
        contact.setEnabled(!isFiltered(body1, body2));   
	}
	
	protected boolean isFiltered(Box2dPhysicsBody body1, Box2dPhysicsBody body2) {
        if( body1.getGameWorldId() != body2.getGameWorldId() )
        	return true;
        body1.setCollisionWithOneWayPlatform(false);
        body2.setCollisionWithOneWayPlatform(false);
        // dispatch and handle the different collisionTypes
        switch( body1.getCollisionHandlerType() ) {
        	case ONE_WAY:
        		if( body2.getCollisionHandlerType()==CollisionHandlerType.SOLID && !body2.isAbove(body1) ) {
        			body2.setCollisionWithOneWayPlatform(true);
        			return true;
        		}
        		
        		break;
        		
        	case NO_GRAV: // TODO
        		break;	
        	case SOLID:
        		 switch( body2.getCollisionHandlerType() ) {
	        		 case ONE_WAY:
	             		if( !body1.isAbove(body2) ) {
	             			body1.setCollisionWithOneWayPlatform(true);
	             			return true;
	             		}
	             		
	             		break;
	             		
	             	case NO_GRAV: // TODO
	             		break;
	             	case SOLID:
	             		break;
        		 }
        }
        
        return false;
	}

}
