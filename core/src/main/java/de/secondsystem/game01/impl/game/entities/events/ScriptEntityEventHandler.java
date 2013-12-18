package de.secondsystem.game01.impl.game.entities.events;

import java.util.UUID;

import org.json.simple.JSONObject;

import de.secondsystem.game01.impl.game.entities.IGameEntity;
import de.secondsystem.game01.impl.map.IGameMap;
import de.secondsystem.game01.impl.scripting.ScriptEnvironment;

public class ScriptEntityEventHandler extends SingleEntityEventHandler {

	private String handlerFuncName;
	
	private ScriptEnvironment env;
	
	public ScriptEntityEventHandler(UUID uuid, ScriptEnvironment env, EntityEventType eventType, String handlerFuncName) {
		super(uuid, eventType);
		this.handlerFuncName = handlerFuncName;
		this.env = env;
	}
	
	public ScriptEntityEventHandler() {	
	}

	@Override
	public Object handle(EntityEventType type, IGameEntity owner,
			Object... args) {
		return env.exec(handlerFuncName, owner, args);
	}

	@SuppressWarnings("unchecked")
	@Override
	public JSONObject serialize() {
		JSONObject obj = super.serialize();
		obj.put("handlerFuncName", handlerFuncName);
		obj.put("class", "ScriptEntityEventHandler");
		
		return obj;
	}

	@Override
	public IEntityEventHandler deserialize(JSONObject obj, IGameMap map) {
		IEntityEventHandler eventHandler = super.deserialize(obj, map);
		if( eventHandler != null )
			return eventHandler;
		
		this.env = map.getScriptEnv();
		this.handlerFuncName = (String) obj.get("handlerFuncName");
		
		map.getEventManager().add(this);
		
		return null;
	}

}
