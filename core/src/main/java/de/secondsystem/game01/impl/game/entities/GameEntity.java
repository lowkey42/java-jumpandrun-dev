package de.secondsystem.game01.impl.game.entities;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.UUID;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.RenderTarget;
import org.jsfml.system.Vector2f;

import com.google.common.collect.Collections2;

import de.secondsystem.game01.impl.game.entities.effects.EffectUtils;
import de.secondsystem.game01.impl.game.entities.effects.GEParticleEffect;
import de.secondsystem.game01.impl.game.entities.effects.IGameEntityEffect;
import de.secondsystem.game01.impl.game.entities.events.EventHandlerCollection;
import de.secondsystem.game01.impl.game.entities.events.EventType;
import de.secondsystem.game01.impl.map.IGameMap;
import de.secondsystem.game01.impl.map.IGameMap.WorldId;
import de.secondsystem.game01.impl.map.physics.IDynamicPhysicsBody;
import de.secondsystem.game01.impl.map.physics.IPhysicsBody;
import de.secondsystem.game01.impl.map.physics.PhysicsContactListener;
import de.secondsystem.game01.model.Attributes;
import de.secondsystem.game01.model.Attributes.Attribute;
import de.secondsystem.game01.model.Attributes.AttributeIf;
import de.secondsystem.game01.model.IAnimated;
import de.secondsystem.game01.model.IAnimated.AnimationType;
import de.secondsystem.game01.model.IDimensioned;
import de.secondsystem.game01.model.IDrawable;
import de.secondsystem.game01.model.IInsideCheck;
import de.secondsystem.game01.model.IMoveable;
import de.secondsystem.game01.model.IScalable;
import de.secondsystem.game01.model.IUpdateable;

class GameEntity extends EventHandlerCollection implements IGameEntity, PhysicsContactListener {

	private final UUID uuid;
	
	protected final GameEntityManager em;
	
	protected final byte orderId;
	
	protected int worldMask;
	
	protected IPhysicsBody physicsBody;
	
	protected IDrawable representation;
	
	protected final IGameMap map;
	
	protected IEditableEntityState editableEntityState;
	
	protected final Set<IGameEntityEffect> effects = new HashSet<>();
	
	protected final PriorityQueue<TimedGameEntityEffect> effectTimer = new PriorityQueue<>(1);
	
	protected boolean dead = false;
	
	private boolean destroyed = false;
	
	public GameEntity(UUID uuid, GameEntityManager em, IGameMap map,
			Attributes attributes) {
		this(uuid, em, attributes.getInteger("worldId", map.getActiveWorldId().id), 
				GameEntityHelper.createRepresentation(map, attributes), 
				GameEntityHelper.createPhysicsBody(map, true, true, true, attributes), map, attributes );
	}
	
	protected GameEntity(UUID uuid, GameEntityManager em, int worldMask, IDrawable representation, 
			IPhysicsBody physicsBody, IGameMap map, Attributes attributes) {
		super( map, attributes );
		
		this.uuid = uuid;
		this.em = em;
		this.worldMask = worldMask;
		this.representation = representation;
		this.physicsBody = physicsBody;
		this.map = map;
		this.orderId = (byte) attributes.getInteger("orderId", 0);
		
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
		
		List<Attributes> effectAttrs = attributes.getObjectList("effects");
		if( effectAttrs!=null ) {
			Vector2f position = physicsBody!=null ? physicsBody.getPosition() : (representation instanceof IMoveable ? ((IMoveable) representation).getPosition() : null);
			float rotation = physicsBody!=null ? physicsBody.getRotation() : (representation instanceof IMoveable ? ((IMoveable) representation).getRotation() : null);
			
			for( Attributes attr : effectAttrs )
				effects.add(EffectUtils.createEventHandler(map, attr, worldMask, position, rotation));
		}
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
		
		while( !effectTimer.isEmpty() && effectTimer.peek().destroyTime<System.currentTimeMillis() )
			removeEffect(effectTimer.poll().effect);
		
		for( IGameEntityEffect e : effects )
			e.update(frameTimeMs);
		
		notify(EventType.UPDATE, this, frameTimeMs);
	}

	@Override
	public void draw(RenderTarget renderTarget) {
		Vector2f position = physicsBody!=null ? physicsBody.getPosition() : (representation instanceof IMoveable ? ((IMoveable) representation).getPosition() : null);
		float rotation = physicsBody!=null ? physicsBody.getRotation() : (representation instanceof IMoveable ? ((IMoveable) representation).getRotation() : null);
		
		if( physicsBody!=null && representation instanceof IMoveable ) {
			((IMoveable) representation).setPosition( physicsBody.getPosition() );
			((IMoveable) representation).setRotation( physicsBody.getRotation() );
		}
		
		if( representation!=null )
			representation.draw(renderTarget);
		
		for( IGameEntityEffect effect : effects )
			effect.draw(renderTarget, position, rotation, worldMask);
	}

	@Override
	public Vector2f getPosition() {
		return (representation instanceof IMoveable) ? ((IMoveable)representation).getPosition() : null;
	}

	@Override
	public void beginContact(IPhysicsBody other) {
		notify(EventType.TOUCHED, this, other.getOwner(), other);
	}

	@Override
	public void endContact(IPhysicsBody other) {
		notify(EventType.UNTOUCHED, this, other.getOwner(), other);
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
	public void forceWorld(WorldId worldId) {
		physicsBody.setWorldIdMask(worldId.id);
		
		this.worldMask = worldId.id;
	}
	
	@Override
	public boolean setWorldMask(int newWorldMask) {
		if( isDead() )
			return false;
		
		if( physicsBody!=null ) {
			if( newWorldMask!=0 && worldMask!=0 && physicsBody instanceof IDynamicPhysicsBody ) {
				if( !((IDynamicPhysicsBody)physicsBody).tryWorldSwitch(newWorldMask) ) {
					addEffect(new GEParticleEffect(map, worldMask, getPosition(), getRotation(), "explosion.png", 50, getWidth(), getHeight(), 
							100, 500, -10, 10, -10, 10, -5, 5, 
							new Color(200, 255, 255), Color.WHITE, 10, 40, 0, 0, 0), 2000);
					return false;
				}
				
			} else 
				physicsBody.setWorldIdMask(newWorldMask);
		}
		
		this.worldMask = newWorldMask;
		return true;
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
			if( physicsBody instanceof IDynamicPhysicsBody )
				((IDynamicPhysicsBody)physicsBody).resetVelocity(true, true, false);
		}
		if( representation instanceof IMoveable ) {
			((IMoveable) representation).setPosition(pos);
		}
	}

	@Override
	public void setRotation(float degree) {
		if( physicsBody!=null ) {
			physicsBody.setRotation(degree);
			if( physicsBody instanceof IDynamicPhysicsBody )
				((IDynamicPhysicsBody)physicsBody).resetVelocity(false, false, true);
		}
		if( representation instanceof IMoveable ) {
			((IMoveable) representation).setRotation(degree);
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
	public void addEffect(IGameEntityEffect effect, int ttl) {
		effects.add(effect);
		effectTimer.add(new TimedGameEntityEffect(effect, ttl+System.currentTimeMillis()));
	}

	@Override
	public void removeEffect(IGameEntityEffect effect) {
		if( effects.remove(effect) )
			effect.onDestroy(map);
	}

	@Override
	public Set<IGameEntityEffect> getEffects() {
		return Collections.unmodifiableSet(effects);
	}
	
	@Override
	public void onDestroy() {
		destroyed = true;
		
		for( IGameEntityEffect e : effects )
			e.onDestroy(map);
		
		effects.clear();
		
		// TODO: destroy physics body
	}
	
	@Override
	public boolean isDestroyed() {
		return destroyed;
	}
	
	@Override
	public Attributes serialize() {
		Vector2f position = physicsBody!=null ? physicsBody.getPosition() : (representation instanceof IMoveable ? ((IMoveable) representation).getPosition() : null);
		float rotation = physicsBody!=null ? physicsBody.getRotation() : (representation instanceof IMoveable ? ((IMoveable) representation).getRotation() : null);
		
		// TODO: may/should be modified for editor
		return new Attributes( editableEntityState!=null ? editableEntityState.getAttributes() : Collections.emptyMap(), 
				new Attributes(
						new Attribute("uuid", uuid.toString()),
						new Attribute("archetype", editableEntityState!=null ? editableEntityState.getArchetype() : "???"),
						new AttributeIf(!effects.isEmpty(), "effects", Collections2.transform(effects, EffectUtils.HANDLER_SERIALIZER)),
						new AttributeIf(orderId!=0, "orderId", orderId),
						new Attribute("x", position.x),
						new Attribute("y", position.y),
						new Attribute("rotation", rotation),
						new Attribute("worldId", worldMask)
		) );
	}

	@Override
	public void setDimensions(float width, float height) {
		if( representation instanceof IScalable )
			((IScalable) representation).setDimensions(width, height);
		else
			if( representation != null )
				System.out.println("Entity" + "(" + toString() + ")" + " can not be scaled." 
									+ " Representation " + representation.getClass().getSimpleName() + " not scalable.");
		
	}

	@Override
	public byte orderId() {
		return orderId;
	}
}

class TimedGameEntityEffect implements Comparable<TimedGameEntityEffect> {
	public final IGameEntityEffect effect;
	public final long destroyTime;
	public TimedGameEntityEffect(IGameEntityEffect effect, long destroyTime) {
		this.effect = effect;
		this.destroyTime = destroyTime;
	}
	@Override
	public int compareTo(TimedGameEntityEffect o) {
		return (destroyTime < o.destroyTime) ? -1 : ((destroyTime == o.destroyTime) ? 0 : 1);
	}
}
