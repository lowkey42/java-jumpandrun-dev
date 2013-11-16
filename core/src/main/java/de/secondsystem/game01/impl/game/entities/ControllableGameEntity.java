package de.secondsystem.game01.impl.game.entities;

import java.util.UUID;

import de.secondsystem.game01.impl.map.IGameMap;
import de.secondsystem.game01.impl.map.physics.IPhysicsBody;
import de.secondsystem.game01.model.IDrawable;

/**
 * e.g. player
 * @author lowkey
 *
 */
class ControllableGameEntity extends GameEntity implements IControllableGameEntity {
	
	private float moveAcceleration;
	
	private float jumpAcceleration;
	
	private HDirection hDirection;
	
	private VDirection vDirection;
	
	private boolean jump = false;
	private long jumpTimer = 0L;
	
	private boolean moved;

	public ControllableGameEntity(UUID uuid, GameEntityManager em, int gameWorldId, IDrawable representation,
			IPhysicsBody physicsBody, float moveAcceleration, float jumpAcceleration, float maxMoveSpeed, float maxJumpSpeed) {
		super(uuid, em, gameWorldId, representation, physicsBody);
		
		this.physicsBody.setMaxVelocityX(maxMoveSpeed);
		this.physicsBody.setMaxVelocityY(maxJumpSpeed);
		
		this.moveAcceleration = moveAcceleration;
		this.jumpAcceleration = jumpAcceleration;
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
		
		jump = true;
		jumpTimer = 0L;
	}
	
	@Override
	public void update(long frameTimeMs) {
		jumpTimer += frameTimeMs;
		
		final float xMove = hDirection==null ? 0 : hDirection==HDirection.LEFT ? -1 : 1;
		final float yMove = vDirection==null ? 0 : vDirection==VDirection.UP   ? -1 : 1;
		
		if( !physicsBody.isAffectedByGravity() )
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

}
