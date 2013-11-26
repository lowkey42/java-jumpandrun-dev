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
	private final float THROWING_POWER_INC = 1.f;
	
	private float moveAcceleration;
	
	private float jumpAcceleration;
	
	protected HDirection hDirection;
	
	protected VDirection vDirection;
	
	protected boolean jump = false;
	private long jumpTimer = 0L;
	
	protected boolean moved;

	private float throwingPower = 0.f;
	
	private HDirection facingDirection = HDirection.RIGHT;
	
	private float lastFrameTimeMs = 0.f;
	
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
		lastFrameTimeMs = frameTimeMs;
		
		final float xMove = hDirection==null ? 0 : hDirection==HDirection.LEFT ? -1 : 1;
		final float yMove = vDirection==null ? 0 : vDirection==VDirection.UP   ? -1 : 1;
		
		if( representation instanceof IAnimated )
		{
			IAnimated anim = ((IAnimated) representation);
		
			if( xMove == 1 )
			{		
				anim.play(AnimationType.MOVE_RIGHT, 0.3f, true, false, anim.isFlipped());
				facingDirection = HDirection.RIGHT;
			}
			else 
				if( xMove == -1 ) {
					anim.play(AnimationType.MOVE_LEFT, 0.3f, true, false, !anim.isFlipped());
					facingDirection = HDirection.LEFT;
				}
				else {
					anim.play(AnimationType.IDLE, 1.f, true, true, false);
				}
		}

		
		if( yMove == -1) // if the user pressed w
			physicsBody.climb(true);
		
		if( physicsBody.isClimbing() )
		{
			physicsBody.move(0.f, moveAcceleration*frameTimeMs * yMove );
			physicsBody.resetVelocity(false, true, false);
		}
		
		physicsBody.move(moveAcceleration*frameTimeMs * xMove, jump && physicsBody.isStable() ? -jumpAcceleration*frameTimeMs : 0 );
	    
	    if( hDirection!=null )
	    	moved = true;
	    else if( moved ) {
	    	physicsBody.resetVelocity(true, false, false);
	    	moved = false;
	    }
		
		super.update(frameTimeMs);
		
		hDirection = null;
		jump = false;
		vDirection = null;
	}

	@Override
	public void liftObject() {
		if( !physicsBody.isBound() ) {
			IPhysicsBody touchingBody;
			if( facingDirection == HDirection.RIGHT ) {
				touchingBody = physicsBody.getTouchingBodyRight();
			}
			else
				touchingBody = physicsBody.getTouchingBodyLeft();
			
			if( touchingBody != null )
				physicsBody.bind(touchingBody, new Vector2f(physicsBody.getPosition().x, physicsBody.getPosition().y));
		}
		else 
		{
			final float xMove = facingDirection==HDirection.RIGHT ? 1.f : -1.f;
			physicsBody.throwBoundBody(xMove*throwingPower, -1*throwingPower);
			throwingPower = 0.f;
		}
	}
	
	@Override
	public void switchWorlds() {
		setWorldId(getWorldId()==0 ? 1 : 0);
	}
	
	@Override
	public void incThrowingPower() {
		if( physicsBody.isBound() )
			throwingPower += THROWING_POWER_INC*lastFrameTimeMs/1000.f;
	}
	

}
