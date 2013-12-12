package de.secondsystem.game01.impl.game.entities.events;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import de.secondsystem.game01.impl.game.entities.events.IEntityEventHandler.EntityEventType;
import de.secondsystem.game01.impl.game.entities.events.impl.ISequencedObject;
import de.secondsystem.game01.impl.scripting.ScriptEnvironment;
import de.secondsystem.game01.model.Attributes;

public class EventManager {
	
	private final Map<UUID, IEntityEventHandler> eventHandlers = new HashMap<>();
	private ScriptEnvironment scriptEnv;
	
	public EventManager() {
	}
	
	public void setScriptEnvironment(ScriptEnvironment env) {
		scriptEnv = env;
	}
	
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
	
	public ScriptEntityEventHandler createScriptEntityEventHandler(EntityEventType eventType, String handlerFuncName) {
		ScriptEntityEventHandler eventHandler = new ScriptEntityEventHandler(UUID.randomUUID(), scriptEnv, eventType, handlerFuncName);
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
	
	public CollectionEntityEventHandler createScriptedEvents(Map<String, Object> events) {
		CollectionEntityEventHandler eventHandler = createCollectionEntityEventHandler();		
		
		for( String eventType : events.keySet() ) {
			ScriptEntityEventHandler event;
			event = createScriptEntityEventHandler(EntityEventType.valueOf(eventType), (String) events.get(eventType));
			eventHandler.addEntityEventHandler(EntityEventType.valueOf(eventType), event);
		}
		
		return eventHandler;
	}
}
