package de.secondsystem.game01.impl.game.entities;

import java.util.Set;
import java.util.UUID;

import de.secondsystem.game01.impl.game.entities.effects.IGameEntityEffect;
import de.secondsystem.game01.impl.game.entities.events.IEventHandlerCollection;
import de.secondsystem.game01.impl.map.ICameraController;
import de.secondsystem.game01.impl.map.IGameMap.WorldId;
import de.secondsystem.game01.impl.map.IWorldDrawable;
import de.secondsystem.game01.impl.map.physics.IPhysicsBody;
import de.secondsystem.game01.model.Attributes;
import de.secondsystem.game01.model.IDimensioned;
import de.secondsystem.game01.model.IInsideCheck;
import de.secondsystem.game01.model.IMoveable;
import de.secondsystem.game01.model.IScalable;
import de.secondsystem.game01.model.ISerializable;
import de.secondsystem.game01.model.IUpdateable;

/**
 * All objects in a map (player, enemies, triggers)
 * @author lowkey
 *
 */
public interface IGameEntity extends IUpdateable, IWorldDrawable, ICameraController, IMoveable, 
									IInsideCheck, IDimensioned, ISerializable, IEventHandlerCollection, IScalable {

	UUID uuid();
	IGameEntityManager manager();
	byte orderId();
	String group();
	
	int getWorldMask();
	boolean isInWorld(WorldId worldId);
	boolean setWorld(WorldId worldId);
	void forceWorld(WorldId worldId);
	boolean setWorldMask(int newWorldMask);
	
	void setDead( boolean dead );
	boolean isDead();

	IWorldDrawable getRepresentation();
	IPhysicsBody getPhysicsBody();
	void setPhysicsBody(IPhysicsBody physicsBody);
	void setPhysicsBodyFromAttributes(Attributes attributes);
	
	IEditableEntityState getEditableState();
	void setEditableState( IEditableEntityState state );
	
	void addEffect( IGameEntityEffect effect );
	void removeEffect( IGameEntityEffect effect );
	Set<IGameEntityEffect> getEffects();
	void addEffect(IGameEntityEffect effect, int ttl);
	
	void onDestroy();
	boolean isDestroyed();
}
