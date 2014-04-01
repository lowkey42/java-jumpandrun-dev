package de.secondsystem.game01.impl.intro;

import org.jsfml.graphics.Sprite;
import org.jsfml.graphics.Texture;
import org.jsfml.window.Keyboard.Key;
import org.jsfml.window.event.Event;

import de.secondsystem.game01.impl.GameContext;
import de.secondsystem.game01.impl.GameState;
import de.secondsystem.game01.impl.gui.MenuButton;
import de.secondsystem.game01.impl.gui.SlideButton;
import de.secondsystem.game01.impl.gui.listeners.IOnClickListener;

public final class SettingsMenuState extends GameState {

	private final GameState playGameState;
	private GameState MainMenu;

	MenuButton changeRes = new MenuButton(200, 120, "Resolution", null);
	MenuButton vSync = new MenuButton(200, 320, "VSync", null);
	MenuButton antiA = new MenuButton(200, 520, "Antialiasing", null);
	MenuButton apply = new MenuButton(540, 520, "APPLY", null);
	MenuButton back = new MenuButton(870, 520, "BACK", null, new IOnClickListener() {
	
		@Override public void onClick() {
			setNextState(MainMenu);
		}
	});

	SlideButton sliderOne = new SlideButton("Volume", 620, 100);
	SlideButton sliderTwo = new SlideButton("Brightness", 620, 300);

	Texture backdropBuffer = new Texture();
	Sprite backdrop = new Sprite();

	public SettingsMenuState(GameState MainMenu, GameState playGameState,
			Sprite backdrop) {
		// Transfering last State into playGameState
		this.playGameState = playGameState;
		this.MainMenu = MainMenu;
		this.backdrop = backdrop;
	}

	@Override
	protected void onStart(GameContext ctx) {

	}

	@Override
	protected void onStop(GameContext ctx) {
		// TODO
	}

	@Override
	protected void onFrame(GameContext ctx, long frameTime) {
		// TODO
		ctx.window.clear();
		
		ctx.window.draw(backdrop);

		//sliderOneBack.draw(ctx.window);
		sliderOne.draw(ctx.window);
		//sliderTwoBack.draw(ctx.window);
		sliderTwo.draw(ctx.window);

		changeRes.draw(ctx.window);
		vSync.draw(ctx.window);
		antiA.draw(ctx.window);
		back.draw(ctx.window);
		apply.draw(ctx.window);

	}

	@SuppressWarnings("incomplete-switch")
	@Override
	protected void processEvent(GameContext ctx, Event event) {
		switch (event.type) {
		case CLOSED:
			ctx.window.close();
			break;
		case MOUSE_BUTTON_RELEASED:
			// Checking if the current mouse position is inside the Button
			// and only the left mouse button is pressed
			// if(sliderOne.sliderSprite.getGlobalBounds().contains(Mouse.getPosition(ctx.window).x,
			// (Mouse.getPosition(ctx.window).y)) &&
			// event.asMouseButtonEvent().button ==
			// org.jsfml.window.Mouse.Button.LEFT)
			// setNextState(new MainGameState("test01"));
			if( event.asMouseButtonEvent().button == org.jsfml.window.Mouse.Button.LEFT ) {
				back.onButtonReleased(ctx.getMousePosition().x, ctx.getMousePosition().y);
			}

			break;
		case KEY_RELEASED:
			if (event.asKeyEvent().key == Key.ESCAPE) {
				ctx.window.draw(backdrop);
				setNextState(playGameState);
			}
		case MOUSE_BUTTON_PRESSED:
		case MOUSE_MOVED:
			sliderOne.mouseover(ctx, event);
			sliderTwo.mouseover(ctx, event);

			break;
		}
	}
	
}