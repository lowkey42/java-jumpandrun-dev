package de.secondsystem.game01.impl.map.physics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jbox2d.collision.shapes.MassData;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.contacts.Contact;
import org.jbox2d.dynamics.contacts.ContactEdge;
import org.jsfml.system.Vector2f;


class Box2dHumanoidPhysicsBody extends Box2dDynamicPhysicsBody implements
		IHumanoidPhysicsBody {
	
	private final static float BASE_HEIGHT = 5;
	
	private final float maxSlope;
	private final float maxReach;
	private final BodySensor exactObjects = new BodySensor();
	private final ObjectDetector leftObjects = new ObjectDetector();
	private final ObjectDetector rightObjects = new ObjectDetector();
		
	
	private float maxThrowVel;
	private float maxLiftWeight;
	private IPhysicsBody liftingBody = null;

	private IPhysicsBody currentClimbingLadder;

	private Fixture baseFixture;
	
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
		
		/*
		 * 0________1
		 *  |      |
		 *  |      |
		 * 5|      |2
		 *   \____/
		 *   4    3
		 */
		
		final float halfB2Width = getWidth() / 2f * BOX2D_SCALE_FACTOR;
		final float halfB2Height = getHeight() / 2f * BOX2D_SCALE_FACTOR;

		float baseHeight = BASE_HEIGHT;
		float baseWidth = (BASE_HEIGHT*2) / (float) Math.tan(Math.toRadians(maxSlope));
		
		if( baseHeight>=getHeight() || baseWidth*2>=getWidth() ) {
			System.err.println("The HumanoidPhysicsBody is smaller than its base ("+baseHeight+">="+getHeight()+" || "+(baseWidth*2)+"+>="+getWidth()+"): THE END IS NEAR!?!?! (Changed values to fit)");
			baseHeight = getHeight()/2;
			baseWidth = Math.min(baseHeight / (float) Math.tan(Math.toRadians(maxSlope)), getWidth()/3);
		}
		
		final float b2BaseHeight = baseHeight * BOX2D_SCALE_FACTOR;
		final float b2BaseWidth = baseWidth * BOX2D_SCALE_FACTOR;
		
		FixtureDef mainBody = new FixtureDef();
		PolygonShape mbs = new PolygonShape();
		
		mbs.m_count = 6;
		mbs.set(new Vec2[]{
			/*0*/ new Vec2(-halfB2Width, -halfB2Height),
			/*1*/ new Vec2( halfB2Width, -halfB2Height),
			/*2*/ new Vec2( halfB2Width, halfB2Height-b2BaseHeight),
			/*3*/ new Vec2( halfB2Width-b2BaseWidth, halfB2Height),
			/*4*/ new Vec2(-halfB2Width+b2BaseWidth, halfB2Height),
			/*5*/ new Vec2(-halfB2Width, halfB2Height-b2BaseHeight),
		}, 6);
				
		mainBody.shape = mbs;
		mainBody.friction = 0.f;
		mainBody.restitution = 0.f;
		mainBody.density = 1.0f;
		mainBody.userData = new Box2dContactListener.FixtureData(false, false, exactObjects);
		baseFixture = body.createFixture(mainBody);
				
		FixtureDef fd = new FixtureDef();
		fd.shape = createShape(PhysicsBodyShape.BOX, getWidth()-baseWidth*2, 2, 0, getHeight()/2+1, 0);
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

	@Override
	public Vec2 getBodyCenterCorrection() {
		return new Vec2(0, -BASE_HEIGHT);
	}
	
	private static final class BodySensor extends ObjectDetector {

		private Set<IPhysicsBody> contactsWithSolids = new HashSet<>();
		
		@Override
		public void onBeginContact(Contact contact, Box2dPhysicsBody other,
				Fixture fixture) {
			super.onBeginContact(contact, other, fixture);
			
			if( contact.isEnabled() && contact.isTouching() && other.getCollisionHandlerType()==CollisionHandlerType.SOLID )
				contactsWithSolids.add(other);
		}

		@Override
		public void onEndContact(Contact contact, Box2dPhysicsBody other,
				Fixture fixture) {
			super.onEndContact(contact, other, fixture);

			contactsWithSolids.remove(other);
		}
		
		public boolean hasContactWithSolid() {
			return !contactsWithSolids.isEmpty();
		}
	}
	private static class ObjectDetector implements Box2dContactListener.FixtureContactListener {
		final Set<IPhysicsBody> bodies = new HashSet<>();
		final Map<IPhysicsBody, Runnable> callbacks = new HashMap<>();
		
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
		
		if( !leftObjects.bodies.contains(other) && !rightObjects.bodies.contains(other) )
			return false;
		
		if( bind(other, new Vector2f(getWidth()/2*BOX2D_SCALE_FACTOR, other.getHeight()/1.9f*BOX2D_SCALE_FACTOR)) ) {
			liftingBody = other;
			
			return true;
		}
		
		return false;
	}

	@Override
	public boolean throwLiftedBody(float strength, Vector2f direction) {
		if( liftingBody!=null ) {
			unbind(liftingBody);
			((Box2dPhysicsBody)liftingBody).body.applyForceToCenter(new Vec2(direction.x*strength, direction.y*strength));
			
			return true;
		}
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
	public byte move(float x, float y) {
		if( isClimbing() ) {
			resetVelocity(true, true, false);
			return super.move(x/1.5f, y);
		}
				
		return super.move(isStable() || !exactObjects.hasContactWithSolid() ? x : 0, y);
	}
	
	@Override
	public boolean tryClimbing() {
		if( isClimbing() )
			return true;
		
		
		for( IPhysicsBody otherBody : exactObjects.bodies ) {
			if( otherBody.getCollisionHandlerType()==CollisionHandlerType.CLIMBABLE ) {
				currentClimbingLadder = otherBody;

				body.setGravityScale(0.f);
				body.setLinearDamping(1.0f);
				
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
		return liftingBody!=null;
	}
	
	@Override
	public void setIdle(boolean idle) {
		this.idle = idle;
		float newFriction = idle && isStable() ? 100.f : 0.0f;
		if( baseFixture.m_friction!=newFriction ) {
			baseFixture.m_friction = newFriction;
			ContactEdge contact = baseFixture.getBody().m_contactList;
			while( contact!=null ) {
				if( contact.contact.m_fixtureA==baseFixture || contact.contact.m_fixtureB==baseFixture ) {
					contact.contact.m_friction = contact.contact.m_fixtureA.m_friction * contact.contact.m_fixtureB.m_friction;
				}
				
				contact = contact.next;
			}
		}
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
