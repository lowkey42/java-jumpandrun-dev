package de.secondsystem.game01.impl.game.entities;

import java.util.UUID;

import org.jsfml.system.Vector2f;

import de.secondsystem.game01.impl.game.entities.events.EntityEventHandler;
import de.secondsystem.game01.impl.map.IGameMap;
import de.secondsystem.game01.impl.map.physics.IPhysicsBody;
import de.secondsystem.game01.model.Attributes;
import de.secondsystem.game01.model.IAnimated;
import de.secondsystem.game01.model.IAnimated.AnimationType;

/**
 * e.g. player
 * @author lowkey
 *
 */
class ControllableGameEntity extends GameEntity implements IControllableGameEntity {
	private final float THROWING_POWER_INC = 2.f;
	
	private float moveAcceleration;
	
	private float jumpAcceleration;
	
	protected HDirection hDirection;
	
	protected VDirection vDirection;
	
	protected boolean jump = false;
	private long jumpTimer = 0L;
	
	protected boolean moved;

	private float throwingPower = 0.f;
	
	private HDirection facingDirection = HDirection.RIGHT;
	
	private boolean liftingEvent = false;
	
	private boolean incThrowingPowerEvent = false;
	
	private boolean lifting;
	
	private boolean pulling;
	
	public ControllableGameEntity(UUID uuid,
			GameEntityManager em, IGameMap map, EntityEventHandler eventHandler,
			Attributes attributes) {
		super(uuid, em, attributes.getInteger("worldId", map.getActiveWorldId()), 
				GameEntityHelper.createRepresentation(attributes), GameEntityHelper.createPhysicsBody(map, true, true, true, attributes), map, eventHandler);

		this.physicsBody.setMaxVelocityX( attributes.getFloat("maxMoveSpeed",Float.MAX_VALUE) );
		this.physicsBody.setMaxVelocityY( attributes.getFloat("maxJumpSpeed",Float.MAX_VALUE) );
		this.physicsBody.setMaxThrowVelocity( attributes.getFloat("maxThrowSpeed",Float.MAX_VALUE) );
		
		this.moveAcceleration = attributes.getFloat("moveAcceleration");
		this.jumpAcceleration = attributes.getFloat("jumpAcceleration");
	}

	@Override
	public void moveHorizontally(HDirection direction) {
		hDirection = direction;
	}

	@Override
	public void moveVertically(VDirection direction) {
		vDirection = direction;
	}

	@Override
	public void jump() {
		if( jumpTimer < 100L )
			return;
		
		physicsBody.climb(false);
		jump = true;
		jumpTimer = 0L;
	}
	
	@Override
	public void update(long frameTimeMs) {
		jumpTimer += frameTimeMs;
		
		final float xMove = hDirection==null ? 0 : hDirection==HDirection.LEFT ? -1 : 1;
		final float yMove = vDirection==null ? 0 : vDirection==VDirection.UP   ? -1 : 1;
			
		processMovement(frameTimeMs, xMove, yMove);
		
		processClimbing(frameTimeMs, xMove, yMove);
		
	    if( liftingEvent )
	    	onLiftingEvent(yMove);
	    
	    if( incThrowingPowerEvent )
	    	onIncThrowingPowerEvent(frameTimeMs);
	    
		super.update(frameTimeMs);
		
		hDirection = null;
		jump = false;
		vDirection = null;
		liftingEvent = false;
		incThrowingPowerEvent = false;
	}
	
	private void processMovement(long frameTimeMs, float xMove, float yMove) {
		facingDirection = xMove == 1 ? HDirection.RIGHT : xMove == -1 ? HDirection.LEFT : facingDirection;
		
		physicsBody.move(moveAcceleration*frameTimeMs * xMove, jump && physicsBody.isStable() ? -jumpAcceleration*frameTimeMs : 0 );
	    
	    if( hDirection!=null )
	    	moved = true;
	    else if( moved ) {
	    	physicsBody.resetVelocity(true, false, false);
	    	moved = false;
	    }
	    
	    animateMovement(xMove, yMove);
	}

	private void onLiftingEvent(float yMove) {
		lifting = false;
		pulling = false;
		if( !physicsBody.isBound() ) {
			IPhysicsBody touchingBody;
			if( facingDirection == HDirection.RIGHT ) {
				touchingBody = physicsBody.getTouchingBodyRight();
			}
			else
				touchingBody = physicsBody.getTouchingBodyLeft();
			
			if( touchingBody != null ) {
				lifting = physicsBody.bind(touchingBody, new Vector2f(physicsBody.getPosition().x, physicsBody.getPosition().y));
				pulling = !lifting;
			}
		}
		else 
		{
			final float xMove = facingDirection==HDirection.RIGHT ? 1.f : -1.f;
			physicsBody.throwBoundBody(xMove*throwingPower, yMove*throwingPower);
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
	
	private void processClimbing(float frameTimeMs, float xMove, float yMove) {
		if( yMove == -1 && !incThrowingPowerEvent ) 
			physicsBody.climb(true);
			
		if( physicsBody.isClimbing() )
		{
			physicsBody.move(0.f, moveAcceleration*frameTimeMs * yMove );
			physicsBody.resetVelocity(false, true, false);
		}
	}
	
	private void onIncThrowingPowerEvent(long frameTimeMs) {
		if( physicsBody.isBound() )
			throwingPower += THROWING_POWER_INC*frameTimeMs/1000.f;
	}
	
	@Override
	public void liftObject() {
		liftingEvent = true;
	}
	
	@Override
	public void switchWorlds() {
		setWorldId(getWorldId()==0 ? 1 : 0);
	}
	
	@Override
	public void incThrowingPower() {
		incThrowingPowerEvent = true;
	}
	

}
