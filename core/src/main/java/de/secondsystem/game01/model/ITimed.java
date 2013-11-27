package de.secondsystem.game01.model;

public interface ITimed {
	void onTick(long frameTimeMs);
	void setTimerInterval(long intervalMs);
}
