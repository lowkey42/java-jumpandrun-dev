package de.secondsystem.game01.impl.map.physics;

import org.jbox2d.callbacks.ContactFilter;
import org.jbox2d.dynamics.Fixture;

class PhysicsContactFilter extends ContactFilter {
	
	public boolean shouldCollide(Fixture fixtureA, Fixture fixtureB)
	{
        Box2dPhysicsBody body1 = (Box2dPhysicsBody) fixtureA.getBody().getUserData();
        Box2dPhysicsBody body2 = (Box2dPhysicsBody) fixtureB.getBody().getUserData();
        
        if( body1.getGameWorldId() != body2.getGameWorldId() )
        	return false;
        
        // dispatch and handle the different collisionTypes
        switch( body1.getCollisionHandlerType() ) {
        	case ONE_WAY:
        		if( body2.getCollisionHandlerType()==CollisionHandlerType.SOLID && body2.body.getLinearVelocity().y < 0 )
        			return false;
        		
        		break;
        		
        	case NO_GRAV: // TODO
        		break;	
        	case SOLID:
        		 switch( body2.getCollisionHandlerType() ) {
	        		 case ONE_WAY:
	             		if( body1.body.getLinearVelocity().y < 0 )
	             			return false;
	             		
	             		break;
	             		
	             	case NO_GRAV: // TODO
	             		break;
	             	case SOLID:
	             		break;
        		 }
        }
        
        return super.shouldCollide(fixtureA, fixtureB);
	}
}
