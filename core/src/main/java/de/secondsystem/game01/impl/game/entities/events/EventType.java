package de.secondsystem.game01.impl.game.entities.events;

public enum EventType {
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
	 * ARGS: IGameEntity: owner, other:IGameEntity
	 * RETURN: unused 
	 */
	USED,
	
	/**
	 * The entity has been enabled/disabled
	 * e.g. a switch that has been pulled
	 */
	ENABLED,
	DISABLED,

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