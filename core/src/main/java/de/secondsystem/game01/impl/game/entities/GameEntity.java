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

import de.secondsystem.game01.impl.game.entities.effects.EffectUtils;
import de.secondsystem.game01.impl.game.entities.effects.GEGlowEffect;
import de.secondsystem.game01.impl.game.entities.effects.IGameEntityEffect;
import de.secondsystem.game01.impl.game.entities.events.EventHandlerCollection;
import de.secondsystem.game01.impl.game.entities.events.EventType;
import de.secondsystem.game01.impl.graphic.ISpriteWrapper;
import de.secondsystem.game01.impl.map.IGameMap;
import de.secondsystem.game01.impl.map.IGameMap.WorldId;
import de.secondsystem.game01.impl.map.physics.IDynamicPhysicsBody;
import de.secondsystem.game01.impl.map.physics.IPhysicsBody;
import de.secondsystem.game01.impl.map.physics.PhysicsContactListener;
import de.secondsystem.game01.model.Attributes;
import de.secondsystem.game01.model.Attributes.Attribute;
import de.secondsystem.game01.model.Attributes.AttributeIf;
import de.secondsystem.game01.model.IDimensioned;
import de.secondsystem.game01.model.IInsideCheck;
import de.secondsystem.game01.model.IUpdateable;

class GameEntity extends EventHandlerCollection implements IGameEntity, PhysicsContactListener {

	private final UUID uuid;
	
	private final String group;
	
	protected final GameEntityManager em;
	
	protected final byte orderId;
	
	protected int worldMask;
	
	protected IPhysicsBody physicsBody;
	
	protected ISpriteWrapper representation;
	
	protected final IGameMap map;
	
	protected IEditableEntityState editableEntityState;
	
	protected final Set<IGameEntityEffect> effects = new HashSet<>();
	
	protected final PriorityQueue<TimedGameEntityEffect> effectTimer = new PriorityQueue<>(1);
	
	protected boolean dead = false;
	
	private boolean destroyed = false;
	
	GameEntity(UUID uuid, GameEntityManager em, IGameMap map,
			Attributes attributes) {
		this(uuid, em, attributes.getInteger("worldId", map.getActiveWorldId().id), 
				GameEntityHelper.createRepresentation(map, attributes), 
				GameEntityHelper.createPhysicsBody(map, true, true, true, attributes), map, attributes );
	}
	
	protected GameEntity(UUID uuid, GameEntityManager em, int worldMask, ISpriteWrapper representation, 
			IPhysicsBody physicsBody, IGameMap map, Attributes attributes) {
		super( map, attributes );
		
		assert( physicsBody!=null || representation!=null );
		
		this.uuid = uuid;
		this.group = attributes.getString("group", "").intern();
		this.em = em;
		this.worldMask = worldMask;
		this.representation = representation;
		this.physicsBody = physicsBody;
		this.map = map;
		this.orderId = (byte) attributes.getInteger("orderId", 0);
		
		final boolean flippedHoriz = attributes.getBoolean("flipHoriz", false);
		final boolean flippedVert = attributes.getBoolean("flipVert", false);
		
		if( physicsBody!=null ) {
			physicsBody.setOwner(this);
			physicsBody.setContactListener(this);
		}
		
		if( representation!=null ) {
			representation.setFlipHoriz(flippedHoriz);
			representation.setFlipVert(flippedVert);
			
			if( physicsBody!=null ) {
				representation.setPosition( physicsBody.getPosition() );
				representation.setRotation( physicsBody.getRotation() );
			}
		}
		
		List<Attributes> effectAttrs = attributes.getObjectList("effects");
		if( effectAttrs!=null ) {
			Vector2f position = physicsBody!=null ? physicsBody.getPosition() : representation.getPosition();
			float rotation = physicsBody!=null ? physicsBody.getRotation() : representation.getRotation();
			
			for( Attributes attr : effectAttrs )
				effects.add(EffectUtils.createEffect(map, attr, worldMask, position, rotation, representation));
		}
		
		GameEntityHelper.addStaticEffects(this, map, attributes);
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
	public String group() {
		return group;
	}
	
	@Override
	public boolean isFlippedHoriz() {
		return representation!=null ? representation.isFlippedHoriz() : false;
	}
	@Override
	public boolean isFlippedVert() {
		return representation!=null ? representation.isFlippedVert() : false;
	}
	@Override
	public void setFlipHoriz(boolean flipped) {
		if( representation!=null )
			representation.setFlipHoriz(flipped);
	}
	@Override
	public void setFlipVert(boolean flipped) {
		if( representation!=null )
			representation.setFlipVert(flipped);
	}
	@Override
	public void flipHoriz() {
		setFlipHoriz(!isFlippedHoriz());
	}
	@Override
	public void flipVert() {
		setFlipVert(!isFlippedVert());
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
	public void draw(RenderTarget renderTarget, WorldId worldId) {
		float rotation = getRotation();
		
		if( physicsBody!=null && representation!=null ) {
			representation.setPosition( physicsBody.getPosition() );
			representation.setRotation( physicsBody.getRotation() );
		}
		
		if( representation!=null )
			representation.draw(renderTarget, worldId);
		
		for( IGameEntityEffect effect : effects )
			effect.draw(renderTarget, worldId, getPosition(), rotation, worldMask);
	}

	@Override
	public Vector2f getPosition() {
		return physicsBody!=null ? physicsBody.getPosition() : representation.getPosition();
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
	public void pressed(float pressure) {
		notify(EventType.HIT, this, pressure);
		if( pressure>=2.0f ) {
			notify(EventType.DAMAGED, this, pressure);
		}
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
					if( representation!=null )
						addEffect(new GEGlowEffect(map, representation, new Color(100, 0, 0, 255), new Color(255, 255, 255, 100), 40, 50, 25), 2000);
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
		if( representation!=null ) {
			representation.setPosition(pos);
		}
	}

	@Override
	public void setRotation(float degree) {
		if( physicsBody!=null ) {
			physicsBody.setRotation(degree);
			if( physicsBody instanceof IDynamicPhysicsBody )
				((IDynamicPhysicsBody)physicsBody).resetVelocity(false, false, true);
		}
		if( representation!=null ) {
			representation.setRotation(degree);
		}
	}

	@Override
	public float getRotation() {
		return physicsBody!=null ? physicsBody.getRotation() : representation.getRotation();
	}

	@Override
	public ISpriteWrapper getRepresentation() {
		return representation;
	}

	@Override
	public IPhysicsBody getPhysicsBody() {
		return physicsBody;
	}
	@Override
	public void setPhysicsBody(IPhysicsBody physicsBody) {
		if( this.physicsBody!=null )
			this.physicsBody.onDestroy();
		
		this.physicsBody = physicsBody;
		
		if( physicsBody!=null ) {
			physicsBody.setOwner(this);
			physicsBody.setContactListener(this);
		}
	}
	@Override
	public void setPhysicsBodyFromAttributes(Attributes attributes) {
		setPhysicsBody(GameEntityHelper.createPhysicsBody(map, true, true, true, attributes));
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
		if( representation!=null )
			return ((IInsideCheck)representation).inside(point);
		
		else
			for(IGameEntityEffect e : effects)
				if( e.inside(point) )
					return true;
		
		return false;
	}

	@Override
	public float getHeight() {
		if( representation!=null )
			return ((IDimensioned) representation).getHeight();
			
		return 1;
	}

	@Override
	public float getWidth() {
		if( representation!=null )
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
		if( !destroyed ) {
			clearEventHandlers();
			
			destroyed = true;
			
			for( IGameEntityEffect e : effects )
				e.onDestroy(map);
			
			effects.clear();
			
			if( physicsBody!=null )
				physicsBody.onDestroy();
		}
	}
	
	@Override
	public boolean isDestroyed() {
		return destroyed;
	}
	
	@Override
	public Attributes serialize() {
		assert( editableEntityState!=null );
		return new Attributes( editableEntityState.getAttributes(),
				new Attributes(
						new Attribute("uuid", uuid.toString()),
						new Attribute("archetype", editableEntityState.getArchetype()),
						new AttributeIf(orderId!=0, "orderId", orderId),
						new Attribute("x", getPosition().x),
						new Attribute("y", getPosition().y),
						new Attribute("flipHoriz", isFlippedHoriz()),
						new Attribute("flipVert", isFlippedVert()),
						new Attribute("rotation", getRotation()),
						new Attribute("width", getWidth()),
						new Attribute("height", getHeight()),
						new Attribute("worldId", worldMask)
		) );
	}

	@Override
	public void setDimensions(float width, float height) {
		if( representation!=null )
			representation.setDimensions(width, height);
		else
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
