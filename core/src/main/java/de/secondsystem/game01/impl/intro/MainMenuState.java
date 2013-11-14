package de.secondsystem.game01.impl.intro;

import org.jsfml.window.event.Event;

import java.nio.file.Paths;

import de.secondsystem.game01.impl.GameContext;
import de.secondsystem.game01.impl.GameState;

/**
 * TODO
 *
 */
public final class MainMenuState extends GameState {
	
	Button myButton = new Button("NewGame", Paths.get("assets", "gui", "buttons", "ButtonNormal.png"), 320, 240);
	
	@Override
	protected void onStart(GameContext ctx) {
		// TODO
		System.out.println(">> MainMenuState erfolgreich aufgerufen <<");
		myButton.create();
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
		
		// ctx.window.draw(newsprite); <--- ???
		
	}

}
