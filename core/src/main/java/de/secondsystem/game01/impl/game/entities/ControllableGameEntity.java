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
		jump = true;
	}
	
	@Override
	public void update(long frameTimeMs) {
		final float xMove = hDirection==null ? 0 : hDirection==HDirection.LEFT ? -1 : 1;
		
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
