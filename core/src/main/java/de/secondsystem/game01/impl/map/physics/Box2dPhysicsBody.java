package de.secondsystem.game01.impl.map.physics;

import java.util.HashSet;
import java.util.Set;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Transform;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.contacts.Contact;
import org.jbox2d.dynamics.joints.RevoluteJoint;
import org.jsfml.system.Vector2f;

final class Box2dPhysicsBody implements IPhysicsBody {
	private static final float BOX2D_SCALE_FACTOR = 0.01f;

	final Body body;
	private final CollisionHandlerType type;
	private int gameWorldId;
	private ContactListener contactListener;
	private float maxXVel = Float.MAX_VALUE;
	private float maxYVel = Float.MAX_VALUE;
	int numFootContacts = 0;
	private boolean usingLadder;
	private boolean collisionWithLadder = false;
	private final Set<Contact> activeContacts = new HashSet<>();
	private Box2dPhysicalWorld physicsWorld;
	private RevoluteJoint revoluteJoint = null;
	private Box2dPhysicsBody touchingBody;
	private final boolean liftable;
	
	private final float height;
	private final float width;
	
	// if the testFixture is colliding in the other world then don't allow switching the world
	private int collisionsWithTestFixture;
	
	Box2dPhysicsBody(Box2dPhysicalWorld world, int gameWorldId, float x,
			float y, float width, float height, float rotation,
			boolean isStatic, CollisionHandlerType type, boolean createFoot, boolean createHand, boolean createTestFixture, boolean liftable) {
		this.gameWorldId = gameWorldId;
		this.type = type;
		physicsWorld = world;
		this.liftable = liftable;
		this.height = height;
		this.width  = width;
		
		// body definition
		BodyDef bd = new BodyDef();
		bd.position.set(new Vec2(x, y).mul(BOX2D_SCALE_FACTOR));
		bd.angle = (float) Math.toRadians(rotation);
		bd.type = isStatic ? BodyType.STATIC : BodyType.DYNAMIC;

		// define shape of the body
		PolygonShape s = new PolygonShape();

		// input half extents
		s.setAsBox(width / 2f * BOX2D_SCALE_FACTOR, height / 2f * BOX2D_SCALE_FACTOR);

		// create the body
		body = world.createBody(bd);

		FixtureDef fd = new FixtureDef();
		fd.shape = s;
		if (CollisionHandlerType.NO_GRAV == type)
			fd.isSensor = true;

		if (!isStatic) {
			// fixture definition

			fd.density = 1.f;
			fd.friction = 0.1f;
			fd.restitution = 0.0f;

			// add fixture to body
			body.createFixture(fd);
			
			if ( createTestFixture ) {
				s.setAsBox(width / 2.1f * BOX2D_SCALE_FACTOR, height / 2.1f * BOX2D_SCALE_FACTOR);
				fd.isSensor = true;
				fd.density = 0.f;
				Fixture testFixture = body.createFixture(fd);
				testFixture.setUserData(new String("testFixture"));
			}
				
			if (createFoot) {
				s.setAsBox(width / 3.f * BOX2D_SCALE_FACTOR, 0.1f, new Vec2(0.f, height / 2.f * BOX2D_SCALE_FACTOR), rotation);
				fd.density = 1.f;
				fd.isSensor = true;
				Fixture footFixture = body.createFixture(fd);
				footFixture.setUserData(new String("foot")); 
				body.setFixedRotation(true);
			}
			
			if (createHand) {
				s.setAsBox(width / 4f * BOX2D_SCALE_FACTOR, 0.1f, new Vec2(width / 2f * BOX2D_SCALE_FACTOR, 0.f), rotation);
				fd.isSensor = true;
				fd.density = 0.f;
				Fixture handFixture = body.createFixture(fd);
				handFixture.setUserData(new String("hand"));
			}

		} else
			body.createFixture(fd);

		body.setUserData(this);
	}
	
	public Body getBody() {
		return body;
	}

	public void setGameWorldId(int id) {
		gameWorldId = id;
	}

	public int getGameWorldId() {
		return gameWorldId;
	}
	
	
	public boolean isAbove(Box2dPhysicsBody body) {
		Transform t = body.body.getTransform();
		
		Vec2 pos = body.body.getLocalCenter();
		// top-left and top-right points of the one-way platform
		Vec2 v1 = Transform.mul(t, new Vec2(pos.x - body.width/2.f, pos.y - body.height/2.f).mul(BOX2D_SCALE_FACTOR));
		Vec2 v2 = Transform.mul(t, new Vec2(pos.x + body.width/2.f, pos.y - body.height/2.f).mul(BOX2D_SCALE_FACTOR));

		t = this.body.getTransform();
		pos = this.body.getLocalCenter();
		// bottom-left and bottom-right points of the entity/player
		Vec2 p1 = Transform.mul(t, new Vec2(pos.x - width/2.f, pos.y + height/2.f).mul(BOX2D_SCALE_FACTOR));
		Vec2 p2 = Transform.mul(t, new Vec2(pos.x + width/2.f, pos.y + height/2.f).mul(BOX2D_SCALE_FACTOR));
		
		if( p1.x < v1.x && v1.y < v2.y)
			return p1.y <= v1.y && p1.y <= v2.y;
		else
			if( p2.x > v2.x && v2.y < v1.y)
				return p1.y <= v1.y && p1.y <= v2.y;
			else { 
				// the cross product of 2 vectors tells us whether the second vector is on the left(cp<0), right(cp>0) side of the first vector or above(cp=0)
				// we construct 2 vectors using 3 points(v1,v2,p1 and v1,v2,p2) and check the sign of the cross product
				return ((v2.x - v1.x)*(p1.y - v1.y) - (v2.y - v1.y)*(p1.x - v1.x)) <= 0 && ((v2.x - v1.x)*(p2.y - v1.y) - (v2.y - v1.y)*(p2.x - v1.x)) <= 0;
			}
	}
	
	@Override
	public void setContactListener(ContactListener contactListener) {
		this.contactListener = contactListener;
	}

	@Override
	public Vector2f getPosition() {
		return Vector2f.div(
				new Vector2f(body.getPosition().x, body.getPosition().y),
				BOX2D_SCALE_FACTOR);
	}

	@Override
	public float getRotation() {
		double a = Math.toDegrees(body.getAngle()) % 360;
		return (float) (a < 0 ? 360 + a : a);
	}

	public boolean beginContact(Contact contact, Box2dPhysicsBody other, Fixture fixture) {
		if( activeContacts.add(contact) ) {
			if (contactListener != null)
				contactListener.beginContact(other);
			
			Object fixtureUD = fixture.getUserData();
			boolean isHand = fixtureUD != null && ((String)fixtureUD).compareTo("hand") == 0 ? true : false;
			boolean isFoot = fixtureUD != null && ((String)fixtureUD).compareTo("foot") == 0 ? true : false;

			if( isFoot && other.getCollisionHandlerType() != CollisionHandlerType.NO_GRAV )
				numFootContacts++;
			else
				if( isHand )
					setTouchingBody(other);
				else {
					if (other.getCollisionHandlerType() == CollisionHandlerType.NO_GRAV && !isHand)
						collisionWithLadder = true;
					else {
						usingLadder = false;
						body.setGravityScale(1.f);
					}
				}
					
			return true;
		}
		
		return false;
	}
	
	public void addCollisionsWithTestFixture(int num) {
		collisionsWithTestFixture += num;
	}

	public boolean endContact(Contact contact, Box2dPhysicsBody other, Fixture fixture) {
		if( activeContacts.remove(contact) ) {
			if (contactListener != null)
				contactListener.endContact(other);
	
			Object fixtureUD = fixture.getUserData();
			boolean isHand = fixtureUD != null && ((String)fixtureUD).compareTo("hand") == 0 ? true : false;
			boolean isFoot = fixtureUD != null && ((String)fixtureUD).compareTo("foot") == 0 ? true : false;

				if( isFoot && other.getCollisionHandlerType() != CollisionHandlerType.NO_GRAV )
					numFootContacts--;
				else
					if( isHand )
						setTouchingBody(null);
					else {
						if ( other.getCollisionHandlerType() == CollisionHandlerType.NO_GRAV && !isHand ) {
							collisionWithLadder = false;
							body.setGravityScale(1.f);
							usingLadder = false;
						}
					}
			
			return true;
		}
		
		return false;
	}

	@Override
	public CollisionHandlerType getCollisionHandlerType() {
		return type;
	}

	@Override
	public boolean isStable() {
		return numFootContacts > 0;
	}

	@Override
	public boolean isAffectedByGravity() {
		return body.getGravityScale() != 0.f;
	}

	@Override
	public byte move(float x, float y) {
		x = limit(body.getLinearVelocity().x, x, maxXVel);
		y = limit(body.getLinearVelocity().y, y, maxYVel);

		body.applyForce(new Vec2(x, y), body.getWorldCenter());

		return (byte) ((x != 0 ? 2 : 0) & (y != 0 ? 1 : 0));
	}

	private static float limit(float current, float mod, float max) {
		return mod < 0 ? Math.max(mod, -max - current) : Math.min(mod, max
				- current);
	}

	@Override
	public void rotate(float angle) {
		body.applyAngularImpulse((float) Math.toRadians(angle));
	}

	@Override
	public void forcePosition(float x, float y) {
		body.setTransform(new Vec2(x, y).mul(BOX2D_SCALE_FACTOR),
				body.getAngle());
	}

	@Override
	public void forceRotation(float angle) {
		body.setTransform(body.getPosition(), (float) Math.toRadians(angle));
	}

	@Override
	public void resetVelocity(boolean x, boolean y, boolean rotation) {
		body.setLinearVelocity(new Vec2(x ? 0 : body.getLinearVelocity().x,
				y ? 0 : body.getLinearVelocity().y));

		if (rotation)
			body.setAngularVelocity(0);
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
		return new Vector2f(body.getPosition().x, body.getPosition().y);
	}

	@Override
	public void useLadder(boolean use) {
		usingLadder = use;
		if (usingLadder) {
			if (collisionWithLadder)
				body.setGravityScale(0.f);
		} else {
			body.setGravityScale(1.f);
			usingLadder = false;
		}
	}

	@Override
	public boolean isUsingLadder() {
		return usingLadder;
	}

	@Override
	public void bind(IPhysicsBody other, Vector2f anchor) {
		assert( physicsWorld != null);
		if( other.isLiftable() )
			revoluteJoint = physicsWorld.createRevoluteJoint(body, ((Box2dPhysicsBody) other).getBody(), new Vec2(anchor.x, anchor.y));
	}

	@Override
	public void unbind() {
		if( revoluteJoint != null )
		{
			physicsWorld.destroyJoint(revoluteJoint);
			revoluteJoint = null;
		}
	}

	public void setTouchingBody(IPhysicsBody body) {
		touchingBody = (Box2dPhysicsBody) body;
	}

	@Override
	public IPhysicsBody getTouchingBody() {
		return touchingBody;
	}

	@Override
	public boolean isBound() {
		return revoluteJoint != null;
	}

	@Override
	public boolean isLiftable() {
		return liftable;
	}

	@Override
	public boolean isTestFixtureColliding() {
		return collisionsWithTestFixture > 0;
	}
	
	
}
