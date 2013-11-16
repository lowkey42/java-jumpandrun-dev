package de.secondsystem.game01.impl.intro;


import org.jsfml.window.event.Event;
import org.jsfml.window.Mouse;

import de.secondsystem.game01.impl.GameContext;
import de.secondsystem.game01.impl.GameState;
import de.secondsystem.game01.impl.game.MainGameState;

/**
 * TODO
 *
 */
public final class MainMenuState extends GameState {
	
	Button newGameBt = new Button("NEW GAME", 500, 40);
	Button loadGameBt = new Button("LOAD GAME", 500, 190);
	Button editorBt = new Button("EDITOR", 500, 340);
	Button settingsBt = new Button("SETTINGS", 500, 490);
	Button exitGameBt = new Button("EXIT GAME", 500, 640);

	//Text myText = new Text("Text", font("x"));
	
	@Override
	protected void onStart(GameContext ctx) {
		// TODO
		System.out.println("MAINMENUSTATE");
	}

	@Override
	protected void onStop(GameContext ctx) {
		// TODO
	}

	@SuppressWarnings("incomplete-switch")
	@Override
	protected void onFrame(GameContext ctx, long frameTime) {
		// TODO Preparing the following code to be able to be used with more than 1 button --> outsourcing into button class
		for(Event event : ctx.window.pollEvents()) {
	        switch(event.type){
	          case CLOSED: ctx.window.close();
	        	 break;
	          case MOUSE_BUTTON_RELEASED:
	          	// Checking if the current mouse position is inside the Button and only the left mouse button is pressed
	          	if(editorBt.newsprite.getGlobalBounds().contains(Mouse.getPosition(ctx.window).x, (Mouse.getPosition(ctx.window).y)) && event.asMouseButtonEvent().button == org.jsfml.window.Mouse.Button.LEFT)
	          		setNextState(new MainGameState("test01"));
	          	if(settingsBt.newsprite.getGlobalBounds().contains(Mouse.getPosition(ctx.window).x, (Mouse.getPosition(ctx.window).y)) && event.asMouseButtonEvent().button == org.jsfml.window.Mouse.Button.LEFT)
	          		//setNextState(new MainSettingsMenu());
	          		;
	          	if(exitGameBt.newsprite.getGlobalBounds().contains(Mouse.getPosition(ctx.window).x, (Mouse.getPosition(ctx.window).y)) && event.asMouseButtonEvent().button == org.jsfml.window.Mouse.Button.LEFT)
	          		ctx.window.close();	          	
	          	break;
	          case MOUSE_BUTTON_PRESSED: 
	          case MOUSE_MOVED: 
	        	newGameBt.mouseover(ctx.window); 
	        	editorBt.mouseover(ctx.window);
	        	settingsBt.mouseover(ctx.window);
	        	loadGameBt.mouseover(ctx.window);
	        	exitGameBt.mouseover(ctx.window);
	        	break;	        	       
	        }
	    }

		newGameBt.draw(ctx.window);
		editorBt.draw(ctx.window);
		settingsBt.draw(ctx.window);
		loadGameBt.draw(ctx.window);
		exitGameBt.draw(ctx.window);
		
		
	}

}
