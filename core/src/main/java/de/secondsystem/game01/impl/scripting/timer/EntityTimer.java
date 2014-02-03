package de.secondsystem.game01.impl.scripting.timer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import de.secondsystem.game01.impl.game.entities.IGameEntity;
import de.secondsystem.game01.impl.scripting.ScriptEnvironment;

public class EntityTimer extends Timer {

	protected final String funcName;
	protected final IGameEntity target;
	protected final List<Object> args;
	
	public EntityTimer(ScriptEnvironment scriptEnv, long intervalMs, boolean repeated, String funcName, IGameEntity target, Object... args) {
		super(scriptEnv, intervalMs, repeated);
		
		this.funcName = funcName;
		this.target = target;
		this.args = Collections.unmodifiableList( Arrays.asList(args) );
	}
	
	
	@Override
	protected void call() {
		final List<Object> argArray = new ArrayList<>();
		argArray.add(this);
		argArray.add(target);
		argArray.addAll(args);
		
		env.exec(funcName, argArray.toArray());
	}
}
