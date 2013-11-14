package de.secondsystem.game01.impl;

import org.jsfml.system.Clock;

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
	private final Clock frameClock = new Clock();

	protected abstract void onStart(GameContext ctx);
	protected abstract void onStop(GameContext ctx);
	protected abstract void onFrame(GameContext ctx, long frameTime);
	
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

		final long frameTime = frameClock.restart().asMilliseconds();
		onFrame(ctx, frameTime);

		ctx.window.display();
		
		if( nextState!=null ) { 
			IState r = nextState;
			nextState = null;
			return r;
		}
		return this;
	}

}
