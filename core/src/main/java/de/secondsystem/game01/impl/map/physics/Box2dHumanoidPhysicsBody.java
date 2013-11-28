package de.secondsystem.game01.impl.map.physics;

import java.util.List;

import org.jbox2d.dynamics.Body;
import org.jsfml.system.Vector2f;


class Box2dHumanoidPhysicsBody extends Box2dDynamicPhysicsBody implements
		IHumanoidPhysicsBody {
	
	private float maxThrowVel;
	private Body liftingBody = null;

	Box2dHumanoidPhysicsBody(Box2dPhysicalWorld world, int gameWorldId, 
			float width, float height, CollisionHandlerType type, 
			boolean createFoot, boolean createTestFixture, float maxXVel, float maxYVel,
			float maxThrowVel, float maxLiftWeight, float maxSlope, float maxReach) {
		super(world, gameWorldId, width, height, type, createFoot, createTestFixture, maxXVel, maxYVel);
		this.maxThrowVel = maxThrowVel;
	}

	@Override
	protected boolean isBodyRotationFixed() {
		return true;
	}
	@Override
	protected Body createBody(float x, float y, float rotation, PhysicsBodyShape shape, float friction, float restitution, float density, Float fixedWeight) {
		// TODO
		return null;
	}
	
	@Override
	public boolean isClimbing() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean liftBody(IPhysicsBody other) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean throwLiftedBody(float strength, Vector2f direction) {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public List<IPhysicsBody> listInteractiveBodies() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IPhysicsBody getNearestInteractiveBody(Vector2f direction) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setMaxThrowVelocity(float vel) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setMaxLiftWeight(float weight) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean tryClimbing() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void stopClimbing() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isLiftingSomething() {
		// TODO Auto-generated method stub
		return false;
	}

//	public void setGameWorldId(int id) {
//		worldIdMask = id;
//		
//		// could/should be done much less hackish
//		boolean gravContact = false;
//		
//		for( ContactEdge c=body.getContactList(); c!=null;  c=c.next ) {
//			Box2dPhysicsBody body1 = (Box2dPhysicsBody) c.contact.getFixtureA().getBody().getUserData();
//			Box2dPhysicsBody body2 = (Box2dPhysicsBody) c.contact.getFixtureB().getBody().getUserData();
//			
//			
//			
//			if( (body1.getGameWorldId() == body2.getGameWorldId()) && (body1.getCollisionHandlerType()==CollisionHandlerType.NO_GRAV || body2.getCollisionHandlerType()==CollisionHandlerType.NO_GRAV) ) {
//				gravContact = true;
//				break;
//			}
//		}
//		
//		if( !gravContact ) {
//			collisionWithLadder = false;
//			body.setGravityScale(1.f);
//			climbing = false;
//		} else {
//			collisionWithLadder = true;
//		}
//	}
	

//	@Override
//	public void throwBoundBody(float x, float y) {
//		Body body = revoluteJoint.getBodyB();
//		unbind();
//		x = x < 0 ? Math.max(x, -maxThrowVel) : Math.min(x, maxThrowVel);
//		y = y < 0 ? Math.max(y, -maxThrowVel) : Math.min(y, maxThrowVel);
//		Box2dPhysicsBody b = ((Box2dPhysicsBody)body.getUserData());
//		float newX = x > 0 ? b.getPosition().x+width/2.f+20f : b.getPosition().x-width/2.f-20f;
//		if( y > 0) 
//			b.forcePosition(newX, b.getPosition().y);
//		
//		if( Math.abs(x) < 1.f ) {
//			b.forcePosition(newX, b.getPosition().y + b.height/2.f);
//		}
//		else
//			body.applyLinearImpulse(new Vec2(x, y), body.getPosition());
//	}
//
//	@Override
//	public void setMaxThrowVelocity(float vel) {
//		maxThrowVel = vel;
//	}
//	
//	@Override
//	public IPhysicsBody getTouchingBodyRight() {
//		return touchingBodiesRight.size() > 0 ? touchingBodiesRight.get(0) : null;
//	}
//
//	@Override
//	public IPhysicsBody getTouchingBodyLeft() {
//		return touchingBodiesLeft.size() > 0 ? touchingBodiesLeft.get(0) : null;
//	}

}
