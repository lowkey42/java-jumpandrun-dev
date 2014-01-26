package de.secondsystem.game01.impl;

public abstract class FuncGameState extends GameState {
	
	@Override
	protected void onStart(GameContext ctx) {
	}

	@Override
	protected void onStop(GameContext ctx) {
	}

	@Override
	protected void onFrame(GameContext ctx, long frameTime) {
		setNextState(new FinalizeState());
	}

}
