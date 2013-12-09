package de.secondsystem.game01.impl.map.physics;

import java.util.HashSet;
import java.util.Set;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.contacts.Contact;
import org.jsfml.system.Vector2f;

class Box2dDynamicPhysicsBody extends Box2dPhysicsBody implements
		IDynamicPhysicsBody {
	
	private final boolean worldSwitchAllowed;
	private final boolean complexStableCheck;
	private float maxXVel;
	private float maxYVel;
	
	private final Set<Contact> worldSwitchSensorContacts = new HashSet<>();
	private final Set<Contact> footSensorContacts = new HashSet<>();
	
	Box2dDynamicPhysicsBody(Box2dPhysicalWorld world, int gameWorldId, 
			float width, float height, boolean interactive, boolean liftable, CollisionHandlerType type, boolean kinematic,
			boolean stableCheck, boolean worldSwitchAllowed, float maxXVel, float maxYVel) {
		super(world, gameWorldId, width, height, interactive, liftable, type, kinematic);

		this.worldSwitchAllowed = worldSwitchAllowed;
		this.complexStableCheck = stableCheck;
		this.maxXVel = maxXVel;
		this.maxYVel = maxYVel;
	}

	@Override
	public boolean isStatic() {
		return false;
	}
	
	@Override
	protected void createFixtures(Body body, PhysicsBodyShape shape, float friction, float restitution, float density, Float fixedWeight) {
		super.createFixtures(body, shape, friction, restitution, density, fixedWeight);
		
		if( worldSwitchAllowed ) {
			createWorldSwitchFixture(body, shape, friction, restitution, density, fixedWeight);
		}
		
		if( complexStableCheck ) {
			FixtureDef fd = new FixtureDef();
			fd.shape = createShape(PhysicsBodyShape.BOX, getWidth() / 2f, 1f, 0, getHeight()/2, 0);
			fd.isSensor = true;
			fd.userData = new Box2dContactListener.FixtureData(false, new StableCheckFCL());
			
			body.createFixture(fd);
		}
	}
	protected void createWorldSwitchFixture(Body body, PhysicsBodyShape shape, float friction, float restitution, float density, Float fixedWeight) {
		FixtureDef fd = new FixtureDef();
		fd.shape = createShape(PhysicsBodyShape.BOX, Math.max(1, getWidth()-10), Math.max(1, getHeight()-10) );
		fd.isSensor = true;
		fd.userData = new Box2dContactListener.FixtureData(true, new WorldSwitchCheckFCL());
		
		body.createFixture(fd);
	}
	
	protected final class StableCheckFCL implements Box2dContactListener.FixtureContactListener {
		@Override public void onEndContact(Contact contact, Box2dPhysicsBody other,
				Fixture fixture) {
			footSensorContacts.remove(contact);
		}
		
		@Override public void onBeginContact(Contact contact, Box2dPhysicsBody other,
				Fixture fixture) {
			if( other.getCollisionHandlerType()==CollisionHandlerType.SOLID || other.getCollisionHandlerType()==CollisionHandlerType.ONE_WAY ) {
				footSensorContacts.add(contact);
			}
		}
	}
	
	private final class WorldSwitchCheckFCL implements Box2dContactListener.FixtureContactListener {
		@Override public void onEndContact(Contact contact, Box2dPhysicsBody other,
				Fixture fixture) {
			worldSwitchSensorContacts.remove(contact);
		}
		
		@Override public void onBeginContact(Contact contact, Box2dPhysicsBody other,
				Fixture fixture) {
			if( other.getCollisionHandlerType()==CollisionHandlerType.SOLID )
				worldSwitchSensorContacts.add(contact);
		}
	}

	@Override
	public boolean isStable() {
		return !complexStableCheck ? getBody().m_contactList!=null && getBody().m_contactList.contact!=null && getVelocity().y==0 : footSensorContacts.size()>0;
	}

	@Override
	public byte move(float x, float y) {
		x = limit(getBody().getLinearVelocity().x, x, maxXVel);
		y = limit(getBody().getLinearVelocity().y, y, maxYVel);

		//if( getBody().getType()==BodyType.KINEMATIC ) {
			getBody().setLinearVelocity(new Vec2(x, y).add(getBody().getLinearVelocity()));
		//}else
		//getBody().applyForce(new Vec2(x, y), getBody().getWorldCenter());
		//getBody().applyLinearImpulse(new Vec2(x/15, y/65), getBody().getWorldCenter());

		return (byte) ((x != 0 ? 2 : 0) | (y != 0 ? 1 : 0));
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
		if( worldSwitchAllowed && worldSwitchSensorContacts.isEmpty() ) {
			setWorldIdMask(id);
			return true;
		}
		
		return false;
	}

	@Override
	public void setLinearVelocity(float x, float y) {
		getBody().setLinearVelocity(new Vec2(x, y));
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
