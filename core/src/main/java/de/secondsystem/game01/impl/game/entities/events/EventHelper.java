package de.secondsystem.game01.impl.game.entities.events;

public class EventHelper {
	
	public static IEntityEventHandler createEntityEventHandler(String className) {
		if( className.compareTo("SequencedEntityEventHandler") == 0 )
			return new SequencedEntityEventHandler();
		
		if( className.compareTo("ScriptEntityEventHandler") == 0 )
			return new ScriptEntityEventHandler();
		
			if( className.compareTo("CollectionEntityEventHandler") == 0 )
				return new CollectionEntityEventHandler();
			
		System.out.println("className " + className + " unknown");
		return null;
	}
	
}
