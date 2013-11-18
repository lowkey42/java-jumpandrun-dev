package de.secondsystem.game01.impl;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.jsfml.graphics.RenderWindow;
import org.jsfml.window.ContextSettings;
import org.jsfml.window.VideoMode;
import org.jsfml.window.WindowStyle;

import de.secondsystem.game01.fsm.IContext;
import de.secondsystem.game01.model.Settings;

/**
 * GameState independent resources (stuff used by most/all GameStates)
 * @author lowkey
 *
 */
public class GameContext implements IContext {

	private static final String WINDOW_TITLE = "GAME_01";
	
	public static final Path CONFIG_PATH = Paths.get("game.cfg");
	
	public final RenderWindow window;
	
	public final Settings settings;
	
	
	/**
	 * Creates a new window and initializes resources required by most/all GameStates (e.g. game-configuration)
	 * @param width Width of the window
	 * @param height Height of the new window
	 * @param title Title of the new window
	 * @param antiAliasingLevel Level of antiAliasing to use or 0 to disable
	 */
	public GameContext() {
		settings = Settings.load(CONFIG_PATH);
		window = new RenderWindow();
		ContextSettings ctxSettings = new ContextSettings(settings.antiAliasingLevel);
		int style = settings.fullscreen ? WindowStyle.FULLSCREEN : WindowStyle.CLOSE|WindowStyle.TITLEBAR;
		
		window.create(new VideoMode(settings.width, settings.height), WINDOW_TITLE, style, ctxSettings);
		window.setVerticalSyncEnabled(settings.verticalSync);
	}

}
