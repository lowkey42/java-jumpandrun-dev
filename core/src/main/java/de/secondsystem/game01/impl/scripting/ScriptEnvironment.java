package de.secondsystem.game01.impl.scripting;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import de.secondsystem.game01.model.Attributes.Attribute;

public class ScriptEnvironment {
	
	public static enum ScriptType {
		JAVA_SCRIPT("javascript");
		
		final String name;
		private ScriptType(String name) { this.name = name; }
	}
	
	private final ScriptEngine engine;
	
	private final List<String> scripts = new ArrayList<>();
	
	public ScriptEnvironment( ScriptType type, Attribute... attributes ) {
		engine = new ScriptEngineManager().getEngineByName(type.name);
		
		for( Attribute att : attributes )
			engine.put(att.key, att.value);
	}
	
	public void load( String name ) throws IOException, ScriptException {
		scripts.add(name);
		try ( Reader reader = Files.newBufferedReader(Paths.get("assets", "scripts", name), StandardCharsets.UTF_8) ){
			engine.eval(reader);
		}
	}
	
	public void bind(String name, Object obj) {
		engine.put(name, obj);
	}
	
	public Object eval( String script ) throws ScriptException {
		return engine.eval(script);
	}
	
	public List<String> list() {
		return Collections.unmodifiableList(scripts);
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

}
