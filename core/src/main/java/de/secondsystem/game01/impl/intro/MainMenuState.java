package de.secondsystem.game01.impl.intro;

import org.jsfml.graphics.Sprite;
import org.jsfml.graphics.Text;
import org.jsfml.graphics.Texture;
import org.jsfml.graphics.TextureCreationException;
import org.jsfml.window.Keyboard.Key;
import org.jsfml.window.event.Event;

import de.secondsystem.game01.impl.FinalizeState;
import de.secondsystem.game01.impl.GameContext;
import de.secondsystem.game01.impl.GameState;
import de.secondsystem.game01.impl.editor.EditorGameState;
import de.secondsystem.game01.impl.game.MainGameState;
import de.secondsystem.game01.impl.map.JsonGameMapSerializer;

/**
 * TODO
 * 
 */
public final class MainMenuState extends GameState {

	private final GameState playGameState;

	
	private final Sprite backdrop = new Sprite();

	
	// --> TODO Abfragen der Fensterbreite bereits beim Erschaffen des Buttons um diesen richtig zu positionieren
	private final MenuButton newGameBt = new MenuButton("NEW GAME", 515, 40, new MenuButton.IOnClickListener() {
		@Override public void onClick() {
			setNextState(new MainGameState("test01"));
		}
	});
	private final MenuButton loadGameBt = new MenuButton("LOAD GAME", 515, 190, new MenuButton.IOnClickListener() {
		@Override public void onClick() {
			setNextState(new MainGameState("test01"));
		}
	});
	private final MenuButton editorBt = new MenuButton("EDITOR", 515, 340, new MenuButton.IOnClickListener() {
		@Override public void onClick() {
			setNextState(new EditorGameState(MainMenuState.this,
					new JsonGameMapSerializer().deserialize("test01",
							true, true)));
		}
	});
	private final MenuButton settingsBt = new MenuButton("SETTINGS", 515, 490, new MenuButton.IOnClickListener() {
		@Override public void onClick() {
			setNextState(new SettingsMenuState(MainMenuState.this, playGameState,
					backdrop));
		}
	});
	private final MenuButton exitGameBt = new MenuButton("EXIT GAME", 515, 640, new MenuButton.IOnClickListener() {
		@Override public void onClick() {
			setNextState(new FinalizeState());
		}
	});
	
	InputText testtext = new InputText(200, 200, 200, "Test");

	public MainMenuState() {
		this.playGameState = null;
	}

	public MainMenuState(GameState playGameState) {
		// Transfering last State into playGameState
		this.playGameState = playGameState;
	}

	@Override
	protected void onStart(GameContext ctx) {
		// TODO

		if (backdrop.getTexture() == null) {
			Texture backdropBuffer = new Texture();
			// Creating Backdrop Texture via monitor screenshot of the stage
			// before, rendered on every frame
			try {
				backdropBuffer.create(ctx.settings.width, ctx.settings.height);
			} catch (TextureCreationException e) {
				e.printStackTrace();
			}
			backdropBuffer.update(ctx.window);
			backdrop.setTexture(backdropBuffer);
		}
	}

	@Override
	protected void onStop(GameContext ctx) {
		// TODO
	}

	@SuppressWarnings("incomplete-switch")
	@Override
	protected void onFrame(GameContext ctx, long frameTime) {
		// TODO Preparing the following code to be able to be used with more
		// than 1 button --> outsourcing into button class

		for (Event event : ctx.window.pollEvents()) {
			switch (event.type) {
			case CLOSED:
				ctx.window.close();
				break;
			case MOUSE_BUTTON_RELEASED:
				if( event.asMouseButtonEvent().button == org.jsfml.window.Mouse.Button.LEFT ) {
					newGameBt.onButtonReleased(event.asMouseButtonEvent().position.x, event.asMouseButtonEvent().position.y);
					editorBt.onButtonReleased(event.asMouseButtonEvent().position.x, event.asMouseButtonEvent().position.y);
					settingsBt.onButtonReleased(event.asMouseButtonEvent().position.x, event.asMouseButtonEvent().position.y);
					exitGameBt.onButtonReleased(event.asMouseButtonEvent().position.x, event.asMouseButtonEvent().position.y);
				}
				break;
			case TEXT_ENTERED:
				if(event.asTextEvent().unicode <= 127 && event.asTextEvent().unicode >= 32){
					System.out.println("TEXT ENTERED: " + event.asTextEvent().unicode);
					testtext.newKey(event);
				}
				break;
			case KEY_RELEASED:
				if ( playGameState!=null && event.asKeyEvent().key == Key.ESCAPE) {
					setNextState(playGameState);
				}

			}
		}

		ctx.window.draw(backdrop);

		newGameBt.draw(ctx.window);
		editorBt.draw(ctx.window);
		settingsBt.draw(ctx.window);
		loadGameBt.draw(ctx.window);
		exitGameBt.draw(ctx.window);

		testtext.draw(ctx.window);
		
	}
}
