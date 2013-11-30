package de.secondsystem.game01.impl.map.physics;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.contacts.Contact;
import org.jsfml.system.Vector2f;


class Box2dHumanoidPhysicsBody extends Box2dDynamicPhysicsBody implements
		IHumanoidPhysicsBody {
	
	private final float maxSlope;
	
	private float maxThrowVel;
	private Body liftingBody = null;

	private final Set<Contact> activeContacts = new HashSet<>();

	protected int numFootContacts = 0;
	private boolean climbing;
	private boolean collisionWithLadder = false;
	private final List<Box2dPhysicsBody> touchingBodiesRight = new ArrayList<>();
	private final List<Box2dPhysicsBody> touchingBodiesLeft  = new ArrayList<>();

	@Deprecated
	private boolean collisionWithOneWayPlatform = false;

	
	Box2dHumanoidPhysicsBody(Box2dPhysicalWorld world, int gameWorldId, 
			float width, float height, CollisionHandlerType type, 
			float maxXVel, float maxYVel,
			float maxThrowVel, float maxLiftWeight, float maxSlope, float maxReach) {
		super(world, gameWorldId, width, height, type, true, true, maxXVel, maxYVel);
		this.maxThrowVel = maxThrowVel;
		
		this.maxSlope = maxSlope;
	}

	@Override
	protected boolean isBodyRotationFixed() {
		return true;
	}
	@Override
	protected void createFixtures(Body body, PhysicsBodyShape shape, float friction, float restitution, float density, Float fixedWeight) {
		createWorldSwitchFixture(body, shape, friction, restitution, density, fixedWeight);
		
		final float baseRad = (float) Math.floor(getWidth()*.9f /2 +0.5);
		final float baseYOffset = Math.min( (float) (Math.tan(Math.toRadians(maxSlope)) * getWidth()/2), baseRad*2);
		
		FixtureDef mainBody = new FixtureDef();
		mainBody.shape = createShape(PhysicsBodyShape.BOX, getWidth(), getHeight()-baseYOffset, 0, -baseYOffset/2, 0 );
		mainBody.friction = 0.f;
		mainBody.restitution = 0.01f;
		mainBody.density = 1.0f;
		body.createFixture(mainBody);
		
		FixtureDef baseBody = new FixtureDef();
		baseBody.shape = createShape(PhysicsBodyShape.CIRCLE, baseRad, baseRad, 0, getHeight()/2-baseRad, 0);
		baseBody.friction = 1.0f;
		baseBody.density = 1.0f;
		baseBody.userData = new Box2dContactListener.FixtureData(false, new StableCheckFCL());
		body.createFixture(baseBody);
		
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
