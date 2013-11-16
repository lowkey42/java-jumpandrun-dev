package de.secondsystem.game01.impl.intro;

import org.jsfml.window.event.Event;

import de.secondsystem.game01.impl.GameContext;
import de.secondsystem.game01.impl.GameState;

public final class SettingsMenuState extends GameState {

	
	@Override
	protected void onStart(GameContext ctx) {
		System.out.println(">> SettingsMenu erfolgreich aufgerufen <<");
	}
	
	
	@Override
	protected void onStop(GameContext ctx) {
		// TODO
	}
	
	
	@Override
	protected void onFrame(GameContext ctx, long frameTime) {
		// TODO
		for(Event event : ctx.window.pollEvents()) {
	        if(event.type == Event.Type.CLOSED) {
	            //The user pressed the close button
	            ctx.window.close();
	        }
	    }
	}
	
}
