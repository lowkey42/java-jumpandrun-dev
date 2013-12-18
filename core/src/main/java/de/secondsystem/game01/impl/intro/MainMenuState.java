package de.secondsystem.game01.impl.intro;

import org.jsfml.graphics.Sprite;
import org.jsfml.graphics.Texture;
import org.jsfml.graphics.TextureCreationException;
import org.jsfml.window.Keyboard.Key;
import org.jsfml.window.event.Event;

import de.secondsystem.game01.impl.FinalizeState;
import de.secondsystem.game01.impl.GameContext;
import de.secondsystem.game01.impl.GameState;
import de.secondsystem.game01.impl.editor.EditorGameState;
import de.secondsystem.game01.impl.game.MainGameState;
import de.secondsystem.game01.impl.gui.GUITestState;
import de.secondsystem.game01.impl.gui.IOnClickListener;
import de.secondsystem.game01.impl.gui.MenuButton;
import de.secondsystem.game01.impl.map.JsonGameMapSerializer;

/**
 * TODO
 * 
 */
public final class MainMenuState extends GameState {

	private final GameState playGameState;

	private final Sprite backdrop = new Sprite();

	
	// --> TODO Abfragen der Fensterbreite bereits beim Erschaffen des Buttons um diesen richtig zu positionieren
	private final MenuButton newGameBt = new MenuButton(515, 40, "NEW GAME", new IOnClickListener() {
		
		@Override public void onClick() {
			setNextState(new MainGameState("test01"));
		}
	});
	
	private final MenuButton editorBt = new MenuButton(515, 190, "EDITOR", new IOnClickListener() {
		
		@Override public void onClick() {
			setNextState(new EditorGameState(MainMenuState.this,
					new JsonGameMapSerializer().deserialize("test01",
							true, true)));
		}
	});
	
	private final MenuButton loadGameBt = new MenuButton(515, 340, "GUI TEST SITE", new IOnClickListener() {
		
		@Override public void onClick() {
			setNextState(new GUITestState(MainMenuState.this, playGameState, backdrop));
		}
	});
	
	private final MenuButton settingsBt = new MenuButton(515, 490, "SETTINGS", new IOnClickListener() {
		
		@Override public void onClick() {
			setNextState(new SettingsMenuState(MainMenuState.this, playGameState, backdrop));
		}
	});
	
	private final MenuButton exitGameBt = new MenuButton(515, 640, "EXIT GAME", new IOnClickListener() {
		
		@Override public void onClick() {
			setNextState(new FinalizeState());
		}
	});
	
	// Constructors
	public MainMenuState() {
		this.playGameState = null;
	}

	public MainMenuState(GameState playGameState) {
		// Transfering last State into playGameState
		this.playGameState = playGameState;
	}

	@Override
	protected void onStart(GameContext ctx) {

		if (backdrop.getTexture() == null) {
			Texture backdropBuffer = new Texture();
			// Creating Backdrop Texture via monitor screenshot of the stage
			// before, rendered on every frame
			try {
				backdropBuffer.create(ctx.getViewWidth()*2, ctx.getViewHeight()*2);
			} catch (TextureCreationException e) {
				e.printStackTrace();
			}
			backdropBuffer.update(ctx.window);
			backdrop.setTexture(backdropBuffer, true);
		}
	}

	@Override
	protected void onStop(GameContext ctx) {
		// TODO
	}

	@SuppressWarnings("incomplete-switch")
	@Override
	protected void onFrame(GameContext ctx, long frameTime) {

		for (Event event : ctx.window.pollEvents()) {
			switch (event.type) {
			case CLOSED:
				ctx.window.close();
				break;
			case MOUSE_BUTTON_RELEASED:
				if( event.asMouseButtonEvent().button == org.jsfml.window.Mouse.Button.LEFT ) {
					boolean dummy =
					newGameBt.onButtonReleased(ctx.getMousePosition().x, ctx.getMousePosition().y) ||
					loadGameBt.onButtonReleased(ctx.getMousePosition().x, ctx.getMousePosition().y) ||
					editorBt.onButtonReleased(ctx.getMousePosition().x, ctx.getMousePosition().y) ||
					settingsBt.onButtonReleased(ctx.getMousePosition().x, ctx.getMousePosition().y) ||
					exitGameBt.onButtonReleased(ctx.getMousePosition().x, ctx.getMousePosition().y);
				}
				break;
			case KEY_RELEASED:
				if ( playGameState!=null && event.asKeyEvent().key == Key.ESCAPE)
					setNextState(playGameState);
			}
		}

		ctx.window.draw(backdrop);

		newGameBt.draw(ctx.window);
		editorBt.draw(ctx.window);
		settingsBt.draw(ctx.window);
		loadGameBt.draw(ctx.window);
		exitGameBt.draw(ctx.window);

	}
}