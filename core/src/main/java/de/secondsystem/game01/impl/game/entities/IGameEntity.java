package de.secondsystem.game01.impl.game.entities;

import java.util.UUID;

import org.json.simple.JSONObject;

import de.secondsystem.game01.impl.game.entities.events.CollectionEntityEventHandler;
import de.secondsystem.game01.impl.game.entities.events.IEntityEventHandler;
import de.secondsystem.game01.impl.map.ICameraController;
import de.secondsystem.game01.impl.map.IGameMap.WorldId;
import de.secondsystem.game01.impl.map.physics.IPhysicsBody;
import de.secondsystem.game01.model.IDrawable;
import de.secondsystem.game01.model.IMoveable;
import de.secondsystem.game01.model.IUpdateable;

/**
 * All objects in a map (player, enemies, triggers)
 * @author lowkey
 *
 */
public interface IGameEntity extends IUpdateable, IDrawable, ICameraController, IMoveable {

	UUID uuid();
	
	void  onUsed();
	float onUsedDraged(float force);
	void  onLifted(IGameEntity liftingEntity);
	void  onUnlifted(IGameEntity unliftingEntity);
	void  onViewed();
	void  onUnviewed();
	
	int getWorldMask();
	boolean isInWorld(WorldId worldId);
	void setWorld(WorldId worldId);
	void setWorldMask(int newWorldMask);

	CollectionEntityEventHandler getEventHandler();
	void setEventHandler(CollectionEntityEventHandler eventHandler);
	
	IDrawable getRepresentation();
	IPhysicsBody getPhysicsBody();
	
	IEditableEntityState getEditableState();
	void setEditableState( IEditableEntityState state );
	
}
