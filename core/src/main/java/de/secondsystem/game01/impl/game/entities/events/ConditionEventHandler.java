package de.secondsystem.game01.impl.game.entities.events;

import javax.script.ScriptException;

import de.secondsystem.game01.impl.map.IGameMap;
import de.secondsystem.game01.impl.scripting.ScriptEnvironment;
import de.secondsystem.game01.model.Attributes;
import de.secondsystem.game01.model.Attributes.Attribute;

public class ConditionEventHandler implements IEventHandler {

	private static final String OWNER_PLACEHOLDER = "args"; 
	
	private final String condition;
	
	private final ScriptEnvironment env;
	
	private final IEventHandler subHandler;

	public ConditionEventHandler(IGameMap map, Attributes attributes) {
		this.env = map.getScriptEnv();
		this.condition = attributes.getString("condition");
		final Attributes sbAttr = attributes.getObject("subHandler");
		this.subHandler = EventUtils.createEventHandler(map, sbAttr);
	}
	public ConditionEventHandler(ScriptEnvironment env, String condition, IEventHandler subHandler) {
		this.env = env;
		this.condition = condition;
		this.subHandler = subHandler;
	}

	@Override
	public Object handle(Object... args) {
		try {
			return Boolean.valueOf(env.eval(condition, new Attribute(OWNER_PLACEHOLDER, args)).toString()) ? subHandler.handle(args) : null;
			
		} catch (ScriptException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public Attributes serialize() {
		return new Attributes(
				new Attribute(EventUtils.FACTORY, EventUtils.normalizeHandlerFactory(IfEHF.class.getName())),
				new Attribute("condition", condition),
				new Attribute("subHandler", subHandler.serialize())
		);
	}

}

final class IfEHF implements IEventHandlerFactory {
	@Override
	public ConditionEventHandler create(IGameMap map, Attributes attributes) {
		return new ConditionEventHandler(map, attributes);
	}
}
