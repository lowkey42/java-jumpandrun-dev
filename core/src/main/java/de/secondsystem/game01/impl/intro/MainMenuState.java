package de.secondsystem.game01.impl.intro;


import org.jsfml.graphics.Image;
import org.jsfml.graphics.Sprite;
import org.jsfml.graphics.Texture;
import org.jsfml.graphics.TextureCreationException;
import org.jsfml.window.Keyboard.Key;
import org.jsfml.window.Mouse;
import org.jsfml.window.event.Event;

import de.secondsystem.game01.impl.FinalizeState;
import de.secondsystem.game01.impl.GameContext;
import de.secondsystem.game01.impl.GameState;
import de.secondsystem.game01.impl.editor.EditorGameState;
import de.secondsystem.game01.impl.game.MainGameState;
import de.secondsystem.game01.impl.map.JsonGameMapSerializer;

/**
 * TODO
 *
 */
public final class MainMenuState extends GameState {
	
	GameState playGameState;
	
	Image backdropTx;
	Texture backdropBuffer = new Texture();
	Sprite backdrop = new Sprite();
	
	MenuButton newGameBt = new MenuButton("NEW GAME", 500, 40);
	MenuButton loadGameBt = new MenuButton("LOAD GAME", 500, 190);
	MenuButton editorBt = new MenuButton("EDITOR", 500, 340);
	MenuButton settingsBt = new MenuButton("SETTINGS", 500, 490);
	MenuButton exitGameBt = new MenuButton("EXIT GAME", 500, 640);
	
	public MainMenuState(GameState playGameState) {
	// Transfering last State into playGameState	
	this.playGameState = playGameState;
	}

	
	@Override
	protected void onStart(GameContext ctx) {
		// TODO
		
		// Creating Backdrop Texture via monitor screenshot of the stage before rendered on every frame
				try {
					backdropBuffer.create(ctx.settings.width, ctx.settings.height);
				} catch (TextureCreationException e) {
					e.printStackTrace();
				}
				backdropBuffer.update(ctx.window);
				backdrop.setTexture(backdropBuffer);
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
	          	if(newGameBt.newsprite.getGlobalBounds().contains(Mouse.getPosition(ctx.window).x, (Mouse.getPosition(ctx.window).y)) && event.asMouseButtonEvent().button == org.jsfml.window.Mouse.Button.LEFT)
	          		setNextState(new MainGameState("test01"));
	          	if(editorBt.newsprite.getGlobalBounds().contains(Mouse.getPosition(ctx.window).x, (Mouse.getPosition(ctx.window).y)) && event.asMouseButtonEvent().button == org.jsfml.window.Mouse.Button.LEFT)
	          		setNextState(new EditorGameState(this, new JsonGameMapSerializer().deserialize("test01", true, true)));
	          	if(settingsBt.newsprite.getGlobalBounds().contains(Mouse.getPosition(ctx.window).x, (Mouse.getPosition(ctx.window).y)) && event.asMouseButtonEvent().button == org.jsfml.window.Mouse.Button.LEFT)
	          		setNextState(new SettingsMenuState());
	          	if(exitGameBt.newsprite.getGlobalBounds().contains(Mouse.getPosition(ctx.window).x, (Mouse.getPosition(ctx.window).y)) && event.asMouseButtonEvent().button == org.jsfml.window.Mouse.Button.LEFT)
	          		setNextState(new FinalizeState());
	          	break;
	          case KEY_RELEASED:
	        	if(event.asKeyEvent().key==Key.ESCAPE) {
	        		setNextState(playGameState); }
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

		ctx.window.draw(backdrop);
		
		newGameBt.draw(ctx.window);
		editorBt.draw(ctx.window);
		settingsBt.draw(ctx.window);
		loadGameBt.draw(ctx.window);
		exitGameBt.draw(ctx.window);
			
	}
}
