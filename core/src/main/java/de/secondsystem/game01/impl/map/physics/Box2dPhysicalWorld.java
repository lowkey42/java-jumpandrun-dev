package de.secondsystem.game01.impl.map.physics;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.World;
import org.jsfml.system.Vector2f;

public final class Box2dPhysicalWorld implements IPhysicalWorld {
	
	private static final float FIXED_STEP = 1/60f;
	private static final int maxSteps = 5;
	private static final int velocityIterations = 8;
	private static final int positionIterations = 3;

	private World physicsWorld;
	
	private float fixedTimestepAccumulator = 0;
	
	@Override
	public void init(Vector2f gravity) {
		physicsWorld = new World(new Vec2(gravity.x, gravity.y));
		physicsWorld.setSleepingAllowed(true);
		physicsWorld.setContactListener(new PhysicsContactListener());
	}

	@Override
	public void update(long frameTime) {
		fixedTimestepAccumulator += frameTime/1000.f;
	    int steps = (int) Math.floor(fixedTimestepAccumulator / FIXED_STEP);
	    if(steps > 0)
	    {
	        fixedTimestepAccumulator -= steps * FIXED_STEP;
	    }
	    
	    int stepsClamped = Math.min(steps, maxSteps);
	 
	    for (int i = 0; i < stepsClamped; ++i)
	    	physicsWorld.step(FIXED_STEP, velocityIterations, positionIterations);
	    physicsWorld.clearForces();
	}

	Body createBody(BodyDef def) {
		return physicsWorld.createBody(def);
	}

	@Override
	public IPhysicsBody createBody(int gameWorldIdMask, float x, float y,
			float width, float height, float rotation, boolean isStatic,
			CollisionHandlerType type, boolean createFoot) {
		return new Box2dPhysicsBody(this, gameWorldIdMask, x, y, width, height, rotation, isStatic, type, createFoot);
	}

}
