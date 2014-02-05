package de.secondsystem.game01.impl.game.entities;

import java.util.List;
import java.util.UUID;

import org.jsfml.system.Vector2f;

import de.secondsystem.game01.impl.game.controller.ControllerUtils;
import de.secondsystem.game01.impl.game.entities.events.EventType;
import de.secondsystem.game01.impl.map.IGameMap;
import de.secondsystem.game01.impl.map.IGameMap.WorldId;
import de.secondsystem.game01.impl.map.physics.IHumanoidPhysicsBody;
import de.secondsystem.game01.impl.map.physics.IHumanoidPhysicsBody.BodyFilter;
import de.secondsystem.game01.impl.map.physics.IPhysicsBody;
import de.secondsystem.game01.model.Attributes;
import de.secondsystem.game01.model.IAnimated;
import de.secondsystem.game01.model.ISerializable;
import de.secondsystem.game01.model.IAnimated.AnimationType;
import de.secondsystem.game01.model.IUpdateable;

/**
 * e.g. player
 * @author lowkey
 *
 */
class ControllableGameEntity extends GameEntity implements IControllableGameEntity {
	
	private boolean possessable;
	
	private IGameEntityController controller; 
	
	private float moveAcceleration;
	
	private float jumpAcceleration;
	
	protected HDirection hDirection;
	
	protected float hFactor;
	
	protected VDirection vDirection;
	
	protected float vFactor;
	
	protected boolean jump = false;
	private long jumpTimer = 0L;
	
	protected boolean moved;
	
	private boolean vMovementAlwaysAllowed = false;
	
	public ControllableGameEntity(UUID uuid, 
			GameEntityManager em, IGameMap map,
			Attributes attributes) {
		super(uuid, em, attributes.getInteger("worldId", map.getActiveWorldId().id), 
				GameEntityHelper.createRepresentation(map, attributes), GameEntityHelper.createPhysicsBody(map, true, true, true, attributes), map, attributes);
		this.moveAcceleration = attributes.getFloat("moveAcceleration", 10);
		this.jumpAcceleration = attributes.getFloat("jumpAcceleration", 10);
		this.vMovementAlwaysAllowed = attributes.getBoolean("verticalMovementAllowed", false);
		this.possessable = attributes.getBoolean("possessable", false);
		
		final Attributes controllerAttributes = attributes.getObject("controller");
		if( controllerAttributes!=null )
			controller = ControllerUtils.createController(this, map, controllerAttributes);
	}

	@Override
	public void moveHorizontally(HDirection direction, float factor) {
		hDirection = direction;
		hFactor = factor;
	}

	@Override
	public void moveVertically(VDirection direction, float factor) {
		vDirection = direction;
		vFactor = factor;
	}

	@Override
	public void jump() {
		if( jumpTimer < 100L )
			return;
		
		if( physicsBody instanceof IHumanoidPhysicsBody )
			((IHumanoidPhysicsBody) physicsBody).stopClimbing();
		
		jump = true;
		jumpTimer = 0L;
	}
	
	@Override
	public void update(long frameTimeMs) {
		if( controller instanceof IUpdateable )
			((IUpdateable) controller).update(frameTimeMs);
		
		jumpTimer += frameTimeMs;
			
		final float xMove = hDirection==HDirection.LEFT ? -hFactor : hFactor;
		final float yMove = vDirection==VDirection.UP   ? -vFactor : vFactor;
		
		processClimbing(frameTimeMs, xMove, yMove);
			
		processMovement(frameTimeMs, xMove, yMove);
	    
		super.update(frameTimeMs);
		
		jump = false;
		
		hFactor = 0;
		vFactor = 0;
	}
	
	private void processMovement(long frameTimeMs, float xMove, float yMove) {
		final float effectiveYMove = isVerticalMovementAllowed() ? moveAcceleration*yMove : (jump && physicsBody.isStable() ? -jumpAcceleration : 0);
		
		if( jump && effectiveYMove != 0 )
			notify(EventType.JUMPED, this);
		
		physicsBody.move(moveAcceleration*frameTimeMs * xMove, effectiveYMove*frameTimeMs );
	    
	    if( hDirection!=null && hFactor!=0 )
	    	moved = true;
	    else if( moved ) {
	    	physicsBody.resetVelocity(true, false, false);
	    	moved = false;
	    }
	    
	    physicsBody.setIdle(!moved);
	    
	    animateMovement(xMove, yMove);
	}
	
	private void animateMovement(float xMove, float yMove) {
		if( representation instanceof IAnimated )
		{
			IAnimated anim = ((IAnimated) representation);
		
			if( xMove == 1 )		
				anim.play(AnimationType.MOVE_RIGHT, 0.3f, true, false, anim.isFlipped());
			else 
				if( xMove == -1 ) 
					anim.play(AnimationType.MOVE_LEFT, 0.3f, true, false, !anim.isFlipped());
				else {
					anim.play(AnimationType.IDLE, 1.f, true, true, false);
				}
		}
	}
	
	private boolean isVerticalMovementAllowed() {
		if( vMovementAlwaysAllowed )
			return true;

		if( !(physicsBody instanceof IHumanoidPhysicsBody) )
			return false;
		
		return ((IHumanoidPhysicsBody) physicsBody).isClimbing();
	}
	
	private void processClimbing(float frameTimeMs, float xMove, float yMove) {
		if( !(physicsBody instanceof IHumanoidPhysicsBody) )
			return;
		
		if( yMove!=0 )
			((IHumanoidPhysicsBody) physicsBody).tryClimbing();
	}

	@Override
	public boolean liftOrThrowObject(float force) {
		if( !(physicsBody instanceof IHumanoidPhysicsBody) )
			return false;
		
		final IHumanoidPhysicsBody hBody = (IHumanoidPhysicsBody) physicsBody;
		final float xMove = hDirection==HDirection.RIGHT ? 1.f : -1.f;
		final float yMove = vDirection==VDirection.DOWN ? 1.f : -1.f;
		
		if( !hBody.isLiftingSomething() ) {
			IPhysicsBody touchingBody = hBody.getNearestBody(new Vector2f(xMove, yMove), IHumanoidPhysicsBody.BF_LIFTABLE);

			if( touchingBody != null && !touchingBody.isKinematic() ) {
				if( hBody.liftBody(touchingBody) && touchingBody.getOwner() instanceof IGameEntity ) {
					final IGameEntity liftedEntity = (IGameEntity) touchingBody.getOwner();
					liftedEntity.notify(EventType.LIFTED, liftedEntity, this);
					return true;
				}
			}
			
		} else {
			final IPhysicsBody liftedBody = hBody.throwLiftedBody(force, new Vector2f(xMove, yMove));
			
			if( liftedBody!=null && liftedBody.getOwner() instanceof IGameEntity ) {
				final IGameEntity liftedEntity = (IGameEntity) liftedBody.getOwner();
				liftedEntity.notify(EventType.UNLIFTED, liftedEntity, this);
				return true;
			}
		}
		
		return false;
	}
	
	@Override
	public void switchWorlds() {
		setWorld(getWorldId()==WorldId.MAIN ? WorldId.OTHER : WorldId.MAIN);
	}

	@Override
	public void setController(IGameEntityController controller) {
		this.controller = controller;
	}
	
	@Override
	public IGameEntityController getController() {
		return controller;
	}

	@Override
	public void use() {
		if( !(physicsBody instanceof IHumanoidPhysicsBody) )
			return;
		
		final float xMove = hDirection==HDirection.RIGHT ? 1.f : -1.f;
		final float yMove = vDirection==VDirection.DOWN ? 1.f : -1.f;
		
		IPhysicsBody nearestBody = ((IHumanoidPhysicsBody) physicsBody ).getNearestBody( new Vector2f(xMove, yMove), IHumanoidPhysicsBody.BF_INTERACTIVE );
		if( nearestBody != null && nearestBody.getOwner() instanceof IGameEntity ) {
			IGameEntity ge = (IGameEntity) nearestBody.getOwner(); 
			ge.notify(EventType.USED, ge, this);
		}
	}
	
	@Override
	public void attack(float force) {
		if( !(physicsBody instanceof IHumanoidPhysicsBody) )
			return;
		
		final float xMove = hDirection==HDirection.RIGHT ? 1.f : -1.f;
		final float yMove = vDirection==VDirection.DOWN ? 1.f : -1.f;
		
		List<IPhysicsBody> bodies = ((IHumanoidPhysicsBody) physicsBody ).listNearBodies(new Vector2f(xMove, yMove), false, BODY_GO_FILTER);
		for( IPhysicsBody body : bodies ) {
			Object result = notify(EventType.ATTACK, this, body.getOwner(), force);
			if( result instanceof Boolean && ((Boolean) result).booleanValue() )
				break;
		}
	}
	
	private static final BodyFilter BODY_GO_FILTER = new BodyFilter() {
		@Override public boolean accept(IPhysicsBody body) {
			return body.getOwner() instanceof IGameEntity;
		}
	};
	
	@Override
	public Attributes serialize() {
		Attributes attributes = super.serialize();
		
		if( controller instanceof ISerializable )
			attributes.put("controller", ((ISerializable) controller).serialize());
		
		// TODO
		
		return attributes;
	}

	@Override
	public boolean isPossessable() {
		return possessable;
	}

	@Override
	public boolean isLiftingSomething() {
		if( !(physicsBody instanceof IHumanoidPhysicsBody) )
			return false;
		
		return ((IHumanoidPhysicsBody) physicsBody).isLiftingSomething();
	}
}
