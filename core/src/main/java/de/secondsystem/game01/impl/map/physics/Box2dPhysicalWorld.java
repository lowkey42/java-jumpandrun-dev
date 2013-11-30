package de.secondsystem.game01.impl.map.physics;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.joints.Joint;
import org.jbox2d.dynamics.joints.RevoluteJoint;
import org.jbox2d.dynamics.joints.RevoluteJointDef;
import org.jsfml.system.Vector2f;

import de.secondsystem.game01.impl.map.IGameMap.WorldId;

public final class Box2dPhysicalWorld implements IPhysicsWorld {
	
	private static final float FIXED_STEP = 1/60f/2;
	private static final int maxSteps = 5;
	private static final int velocityIterations = 8;
	private static final int positionIterations = 3;

	World physicsWorld;
	
	private float fixedTimestepAccumulator = 0;
	
	@Override
	public void init(Vector2f gravity) {
		physicsWorld = new World(new Vec2(gravity.x, gravity.y));
		physicsWorld.setSleepingAllowed(true);
		physicsWorld.setContactListener(new Box2dContactListener());
	//	physicsWorld.setAutoClearForces(true);
	}

	@Override
	public void update(long frameTime) {
		float dt = frameTime/1000.f;
		float max = 1.f/60;
		while (dt>=max) {
			physicsWorld.step(max/2, velocityIterations, positionIterations);
			dt -= max/2;
		}
		physicsWorld.step(dt, velocityIterations, positionIterations); //This syncs up the physics engine to the current frame
		physicsWorld.clearForces();

//		physicsWorld.step(frameTime/1000.f, velocityIterations, positionIterations);
//		fixedTimestepAccumulator += frameTime/1000.f;
//	    int steps = (int) Math.floor(fixedTimestepAccumulator / FIXED_STEP);
//	    if(steps > 0)
//	    {
//	        fixedTimestepAccumulator -= steps * FIXED_STEP;
//	    }
//	    
//	    int stepsClamped = Math.min(steps, maxSteps);
//	 
//	    for (int i = 0; i < stepsClamped; ++i) {
//	    	physicsWorld.step(FIXED_STEP, velocityIterations, positionIterations);
//	    	
//	    	if( i>0 && i%2==0 )
//	    	    physicsWorld.clearForces();
//	    }
//	    physicsWorld.clearForces();
	}

	Body createBody(BodyDef def) {
		return physicsWorld.createBody(def);
	}

	public RevoluteJoint createRevoluteJoint(Body body1, Body body2, Vec2 anchor) {	
		RevoluteJointDef jointDef = new RevoluteJointDef();
		jointDef.initialize(body1, body2, anchor);
		
		return (RevoluteJoint) physicsWorld.createJoint(jointDef);
	}

	public void destroyJoint(Joint joint) {
		physicsWorld.destroyJoint(joint);
	}

//	@Override
//	public IPhysicsBody createStaticBody(int gameWorldIdMask, float x, float y,
//			float width, float height, float rotation, CollisionHandlerType type) {
//		return new Box2dPhysicsBody(this, gameWorldIdMask, x, y, width, height, rotation, true, type, false, false, false);
//	}
//
//	@Override
//	public IPhysicsBody createDynamicBody(int gameWorldIdMask, float x,
//			float y, float width, float height, float rotation,
//			CollisionHandlerType type, int features) {
//		return new Box2dPhysicsBody(this, gameWorldIdMask, x, y, width, height, rotation, false, type, 
//				PhysicalBodyFeatures.has(features, PhysicalBodyFeatures.STABLE_CHECK), 
//				PhysicalBodyFeatures.has(features, PhysicalBodyFeatures.SIDE_CONTACT_CHECK),
//				PhysicalBodyFeatures.has(features, PhysicalBodyFeatures.WORLD_SWITCH_CHECK) );
//	}
//
//	@Override
//	public IHumanoidPhysicsBody createHumanoidBody(int worldIdMask, float x,
//			float y, float width, float height, float maxSlope, float maxReach,
//			float rotation, int features) {
//		// TODO Auto-generated method stub
//		return null;
//	}

	@Override
	public PhysicsBodyFactory factory() {
		return new Box2dPhysicsBodyFactory();
	}


	private class Box2dPhysicsBodyFactory implements PhysicsBodyFactory {
		int worldMask;
		float x;
		float y;
		float rotation;
		float width;
		float height;
		float density = 1.f;
		Float fixedWeight;
		float friction = 0.5f;
		float restitution = 0.f;
		CollisionHandlerType type = CollisionHandlerType.SOLID;
		PhysicsBodyShape shape = null;
		
		@Override public PhysicsBodyFactory inWorld(WorldId worldId) {
			worldMask |= worldId.id;
			return this;
		}

		@Override
		public PhysicsBodyFactory worldMask(int worldMask) {
			this.worldMask = worldMask;
			return this;
		}
	
		@Override public PhysicsBodyFactory position(float x, float y) {
			this.x = x;
			this.y = y;
			return this;
		}
	
		@Override public PhysicsBodyFactory rotation(float rotation) {
			this.rotation = rotation;
			return this;
		}
	
		@Override public PhysicsBodyFactory dimension(float width, float height) {
			this.width = width;
			this.height = height;
			return this;
		}

		@Override public PhysicsBodyFactory weight(float weight) {
			fixedWeight = weight;
			return this;
		}

		@Override public PhysicsBodyFactory density(float weightPerPx) {
			density = weightPerPx;
			return this;
		}

		@Override public PhysicsBodyFactory friction(float friction) {
			this.friction = friction;
			return this;
		}

		@Override public PhysicsBodyFactory restitution(float restitution) {
			this.restitution = restitution;
			return this;
		}
	
		@Override public PhysicsBodyFactory type(CollisionHandlerType type) {
			this.type = type;
			return this;
		}
	
		@Override public StaticPhysicsBodyFactory staticBody(PhysicsBodyShape shape) {
			this.shape = shape;
			return new Box2dStaticPhysicsBodyFactory();
		}
	
		@Override public DynamicPhysicsBodyFactory dynamicBody(PhysicsBodyShape shape) {
			this.shape = shape;
			return new Box2dDynamicPhysicsBodyFactory();
		}
	
		@Override public HumanoidPhysicsBodyFactory humanoidBody() {
			return new Box2dHumanoidPhysicsBodyFactory();
		}
	
	
		class Box2dStaticPhysicsBodyFactory implements StaticPhysicsBodyFactory {
			@Override public IPhysicsBody create() {
				Box2dPhysicsBody b = new Box2dPhysicsBody(Box2dPhysicalWorld.this, worldMask, width, height, type);
				b.initBody(x, y, rotation, shape, friction, restitution, density, fixedWeight);
				return b;
			}
		}
		
		class Box2dDynamicPhysicsBodyFactory extends Box2dStaticPhysicsBodyFactory implements DynamicPhysicsBodyFactory {
			boolean stableCheck = false;
			boolean worldSwitchAllowed = false;
			float maxXSpeed = Float.MAX_VALUE;
			float maxYSpeed = Float.MAX_VALUE;
			
			@Override public IDynamicPhysicsBody create() {
				Box2dDynamicPhysicsBody b = new Box2dDynamicPhysicsBody(Box2dPhysicalWorld.this, worldMask, width, height, type, stableCheck, worldSwitchAllowed, maxXSpeed, maxYSpeed);
				b.initBody(x, y, rotation, shape, friction, restitution, density, fixedWeight);
				return b;
			}
	
			@Override public DynamicPhysicsBodyFactory stableCheck(boolean enable) {
				stableCheck = enable;
				return this;
			}
	
			@Override public DynamicPhysicsBodyFactory worldSwitch(boolean allowed) {
				worldSwitchAllowed = allowed;
				return this;
			}
	
			@Override public DynamicPhysicsBodyFactory maxXSpeed(float speed) {
				maxXSpeed = speed;
				return this;
			}
	
			@Override public DynamicPhysicsBodyFactory maxYSpeed(float speed) {
				maxYSpeed = speed;
				return this;
			}
		}
		
		class Box2dHumanoidPhysicsBodyFactory extends Box2dDynamicPhysicsBodyFactory implements HumanoidPhysicsBodyFactory {
			float maxSlope = 45;
			float maxReach = 10;
			float maxThrowSpeed = Float.MAX_VALUE;
			float maxLiftWeight = Float.MAX_VALUE;
			
			@Override public HumanoidPhysicsBodyFactory maxXSpeed(float speed) {
				return (HumanoidPhysicsBodyFactory) super.maxXSpeed(speed);
			}
	
			@Override public HumanoidPhysicsBodyFactory maxYSpeed(float speed) {
				return (HumanoidPhysicsBodyFactory) super.maxYSpeed(speed);
			}
	
			@Override public HumanoidPhysicsBodyFactory maxSlope(float degree) {
				maxSlope = degree;
				return this;
			}
	
			@Override public HumanoidPhysicsBodyFactory maxReach(float px) {
				maxReach = px;
				return this;
			}

			@Override public HumanoidPhysicsBodyFactory maxThrowSpeed(float speed) {
				maxThrowSpeed = speed;
				return this;
			}

			@Override public HumanoidPhysicsBodyFactory maxLiftWeight(float weight) {
				maxLiftWeight = weight;
				return this;
			}
	
			@Override public IHumanoidPhysicsBody create() {
				Box2dHumanoidPhysicsBody b = new Box2dHumanoidPhysicsBody(Box2dPhysicalWorld.this, worldMask, width, height, type, maxXSpeed, maxYSpeed, 
						maxThrowSpeed, maxLiftWeight, maxSlope, maxReach);
				b.initBody(x, y, rotation, null, friction, restitution, density, fixedWeight);
				return b;
			}
		}
	}
	
}