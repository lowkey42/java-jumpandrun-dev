package de.secondsystem.game01.impl.game.entities;

import java.util.UUID;

import org.jsfml.graphics.RenderTarget;
import org.jsfml.system.Vector2f;

import de.secondsystem.game01.impl.map.IGameMap;
import de.secondsystem.game01.impl.map.IGameMap.IWorldSwitchListener;
import de.secondsystem.game01.impl.map.physics.IPhysicsBody;
import de.secondsystem.game01.model.Attributes;
import de.secondsystem.game01.model.IDrawable;
import de.secondsystem.game01.model.IMoveable;
import de.secondsystem.game01.model.IUpdateable;

class GameEntity implements IGameEntity, IWorldSwitchListener {

	private final UUID uuid;

	private boolean followWorldSwitch;
	
	protected final GameEntityManager em;
	
	protected int gameWorldId;
	
	protected IPhysicsBody physicsBody;
	
	protected IDrawable representation;
	

	public GameEntity(UUID uuid,
			GameEntityManager em, IGameMap map,
			Attributes attributes) {
		this(uuid, em, attributes.getInteger("worldId", map.getActiveGameWorldId()), GameEntityHelper.createRepresentation(attributes), GameEntityHelper.createPhysicsBody(map, true, attributes));
	}
	
	public GameEntity(UUID uuid, GameEntityManager em, int gameWorldId, IDrawable representation, IPhysicsBody physicsBody) {
		this.uuid = uuid;
		this.em = em;
		this.gameWorldId = gameWorldId;
		this.representation = representation;
		this.physicsBody = physicsBody;
	}
	
	@Override
	public UUID uuid() {
		return uuid;
	}

	@Override
	public void update(long frameTimeMs) {
		if( representation instanceof IUpdateable )
			((IUpdateable) representation).update(frameTimeMs);
	}

	@Override
	public void draw(RenderTarget renderTarget) {
		if( physicsBody!=null && representation instanceof IMoveable ) {
			((IMoveable) representation).setPosition( physicsBody.getPosition() );
			((IMoveable) representation).setRotation( physicsBody.getRotation() );
		}
		
		representation.draw(renderTarget);
	}

	@Override
	public void onWorldSwitch(int newWorldId) {
		physicsBody.setGameWorldId(newWorldId);
		gameWorldId = newWorldId;
	}

	@Override
	public void setFollowWorldSwitch(boolean followWorldSwitch) {
		this.followWorldSwitch = followWorldSwitch;
		
		if( followWorldSwitch )
			em.map.registerWorldSwitchListener(this);
		else
			em.map.deregisterWorldSwitchListener(this);
	}

	@Override
	public boolean getFollowWorldSwitch() {
		return followWorldSwitch;
	}

	@Override
	public Vector2f getPosition() {
		return (representation instanceof IMoveable) ? ((IMoveable)representation).getPosition() : null;
	}

	@Override
	public Vector2f getLastStablePosition() {
		// TODO
		return null;
	}

}
