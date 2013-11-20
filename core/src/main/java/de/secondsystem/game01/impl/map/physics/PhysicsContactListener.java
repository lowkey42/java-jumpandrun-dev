package de.secondsystem.game01.impl.map.physics;

import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.dynamics.contacts.Contact;

class PhysicsContactListener implements ContactListener {

	@Override
	public void beginContact(Contact contact) {
		Box2dPhysicsBody body1 = (Box2dPhysicsBody) contact.getFixtureA().getBody().getUserData();
		Box2dPhysicsBody body2 = (Box2dPhysicsBody) contact.getFixtureB().getBody().getUserData();
		
		if( !isFiltered(body1, body2) ) {
			if( body1.beginContact(contact, body2) && contact.getFixtureA().getUserData() != null && body2.getCollisionHandlerType() != CollisionHandlerType.NO_GRAV )
				body1.incFootContacts();
			if( body2.beginContact(contact, body1) && contact.getFixtureB().getUserData() != null && body1.getCollisionHandlerType() != CollisionHandlerType.NO_GRAV )
				body2.incFootContacts();
		}
	}

	@Override
	public void endContact(Contact contact) {
		Box2dPhysicsBody body1 = (Box2dPhysicsBody) contact.getFixtureA().getBody().getUserData();
		Box2dPhysicsBody body2 = (Box2dPhysicsBody) contact.getFixtureB().getBody().getUserData();
		
		if( body1.endContact(contact, body2) && contact.getFixtureA().getUserData() != null && body2.getCollisionHandlerType() != CollisionHandlerType.NO_GRAV )
			body1.decFootContacts();
		if( body2.endContact(contact, body1) && contact.getFixtureB().getUserData() != null && body1.getCollisionHandlerType() != CollisionHandlerType.NO_GRAV )
			body2.decFootContacts();
	}

	@Override
	public void postSolve(Contact contact, ContactImpulse arg1) {
		// not used currently
	}

	@Override
	public void preSolve(Contact contact, Manifold arg1) {
        Box2dPhysicsBody body1 = (Box2dPhysicsBody) contact.m_fixtureA.getBody().getUserData();
        Box2dPhysicsBody body2 = (Box2dPhysicsBody) contact.m_fixtureB.getBody().getUserData();
        
        contact.setEnabled(!isFiltered(body1, body2));
	}
	
	protected boolean isFiltered(Box2dPhysicsBody body1, Box2dPhysicsBody body2) {
        if( body1.getGameWorldId() != body2.getGameWorldId() )
        	return true;
        
        // dispatch and handle the different collisionTypes
        switch( body1.getCollisionHandlerType() ) {
        	case ONE_WAY:
        		if( body2.getCollisionHandlerType()==CollisionHandlerType.SOLID && body2.body.getLinearVelocity().y < 0 )
        			return true;
        		
        		break;
        		
        	case NO_GRAV: // TODO
        		break;	
        	case SOLID:
        		 switch( body2.getCollisionHandlerType() ) {
	        		 case ONE_WAY:
	             		if( body1.body.getLinearVelocity().y < 0 )
	             			return true;
	             		
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
