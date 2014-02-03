package de.secondsystem.game01.model;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Properties;
import java.util.TreeSet;

import org.jsfml.window.Keyboard.Key;

public final class Settings {

	public final int height;
	
	public final int width;
	
	public final byte volume;
	
	public final byte brightness;
	
	public final int antiAliasingLevel;
	
	public final boolean verticalSync;
	
	public final boolean fullscreen;
	
	public final boolean dynamicLight;
	
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
		public final Key lift;
		private KeyMapping() {
			moveLeft = Key.A;
			moveRight = Key.D;
			moveUp = Key.W;
			moveDown = Key.S;
			jump = Key.SPACE;
			attack = Key.RCONTROL;
			switchWorld = Key.TAB;
			use = Key.W;
			lift = Key.E;
		}
		public KeyMapping(KeyMapping other) {
			this.moveLeft = other.moveLeft;
			this.moveRight = other.moveRight;
			this.moveUp = other.moveUp;
			this.moveDown = other.moveDown;
			this.jump = other.jump;
			this.attack = other.attack;
			this.switchWorld = other.switchWorld;
			this.use = other.use;
			this.lift = other.lift;
		}
		public KeyMapping(Key moveLeft, Key moveRight, Key moveUp,
				Key moveDown, Key jump, Key attack, Key switchWorld, Key use, Key lift) {
			this.moveLeft = moveLeft;
			this.moveRight = moveRight;
			this.moveUp = moveUp;
			this.moveDown = moveDown;
			this.jump = jump;
			this.attack = attack;
			this.switchWorld = switchWorld;
			this.use = use;
			this.lift = lift;
		}
	}
	
	private Settings() {
		height = 786;
		width = 1280;
		volume = 90;
		brightness = 50;
		antiAliasingLevel = 0;
		verticalSync = true;
		fullscreen = false;
		dynamicLight = true;
		keyMapping = new KeyMapping();
	}
	
	public Settings(Settings other) {
		this.height = other.height;
		this.width = other.width;
		this.volume = other.volume;
		this.brightness = other.brightness;
		this.antiAliasingLevel = other.antiAliasingLevel;
		this.verticalSync = other.verticalSync;
		this.fullscreen = other.fullscreen;
		this.dynamicLight = other.dynamicLight;
		this.keyMapping = new KeyMapping(other.keyMapping);
	}
	
	public Settings(int height, int width, byte volume, byte brightness,
			int antiAliasingLevel, boolean verticalSync, boolean fullscreen,
			boolean dynamicLight, KeyMapping keyMapping) {
		super();
		this.height = height;
		this.width = width;
		this.volume = volume;
		this.brightness = brightness;
		this.antiAliasingLevel = antiAliasingLevel;
		this.verticalSync = verticalSync;
		this.fullscreen = fullscreen;
		this.dynamicLight = dynamicLight;
		this.keyMapping = keyMapping;
	}



	public static Settings load(Path path) throws IOException {
		if( !Files.exists(path) )
			return storeDefaults(path);
		
		try ( Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8) ){
			Properties prop = new Properties();
			prop.load(reader);
			
			return new Settings(
					Integer.valueOf(prop.getProperty("height")),
					Integer.valueOf(prop.getProperty("width")),
					Byte.valueOf(prop.getProperty("volume")),
					Byte.valueOf(prop.getProperty("brightness")),
					Integer.valueOf(prop.getProperty("antiAliasingLevel")),
					Boolean.valueOf(prop.getProperty("verticalSync")),
					Boolean.valueOf(prop.getProperty("fullscreen")),
					Boolean.valueOf(prop.getProperty("dynamicLight")),
					loadMapping( prop, "keyMapping")
			);
			
		} catch( NullPointerException | NumberFormatException e ) {
			System.err.println("Error in settings-file: "+e.getMessage());
			e.printStackTrace();
			
			return storeDefaults(path);
		}
	}
	private static KeyMapping loadMapping(Properties prop, String prefix) {
		return new KeyMapping(
				Key.valueOf(prop.getProperty(prefix+".moveLeft")),
				Key.valueOf(prop.getProperty(prefix+".moveRight")),
				Key.valueOf(prop.getProperty(prefix+".moveUp")),
				Key.valueOf(prop.getProperty(prefix+".moveDown")),
				Key.valueOf(prop.getProperty(prefix+".jump")),
				Key.valueOf(prop.getProperty(prefix+".attack")),
				Key.valueOf(prop.getProperty(prefix+".switchWorld")),
				Key.valueOf(prop.getProperty(prefix+".use")),
				Key.valueOf(prop.getProperty(prefix+".lift"))
		);
	}
	
	@SuppressWarnings("serial")
	public static void store(Settings settings, Path path) throws IOException {
		try ( Writer writer = Files.newBufferedWriter( path, StandardCharsets.UTF_8) ){
			Properties prop = new Properties() {	// inline-class forces alphabetic ordering 
			    @Override
			    public synchronized Enumeration<Object> keys() {
			        return Collections.enumeration(new TreeSet<Object>(super.keySet()));
			    }
			};
			
			prop.put("height", 				Integer.toString(settings.height) );
			prop.put("width", 				Integer.toString(settings.width) );
			prop.put("volume", 				Byte.toString(settings.volume) );
			prop.put("brightness", 			Byte.toString(settings.brightness) );
			prop.put("antiAliasingLevel", 	Integer.toString(settings.antiAliasingLevel) );
			prop.put("verticalSync", 		Boolean.toString(settings.verticalSync) );
			prop.put("fullscreen", 			Boolean.toString(settings.fullscreen) );
			prop.put("dynamicLight", 		Boolean.toString(settings.dynamicLight) );

			prop.put("keyMapping.moveLeft", 	settings.keyMapping.moveLeft.name() );
			prop.put("keyMapping.moveRight", 	settings.keyMapping.moveRight.name() );
			prop.put("keyMapping.moveUp", 		settings.keyMapping.moveUp.name() );
			prop.put("keyMapping.moveDown", 	settings.keyMapping.moveDown.name() );
			prop.put("keyMapping.jump", 		settings.keyMapping.jump.name() );
			prop.put("keyMapping.attack", 		settings.keyMapping.attack.name() );
			prop.put("keyMapping.switchWorld", 	settings.keyMapping.switchWorld.name() );
			prop.put("keyMapping.use", 			settings.keyMapping.use.name() );
			prop.put("keyMapping.lift", 		settings.keyMapping.lift.name() );
			
			prop.store(writer, "This file is automatically generated. MODIFICATION MAY CAUSE UNDEFINED BEHAVIOR!");
		}
	}

	private static Settings storeDefaults(Path path) throws IOException {
		System.out.println("settings-file is missing or in error. replacing with defaults...");
		
		if( Files.exists(path) )
			Files.move( path, Paths.get(path+".error"+System.currentTimeMillis()) );
		
		Settings def = new Settings();
		store(def, path);
		
		return def;
	}
	
}
