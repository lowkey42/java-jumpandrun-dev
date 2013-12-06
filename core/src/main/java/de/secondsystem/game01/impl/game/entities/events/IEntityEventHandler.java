package de.secondsystem.game01.impl.game.entities.events;

import java.util.Set;

import de.secondsystem.game01.impl.game.entities.IGameEntity;

public interface IEntityEventHandler {

	public enum EntityEventType {
		/**
		 * The entity is touched by another one.
		 * ARGS: other:IPhysicsBody, force:Vector2f
		 * RETURN: unused 
		 */
		TOUCHED,
		
		/**
		 * The entity is no longer touched by another one.
		 * ARGS: other:IGameEntity
		 * RETURN: unused 
		 */
		UNTOUCHED,
		
		/**
		 * The player is trying to use this entity
		 * ARGS: unused
		 * RETURN: unused 
		 */
		USED,

		/**
		 * The player has drag-used this entity (e.g. a lever)
		 * ARGS: force:float
		 * RETURN: animationDiff:float 
		 */
		USED_DRAGED,
		
		/**
		 * ARGS: IGameEntity lifting entity
		 */
		LIFTED,
		/**
		 * ARGS: IGameEntity lifting entity
		 */
		UNLIFTED,
		
		/**
		 * The entity just appeared on the screen
		 * ARGS: unused
		 * RETURN: unused 
		 */
		VIEWED,
		
		/**
		 * The entity just left the screen
		 * ARGS: unused
		 * RETURN: unused 
		 */
		UNVIEWED;
	}
	
	Object handle( EntityEventType type, IGameEntity owner, Object... args );
	
	Object handle( EntityEventType type, Object... args);
	
	boolean isHandled( EntityEventType type );
	
	Set<EntityEventType> getHandled();
}
