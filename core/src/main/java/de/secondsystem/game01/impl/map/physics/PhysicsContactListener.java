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
		
		// fixtureUserDataA/B
		Object fixtureUDA = contact.getFixtureA().getUserData();
		Object fixtureUDB = contact.getFixtureB().getUserData();
		boolean isFootA = fixtureUDA != null && ((String)fixtureUDA).compareTo("foot") == 0 ? true : false;
		boolean isFootB = fixtureUDB != null && ((String)fixtureUDB).compareTo("foot") == 0 ? true : false;
		
		boolean isHandA = fixtureUDA != null && ((String)fixtureUDA).compareTo("hand") == 0 ? true : false;
		boolean isHandB = fixtureUDB != null && ((String)fixtureUDB).compareTo("hand") == 0 ? true : false;
		
		if( !isFiltered(body1, body2) ) {
			if( body1.beginContact(contact, body2) )
				if( isFootA && body2.getCollisionHandlerType() != CollisionHandlerType.NO_GRAV )
					body1.incFootContacts();
				else
					if( isHandA )
						body1.setTouchingBody(body2);
			
			if( body2.beginContact(contact, body1) )
				if( isFootB && body1.getCollisionHandlerType() != CollisionHandlerType.NO_GRAV )
					body2.incFootContacts();
				else
					if( isHandB )
						body2.setTouchingBody(body1);
		}
	}

	@Override
	public void endContact(Contact contact) {
		Box2dPhysicsBody body1 = (Box2dPhysicsBody) contact.getFixtureA().getBody().getUserData();
		Box2dPhysicsBody body2 = (Box2dPhysicsBody) contact.getFixtureB().getBody().getUserData();
		
		// fixtureUserDataA/B
		Object fixtureUDA = contact.getFixtureA().getUserData();
		Object fixtureUDB = contact.getFixtureB().getUserData();
		boolean isFootA = fixtureUDA != null && ((String)fixtureUDA).compareTo("foot") == 0 ? true : false;
		boolean isFootB = fixtureUDB != null && ((String)fixtureUDB).compareTo("foot") == 0 ? true : false;
		
		boolean isHandA = fixtureUDA != null && ((String)fixtureUDA).compareTo("hand") == 0 ? true : false;
		boolean isHandB = fixtureUDB != null && ((String)fixtureUDB).compareTo("hand") == 0 ? true : false;
		
		if( body1.endContact(contact, body2) )
			if( isFootA && body2.getCollisionHandlerType() != CollisionHandlerType.NO_GRAV )
				body1.decFootContacts();
			else
				if( isHandA )
					body1.setTouchingBody(null);
		
		if( body2.endContact(contact, body1) )
			if( isFootB && body1.getCollisionHandlerType() != CollisionHandlerType.NO_GRAV )
				body2.decFootContacts();
			else
				if( isHandB )
					body2.setTouchingBody(null);
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
