package de.secondsystem.game01.model;

import java.nio.file.Path;

import org.jsfml.window.Keyboard.Key;

public final class Settings {

	public final int height;
	
	public final int width;
	
	public final int antiAliasingLevel;
	
	public final boolean verticalSync;
	
	public final boolean fullscreen;
	
	public final KeyMapping keyMapping;
	
	public static final class KeyMapping {
		public final Key moveLeft;
		public final Key moveRight;
		public final Key moveUp;
		public final Key moveDown;
		public final Key jump;
		public final Key attack;
		public final Key switchWorld;
		public final Key use;
		public KeyMapping() {
			moveLeft = Key.A;
			moveRight = Key.D;
			moveUp = Key.W;
			moveDown = Key.S;
			jump = Key.SPACE;
			attack = Key.RCONTROL;
			switchWorld = Key.TAB;
			use = Key.E;
		}
	}
	
	public Settings() {
		height = 786;
		width = 1280;
		antiAliasingLevel = 0;
		verticalSync = true;
		fullscreen = false;
		keyMapping = new KeyMapping();
	}
	
	
	public static Settings load(Path path) {
		return new Settings();	// TODO
	}
	
}
