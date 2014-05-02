package de.secondsystem.game01.impl;

import java.io.IOException;

import org.jsfml.graphics.View;
import org.jsfml.system.Clock;
import org.jsfml.system.Vector2f;
import org.jsfml.window.event.Event;

import de.secondsystem.game01.fsm.IContext;
import de.secondsystem.game01.fsm.IState;
import de.secondsystem.game01.impl.game.debug.ClockHUD;
import de.secondsystem.game01.impl.game.debug.FrameClock;

/**
 * Basis-Klasse für alle grafischen Zustände der Anwendung
 * @author lowkey
 *
 */
public abstract class GameState implements IState {

	private GameContext ctx;
	private IState nextState;
	private final Clock frameClock = new Clock();
	private boolean firstFrame = false;

	private final FrameClock debugFrameClock = new FrameClock();
	private final ClockHUD debugFcHud;
	private boolean displayFrameClock = false;

	protected abstract void onStart(GameContext ctx);
	protected abstract void onStop(GameContext ctx);
	protected abstract void onFrame(GameContext ctx, long frameTime);
	
	protected final void setNextState(IState state) {
		nextState = state;
	}
	
	public GameState() {
		ClockHUD clock = null;
		try {
			clock = new ClockHUD(debugFrameClock);
		} catch (IOException e) {
		}

		debugFcHud = clock;
	}
	
	@Override
	public final void enter(IContext newCtx) {
		assert(newCtx instanceof GameContext);
		
		this.ctx = (GameContext) newCtx;
		
		ctx.window.setView(new View(Vector2f.div(ctx.window.getView().getSize(), 2), ctx.window.getView().getSize()));
		
		onStart(ctx);
		firstFrame = true;
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
		
		debugFrameClock.beginFrame();
		
	//	ctx.window.clear();

		long frameTime = frameClock.restart().asMilliseconds();
		if( firstFrame ) {
			firstFrame = false;
			frameTime = 1;
		}
		
		if( frameTime > 16*4 ) {
			System.out.println("JITTER: "+frameTime+"  of "+(frameTime-(1000./60.)));
		}
		
		for(Event event : ctx.window.pollEvents()) {
	        if(event.type == Event.Type.CLOSED)
	            ctx.window.close();
	        else if( event.type == Event.Type.KEY_RELEASED )
	        	switch( event.asKeyEvent().key ) {
	        		case F11:
	        			displayFrameClock = !displayFrameClock;
	        			break;
	        		case F10:
	        			debugFrameClock.clear();
	        			break;
	        		default:
	        			break;
	        	}

	        processEvent(ctx, event);
	    }
		
		onFrame(ctx, frameTime);

		if( displayFrameClock )
			ctx.window.draw(debugFcHud);
		
		ctx.window.display();
		
		debugFrameClock.endFrame();
		
		if( nextState!=null ) { 
			IState r = nextState;
			nextState = null;
			return r;
		}
		return this;
	}
	
	protected void processEvent(GameContext ctx, Event event) {
	}

}
