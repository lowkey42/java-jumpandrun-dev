package de.secondsystem.game01.impl.scripting;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;

import javax.script.Invocable;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import de.secondsystem.game01.impl.scripting.timer.TimerManager;
import de.secondsystem.game01.model.Attributes;
import de.secondsystem.game01.model.IUpdateable;
import de.secondsystem.game01.model.Attributes.Attribute;

public class ScriptEnvironment implements IUpdateable {
	
	public static enum ScriptType {
		JAVA_SCRIPT("javascript");
		
		final String name;
		private ScriptType(String name) { this.name = name; }
	}
	
	private final ScriptEngine engine;
	
	private final List<String> scriptsToLoad = new ArrayList<>();
	
	private final List<String> scripts = new ArrayList<>();
	
	private final TimerManager timerManager = new TimerManager(this);
	
	public ScriptEnvironment( ScriptType type, IScriptApi scriptApi, Attributes attributes ) {
		engine = new ScriptEngineManager().getEngineByName(type.name);
		
		engine.put("timer", timerManager);
		engine.put("System", this);
		engine.put("API", scriptApi!=null ? scriptApi : new DummyScriptApi());
		
		for( Entry<String, Object> att : attributes.entrySet() )
			engine.put(att.getKey(), att.getValue());
		
		try {
			load("defaults.js");
		} catch (IOException | ScriptException e) {
			System.err.println("Unable to load defaults.js: "+e.getMessage());
		}
	}

	public void queueLoad( String name ) throws IOException, ScriptException {
		scriptsToLoad.add(name);
	}
	public void load( String name ) throws IOException, ScriptException {
		scripts.add(name);
		try ( Reader reader = Files.newBufferedReader(Paths.get("assets", "scripts", name), StandardCharsets.UTF_8) ){
			engine.eval(reader);
		}
	}
	
	public Object eval( String script, Attribute arg ) throws ScriptException {
		final String argName = "_tmp"+System.nanoTime();
		
		engine.getContext().setAttribute(argName, arg.value, ScriptContext.ENGINE_SCOPE);
		
		try {
			return engine.eval(script.replace(arg.key, argName));
			
		} finally {
			engine.getContext().removeAttribute(argName, ScriptContext.ENGINE_SCOPE);
		}
	}
	
	public void bind(String name, Object obj) {
		engine.put(name, obj);
	}
	
	public Object eval( String script ) throws ScriptException {
		return engine.eval(script);
	}
	
	public List<String> list() {
		List<String> s = new ArrayList<>(scriptsToLoad);
		s.addAll(scripts);
		return Collections.unmodifiableList(s);
	}
	
	public Object exec( String function, Object... args ) {
		if( engine instanceof Invocable ) {
			try {
				return ((Invocable) engine).invokeFunction(function, args);
				
			} catch(NoSuchMethodException e) {
				// nothing
			} catch(ScriptException e) {
				System.err.println("Error in scripts '"+scripts+"': "+e.getMessage());
				e.printStackTrace();
			}
		}
		
		return null;
	}

	@Override
	public void update(long frameTimeMs) {
		if( !scriptsToLoad.isEmpty() ) {
			for( String script : scriptsToLoad )
				try {
					load(script);
				} catch (IOException | ScriptException e) {
					System.err.println("Error loading script '"+script+"': "+e.getMessage());
					e.printStackTrace();
				}
			
			scriptsToLoad.clear();
		}
		
		timerManager.update(frameTimeMs);
	}
	
	public TimerManager getTimerManager() {
		return timerManager;
	}

}
