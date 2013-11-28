package de.secondsystem.game01.impl.map.physics;

import java.util.List;


class Box2dHumanoidPhysicsBody extends Box2dDynamicPhysicsBody implements
		IHumanoidPhysicsBody {
	
	// if the testFixture is colliding in the other world then don't allow switching the world
	private int collisionsWithTestFixture;
	
	private float maxThrowVel = Float.MAX_VALUE;

	Box2dHumanoidPhysicsBody(Box2dPhysicalWorld world, int gameWorldId,
			float x, float y, float width, float height, float rotation,
			boolean isStatic, CollisionHandlerType type, boolean createFoot,
			boolean createHand, boolean createTestFixture) {
		super(world, gameWorldId, x, y, width, height, rotation, isStatic, type,
				createFoot, createHand, createTestFixture);
		// TODO Auto-generated constructor stub
	}

	
	public void addCollisionsWithTestFixture(int num) {
		collisionsWithTestFixture += num;
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
	public boolean throwLiftedBody(float strength) {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public List<IPhysicsBody> listInteractiveBodies() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IPhysicsBody getNearestInteractiveBody() {
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

}
