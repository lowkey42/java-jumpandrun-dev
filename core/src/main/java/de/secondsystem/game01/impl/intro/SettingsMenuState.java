package de.secondsystem.game01.impl.intro;

import org.jsfml.graphics.Sprite;
import org.jsfml.window.Keyboard.Key;
import org.jsfml.window.event.Event;

import de.secondsystem.game01.impl.GameContext;
import de.secondsystem.game01.impl.GameState;
import de.secondsystem.game01.impl.gui.GUIGameStateSimpleLayout;
import de.secondsystem.game01.impl.gui.listeners.IOnClickListener;

public final class SettingsMenuState extends GUIGameStateSimpleLayout {

	@Override
	protected int getElementSpacing() {
		return 100;
	}

	@Override
	protected int getXPosition() {
		return 800;
	}

	@Override
	protected int getYPosition() {
		return 200;
	}

	@Override
	protected void initGui(GameContext ctx) {
		createButton("Resolution", new IOnClickListener() {
			@Override public void onClick() {
				// TODO
			}
		});
		createButton("VSync", new IOnClickListener() {
			@Override public void onClick() {
				// TODO
			}
		});
		createButton("Antialiasing", new IOnClickListener() {
			@Override public void onClick() {
				// TODO
			}
		});


		createLabel("Volume").setFor(createSlider());
		createLabel("Brightness").setFor(createSlider());
		

		createButton("Apply", new IOnClickListener() {
			@Override public void onClick() {
				// TODO: save
			}
		});
		createButton("Back", new IOnClickListener() {
			@Override public void onClick() {
				setNextState(MainMenu);
			}
		});
	}


	private final GameState playGameState;
	private GameState MainMenu;
	
	private final Sprite backdrop;

	public SettingsMenuState(GameState MainMenu, GameState playGameState,
			Sprite backdrop) {
		// Transfering last State into playGameState
		this.playGameState = playGameState;
		this.MainMenu = MainMenu;
		this.backdrop = backdrop;
	}

	@Override
	protected void onStart(GameContext ctx) {
		super.onStart(ctx);
	}

	@Override
	protected void onStop(GameContext ctx) {
		// TODO
	}

	@Override
	protected void onFrame(GameContext ctx, long frameTime) {
		ctx.window.clear();
		
		ctx.window.draw(backdrop);

		super.onFrame(ctx, frameTime);
	}

	@SuppressWarnings("incomplete-switch")
	@Override
	protected void processEvent(GameContext ctx, Event event) {
		switch (event.type) {
			case KEY_RELEASED:
				if (event.asKeyEvent().key == Key.ESCAPE) {
					ctx.window.draw(backdrop);
					setNextState(playGameState);
				}
				break;
		}
	}
	
}