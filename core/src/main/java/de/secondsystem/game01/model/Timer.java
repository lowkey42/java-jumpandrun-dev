package de.secondsystem.game01.model;

public class Timer {
	private boolean enabled = true;
	private long elapsedTimeMs = 0L;
	private long intervalMs = 1000L;
	private long tickCount = 0L;
	
	public Timer() {
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
	
	public void update(long frameTimeMs, ITimed timed) {
		if( enabled ) {
			elapsedTimeMs += frameTimeMs;
			
			if( elapsedTimeMs >= intervalMs ) {
				elapsedTimeMs = 0L;
				tickCount++;
				timed.onTick(frameTimeMs);			
			}
		}
	}
	
	public void setInterval(long intervalMs) {
		this.intervalMs = intervalMs;
	}
	
	public void reset() {
		enabled = true;
		elapsedTimeMs = 0L;
		tickCount = 0L;
	}
}
