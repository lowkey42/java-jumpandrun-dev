package de.secondsystem.game01.impl.map.physics;

import java.util.HashMap;
import java.util.Map;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.MassData;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.collision.shapes.ShapeType;
import org.jbox2d.common.Rot;
import org.jbox2d.common.Transform;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.contacts.Contact;
import org.jbox2d.dynamics.contacts.ContactEdge;
import org.jbox2d.dynamics.joints.Joint;
import org.jbox2d.dynamics.joints.RevoluteJoint;
import org.jsfml.system.Vector2f;

import de.secondsystem.game01.impl.map.physics.Box2dContactListener.FixtureContactListener;


class Box2dPhysicsBody implements IPhysicsBody, FixtureContactListener {
	protected static final float BOX2D_SCALE_FACTOR = 0.01f;
	
	protected static final Vec2 toBox2dCS( float x, float y ) {
		return new Vec2(x, y).mul(BOX2D_SCALE_FACTOR);
	}
	protected static final Vector2f fromBox2dCS( float x, float y ) {
		return Vector2f.div(new Vector2f(x, y), BOX2D_SCALE_FACTOR);
	}
	
	protected final boolean kinematic;
	protected boolean idle;
	
	protected final Box2dPhysicalWorld parent;
	protected final CollisionHandlerType type;
	protected final float height, width;
	protected final boolean interactive, liftable;
	protected Body body;
	
	private int worldIdMask;
	private Object owner;
	
	private PhysicsContactListener contactListener;

	private final Map<IPhysicsBody, Joint> boundBodies = new HashMap<>();
	
	protected Box2dPhysicsBody liftingBody;
	
	Box2dPhysicsBody(Box2dPhysicalWorld world, int worldIdMask, float width, float height, boolean interactive, boolean liftable, 
			CollisionHandlerType type, boolean kinematic ) {
		this.worldIdMask = worldIdMask;
		this.type = type;
		this.parent = world;
		this.height = height;
		this.width  = width;
		this.interactive = interactive;
		this.liftable = liftable;
		this.kinematic = kinematic;
	}
	
	@Override
	public boolean isStatic() {
		return true;
	}
	protected boolean isBodyRotationFixed() {
		return false;
	}

	final void initBody(float x, float y, float rotation, PhysicsBodyShape shape, float friction, float restitution, float density, Float fixedWeight) {
		body = createBody(x, y, rotation);
		createFixtures(body, shape, friction, restitution, density, fixedWeight);
	}
	protected final Body createBody(float x, float y, float rotation) {
		BodyDef bd = new BodyDef();
		bd.position.set(toBox2dCS(x, y));
		bd.angle = (float) Math.toRadians(rotation);
		bd.type = isStatic() ? BodyType.STATIC : kinematic ? BodyType.KINEMATIC : BodyType.DYNAMIC;
		bd.fixedRotation = isBodyRotationFixed();

		Body body = parent.physicsWorld.createBody(bd);
		body.setUserData(this);
		
		return body;
	}
	protected void createFixtures(Body body, PhysicsBodyShape shape, float friction, float restitution, float density, Float fixedWeight) {
		FixtureDef fd = new FixtureDef();
		fd.shape = createShape(shape, width, height);
		if (CollisionHandlerType.CLIMBABLE == type)
			fd.isSensor = true;
		
		fd.friction = friction;
		fd.restitution = restitution;
		
		// calculate the required density to reach the fixed mass 
		if( fixedWeight!=null ) {
			MassData md = new MassData();
			fd.shape.computeMass(md, 1.0f);
			density = fixedWeight/md.mass;
		}
		fd.density = density;
		fd.userData = null;
		
		body.createFixture(fd);
	}
	protected static Shape createShape(PhysicsBodyShape shape, float width, float height) {
		return createShape(shape, width, height, 0, 0, 0);
	}
	protected static Shape createTrapeziumShape(float width, float height, float topDiff, float bottomDiff, float x, float y) {
		PolygonShape trap = new PolygonShape();
		trap.setAsBox(width / 2f * BOX2D_SCALE_FACTOR, height / 2f * BOX2D_SCALE_FACTOR, toBox2dCS(x,y), 0);

		trap.m_vertices[0].set(trap.m_vertices[0].x-topDiff* BOX2D_SCALE_FACTOR, trap.m_vertices[0].y);
		trap.m_vertices[1].set(trap.m_vertices[1].x+topDiff* BOX2D_SCALE_FACTOR, trap.m_vertices[1].y);
		trap.m_vertices[2].set(trap.m_vertices[2].x+bottomDiff* BOX2D_SCALE_FACTOR, trap.m_vertices[2].y);
		trap.m_vertices[3].set(trap.m_vertices[3].x-bottomDiff* BOX2D_SCALE_FACTOR, trap.m_vertices[3].y);
		
		return trap;
	}
	protected static Shape createShape(PhysicsBodyShape shape, float width, float height, float x, float y, float rotation) {
		switch( shape ) {
			case BOX:
				PolygonShape box = new PolygonShape();
				box.setAsBox(width / 2f * BOX2D_SCALE_FACTOR, height / 2f * BOX2D_SCALE_FACTOR, toBox2dCS(x,y), rotation);
				return box;
				
			case CIRCLE:
				assert( Math.abs(height-width)<0.00001 );
				CircleShape circle = new CircleShape();
				circle.setRadius(height * BOX2D_SCALE_FACTOR);
				circle.m_p.x = x*BOX2D_SCALE_FACTOR;
				circle.m_p.y = y*BOX2D_SCALE_FACTOR;
				return circle;
				
			default:
				throw new RuntimeException("Unsupported Shape-Type: "+shape);
		}
	}
	
	public final Body getBody() {
		return body;
	}

	protected final int getWorldIdMask() {
		return worldIdMask;
	}
	protected final void setWorldIdMask(int worldIdMask) {
		this.worldIdMask = worldIdMask;
		
		// wakeup body
		body.setAwake(true);
		
		// find new contacts
		body.setTransform(body.getPosition(), body.getAngle());
		
		// recall contact-listener
		for(ContactEdge contact = body.m_contactList; contact!=null; contact=contact.next ) {
			parent.physicsWorld.getContactManager().m_contactListener.preSolve(contact.contact, null);
			
			if(contact.contact.isTouching())
				if( !contact.contact.isEnabled() ) {
					parent.physicsWorld.getContactManager().m_contactListener.endContact(contact.contact);
				} else {
					parent.physicsWorld.getContactManager().m_contactListener.beginContact(contact.contact);
				}
		}
	}
	public final boolean isInWorld( int worldId ) {
		return (worldIdMask&worldId) != 0;
	}
	public final void addToWorld(int worldId) {
		worldIdMask|= worldId;
	}
		
	@Override
	public final void setContactListener(PhysicsContactListener contactListener) {
		this.contactListener = contactListener;
	}

	@Override
	public final Vector2f getPosition() {
		return fromBox2dCS(body.getPosition().x, body.getPosition().y);
	}

	@Override
	public final float getRotation() {
		double a = Math.toDegrees(body.getAngle()) % 360;
		return (float) (a < 0 ? 360 + a : a);
	}

	@Override
	public void onBeginContact(Contact contact, Box2dPhysicsBody other, Fixture fixture) {
		if( contactListener!=null )
			contactListener.beginContact(other);
	}
	@Override
	public void onEndContact(Contact contact, Box2dPhysicsBody other, Fixture fixture) {
		if( contactListener!=null )
			contactListener.endContact(other);
	}
	public boolean isContactFiltered(Contact contact, Box2dPhysicsBody other, Fixture ownFixture, Fixture otherFixture) {
		switch (type) {
			case ONE_WAY:
				return other.liftingBody==null ? !other.isAbove(this, otherFixture) : true;
				
			case CLIMBABLE:
			case SOLID:
			default:
				return false;
		}
	}
	
	private boolean isPointAbove(Vec2 v1, Vec2 v2, Vec2 checkPoint) {
		return ((v2.x - v1.x)*(checkPoint.y - v1.y) - (v2.y - v1.y)*(checkPoint.x - v1.x)) <= 0;
	}
	
	public Vec2 getBodyCenterCorrection() {
		return new Vec2(0, 0);
	}
	
	public boolean isAbove(Box2dPhysicsBody body, Fixture otherFixture) {
		// compute top-left and top-right points of the one-way platform
		Transform t = body.body.getTransform();
		Vec2 pos = body.getBodyCenterCorrection();
		Vec2 v1 = Transform.mul(t, new Vec2( pos.x-body.width/2.f, pos.y-body.height/2.f).mul(BOX2D_SCALE_FACTOR));
		Vec2 v2 = Transform.mul(t, new Vec2( pos.x+body.width/2.f, pos.y-body.height/2.f).mul(BOX2D_SCALE_FACTOR));
		
		if( otherFixture.getType() == ShapeType.CIRCLE ) {
			CircleShape shape = (CircleShape) otherFixture.m_shape;
			pos = Transform.mul(this.body.getTransform(), shape.m_p);
			
			if( isPointAbove(v1, v2, pos) ) {
				
				float r = shape.m_radius;
			
				// compute the shortest distance between the point and the line(v1 -> v2)
				Vec2 n = new Vec2(v1.x-v2.x, v1.y-v2.y); 
				n.normalize(); // line unit vector
				Vec2 c1 = v1.sub(pos);
				Vec2 c2 = n.mul(Vec2.dot(c1, n));
				
				// special case at corners
//				if(  (this.body.m_linearVelocity.y > .0) && ( (pos.x < v1.x && v1.y <= v2.y) || ( pos.x > v2.x && v2.y <= v1.y ) ) )
//					return c1.sub(c2).length() >= r-0.1f;
					
				return c1.sub(c2).length() >= r-0.01f; // -0.01f because of inaccuracy reasons
			}
		}
	
		// bottom-left and bottom-right points of the entity/player
		t   = this.body.getTransform();
		pos = getBodyCenterCorrection();
		Vec2 p1 = Transform.mul(t, new Vec2( pos.x-width/2.f, pos.y+height/2.f ).mul(BOX2D_SCALE_FACTOR));
		Vec2 p2 = Transform.mul(t, new Vec2( pos.x+width/2.f, pos.y+height/2.f ).mul(BOX2D_SCALE_FACTOR));
		
		if( p1.x < v1.x && v1.y < v2.y)
			return p1.y <= v1.y && p1.y <= v2.y;

		if( p2.x > v2.x && v2.y < v1.y)
			return p1.y <= v1.y && p1.y <= v2.y;
		
		// the cross product of 2 vectors tells us whether the second vector is on the left(cp<0), right(cp>0) side of the first vector or above(cp=0)
		// we construct 2 vectors using 3 points(v1,v2,p1 and v1,v2,p2) and check the sign of the cross product
		return isPointAbove(v1, v2, p1) && isPointAbove(v1, v2, p2);
	}
	
	@Override
	public CollisionHandlerType getCollisionHandlerType() {
		return type;
	}

	@Override
	public void setPosition(Vector2f pos) {
		body.setTransform(toBox2dCS(pos.x, pos.y), body.getAngle());
	}

	@Override
	public void setRotation(float angle) {
		body.setTransform(body.getPosition(), (float) Math.toRadians(angle));
	}

	public RevoluteJoint bind(IPhysicsBody other, Vector2f anchor, Float maxForce) {
		if( !boundBodies.containsKey(other) ) {
			final Box2dPhysicsBody otherBody = (Box2dPhysicsBody) other;
			
			RevoluteJoint joint = parent.createRevoluteJoint(body, otherBody.getBody(), new Vec2(anchor.x*BOX2D_SCALE_FACTOR, anchor.y*BOX2D_SCALE_FACTOR), maxForce);
			
			if( joint==null )
				return null;
			
			boundBodies.put(other, joint);
			otherBody.boundBodies.put(other, joint);
			
			return joint;
		}
		
		return null;
	}
	@Override
	public boolean bind(IPhysicsBody other, Vector2f anchor) {
		return bind(other, anchor, null)!=null;
	}

	@Override
	public void unbind(IPhysicsBody other) {
		Joint joint = boundBodies.get(other);
		if( joint==null )
			joint = ((Box2dPhysicsBody)other).boundBodies.get(this);
		
		if( joint!=null )
			parent.destroyJoint(joint);

		((Box2dPhysicsBody)other).boundBodies.remove(this);
		boundBodies.remove(other);
	}

	@Override
	public boolean isBound() {
		return !boundBodies.isEmpty();
	}
	
	@Override
	public boolean isBound(IPhysicsBody other) {
		return boundBodies.containsKey(other);
	}
	
	@Override
	public Object getOwner() {
		return owner;
	}

	@Override
	public void setOwner(Object owner) {
		this.owner = owner;
	}

	@Override
	public IPhysicsWorld getParent() {
		return parent;
	}

	@Override
	public float getWeight() {
		return body.m_mass;
	}
	@Override
	public float getHeight() {
		return height;
	}
	@Override
	public float getWidth() {
		return width;
	}
	@Override
	public boolean isInteractive() {
		return interactive;
	}
	@Override
	public boolean isLiftable() {
		return liftable;
	}
	@Override
	public void setIdle(boolean idle) {
		this.idle = idle;
	}
	@Override
	public boolean isKinematic() {
		return kinematic;
	}
	
}
