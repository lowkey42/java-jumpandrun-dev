package de.secondsystem.game01.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.script.ScriptException;

import de.secondsystem.game01.impl.scripting.ScriptEnvironment;
import de.secondsystem.game01.model.IUpdateable;

public class DevConsole implements IUpdateable {

	private ScriptEnvironment env;
	
	private StringBuilder input = new StringBuilder();
	
	public DevConsole() {
	}

	public void setScriptEnvironment(ScriptEnvironment env) {
		this.env = env;
	}
	
	@Override
	public void update(long frameTimeMs) {
		if( env==null )
			return;
		
		List<String> commands = read();
		
		for( String command : commands ) {
			try {
				Object ret = env.eval(command);
				
				System.err.println("DEV# success: "+ret);
				
			} catch (ScriptException e) {
				System.err.println("DEV# error: "+e.getMessage());
				e.printStackTrace();
			}
		}
	}

	private List<String> read() {
		List<String> commands = new ArrayList<>(1); 
		
		try {
			while( System.in.available()>0 ) {
				int r = System.in.read();
				if( r==-1 )
					break;
				
				if( r=='\n' ) {
					commands.add( input.toString() );
					input.setLength(0);
					
				} else
					input.append( (char)r );
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return commands;
	}

}
