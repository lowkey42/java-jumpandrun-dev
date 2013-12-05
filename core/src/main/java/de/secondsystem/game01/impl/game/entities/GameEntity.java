package de.secondsystem.game01.impl.game.entities;

import java.util.UUID;

import org.jsfml.graphics.RenderTarget;
import org.jsfml.system.Vector2f;

import de.secondsystem.game01.impl.game.entities.events.IEntityEventHandler;
import de.secondsystem.game01.impl.game.entities.events.IEntityEventHandler.EntityEventType;
import de.secondsystem.game01.impl.map.IGameMap;
import de.secondsystem.game01.impl.map.IGameMap.WorldId;
import de.secondsystem.game01.impl.map.physics.IDynamicPhysicsBody;
import de.secondsystem.game01.impl.map.physics.IPhysicsBody;
import de.secondsystem.game01.impl.map.physics.PhysicsContactListener;
import de.secondsystem.game01.model.Attributes;
import de.secondsystem.game01.model.IAnimated;
import de.secondsystem.game01.model.IDrawable;
import de.secondsystem.game01.model.IMoveable;
import de.secondsystem.game01.model.IUpdateable;
import de.secondsystem.game01.model.IAnimated.AnimationType;

class GameEntity implements IGameEntity, PhysicsContactListener {

	private final UUID uuid;
	
	protected final GameEntityManager em;
	
	protected int worldMask;
	
	protected IDynamicPhysicsBody physicsBody;
	
	protected IDrawable representation;
	
	protected IEntityEventHandler eventHandler;
	
	protected final IGameMap map;
	
	protected boolean used = false;
	
	public GameEntity(UUID uuid,
			GameEntityManager em, IGameMap map,
			Attributes attributes) {
		this(uuid, em, attributes.getInteger("worldId", map.getActiveWorldId().id), 
				GameEntityHelper.createRepresentation(attributes), GameEntityHelper.createPhysicsBody(map, true, true, true, attributes), map, 
				GameEntityHelper.createEventHandler(em, attributes) );
	}
	
	public GameEntity(UUID uuid, GameEntityManager em, int worldMask, IDrawable representation, IDynamicPhysicsBody physicsBody, IGameMap map, IEntityEventHandler eventHandler) {
		this.uuid = uuid;
		this.em = em;
		this.worldMask = worldMask;
		this.representation = representation;
		this.physicsBody = physicsBody;
		this.map = map;
		this.eventHandler = eventHandler;
		
		if( physicsBody!=null )
			physicsBody.setOwner(this);
		
		if( physicsBody!=null && representation instanceof IMoveable ) {
			((IMoveable) representation).setPosition( physicsBody.getPosition() );
			((IMoveable) representation).setRotation( physicsBody.getRotation() );
		}
		
		if( representation instanceof IAnimated )
			((IAnimated) representation).play(AnimationType.IDLE, 1.f, false, true, false);
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

	@Override
	public void onUsed() {
		used = !used;
		if( eventHandler!=null && eventHandler.isHandled(EntityEventType.USED) ) 
			eventHandler.handle(EntityEventType.USED, this);
		
		if( representation instanceof IAnimated )
			if( used )
				((IAnimated) representation).play(AnimationType.USED, 1.f, true, true, false);
			else
				((IAnimated) representation).play(AnimationType.IDLE, 1.f, true, true, false);
	}

	@Override
	public float onUsedDraged(float force) {
		if( eventHandler!=null && eventHandler.isHandled(EntityEventType.USED_DRAGED) ) 
			return (float) eventHandler.handle(EntityEventType.USED_DRAGED, this, force);
		return 0.f;
	}

	@Override
	public void onLifted(IGameEntity liftingEntity) {
		if( eventHandler!=null && eventHandler.isHandled(EntityEventType.LIFTED) ) 
			eventHandler.handle(EntityEventType.LIFTED, this, liftingEntity);
	}

	@Override
	public void onUnlifted(IGameEntity unliftingEntity) {
		if( eventHandler!=null && eventHandler.isHandled(EntityEventType.UNLIFTED) ) 
			eventHandler.handle(EntityEventType.UNLIFTED, this, unliftingEntity);
	}

	@Override
	public void onViewed() {
		if( eventHandler!=null && eventHandler.isHandled(EntityEventType.VIEWED) ) 
			eventHandler.handle(EntityEventType.VIEWED, this);
	}

	@Override
	public void onUnviewed() {
		if( eventHandler!=null && eventHandler.isHandled(EntityEventType.UNVIEWED) ) 
			eventHandler.handle(EntityEventType.UNVIEWED, this);
	}
	
	@Override
	public int getWorldMask() {
		return worldMask;
	}

	@Override
	public boolean isInWorld(WorldId worldId) {
		return (worldMask|worldId.id) !=0;
	}

	@Override
	public void setWorldMask(int newWorldMask) {
		if( physicsBody==null || physicsBody.tryWorldSwitch(newWorldMask) )
			this.worldMask = newWorldMask;

		else
			System.out.println("WorldSwitch of '"+uuid()+"' cancled: Collision detected by isTestFixtureColliding()");	// TODO: replace debug-logging with visual feedback
	}

	@Override
	public WorldId getWorldId() {
		return WorldId.byId(worldMask);
	}

	@Override
	public void setWorld(WorldId worldId) {
		setWorldMask(worldId.id);
	}

	@Override
	public void setPosition(Vector2f pos) {
		if( physicsBody!=null ) {
			physicsBody.setPosition(pos);
			physicsBody.resetVelocity(true, true, false);
		}
	}

	@Override
	public void setRotation(float degree) {
		if( physicsBody!=null ) {
			physicsBody.setRotation(degree);
			physicsBody.resetVelocity(false, false, true);
		}
	}

	@Override
	public float getRotation() {
		return physicsBody!=null ? physicsBody.getRotation() : 0;
	}

	@Override
	public boolean isUsed() {
		return used;
	}

	@Override
	public String getArchetype() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Attributes serialize() {
		// TODO Auto-generated method stub
		return null;
	}

}
