package de.secondsystem.game01.impl.game.entities;

import java.util.UUID;

import org.jsfml.graphics.RenderTarget;
import org.jsfml.system.Vector2f;

import de.secondsystem.game01.impl.game.entities.events.EntityEventHandler;
import de.secondsystem.game01.impl.game.entities.events.EntityEventHandler.EntityEventType;
import de.secondsystem.game01.impl.map.IGameMap;
import de.secondsystem.game01.impl.map.physics.ContactListener;
import de.secondsystem.game01.impl.map.physics.IDynamicPhysicsBody;
import de.secondsystem.game01.impl.map.physics.IPhysicsBody;
import de.secondsystem.game01.model.Attributes;
import de.secondsystem.game01.model.IDrawable;
import de.secondsystem.game01.model.IMoveable;
import de.secondsystem.game01.model.IUpdateable;

class GameEntity implements IGameEntity, ContactListener {

	private final UUID uuid;
	
	protected final GameEntityManager em;
	
	protected int gameWorldId;
	
	protected IDynamicPhysicsBody physicsBody;
	
	protected IDrawable representation;
	
	protected EntityEventHandler eventHandler;
	
	protected final IGameMap map;
	
	public GameEntity(UUID uuid,
			GameEntityManager em, IGameMap map, EntityEventHandler eventHandler,
			Attributes attributes) {
		this(uuid, em, attributes.getInteger("worldId", map.getActiveWorldId()), 
				GameEntityHelper.createRepresentation(attributes), GameEntityHelper.createPhysicsBody(map, true, true, true, attributes), map, eventHandler);
	}
	
	public GameEntity(UUID uuid, GameEntityManager em, int gameWorldId, IDrawable representation, IDynamicPhysicsBody physicsBody, IGameMap map, EntityEventHandler eventHandler) {
		this.uuid = uuid;
		this.em = em;
		this.gameWorldId = gameWorldId;
		this.representation = representation;
		this.physicsBody = physicsBody;
		this.map = map;
		
		if( physicsBody!=null )
			physicsBody.setOwner(this);
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
	public Vector2f getPosition() {
		return (representation instanceof IMoveable) ? ((IMoveable)representation).getPosition() : null;
	}

	@Override
	public int getWorldId() {
		return gameWorldId;
	}

	@Override
	public void setWorldId(int newWorldId) {
		if( physicsBody==null || physicsBody.tryWorldSwitch(newWorldId) )
			gameWorldId = newWorldId;
			
		else
			System.out.println("WorldSwitch of '"+uuid()+"' cancled: Collision detected by isTestFixtureColliding()");	// TODO: replace debug-logging with visual feedback
	}

	@Override
	public void beginContact(IPhysicsBody other) {
		if( eventHandler!=null && !other.isStatic() && eventHandler.isHandled(EntityEventType.TOUCHED) ) {
			eventHandler.handle(EntityEventType.TOUCHED, this, other);
		}
	}

	@Override
	public void endContact(IPhysicsBody other) {
		if( eventHandler!=null && !other.isStatic() && eventHandler.isHandled(EntityEventType.UNTOUCHED) ) {
			eventHandler.handle(EntityEventType.UNTOUCHED, this, other);
		}
	}

}
