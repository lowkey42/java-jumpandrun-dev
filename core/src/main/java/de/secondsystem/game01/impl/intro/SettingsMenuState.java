package de.secondsystem.game01.impl.intro;

import org.jsfml.graphics.Sprite;
import org.jsfml.graphics.Texture;
import org.jsfml.window.Keyboard.Key;
import org.jsfml.window.Mouse;
import org.jsfml.window.event.Event;

import de.secondsystem.game01.impl.GameContext;
import de.secondsystem.game01.impl.GameState;

public final class SettingsMenuState extends GameState {

	GameState playGameState;
	GameState MainMenu;

	MenuButton changeRes = new MenuButton("Change Resolution", 200, 120);
	MenuButton vSync = new MenuButton("VSync", 200, 320);
	MenuButton antiA = new MenuButton("Antialiasing", 200, 520);
	MenuButton apply = new MenuButton("APPLY", 540, 520);
	MenuButton back = new MenuButton("BACK", 870, 520);

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

	@SuppressWarnings("incomplete-switch")
	@Override
	protected void onFrame(GameContext ctx, long frameTime) {
		// TODO
		for (Event event : ctx.window.pollEvents()) {
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
				if (back.newsprite.getGlobalBounds().contains(
						Mouse.getPosition(ctx.window).x,
						(Mouse.getPosition(ctx.window).y))
						&& event.asMouseButtonEvent().button == org.jsfml.window.Mouse.Button.LEFT) {
					setNextState(MainMenu);
				}

				break;
			case KEY_RELEASED:
				if (event.asKeyEvent().key == Key.ESCAPE) {
					ctx.window.draw(backdrop);
					setNextState(playGameState);
				}
			case MOUSE_BUTTON_PRESSED:
			case MOUSE_MOVED:
				changeRes.mouseover(ctx.window);
				vSync.mouseover(ctx.window);
				antiA.mouseover(ctx.window);
				back.mouseover(ctx.window);
				apply.mouseover(ctx.window);
				sliderOne.mouseover(ctx.window, event);
				sliderTwo.mouseover(ctx.window, event);

				break;
			}
		}

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

}
