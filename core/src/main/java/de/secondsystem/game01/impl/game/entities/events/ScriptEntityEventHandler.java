package de.secondsystem.game01.impl.game.entities.events;

import de.secondsystem.game01.impl.game.entities.IGameEntity;
import de.secondsystem.game01.impl.scripting.ScriptEnvironment;

public class ScriptEntityEventHandler extends SingleEntityEventHandler {

	private final String handlerFuncName;
	
	private final ScriptEnvironment env;
	
	public ScriptEntityEventHandler(ScriptEnvironment env, EntityEventType eventType, String handlerFuncName) {
		super(eventType);
		this.handlerFuncName = handlerFuncName;
		this.env = env;
	}

	@Override
	public Object handle(EntityEventType type, IGameEntity owner,
			Object... args) {
		return env.exec(handlerFuncName, owner, args);
	}

	@Override
	public Object handle(EntityEventType type, Object... args) {
		return env.exec(handlerFuncName, args);
	}

}
