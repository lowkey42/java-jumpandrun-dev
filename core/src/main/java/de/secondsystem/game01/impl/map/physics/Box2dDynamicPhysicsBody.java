package de.secondsystem.game01.impl.map.physics;

import org.jbox2d.common.Vec2;
import org.jsfml.system.Vector2f;

class Box2dDynamicPhysicsBody extends Box2dPhysicsBody implements
		IDynamicPhysicsBody {

	private float maxXVel = Float.MAX_VALUE;
	private float maxYVel = Float.MAX_VALUE;

	private boolean collisionWithOneWayPlatform = false;
	
	
	Box2dDynamicPhysicsBody(Box2dPhysicalWorld world, int gameWorldId, float x,
			float y, float width, float height, float rotation,
			boolean isStatic, CollisionHandlerType type, boolean createFoot,
			boolean createHand, boolean createTestFixture) {
		super(world, gameWorldId, x, y, width, height, rotation, isStatic, type,
				createFoot, createHand, createTestFixture);
		// TODO Auto-generated constructor stub
	}
	

	@Override
	public boolean isStable() {
		return numFootContacts > 0 && !collisionWithOneWayPlatform;
	}

	@Override
	public byte move(float x, float y) {
		x = limit(body.getLinearVelocity().x, x, maxXVel);
		y = limit(body.getLinearVelocity().y, y, maxYVel);

		body.applyForce(new Vec2(x, y), body.getWorldCenter());
		//body.applyLinearImpulse(new Vec2(x/15, y/65), body.getWorldCenter());

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
	public boolean tryWorldSwitch(int id) {
		// TODO Auto-generated method stub
		return false;
	}
	
	public void setCollisionWithOneWayPlatform(boolean collision) {
		collisionWithOneWayPlatform = collision;
	}
	
}
