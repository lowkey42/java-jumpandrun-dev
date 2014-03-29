package de.secondsystem.game01.impl.game.entities;

import java.util.List;
import java.util.UUID;

import org.jsfml.graphics.RenderTarget;
import org.jsfml.system.Vector2f;

import de.secondsystem.game01.impl.game.controller.ControllerUtils;
import de.secondsystem.game01.impl.game.entities.events.EventType;
import de.secondsystem.game01.impl.map.IGameMap;
import de.secondsystem.game01.impl.map.IGameMap.WorldId;
import de.secondsystem.game01.impl.map.physics.IDynamicPhysicsBody;
import de.secondsystem.game01.impl.map.physics.IHumanoidPhysicsBody;
import de.secondsystem.game01.impl.map.physics.IHumanoidPhysicsBody.BodyFilter;
import de.secondsystem.game01.impl.map.physics.IPhysicsBody;
import de.secondsystem.game01.model.Attributes;
import de.secondsystem.game01.model.GameException;
import de.secondsystem.game01.model.IAnimated;
import de.secondsystem.game01.model.IDrawable;
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
	
	protected HDirection hDirection = HDirection.RIGHT;
	
	protected float hFactor;
	
	protected VDirection vDirection= VDirection.FORWARD;
	
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
		
		if( !(physicsBody instanceof IDynamicPhysicsBody) )
			throw new GameException("A ControllableGameEntity (uuid="+uuid+") doesn' make sense with a static PhysicsBody!");
	}

	private IDynamicPhysicsBody dynPhysicsBody() {
		return (IDynamicPhysicsBody) physicsBody;
	}
	
	@Override
	public void draw(RenderTarget renderTarget) {
		super.draw(renderTarget);
		if( controller instanceof IDrawable )
			((IDrawable) controller).draw(renderTarget);
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
		final float yMove;
		switch( vDirection ) {
			case DOWN:
				yMove = vFactor;
				break;
			case UP:
				yMove = -vFactor;
				break;
			case FORWARD:
			default:
				yMove = 0;
				break;
		}
		
		processClimbing(frameTimeMs, xMove, yMove);
			
		processMovement(frameTimeMs, xMove, yMove);
	    
		super.update(frameTimeMs);
		
		jump = false;
		
		hFactor = 0;
		vFactor = 0;
		vDirection = VDirection.FORWARD;
	}
	
	private void processMovement(long frameTimeMs, float xMove, float yMove) {
		final float effectiveYMove = isVerticalMovementAllowed() ? moveAcceleration*yMove : (jump && dynPhysicsBody().isStable() ? -jumpAcceleration : 0);
		
		if( jump && effectiveYMove != 0 )
			notify(EventType.JUMPED, this);
		
		dynPhysicsBody().move(moveAcceleration*frameTimeMs * xMove, effectiveYMove*frameTimeMs );
	    
	    if( hFactor!=0 )
	    	moved = true;
	    else if( moved ) {
	    	dynPhysicsBody().resetVelocity(true, false, false);
	    	moved = false;
	    }
	    
	    physicsBody.setIdle(!moved);
	    
	    animateMovement(xMove, yMove);
	}
	
	private void animateMovement(float xMove, float yMove) {
		if( representation instanceof IAnimated )
		{
			IAnimated anim = ((IAnimated) representation);

			anim.setFlip(hDirection==HDirection.LEFT);
			
			if( xMove!=0 )
				anim.play(AnimationType.MOVE, 0.3f, true);
			else
				anim.play(AnimationType.IDLE, 1.f, true);
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
		
		if( !hBody.isLiftingSomething() ) {
			IPhysicsBody touchingBody = hBody.getNearestBody(createDirectionVector(), IHumanoidPhysicsBody.BF_LIFTABLE);

			if( touchingBody != null && !touchingBody.isKinematic() ) {
				if( hBody.liftBody(touchingBody) && touchingBody.getOwner() instanceof IGameEntity ) {
					final IGameEntity liftedEntity = (IGameEntity) touchingBody.getOwner();
					liftedEntity.notify(EventType.LIFTED, liftedEntity, this);
					return true;
				}
			}
			
		} else {
			final IPhysicsBody liftedBody = hBody.throwLiftedBody(force, createDirectionVector());
			
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
		WorldId newWorldId = getWorldId()==WorldId.MAIN ? WorldId.OTHER : WorldId.MAIN;
		
		if( !setWorld(newWorldId) )
			return;
		
		if( physicsBody instanceof IHumanoidPhysicsBody && ((IHumanoidPhysicsBody) physicsBody).isLiftingSomething() ) {
			IPhysicsBody body = ((IHumanoidPhysicsBody) physicsBody).getLiftedBody();
			if( !(body.getOwner() instanceof IGameEntity) 
					|| !((IGameEntity)body.getOwner()).setWorld(newWorldId) ) {
				((IHumanoidPhysicsBody) physicsBody).throwLiftedBody(3, createDirectionVector());
			}
		}
	}

	@Override
	public void setController(IGameEntityController controller) {
		this.controller = controller;
	}
	
	@Override
	public IGameEntityController getController() {
		return controller;
	}

	protected Vector2f createDirectionVector() {
		return new Vector2f(hDirection==HDirection.RIGHT ? 1.f : -1.f, vDirection==VDirection.DOWN ? 1.f : vDirection==VDirection.UP ? -1.f : 0);
	}
	
	@Override
	public boolean isUsePossible() {
		if( !(physicsBody instanceof IHumanoidPhysicsBody) )
			return false;
		
		IPhysicsBody nearestBody = ((IHumanoidPhysicsBody) physicsBody ).getNearestBody( createDirectionVector(), IHumanoidPhysicsBody.BF_INTERACTIVE );
		return nearestBody != null && nearestBody.getOwner() instanceof IGameEntity;
	}
	
	@Override
	public void use() {
		if( !(physicsBody instanceof IHumanoidPhysicsBody) )
			return;
		
		IPhysicsBody nearestBody = ((IHumanoidPhysicsBody) physicsBody ).getNearestBody( createDirectionVector(), IHumanoidPhysicsBody.BF_INTERACTIVE );
		if( nearestBody != null && nearestBody.getOwner() instanceof IGameEntity ) {
			IGameEntity ge = (IGameEntity) nearestBody.getOwner(); 
			ge.notify(EventType.USED, ge, this);
			
			IPhysicsBody body = ((IHumanoidPhysicsBody) physicsBody).getLiftedBody();
			if( body!=null ) {
				ge.notify(EventType.TOUCHED, ge, body.getOwner(), body);
				((IGameEntity) body.getOwner()).notify(EventType.TOUCHED, body.getOwner(), ge, ge.getPhysicsBody());
			}
		}
	}
	
	@Override
	public void attack(float force) {
		if( !(physicsBody instanceof IHumanoidPhysicsBody) )
			return;
		
		List<IPhysicsBody> bodies = ((IHumanoidPhysicsBody) physicsBody ).listNearBodies(createDirectionVector(), false, BODY_GO_FILTER);
		for( IPhysicsBody body : bodies ) {
			Object result = notify(EventType.ATTACK, this, body.getOwner(), Math.min(1, force*2));
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
