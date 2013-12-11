package de.secondsystem.game01.impl.game.entities;

import java.util.UUID;

import org.jsfml.system.Vector2f;
import org.json.simple.JSONObject;

import de.secondsystem.game01.impl.map.IGameMap;
import de.secondsystem.game01.impl.map.IGameMap.WorldId;
import de.secondsystem.game01.impl.map.physics.IHumanoidPhysicsBody;
import de.secondsystem.game01.impl.map.physics.IPhysicsBody;
import de.secondsystem.game01.model.Attributes;
import de.secondsystem.game01.model.IAnimated;
import de.secondsystem.game01.model.IAnimated.AnimationType;
import de.secondsystem.game01.model.IUpdateable;

/**
 * e.g. player
 * @author lowkey
 *
 */
class ControllableGameEntity extends GameEntity implements IControllableGameEntity {
	private final float THROWING_POWER_INC = 2.f;
	
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

	private float throwingPower = 0.f;
	
	private HDirection facingDirectionH = HDirection.RIGHT;
	
	private boolean liftingEvent = false;
	
	private boolean incThrowingPowerEvent = false;
	
	private boolean lifting;
	
	private boolean pulling;
	
	private boolean vMovementAlwaysAllowed = false;
	
	private boolean useEvent = false;
	
	public ControllableGameEntity(UUID uuid, 
			GameEntityManager em, IGameMap map,
			Attributes attributes) {
		super(uuid, em, attributes.getInteger("worldId", map.getActiveWorldId().id), 
				GameEntityHelper.createRepresentation(attributes), GameEntityHelper.createPhysicsBody(map, true, true, true, attributes), map);
		this.moveAcceleration = attributes.getFloat("moveAcceleration", 10);
		this.jumpAcceleration = attributes.getFloat("jumpAcceleration", 10);
		this.vMovementAlwaysAllowed = attributes.getBoolean("verticalMovementAllowed", false);
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
			
		final float xMove = hDirection==null || incThrowingPowerEvent ? 0 : hDirection==HDirection.LEFT ? -hFactor : hFactor;
		final float yMove = vDirection==null || incThrowingPowerEvent ? 0 : vDirection==VDirection.UP   ? -vFactor : vFactor;
		
		processClimbing(frameTimeMs, xMove, yMove);
			
		processMovement(frameTimeMs, xMove, yMove);
		
	    if( liftingEvent )
	    	onLiftingEvent();
	    
	    if( incThrowingPowerEvent )
	    	onIncThrowingPowerEvent(frameTimeMs);
	    
	    if( useEvent )
	    	onUseEvent(xMove, yMove);
	    
		super.update(frameTimeMs);
		
		hDirection = null;
		jump = false;
		vDirection = null;
		liftingEvent = false;
		incThrowingPowerEvent = false;
		useEvent = false;
	}
	
	private void processMovement(long frameTimeMs, float xMove, float yMove) {
		facingDirectionH = xMove > 0 ? HDirection.RIGHT : xMove < 0 ? HDirection.LEFT : facingDirectionH;
		
		final float effectiveYMove = isVerticalMovementAllowed() ? moveAcceleration*yMove : (jump && physicsBody.isStable() ? -jumpAcceleration : 0);
		
		physicsBody.move(moveAcceleration*frameTimeMs * xMove, effectiveYMove*frameTimeMs );
	    
	    if( hDirection!=null )
	    	moved = true;
	    else if( moved ) {
	    	physicsBody.resetVelocity(true, false, false);
	    	moved = false;
	    }
	    
	    physicsBody.setIdle(!moved);
	    
	    animateMovement(xMove, yMove);
	}

	private void onLiftingEvent() {
		if( !(physicsBody instanceof IHumanoidPhysicsBody) )
			return;
		
		final IHumanoidPhysicsBody hBody = (IHumanoidPhysicsBody) physicsBody;
		final float xMove = facingDirectionH==HDirection.RIGHT ? 1.f : -1.f;
		final float yMove = vDirection==VDirection.DOWN ? 1.f : -1.f;
		
		
		lifting = false;
		pulling = false;
		if( !hBody.isLiftingSomething() ) {
			IPhysicsBody touchingBody = hBody.getNearestInteractiveBody(new Vector2f(xMove, yMove));

			if( touchingBody != null && !touchingBody.isKinematic() ) {
				lifting = hBody.liftBody(touchingBody);
				pulling = !lifting;
				if( lifting )
					((IGameEntity) touchingBody.getOwner()).onLifted(this);
			}
			
		} else {
			hBody.throwLiftedBody(throwingPower, new Vector2f(xMove, yMove));
			throwingPower = 0.f;
		}
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
	
	private void onIncThrowingPowerEvent(long frameTimeMs) {
		if( physicsBody instanceof IHumanoidPhysicsBody && ((IHumanoidPhysicsBody) physicsBody).isLiftingSomething() )
			throwingPower += THROWING_POWER_INC*frameTimeMs/1000.f;
	}
	
	private void onUseEvent(float xMove, float yMove) {
		if( !(physicsBody instanceof IHumanoidPhysicsBody) )
			return;
		
		IPhysicsBody nearestBody = ( (IHumanoidPhysicsBody) physicsBody ).getNearestInteractiveBody( new Vector2f(xMove, yMove) );
		if( nearestBody != null ) {
			IGameEntity ge = (IGameEntity) nearestBody.getOwner();
			ge.onUsed();
		}
	}
	
	@Override
	public void liftObject() {
		liftingEvent = true;
	}
	
	@Override
	public void switchWorlds() {
		setWorld(getWorldId()==WorldId.MAIN ? WorldId.OTHER : WorldId.MAIN);
	}
	
	@Override
	public void incThrowingPower() {
		incThrowingPowerEvent = true;
	}

	@Override
	public void setController(IGameEntityController controller) {
		this.controller = controller;
	}

	@Override
	public void use() {
		useEvent = true;
	}
}
