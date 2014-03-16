package de.secondsystem.game01.impl.map.physics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.contacts.Contact;
import org.jbox2d.dynamics.contacts.ContactEdge;
import org.jbox2d.dynamics.joints.RevoluteJoint;
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
	private float maxLiftForce;
	private IPhysicsBody liftedBody = null;
	private RevoluteJoint liftJoint = null;

	private IPhysicsBody currentClimbingLadder;

	private Fixture baseFixture;
	
	Box2dHumanoidPhysicsBody(Box2dPhysicalWorld world, int gameWorldId, 
			float width, float height, boolean interactive, boolean liftable, CollisionHandlerType type, 
			float maxXVel, float maxYVel,
			float maxThrowVel, float maxLiftWeight, float maxLiftForce, float maxSlope, float maxReach) {
		super(world, gameWorldId, width, height, interactive, liftable, type, false, true, true, maxXVel, maxYVel);
		this.maxThrowVel = maxThrowVel;
		this.maxLiftWeight = maxLiftWeight;
		this.maxLiftForce = maxLiftForce;
		
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
		mainBody.userData = new Box2dContactListener.FixtureData(false, false, true, exactObjects);
		baseFixture = body.createFixture(mainBody);
				
		FixtureDef fd = new FixtureDef();
		fd.shape = createShape(PhysicsBodyShape.BOX, getWidth()-baseWidth*2, 2, 0, getHeight()/2+1, 0);
		fd.isSensor = true;
		fd.userData = new Box2dContactListener.FixtureData(false, true, false, new StableCheckFCL());
		
		body.setLinearDamping(1.0f);
		body.createFixture(fd);
		
		FixtureDef leftObjSensor = new FixtureDef();
		leftObjSensor.shape = createShape(PhysicsBodyShape.BOX, getWidth()/2+maxReach, getHeight(), -maxReach-getWidth()/4, 0, 0 );
		leftObjSensor.isSensor = true;
		leftObjSensor.userData = new Box2dContactListener.FixtureData(false, true, false, leftObjects);
		body.createFixture(leftObjSensor);
		
		FixtureDef rightObjSensor = new FixtureDef();
		rightObjSensor.shape = createShape(PhysicsBodyShape.BOX, getWidth()/2 +maxReach, getHeight(), maxReach+getWidth()/4, 0, 0 );
		rightObjSensor.isSensor = true;
		rightObjSensor.userData = new Box2dContactListener.FixtureData(false, true, false, rightObjects);
		body.createFixture(rightObjSensor);
	}

	@Override
	public Vec2 getBodyCenterCorrection() {
		return new Vec2(0, -BASE_HEIGHT);
	}
	
	private final class BodySensor extends ObjectDetector {

		private Set<IPhysicsBody> contactsWithSolids = new HashSet<>();
		
		@Override
		public void onBeginContact(Contact contact, Box2dPhysicsBody other,
				Fixture fixture) {
			super.onBeginContact(contact, other, fixture);
			
			if( contact.isEnabled() && contact.isTouching() && other.getCollisionHandlerType()==CollisionHandlerType.SOLID && other!=liftedBody )
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
		
		liftJoint = bind(other, getPosition(), maxLiftForce);
		
		if( liftJoint!=null ) {
			liftedBody = other;
			((Box2dPhysicsBody)liftedBody).liftingBody = this;
			
			return true;
		}
		
		return false;
	}

	@Override
	public IPhysicsBody throwLiftedBody(float strength, Vector2f direction) {
		if( isLiftingSomething() ) {
			final Box2dPhysicsBody lBody = (Box2dPhysicsBody) liftedBody;
			
			strength = Math.min(maxThrowVel, strength);
			unbind(lBody);
			lBody.body.applyForceToCenter(new Vec2(direction.x*strength, direction.y*strength));
			lBody.liftingBody = null;
			liftedBody = null;
			liftJoint = null;
			
			return lBody;
		}
		return null;
	}

	private final Comparator<IPhysicsBody> distanceComparator = new Comparator<IPhysicsBody>() {
		@Override public int compare(IPhysicsBody o1, IPhysicsBody o2) {
			double dist1 = (getPosition().x-o1.getPosition().x)*(getPosition().x-o1.getPosition().x)
					+ (getPosition().y-o1.getPosition().y)*(getPosition().y-o1.getPosition().y);
			
			double dist2 = (getPosition().x-o2.getPosition().x)*(getPosition().x-o2.getPosition().x)
					+ (getPosition().y-o2.getPosition().y)*(getPosition().y-o2.getPosition().y);
			
			return Double.valueOf(dist1).compareTo(dist2);
		}
	};
	
	@Override
	public List<IPhysicsBody> listNearBodies(Vector2f direction, boolean checkBack, BodyFilter filter) {
		List<IPhysicsBody> bodies = new ArrayList<>(leftObjects.bodies.size() + rightObjects.bodies.size());
	
		if( direction.x<0 ) {
			for( IPhysicsBody body : leftObjects.bodies )
				if( filter.accept(body) )
					bodies.add(body);

			Collections.sort(bodies, distanceComparator);
			
			if( checkBack )
				for( IPhysicsBody body : rightObjects.bodies )
					if( filter.accept(body) )
						bodies.add(body);
		
		} else {
			for( IPhysicsBody body : rightObjects.bodies )
				if( filter.accept(body) )
					bodies.add(body);

			Collections.sort(bodies, distanceComparator);

			if( checkBack )
				for( IPhysicsBody body : leftObjects.bodies )
					if( filter.accept(body) )
						bodies.add(body);
		}
		
		return bodies;
	}

	@Override
	public IPhysicsBody getNearestBody(Vector2f direction, BodyFilter filter) {
		final boolean leftFirst = direction.x<0;
		final float x = getPosition().x;
		final float y = getPosition().y;
		
		double nearestDist = Float.MAX_VALUE;
		IPhysicsBody nearestBody = null;
		
		Collection<IPhysicsBody> bodies = leftFirst ? leftObjects.bodies : rightObjects.bodies;
		for( IPhysicsBody body : bodies ) {
			if( filter.accept(body) ) {
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
				if( filter.accept(body) ) {
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
		maxLiftWeight = weight;
	}

	@Override
	public byte move(float x, float y) {
		if( isLiftingSomething() ) {
			float a = ((float) Math.toDegrees(Math.atan2(getPosition().y-liftedBody.getPosition().y, getPosition().x-liftedBody.getPosition().x))-90) % 360;
			a = (a < 0 ? 360 + a : a);
			
			if( a<350 && a>=180  )
				liftJoint.setMotorSpeed(Math.min((355-a)/4, 10));
			else if( a>10 )
				liftJoint.setMotorSpeed(-Math.min(a/4, 10));
			else
				liftJoint.setMotorSpeed(0);
		}
		
		if( isClimbing() ) {
			resetVelocity(true, true, false);
			return super.move(x/1.5f, y);
		}
		
		return super.move(isStable() || !exactObjects.hasContactWithSolid() ? x : x/5, y);
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
		return liftedBody!=null;
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
}
