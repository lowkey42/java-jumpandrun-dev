package de.secondsystem.game01.impl.timer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import de.secondsystem.game01.impl.game.entities.IGameEntity;
import de.secondsystem.game01.impl.scripting.ScriptEnvironment;

public class Timer implements Comparable<Timer>{
	private boolean enabled = true;
	private long tickCount = 0L;
	private final String funcName;
	private final ScriptEnvironment env;
	private final IGameEntity target;
	private final long intervalMs;
	private final boolean repeated;	
	private final List<Object> args;
	private long startTimeMs;
	
	public Timer(ScriptEnvironment scriptEnv, long intervalMs, boolean repeated, String funcName, IGameEntity target, Object... args) {
		env = scriptEnv;	
		this.funcName = funcName;
		this.intervalMs = intervalMs;
		this.repeated = repeated;
		this.target = target;
		startTimeMs = System.currentTimeMillis();

		this.args = Collections.unmodifiableList( Arrays.asList(args) );
	}
	
	
	public boolean onTick() {
		tickCount++;
		
		final List<Object> argArray = new ArrayList<>();
		argArray.add(this);
		argArray.add(target);
		argArray.addAll(args);
		
		env.exec(funcName, argArray.toArray());
		
		return repeated && enabled;
	}
	
	public long startAt() {
		return startTimeMs+intervalMs;
	}
	
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;	
	}
	
	public long getTickCount() {
		return tickCount;
	}
	
	public boolean isEnabled() {
		return enabled;
	}
	
	public void reset() {
		startTimeMs = System.currentTimeMillis();
	}
	
	@Override
	public int compareTo(Timer timer) {
		long t1 = this.startAt();
		long t2 = timer.startAt();
		return t1 > t2 ? 1 : t1 < t2 ? -1 : 0;
	}
}
