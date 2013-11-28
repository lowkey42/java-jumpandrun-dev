package de.secondsystem.game01.impl.map.physics;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.contacts.Contact;
import org.jsfml.system.Vector2f;

class Box2dDynamicPhysicsBody extends Box2dPhysicsBody implements
		IDynamicPhysicsBody {

	private float maxXVel;
	private float maxYVel;

	private boolean collisionWithOneWayPlatform = false;

	// if the testFixture is colliding in the other world then don't allow switching the world
	private int collisionsWithTestFixture;

	private final Set<Contact> activeContacts = new HashSet<>();

	protected int numFootContacts = 0;
	private boolean climbing;
	private boolean collisionWithLadder = false;
	private final List<Box2dPhysicsBody> touchingBodiesRight = new ArrayList<>();
	private final List<Box2dPhysicsBody> touchingBodiesLeft  = new ArrayList<>();
	
	Box2dDynamicPhysicsBody(Box2dPhysicalWorld world, int gameWorldId, 
			float width, float height, CollisionHandlerType type, 
			boolean createFoot, boolean createTestFixture, float maxXVel, float maxYVel) {
		super(world, gameWorldId, width, height, type);
		
		this.maxXVel = maxXVel;
		this.maxYVel = maxYVel;
	}

	@Override
	public boolean isStatic() {
		return false;
	}
	@Override
	protected Body createBody(float x, float y, float rotation, PhysicsBodyShape shape, float friction, float restitution, float density, Float fixedWeight) {
		return super.createBody(x, y, rotation, shape, friction, restitution, density, fixedWeight);
	}

	public void addCollisionsWithTestFixture(int num) {
		collisionsWithTestFixture += num;
	}
	

	@Override
	public boolean isStable() {
		return numFootContacts > 0 && !collisionWithOneWayPlatform;
	}

	@Override
	public byte move(float x, float y) {
		x = limit(getBody().getLinearVelocity().x, x, maxXVel);
		y = limit(getBody().getLinearVelocity().y, y, maxYVel);

		getBody().applyForce(new Vec2(x, y), getBody().getWorldCenter());
		//body.applyLinearImpulse(new Vec2(x/15, y/65), body.getWorldCenter());

		return (byte) ((x != 0 ? 2 : 0) & (y != 0 ? 1 : 0));
	}

	private static float limit(float current, float mod, float max) {
		return mod < 0 ? Math.max(mod, -max - current) : Math.min(mod, max
				- current);
	}

	@Override
	public void rotate(float angle) {
		getBody().applyAngularImpulse((float) Math.toRadians(angle));
	}

	@Override
	public void resetVelocity(boolean x, boolean y, boolean rotation) {
		getBody().setLinearVelocity(new Vec2(x ? 0 : getBody().getLinearVelocity().x,
				y ? 0 : getBody().getLinearVelocity().y));

		if (rotation)
			getBody().setAngularVelocity(0);
	}

	@Override
	public void setMaxVelocityX(float x) {
		maxXVel = x;
	}

	@Override
	public void setMaxVelocityY(float y) {
		maxYVel = y;
	}

	@Override
	public Vector2f getVelocity() {
		return new Vector2f(getBody().getPosition().x, getBody().getPosition().y);
	}
	
	
	@Override
	public boolean tryWorldSwitch(int id) {
		// TODO Auto-generated method stub
		return false;
	}
	
	public void setCollisionWithOneWayPlatform(boolean collision) {
		collisionWithOneWayPlatform = collision;
	}
	
//	public boolean beginContact(Contact contact, Box2dPhysicsBody other, Fixture fixture) {
//		if( activeContacts.add(contact) ) {
//			if (contactListener != null)
//				contactListener.beginContact(other);
//			
//			Object fixtureUD = fixture.getUserData();
//			boolean isRightHand = fixtureUD != null && ((String)fixtureUD).compareTo("rightHand") == 0 ? true : false;
//			boolean isLeftHand = fixtureUD != null && ((String)fixtureUD).compareTo("leftHand") == 0 ? true : false;
//			boolean isFoot = fixtureUD != null && ((String)fixtureUD).compareTo("foot") == 0 ? true : false;
//
//			if( isFoot && other.getCollisionHandlerType() != CollisionHandlerType.NO_GRAV )
//				numFootContacts++;
//			else
//				if( isRightHand && other.getCollisionHandlerType() == CollisionHandlerType.SOLID )
//					touchingBodiesRight.add(other);
//				else
//					if( isLeftHand && other.getCollisionHandlerType() == CollisionHandlerType.SOLID )
//						touchingBodiesLeft.add(other);
//					else {
//						if (other.getCollisionHandlerType() == CollisionHandlerType.NO_GRAV && !isRightHand && !isLeftHand)
//							collisionWithLadder = true;
//						else {
//							climbing = false;
//							body.setGravityScale(1.f);
//						}
//					}
//					
//			return true;
//		}
//		
//		return false;
//	}
//
//	public boolean endContact(Contact contact, Box2dPhysicsBody other, Fixture fixture) {
//		if( activeContacts.remove(contact) ) {
//			if (contactListener != null)
//				contactListener.endContact(other);
//	
//			Object fixtureUD = fixture.getUserData();
//			boolean isRightHand = fixtureUD != null && ((String)fixtureUD).compareTo("rightHand") == 0 ? true : false;
//			boolean isLeftHand = fixtureUD != null && ((String)fixtureUD).compareTo("leftHand") == 0 ? true : false;
//			boolean isFoot = fixtureUD != null && ((String)fixtureUD).compareTo("foot") == 0 ? true : false;
//
//				if( isFoot && other.getCollisionHandlerType() != CollisionHandlerType.NO_GRAV )
//					numFootContacts--;
//				else
//					if( isRightHand )
//						touchingBodiesRight.remove(other);
//					else
//						if( isLeftHand )
//							touchingBodiesLeft.remove(other);
//						else {
//							if ( other.getCollisionHandlerType() == CollisionHandlerType.NO_GRAV && !isRightHand && !isLeftHand ) {
//								collisionWithLadder = false;
//								body.setGravityScale(1.f);
//								climbing = false;
//							}
//						}
//			
//			return true;
//		}
//		
//		return false;
//	}
	

}
