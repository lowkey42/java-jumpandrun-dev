package de.secondsystem.game01.impl.game.entities.events.impl;

public interface IPlayedBack {
	void onPlay();
	void onReverse();
	void onStop();
	void onResume();
	void onPause();
}
