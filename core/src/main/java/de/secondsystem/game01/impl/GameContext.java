package de.secondsystem.game01.impl;

import org.jsfml.graphics.RenderWindow;
import org.jsfml.window.ContextSettings;
import org.jsfml.window.VideoMode;
import org.jsfml.window.WindowStyle;

import de.secondsystem.game01.fsm.IContext;

/**
 * Zustandsunabhängiger Status der Anwendung
 * @author lowkey
 *
 */
public class GameContext implements IContext {

	public final RenderWindow window;
	
	// TODO: context-stuff
	
	// addition ! reason: lack of anti-aliasing // TODO: REMOVE COMMENT
	public GameContext( int width, int height, String title, int antiAliasingLevel ) {
		// create window
		window = new RenderWindow();
		// anti-aliasing diminishes the stair-step effect
		ContextSettings settings = new ContextSettings(antiAliasingLevel);
		window.create(new VideoMode(width, height), title, WindowStyle.DEFAULT, settings);
		window.setVerticalSyncEnabled(true);
	}

}
