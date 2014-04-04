package de.secondsystem.game01.impl.intro;

import org.jsfml.graphics.Sprite;
import org.jsfml.system.Vector2f;
import org.jsfml.window.Keyboard.Key;
import org.jsfml.window.event.Event;

import de.secondsystem.game01.impl.GameContext;
import de.secondsystem.game01.impl.GameState;
import de.secondsystem.game01.impl.gui.GUIGameState;
import de.secondsystem.game01.impl.gui.LayoutElementContainer;
import de.secondsystem.game01.impl.gui.LayoutElementContainer.Layout;
import de.secondsystem.game01.impl.gui.LayoutElementContainer.LayoutDirection;
import de.secondsystem.game01.impl.gui.listeners.IOnClickListener;

public final class SettingsMenuState extends GUIGameState {

	@Override
	protected Layout getLayout() {
		return new Layout(LayoutDirection.VERTICAL, 25);
	}

	@Override
	protected Vector2f getPosition() {
		return new Vector2f(800, 50);
	}

	@Override
	protected void initGui(GameContext ctx, LayoutElementContainer c) {
		c.createButton("Resolution", new IOnClickListener() {
			@Override public void onClick() {
				// TODO
			}
		});
		c.createButton("VSync", new IOnClickListener() {
			@Override public void onClick() {
				// TODO
			}
		});
		c.createButton("Antialiasing", new IOnClickListener() {
			@Override public void onClick() {
				// TODO
			}
		});


		c.createLabel("Volume").setFor(c.createSlider());
		c.createLabel("Brightness").setFor(c.createSlider());
		

		c.createButton("Apply", new IOnClickListener() {
			@Override public void onClick() {
				// TODO: save
			}
		});
		c.createButton("Back", new IOnClickListener() {
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
		super.processEvent(ctx, event);
		
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
