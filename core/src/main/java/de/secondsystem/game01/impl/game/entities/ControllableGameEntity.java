package de.secondsystem.game01.impl.game.entities;

import java.util.UUID;

import org.jbox2d.common.Vec2;

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
	
	private float moveAcceleration;
	
	private float jumpAcceleration;
	
	protected HDirection hDirection;
	
	protected VDirection vDirection;
	
	protected boolean jump = false;
	private long jumpTimer = 0L;
	
	protected boolean moved;

	private boolean lifting = false;

	public ControllableGameEntity(UUID uuid,
			GameEntityManager em, IGameMap map,
			Attributes attributes) {
		super(uuid, em, attributes.getInteger("worldId", map.getActiveGameWorldId()), 
				GameEntityHelper.createRepresentation(attributes), GameEntityHelper.createPhysicsBody(map, true, true, true, attributes));

		this.physicsBody.setMaxVelocityX( attributes.getFloat("maxMoveSpeed",Float.MAX_VALUE) );
		this.physicsBody.setMaxVelocityY( attributes.getFloat("maxJumpSpeed",Float.MAX_VALUE) );
		
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
		
		physicsBody.useObject(false);
		jump = true;
		jumpTimer = 0L;
	}
	
	@Override
	public void update(long frameTimeMs) {
		jumpTimer += frameTimeMs;
		
		final float xMove = hDirection==null ? 0 : hDirection==HDirection.LEFT ? -1 : 1;
		final float yMove = vDirection==null ? 0 : vDirection==VDirection.UP   ? -1 : 1;
		
		if( representation instanceof IAnimated )
		{
			IAnimated anim = ((IAnimated) representation);
		
			if( xMove == 1 )
			{		
				anim.play(AnimationType.MOVE_RIGHT, 0.3f, true, false, anim.isFlipped());
			}
			else 
				if( xMove == -1 )
					anim.play(AnimationType.MOVE_LEFT, 0.3f, true, false, !anim.isFlipped());
				else
					anim.play(AnimationType.IDLE, 1.f, true, true, false);
		}

		
		if( yMove == -1) // if the user pressed w
			physicsBody.useObject(true);
		
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
	    
		if( lifting)
		{
			IPhysicsBody touchingBody = physicsBody.getTouchingBody();
			if( touchingBody != null && !physicsBody.hasJoint())
				physicsBody.bind(touchingBody, new Vec2(physicsBody.getPosition().x, physicsBody.getPosition().y));
		}
		else
			if( physicsBody.hasJoint() )
				physicsBody.unbind();
		
		super.update(frameTimeMs);
		
		hDirection = null;
		jump = false;
		vDirection = null;
	}

	@Override
	public void liftObject(boolean lift) {
		lifting = lift;
	}
	

}
