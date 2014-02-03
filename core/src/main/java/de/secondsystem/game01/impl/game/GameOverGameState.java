package de.secondsystem.game01.impl.game;

import de.secondsystem.game01.impl.GameContext;
import de.secondsystem.game01.impl.GameState;
import de.secondsystem.game01.impl.intro.MainMenuState;

public class GameOverGameState extends GameState {

	@Override
	protected void onStart(GameContext ctx) {
	}

	@Override
	protected void onStop(GameContext ctx) {
	}

	@Override
	protected void onFrame(GameContext ctx, long frameTime) {
		// TODO: show info on screen and restart last level
		System.out.println("GAME OVER");
		setNextState(new MainMenuState());
	}

}
