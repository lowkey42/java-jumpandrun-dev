package de.secondsystem.game01.impl;

import org.jsfml.graphics.RenderWindow;
import org.jsfml.window.ContextSettings;
import org.jsfml.window.VideoMode;
import org.jsfml.window.WindowStyle;

import de.secondsystem.game01.fsm.IContext;

/**
 * GameState independent resources (stuff used by most/all GameStates)
 * @author lowkey
 *
 */
public class GameContext implements IContext {

	public final RenderWindow window;
	
	// TODO: other context-stuff
	
	/**
	 * Creates a new window and initializes resources required by most/all GameStates (e.g. game-configuration)
	 * @param width Width of the window
	 * @param height Height of the new window
	 * @param title Title of the new window
	 * @param antiAliasingLevel Level of antiAliasing to use or 0 to disable
	 */
	public GameContext( int width, int height, String title, int antiAliasingLevel ) {
		window = new RenderWindow();
		ContextSettings settings = new ContextSettings(antiAliasingLevel);
		window.create(new VideoMode(width, height), title, WindowStyle.DEFAULT, settings);
		window.setVerticalSyncEnabled(true);
	}

}
