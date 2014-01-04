package de.secondsystem.game01.impl.game.entities;

import java.util.UUID;

import de.secondsystem.game01.impl.game.entities.events.IEventHandler;
import de.secondsystem.game01.impl.game.entities.events.IEventHandlerCollection;
import de.secondsystem.game01.impl.map.ICameraController;
import de.secondsystem.game01.impl.map.IGameMap.WorldId;
import de.secondsystem.game01.impl.map.physics.IPhysicsBody;
import de.secondsystem.game01.model.IDrawable;
import de.secondsystem.game01.model.IMoveable;
import de.secondsystem.game01.model.ISerializable;
import de.secondsystem.game01.model.IUpdateable;

/**
 * All objects in a map (player, enemies, triggers)
 * @author lowkey
 *
 */
public interface IGameEntity extends IUpdateable, IDrawable, ICameraController, IMoveable, ISerializable {

	UUID uuid();
	IGameEntityManager manager();
	
	void  onUsed();
	float onUsedDraged(float force);
	void  onLifted(IGameEntity liftingEntity);
	void  onUnlifted(IGameEntity unliftingEntity);
	void  onViewed();
	void  onUnviewed();
	
	int getWorldMask();
	boolean isInWorld(WorldId worldId);
	boolean setWorld(WorldId worldId);
	boolean setWorldMask(int newWorldMask);
	
	void setDead( boolean dead );
	boolean isDead();

	IDrawable getRepresentation();
	IPhysicsBody getPhysicsBody();
	
	IEditableEntityState getEditableState();
	void setEditableState( IEditableEntityState state );
	
}
