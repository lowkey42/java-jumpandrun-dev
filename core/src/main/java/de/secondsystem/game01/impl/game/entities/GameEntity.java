package de.secondsystem.game01.impl.game.entities;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.jsfml.graphics.RenderTarget;
import org.jsfml.system.Vector2f;

import de.secondsystem.game01.impl.game.entities.events.EventHandlerCollection;
import de.secondsystem.game01.impl.game.entities.events.EventType;
import de.secondsystem.game01.impl.map.IGameMap;
import de.secondsystem.game01.impl.map.IGameMap.WorldId;
import de.secondsystem.game01.impl.map.physics.IDynamicPhysicsBody;
import de.secondsystem.game01.impl.map.physics.IPhysicsBody;
import de.secondsystem.game01.impl.map.physics.PhysicsContactListener;
import de.secondsystem.game01.model.Attributes;
import de.secondsystem.game01.model.Attributes.Attribute;
import de.secondsystem.game01.model.IAnimated;
import de.secondsystem.game01.model.IAnimated.AnimationType;
import de.secondsystem.game01.model.IDimensioned;
import de.secondsystem.game01.model.IDrawable;
import de.secondsystem.game01.model.IInsideCheck;
import de.secondsystem.game01.model.IMoveable;
import de.secondsystem.game01.model.IUpdateable;

class GameEntity extends EventHandlerCollection implements IGameEntity, PhysicsContactListener {

	private final UUID uuid;
	
	protected final GameEntityManager em;
	
	protected int worldMask;
	
	protected IDynamicPhysicsBody physicsBody;
	
	protected IDrawable representation;
	
	protected final IGameMap map;
	
	protected IEditableEntityState editableEntityState;
	
	protected Set<IGameEntityEffect> effects = new HashSet<>();
	
	protected boolean dead = false;
	
	public GameEntity(UUID uuid, GameEntityManager em, IGameMap map,
			Attributes attributes) {
		this(uuid, em, attributes.getInteger("worldId", map.getActiveWorldId().id), 
				GameEntityHelper.createRepresentation(map, attributes), 
				GameEntityHelper.createPhysicsBody(map, true, true, true, attributes), map, attributes );
	}
	
	protected GameEntity(UUID uuid, GameEntityManager em, int worldMask, IDrawable representation, 
			IDynamicPhysicsBody physicsBody, IGameMap map, Attributes attributes) {
		super( map, attributes );
		
		this.uuid = uuid;
		this.em = em;
		this.worldMask = worldMask;
		this.representation = representation;
		this.physicsBody = physicsBody;
		this.map = map;
		
		if( physicsBody!=null ) {
			physicsBody.setOwner(this);
			physicsBody.setContactListener(this);
		}
		
		if( physicsBody!=null && representation instanceof IMoveable ) {
			((IMoveable) representation).setPosition( physicsBody.getPosition() );
			((IMoveable) representation).setRotation( physicsBody.getRotation() );
		}
		
		if( representation instanceof IAnimated )
			((IAnimated) representation).play(AnimationType.IDLE, 1.f, true, true, false);
	}
	
	@Override
	public UUID uuid() {
		return uuid;
	}
	@Override
	public IGameEntityManager manager() {
		return em;
	}

	@Override
	public void update(long frameTimeMs) {
		if( representation instanceof IUpdateable )
			((IUpdateable) representation).update(frameTimeMs);
		
		notify(EventType.UPDATE, this);
	}

	@Override
	public void draw(RenderTarget renderTarget) {
		Vector2f position = physicsBody!=null ? physicsBody.getPosition() : (representation instanceof IMoveable ? ((IMoveable) representation).getPosition() : null);
		float rotation = physicsBody!=null ? physicsBody.getRotation() : (representation instanceof IMoveable ? ((IMoveable) representation).getRotation() : null);
		
		if( physicsBody!=null && representation instanceof IMoveable ) {
			((IMoveable) representation).setPosition( physicsBody.getPosition() );
			((IMoveable) representation).setRotation( physicsBody.getRotation() );
		}
		
		representation.draw(renderTarget);
		
		for( IGameEntityEffect effect : effects )
			effect.draw(renderTarget, position, rotation);
	}

	@Override
	public Vector2f getPosition() {
		return (representation instanceof IMoveable) ? ((IMoveable)representation).getPosition() : null;
	}

	@Override
	public void beginContact(IPhysicsBody other) {
		notify(EventType.TOUCHED, this, other);
	}

	@Override
	public void endContact(IPhysicsBody other) {
		notify(EventType.UNTOUCHED, this, other);
	}

	@Override
	public void onUsed() {
		notify(EventType.USED, this);
	}

	@Override
	public float onUsedDraged(float force) {
		Object r =notify(EventType.USED_DRAGED, this, force);
		return r instanceof Float ? (float) r : 0.f;
	}

	@Override
	public void onLifted(IGameEntity liftingEntity) {
		notify(EventType.LIFTED, this, liftingEntity);
	}

	@Override
	public void onUnlifted(IGameEntity unliftingEntity) {
		notify(EventType.UNLIFTED, this, unliftingEntity);
	}

	@Override
	public void onViewed() {
		notify(EventType.VIEWED, this);
	}

	@Override
	public void onUnviewed() {
		notify(EventType.UNVIEWED, this);
	}
	
	@Override
	public int getWorldMask() {
		return worldMask;
	}

	@Override
	public boolean isInWorld(WorldId worldId) {
		return (worldMask&worldId.id) !=0;
	}

	@Override
	public boolean setWorldMask(int newWorldMask) {
		if( isDead() )
			return false;
		
		if( physicsBody==null || physicsBody.tryWorldSwitch(newWorldMask) ) {
			this.worldMask = newWorldMask;
			return true;

		} else {
			System.out.println("WorldSwitch of '"+uuid()+"' cancled: Collision detected by isTestFixtureColliding()");	// TODO: replace debug-logging with visual feedback
			return false;
		}
	}

	@Override
	public WorldId getWorldId() {
		return WorldId.byId(worldMask);
	}

	@Override
	public boolean setWorld(WorldId worldId) {
		return setWorldMask(worldId.id);
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
	public IDrawable getRepresentation() {
		return representation;
	}

	@Override
	public IPhysicsBody getPhysicsBody() {
		return physicsBody;
	}

	@Override
	public IEditableEntityState getEditableState() {
		return editableEntityState;
	}

	@Override
	public void setEditableState(IEditableEntityState state) {
		this.editableEntityState = state;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName()+serialize();
	}

	@Override
	public void setDead(boolean dead) {
		this.dead = dead;
	}

	@Override
	public boolean isDead() {
		return dead;
	}

	@Override
	public boolean inside(Vector2f point) {
		if( representation instanceof IInsideCheck )
			return ((IInsideCheck)representation).inside(point);
		
		return false;
	}

	@Override
	public float getHeight() {
		if( representation instanceof IDimensioned )
			return ((IDimensioned) representation).getHeight();
			
		return 1;
	}

	@Override
	public float getWidth() {
		if( representation instanceof IDimensioned )
			return ((IDimensioned) representation).getWidth();
			
		return 1;
	}

	@Override
	public void addEffect(IGameEntityEffect effect) {
		effects.add(effect);
	}

	@Override
	public void removeEffect(IGameEntityEffect effect) {
		effects.remove(effect);
	}

	@Override
	public Set<IGameEntityEffect> getEffects() {
		return Collections.unmodifiableSet(effects);
	}
	
	@Override
	public Attributes serialize() {
		// TODO: may/should be modified for editor
		return new Attributes( editableEntityState!=null ? editableEntityState.getAttributes() : Collections.emptyMap(), 
				new Attributes(
						new Attribute("uuid", uuid.toString()),
						new Attribute("archetype", editableEntityState!=null ? editableEntityState.getArchetype() : "???")
		) );
	}
}
