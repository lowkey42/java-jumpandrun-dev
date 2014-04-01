package de.secondsystem.game01.impl.game.entities.events;

import javax.script.ScriptException;

import de.secondsystem.game01.impl.map.IGameMap;
import de.secondsystem.game01.impl.scripting.ScriptEnvironment;
import de.secondsystem.game01.model.Attributes;
import de.secondsystem.game01.model.Attributes.Attribute;

public class ScriptEventHandler implements IEventHandler {

	private String handlerFuncName;
	
	private String functionBody;
	
	private ScriptEnvironment env;
	
	public ScriptEventHandler(IGameMap map, Attributes attributes) {
		this.env = map.getScriptEnv();
		this.handlerFuncName = attributes.getString("func");
		this.functionBody = attributes.getString("body");
	}
	public ScriptEventHandler(ScriptEnvironment env, String handlerFuncName) {
		this.handlerFuncName = handlerFuncName;
		this.env = env;
	}

	@Override
	public Object handle(Object... args) {
		if( functionBody!=null )
			try {
				System.out.println("exec-script: \""+functionBody+"\"");
				return env.eval(functionBody, new Attribute("args", args));
				
			} catch (ScriptException e) {
				e.printStackTrace();
				return null;
			}
		else
			return env.exec(handlerFuncName, args);
	}

	@Override
	public Attributes serialize() {
		Attributes attr = new Attributes();
		if( handlerFuncName!=null )
			attr.put("func", handlerFuncName);
		
		if( functionBody!=null )
			attr.put("body", functionBody);
		
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