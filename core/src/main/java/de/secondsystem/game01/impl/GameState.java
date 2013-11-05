package de.secondsystem.game01.impl;

import de.secondsystem.game01.fsm.IContext;
import de.secondsystem.game01.fsm.IState;

/**
 * Basis-Klasse für alle grafischen Zustände der Anwendung
 * @author lowkey
 *
 */
public abstract class GameState implements IState {

	private GameContext ctx;
	private IState nextState;

	protected abstract void onStart(GameContext ctx);
	protected abstract void onStop(GameContext ctx);
	protected abstract void onFrame(GameContext ctx);
	
	protected final void setNextState(IState state) {
		nextState = state;
	}
	
	@Override
	public final void enter(IContext newCtx) {
		assert(newCtx instanceof GameContext);
		
		this.ctx = (GameContext) newCtx;
		onStart(ctx);
	}

	@Override
	public final IContext exit() {
		onStop(ctx);
		return ctx;
	}

	@Override
	public IState update() {
		if( !ctx.window.isOpen() )
			return new FinalizeState();
		
		ctx.window.clear();

		onFrame(ctx);

		ctx.window.display();
		
		return nextState!=null ? nextState : this;
	}

}
