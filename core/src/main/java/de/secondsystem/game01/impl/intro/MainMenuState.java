package de.secondsystem.game01.impl.intro;

import org.jsfml.window.event.Event;

import de.secondsystem.game01.impl.GameContext;
import de.secondsystem.game01.impl.GameState;

/**
 * TODO
 *
 */
public final class MainMenuState extends GameState {
	
	@Override
	protected void onStart(GameContext ctx) {
		// TODO
		System.out.println("dfdfd");
	}

	@Override
	protected void onStop(GameContext ctx) {
		// TODO
	}

	@Override
	protected void onFrame(GameContext ctx) {
		// TODO
		for(Event event : ctx.window.pollEvents()) {
	        if(event.type == Event.Type.CLOSED) {
	            //The user pressed the close button
	            ctx.window.close();
	        }
	    }
	}

}
