package de.secondsystem.game01.impl.intro;

import org.jsfml.window.event.Event;

import de.secondsystem.game01.impl.GameContext;
import de.secondsystem.game01.impl.GameState;

/**
 * TODO
 *
 */
public final class MainMenuState extends GameState {
	
	Button myButton = new Button("NEW GAME", 320, 240);
	
	@Override
	protected void onStart(GameContext ctx) {
		// TODO
		System.out.println(">> MainMenuState erfolgreich aufgerufen <<");
	}

	@Override
	protected void onStop(GameContext ctx) {
		// TODO
	}

	@Override
	protected void onFrame(GameContext ctx, long frameTime) {
		// TODO
		for(Event event : ctx.window.pollEvents()) {
	        switch(event.type){
	          case CLOSED: ctx.window.close();
	        	 break;
	          case MOUSE_BUTTON_RELEASED: System.out.println("Button released! "+ event.asMouseButtonEvent().button);
	          	;
	          case MOUSE_MOVED: System.out.println("Mouse moved! "+ event.asMouseEvent().position);
	        	 break;	        	       
	        }
	    }
		
		
		myButton.draw(ctx.window);
		//ctx.window.draw(newsprite); <--- ???
		
	}

}
