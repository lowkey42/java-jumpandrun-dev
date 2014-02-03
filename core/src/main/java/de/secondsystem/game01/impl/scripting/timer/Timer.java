package de.secondsystem.game01.impl.scripting.timer;

import de.secondsystem.game01.impl.scripting.ScriptEnvironment;

public abstract class Timer implements Comparable<Timer> {

	protected abstract void call();

	private boolean enabled = true;

	private long tickCount = 0L;
	protected final ScriptEnvironment env;
	private final long intervalMs;
	private final boolean repeated;
	private long startTimeMs;

	public Timer(ScriptEnvironment scriptEnv, long intervalMs, boolean repeated) {
		env = scriptEnv;	
		this.intervalMs = intervalMs;
		this.repeated = repeated;
		startTimeMs = System.currentTimeMillis();
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
	
	public boolean onTick() {
		tickCount++;
		
		call();
		
		return repeated && enabled;
	}

	@Override
	public int compareTo(Timer timer) {
		long t1 = this.startAt();
		long t2 = timer.startAt();
		return t1 > t2 ? 1 : t1 < t2 ? -1 : 0;
	}

}