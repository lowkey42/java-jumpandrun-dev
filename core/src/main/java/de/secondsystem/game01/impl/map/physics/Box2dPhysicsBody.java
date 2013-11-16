package de.secondsystem.game01.impl.map.physics;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jsfml.system.Vector2f;

final class Box2dPhysicsBody implements IPhysicsBody {
	private static final float BOX2D_SCALE_FACTOR = 0.01f;
	
	final Body body;
	private final CollisionHandlerType type;
	private int gameWorldId;
	private ContactListener contactListener;
	private float maxXVel = Float.MAX_VALUE;
	private float maxYVel = Float.MAX_VALUE;
	
	Box2dPhysicsBody(Box2dPhysicalWorld world, int gameWorldId, float x, float y, float width, float height, float rotation, boolean isStatic, CollisionHandlerType type) {
		this.gameWorldId = gameWorldId;
		this.type = type;
		
		// body definition
		BodyDef bd = new BodyDef();
		bd.position.set( new Vec2(x, y).mul(BOX2D_SCALE_FACTOR) );  
		bd.angle = (float)Math.toRadians(rotation);
		bd.type = isStatic ? BodyType.STATIC : BodyType.DYNAMIC;
		
		// define shape of the body
		PolygonShape s = new PolygonShape();
		
		// input half extents
		s.setAsBox(width/2f*BOX2D_SCALE_FACTOR, height/2f*BOX2D_SCALE_FACTOR);
		
		// create the body
		body = world.createBody(bd);
		
		if( !isStatic )
		{
			// fixture definition
			FixtureDef fd = new FixtureDef();
			fd.shape = s;
			fd.density = 1.0f;
			fd.friction = 0.6f;
			fd.restitution = 0.0f;
			
			//add fixture to body
			body.createFixture(fd);
		}
		else
			body.createFixture(s, 0f);	
		
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

	@Override
	public void setContactListener(ContactListener contactListener) {
		this.contactListener = contactListener;
	}

	@Override
	public Vector2f getPosition() {
		return Vector2f.div( new Vector2f(body.getPosition().x, body.getPosition().y), BOX2D_SCALE_FACTOR);
	}

	@Override
	public float getRotation() {
		double a = Math.toDegrees(body.getAngle()) % 360;
		return (float) (a<0 ? 360+a : a);
	}
	
	public void beginContact(Box2dPhysicsBody other) {
		if( contactListener!=null )
			contactListener.beginContact(other);
	}
	public void endContact(Box2dPhysicsBody other) {
		if( contactListener!=null )
			contactListener.endContact(other);
	}

	@Override
	public CollisionHandlerType getCollisionHandlerType() {
		return type;
	}

	@Override
	public boolean isStable() {
		return nearEqual(body.getLinearVelocity().y ,0.f);	// TODO: find better check
	}

	private static boolean nearEqual(float a, float b) {
		return Math.abs(a - b) < 0.0000001f; 
	}

	@Override
	public byte move(float x, float y) {
		x = limit(body.getLinearVelocity().x, x, maxXVel);
		y = limit(body.getLinearVelocity().y, y, maxYVel);
		
		body.applyForce(new Vec2(x, y), body.getWorldCenter());
		
		return (byte) ((x!=0 ? 2 : 0) & (y!=0 ? 1 : 0));
	}
	
	private static float limit( float current, float mod, float max ) {
		return mod<0 ? Math.max(mod, -max-current) : Math.min(mod, max-current);
	}

	@Override
	public void rotate(float angle) {
		body.applyAngularImpulse((float) Math.toRadians(angle));
	}

	@Override
	public void forcePosition(float x, float y) {
		body.setTransform(new Vec2(x, y).mul(BOX2D_SCALE_FACTOR), body.getAngle() );
	}

	@Override
	public void forceRotation(float angle) {
		body.setTransform( body.getPosition(), (float) Math.toRadians(angle) );
	}

	@Override
	public void resetVelocity(boolean x, boolean y, boolean rotation) {
		body.setLinearVelocity(new Vec2(x ? 0 : body.getLinearVelocity().x, y ? 0 : body.getLinearVelocity().y));
		
		if( rotation )
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
}
