package de.secondsystem.game01.impl.scripting.timer;

import javax.script.ScriptException;

import de.secondsystem.game01.impl.scripting.ScriptEnvironment;
import de.secondsystem.game01.model.Attributes.Attribute;

public final class FunctionTimer extends Timer {

	private final Object callable;
	
	public FunctionTimer(ScriptEnvironment scriptEnv, long intervalMs,
			boolean repeated, Object callable) {
		super(scriptEnv, intervalMs, repeated);
		
		this.callable = callable;
	}

	@Override
	protected void call() {
		if( callable instanceof Runnable )
			((Runnable) callable).run();
		
		else
			try {
				env.eval("f()", new Attribute("f", callable));
				
			} catch (ScriptException e) {
				System.err.println("Error calling function: "+e.getMessage());
				e.printStackTrace();
			}
	}

}
