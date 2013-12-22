package de.secondsystem.game01.impl.game.entities.events;

import java.util.Set;
import java.util.UUID;

import org.json.simple.JSONObject;

import de.secondsystem.game01.impl.game.entities.IGameEntity;
import de.secondsystem.game01.impl.map.IGameMap;

public interface IEntityEventHandler {

	public enum EntityEventType {
		/**
		 * The entity has been damaged/killed
		 * ARGS: IGameEntity: owner, IGameEntity other
		 * RETURN: unused
		 */
		DAMAGED,
		
		/**
		 * The entity has been reanimated
		 * ARGS: IGameEntity: owner
		 * RETURN: unused
		 */
		REANIMATED,
		
		/**
		 * The entity is touched by another one.
		 * ARGS: IGameEntity: owner, other:IPhysicsBody, force:Vector2f
		 * RETURN: unused 
		 */
		TOUCHED,
		
		/**
		 * The entity is no longer touched by another one.
		 * ARGS: IGameEntity: owner, other:IGameEntity
		 * RETURN: unused 
		 */
		UNTOUCHED,
		
		/**
		 * The player is trying to use this entity
		 * ARGS: IGameEntity: owner
		 * RETURN: unused 
		 */
		USED,

		/**
		 * The player has drag-used this entity (e.g. a lever)
		 * ARGS: IGameEntity: owner, force:float
		 * RETURN: animationDiff:float 
		 */
		USED_DRAGED,
		
		/**
		 * ARGS: IGameEntity: owner, IGameEntity lifting entity
		 */
		LIFTED,
		/**
		 * ARGS: IGameEntity: owner, IGameEntity lifting entity
		 */
		UNLIFTED,
		
		/**
		 * The entity just appeared on the screen
		 * ARGS: IGameEntity: owner
		 * RETURN: unused 
		 */
		VIEWED,
		
		/**
		 * The entity just left the screen
		 * ARGS: IGameEntity: owner
		 * RETURN: unused 
		 */
		UNVIEWED,
		
		/**
		 * Called on update of the entity.
		 * ARGS: IGameEntity: owner
		 * RETURN: unused
		 */
		UPDATE,
		/**
		 * The entity just jumped.
		 *  ARGS: IGameEntity: owner
		 *  RETURN: unused
		 */
		JUMPED;
	}
	
	Object handle( EntityEventType type, IGameEntity owner, Object... args );
	
	boolean isHandled( EntityEventType type );
	
	Set<EntityEventType> getHandled();
	
	JSONObject serialize();
	
	/**
	 * @return null if this entity event handler does not exist
	 */
	IEntityEventHandler deserialize(JSONObject obj, IGameMap map);
	
	UUID uuid();
}
