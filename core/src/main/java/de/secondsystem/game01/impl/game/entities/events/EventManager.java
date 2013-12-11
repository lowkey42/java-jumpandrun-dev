package de.secondsystem.game01.impl.game.entities.events;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import de.secondsystem.game01.impl.game.entities.events.IEntityEventHandler.EntityEventType;
import de.secondsystem.game01.impl.game.entities.events.impl.ISequencedObject;
import de.secondsystem.game01.impl.scripting.ScriptEnvironment;

public class EventManager {
	
	private final Map<UUID, IEntityEventHandler> eventHandlers = new HashMap<>();
	
	public IEntityEventHandler createEntityEventHandler(String className) {
		if( className.compareTo("SequencedEntityEventHandler") == 0 )
			return new SequencedEntityEventHandler();
		
		if( className.compareTo("ScriptEntityEventHandler") == 0 )
			return new ScriptEntityEventHandler();
		
			if( className.compareTo("CollectionEntityEventHandler") == 0 )
				return new CollectionEntityEventHandler();
			
		System.out.println("className " + className + " unknown");
		return null;
	}
	
	public void add(IEntityEventHandler eventHandler) {
		eventHandlers.put(eventHandler.uuid(), eventHandler);
	}
	
	public SequencedEntityEventHandler createSequencedEntityEventHandler(EntityEventType eventType, ISequencedObject sequencedObject) {
		SequencedEntityEventHandler eventHandler = new SequencedEntityEventHandler(UUID.randomUUID(), eventType, sequencedObject);
		add(eventHandler);
		
		return eventHandler;
	}
	
	public ScriptEntityEventHandler createScriptEntityEventHandler(ScriptEnvironment env, EntityEventType eventType, String handlerFuncName) {
		ScriptEntityEventHandler eventHandler = new ScriptEntityEventHandler(UUID.randomUUID(), env, eventType, handlerFuncName);
		add(eventHandler);
		
		return eventHandler;
	}
	
	public CollectionEntityEventHandler createCollectionEntityEventHandler() {
		CollectionEntityEventHandler eventHandler = new CollectionEntityEventHandler(UUID.randomUUID());
		add(eventHandler);
		
		return eventHandler;
	}
	
	public IEntityEventHandler get(UUID uuid) {
		return eventHandlers.get(uuid);
	}
}
