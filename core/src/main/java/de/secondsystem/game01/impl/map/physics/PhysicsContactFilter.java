package de.secondsystem.game01.impl.map.physics;

import org.jbox2d.callbacks.ContactFilter;
import org.jbox2d.dynamics.Fixture;

import de.secondsystem.game01.impl.map.objects.CollisionObject;
import de.secondsystem.game01.impl.map.objects.CollisionObject.CollisionType;
import de.secondsystem.game01.impl.map.objects.TestCharacter;

public class PhysicsContactFilter extends ContactFilter {
	
	public boolean shouldCollide(Fixture fixtureA, Fixture fixtureB)
	{
        PhysicsBody body1 = (PhysicsBody) fixtureA.getBody().getUserData();
        PhysicsBody body2 = (PhysicsBody) fixtureB.getBody().getUserData();
        
        if( body1.getGameWorldId() != body2.getGameWorldId() )
        	return false;
        
        CollisionObject co = (CollisionObject) (body1 instanceof CollisionObject ? body1 : (body2 instanceof CollisionObject) ? body2 : null);
        TestCharacter   tc = (TestCharacter) (body1 instanceof TestCharacter ? body1 : (body2 instanceof TestCharacter) ? body2 : null);
        
        if( co != null && tc != null)
        {
        	if( co.getType() == CollisionType.ONE_WAY && tc.getBody().getLinearVelocity().y < 0)
        			return false;
        }
        
        return super.shouldCollide(fixtureA, fixtureB);
	}
}
