package de.secondsystem.game01.impl;

import org.jsfml.graphics.RenderWindow;
import org.jsfml.window.VideoMode;

import de.secondsystem.game01.fsm.IContext;

/**
 * Zustandsunabhängiger Status der Anwendung
 * @author lowkey
 *
 */
public class GameContext implements IContext {

	public final RenderWindow window;
	
	// TODO: context-stuff
	
	public GameContext( int width, int height, String title ) {
		window = new RenderWindow();
		window.create(new VideoMode(width, height), title);
		window.setVerticalSyncEnabled(true);
	}

}
