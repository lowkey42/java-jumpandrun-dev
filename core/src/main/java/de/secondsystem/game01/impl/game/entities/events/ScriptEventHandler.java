package de.secondsystem.game01.impl.game.entities.events;

import de.secondsystem.game01.impl.map.IGameMap;
import de.secondsystem.game01.impl.scripting.ScriptEnvironment;
import de.secondsystem.game01.model.Attributes;

public class ScriptEventHandler implements IEventHandler {

	private String handlerFuncName;
	
	private ScriptEnvironment env;
	
	public ScriptEventHandler(IGameMap map, Attributes attributes) {
		this.env = map.getScriptEnv();
		this.handlerFuncName = attributes.getString("handlerFuncName");
	}
	public ScriptEventHandler(ScriptEnvironment env, String handlerFuncName) {
		this.handlerFuncName = handlerFuncName;
		this.env = env;
	}

	@Override
	public Object handle(Object... args) {
		return env.exec(handlerFuncName, args);
	}

	@Override
	public Attributes serialize() {
		Attributes attr = new Attributes();
		attr.put("handlerFuncName", handlerFuncName);
		attr.put(EventUtils.FACTORY, EventUtils.normalizeHandlerFactory(ScriptEHF.class.getName()) );
		
		return attr;
	}

}

final class ScriptEHF implements IEventHandlerFactory {
	@Override
	public ScriptEventHandler create(IGameMap map, Attributes attributes) {
		return new ScriptEventHandler(map, attributes);
	}
}