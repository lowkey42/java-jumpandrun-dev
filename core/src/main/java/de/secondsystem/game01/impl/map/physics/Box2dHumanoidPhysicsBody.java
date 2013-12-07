package de.secondsystem.game01.impl.map.physics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.contacts.Contact;
import org.jsfml.system.Vector2f;


class Box2dHumanoidPhysicsBody extends Box2dDynamicPhysicsBody implements
		IHumanoidPhysicsBody {
	
	private final float maxSlope;
	private final float maxReach;
	private final ObjectDetector exactObjects = new ObjectDetector();
	private final ObjectDetector leftObjects = new ObjectDetector();
	private final ObjectDetector rightObjects = new ObjectDetector();
		
	
	private float maxThrowVel;
	private float maxLiftWeight;
	private Body liftingBody = null;

	private IPhysicsBody currentClimbingLadder;

	
	Box2dHumanoidPhysicsBody(Box2dPhysicalWorld world, int gameWorldId, 
			float width, float height, boolean interactive, boolean liftable, CollisionHandlerType type, 
			float maxXVel, float maxYVel,
			float maxThrowVel, float maxLiftWeight, float maxSlope, float maxReach) {
		super(world, gameWorldId, width, height, interactive, liftable, type, false, true, true, maxXVel, maxYVel);
		this.maxThrowVel = maxThrowVel;
		this.maxLiftWeight = maxLiftWeight;
		
		this.maxSlope = maxSlope;
		this.maxReach = maxReach;
	}

	@Override
	protected boolean isBodyRotationFixed() {
		return true;
	}
	@Override
	protected void createFixtures(Body body, PhysicsBodyShape shape, float friction, float restitution, float density, Float fixedWeight) {
		createWorldSwitchFixture(body, shape, friction, restitution, density, fixedWeight);
		
		final float baseRad = (float) Math.floor(getWidth()/2 );
		final float baseYOffset = Math.min( (float) (Math.tan(Math.toRadians(maxSlope)) * getWidth()/2), baseRad*2); // causes glitches (entity gets stuck on edges)
		
		FixtureDef mainBody = new FixtureDef();
		mainBody.shape = createShape(PhysicsBodyShape.BOX, getWidth(), getHeight()-baseYOffset, 0, -baseYOffset/2, 0 );
		mainBody.friction = 0.f;
		mainBody.restitution = 0.3f;
		mainBody.density = 1.0f;
		mainBody.userData = new Box2dContactListener.FixtureData(false, false, exactObjects);
		body.createFixture(mainBody);
		
		FixtureDef baseBody = new FixtureDef();
		baseBody.shape = createShape(PhysicsBodyShape.CIRCLE, baseRad, baseRad, 0, getHeight()/2-baseRad, 0);
		baseBody.friction = 1.0f;
		baseBody.density = 1.0f;
		baseBody.userData = new Box2dContactListener.FixtureData(false, false, exactObjects);
		body.createFixture(baseBody);
		
		FixtureDef fd = new FixtureDef();
		fd.shape = createShape(PhysicsBodyShape.BOX, getWidth() / 2f, getHeight()/2, 0, getHeight()/2, 0);
		fd.isSensor = true;
		fd.userData = new Box2dContactListener.FixtureData(false, new StableCheckFCL());
		
		body.setLinearDamping(1.0f);
		body.createFixture(fd);
		
		FixtureDef leftObjSensor = new FixtureDef();
		leftObjSensor.shape = createShape(PhysicsBodyShape.BOX, getWidth()/2+maxReach, getHeight(), -maxReach-getWidth()/4, 0, 0 );
		leftObjSensor.isSensor = true;
		leftObjSensor.userData = new Box2dContactListener.FixtureData(false, leftObjects);
		body.createFixture(leftObjSensor);
		
		FixtureDef rightObjSensor = new FixtureDef();
		rightObjSensor.shape = createShape(PhysicsBodyShape.BOX, getWidth()/2 +maxReach, getHeight(), maxReach+getWidth()/4, 0, 0 );
		rightObjSensor.isSensor = true;
		rightObjSensor.userData = new Box2dContactListener.FixtureData(false, rightObjects);
		body.createFixture(rightObjSensor);
	}
	
	private static final class ObjectDetector implements Box2dContactListener.FixtureContactListener {
		public final Set<IPhysicsBody> bodies = new HashSet<>();
		public final Map<IPhysicsBody, Runnable> callbacks = new HashMap<>();
		
		@Override public void onBeginContact(Contact contact, Box2dPhysicsBody other,
				Fixture fixture) {
			bodies.add(other);
		}

		@Override public void onEndContact(Contact contact, Box2dPhysicsBody other,
				Fixture fixture) {
			if( bodies.remove(other) ) {
				Runnable cb = callbacks.get(other);
				if( cb!=null )
					cb.run();
			}
		}
	}
	
	@Override
	public boolean liftBody(IPhysicsBody other) {
		if( other.getWeight()>maxLiftWeight )
			return false;
		
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
		List<IPhysicsBody> bodies = new ArrayList<>(leftObjects.bodies.size() + rightObjects.bodies.size());
	
		for( IPhysicsBody body : leftObjects.bodies )
			if( body.isInteractive() || body.isLiftable() )
				bodies.add(body);
		
		for( IPhysicsBody body : rightObjects.bodies )
			if( body.isInteractive() || body.isLiftable() )
				bodies.add(body);
		
		return bodies;
	}

	@Override
	public IPhysicsBody getNearestInteractiveBody(Vector2f direction) {
		final boolean leftFirst = direction.x<0;
		final float x = getPosition().x;
		final float y = getPosition().y;
		
		double nearestDist = Float.MAX_VALUE;
		IPhysicsBody nearestBody = null;
		
		Collection<IPhysicsBody> bodies = leftFirst ? leftObjects.bodies : rightObjects.bodies;
		for( IPhysicsBody body : bodies ) {
			if( body.isInteractive() || body.isLiftable() ) {
				double dist = Math.pow(x-body.getPosition().x, 2) + Math.pow(y-body.getPosition().y, 2);
				if( dist<=nearestDist ) {
					nearestDist = dist;
					nearestBody = body;
				}
			}
		}
		
		if( nearestDist>Math.pow(getWidth()/2, 2) ) {
			nearestDist-=maxReach;
			
			bodies = !leftFirst ? leftObjects.bodies : rightObjects.bodies;
			for( IPhysicsBody body : bodies ) {
				if( body.isInteractive() || body.isLiftable() ) {
					double dist = Math.pow(x-body.getPosition().x, 2) + Math.pow(y-body.getPosition().y, 2);
					if( dist<=nearestDist ) {
						nearestDist = dist;
						nearestBody = body;
					}
				}
			}
		}
		
		return nearestBody;
	}

	@Override
	public void setMaxThrowVelocity(float vel) {
		maxThrowVel = vel;
	}

	@Override
	public void setMaxLiftWeight(float weight) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean tryClimbing() {
		if( isClimbing() )
			return true;
		
		
		for( IPhysicsBody otherBody : exactObjects.bodies ) {
			if( otherBody.getCollisionHandlerType()==CollisionHandlerType.CLIMBABLE ) {
				currentClimbingLadder = otherBody;

				body.setGravityScale(0.f);
				body.setLinearDamping(10.0f);
				
				exactObjects.callbacks.put(otherBody, new Runnable() {
					
					@Override
					public void run() {
						stopClimbing();
					}
				});
				
				return true;
			}
		}
		
		return false;
	}

	@Override
	public void stopClimbing() {
		currentClimbingLadder = null;
		body.setGravityScale(1.f);
		body.setLinearDamping(1.0f);
	}
	
	@Override
	public boolean isClimbing() {
		return currentClimbingLadder!=null;
	}

	@Override
	public boolean isContactFiltered(Contact contact, Box2dPhysicsBody other, Fixture ownFixture, Fixture otherFixture) {
		if( isClimbing() && other.getCollisionHandlerType()==CollisionHandlerType.ONE_WAY )
			return true;
		
		return false;
	}
	
	@Override
	public boolean isLiftingSomething() {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public void setIdle(boolean idle) {
		this.idle = idle;
		//baseFixture.m_friction = idle ? 100.f : 0.1f;
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
