package de.secondsystem.game01.impl.game.entities.events;

import java.util.Set;

import de.secondsystem.game01.impl.game.entities.IGameEntity;

public interface EntityEventHandler {

	public enum EntityEventType {
		/**
		 * The entity is touched by another one.
		 * ARGS: other:IPhysicsBody, force:Vector2f
		 * RETURN: unused 
		 */
		TOUCHED("TOUCHED"),
		
		/**
		 * The entity is no longer touched by another one.
		 * ARGS: other:IGameEntity
		 * RETURN: unused 
		 */
		UNTOUCHED("UNTOUCHED"),
		
		/**
		 * The player is trying to use this entity
		 * ARGS: unused
		 * RETURN: unused 
		 */
		USED("USED"),

		/**
		 * The player has drag-used this entity (e.g. a lever)
		 * ARGS: force:float
		 * RETURN: animationDiff:float 
		 */
		USED_DRAGED("USED_DRAGED"),
		
		/**
		 * ARGS: IGameEntity lifting entity
		 */
		LIFTED("LIFTED"),
		/**
		 * ARGS: IGameEntity lifting entity
		 */
		UNLIFTED("UNLIFTED"),
		
		/**
		 * The entity just appeared on the screen
		 * ARGS: unused
		 * RETURN: unused 
		 */
		VIEWED("VIEWED"),
		
		/**
		 * The entity just left the screen
		 * ARGS: unused
		 * RETURN: unused 
		 */
		UNVIEWED("UNVIEWED"),
		
		/**
		 * The internal timer of the entity just ticked.
		 * ARGS: timer:Timer
		 * RETURN: unused 
		 */
		TIMER_TICK("TIMER_TICK");
		
		public final String id;
		
		private EntityEventType(String id) {
				this.id = id;
			}
		
		public static EntityEventType getById(String id) {
				for( EntityEventType type : values() )
					if( type.id.equals(id) )
						return type;
				
				return null;
			}
		}
	
	Object handle( EntityEventType type, IGameEntity owner, Object... args );
	
	boolean isHandled( EntityEventType type );
	
	Set<EntityEventType> getHandled();
}
