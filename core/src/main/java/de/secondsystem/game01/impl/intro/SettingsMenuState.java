package de.secondsystem.game01.impl.intro;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.Sprite;
import org.jsfml.graphics.Texture;
import org.jsfml.graphics.TextureCreationException;
import org.jsfml.window.Mouse;
import org.jsfml.window.Keyboard.Key;
import org.jsfml.window.event.Event;

import de.secondsystem.game01.impl.FinalizeState;
import de.secondsystem.game01.impl.GameContext;
import de.secondsystem.game01.impl.GameState;
import de.secondsystem.game01.impl.editor.EditorGameState;
import de.secondsystem.game01.impl.game.MainGameState;
import de.secondsystem.game01.impl.map.JsonGameMapSerializer;

public final class SettingsMenuState extends GameState {

	
	GameState playGameState;
	
	SlideButton sliderOne = new SlideButton("VOLUME", 600, 80);
	SlideButton sliderTwo = new SlideButton("BRIGHTNESS", 600, 280);
	
	Texture backdropBuffer = new Texture();
	Sprite backdrop = new Sprite();
	
	public SettingsMenuState(GameState playGameState) {
		// Transfering last State into playGameState	
		this.playGameState = playGameState;
	}
	
	
	@Override
	protected void onStart(GameContext ctx) {
		System.out.println(">> SettingsMenu erfolgreich aufgerufen <<");
		ctx.window.clear(Color.BLACK);
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
	
	
	@Override
	protected void onFrame(GameContext ctx, long frameTime) {
		// TODO
		for(Event event : ctx.window.pollEvents()) {
	        switch(event.type){
	          case CLOSED: ctx.window.close();
	        	 break;
	          case MOUSE_BUTTON_RELEASED:
	          	// Checking if the current mouse position is inside the Button and only the left mouse button is pressed
	          	//if(sliderOne.sliderSprite.getGlobalBounds().contains(Mouse.getPosition(ctx.window).x, (Mouse.getPosition(ctx.window).y)) && event.asMouseButtonEvent().button == org.jsfml.window.Mouse.Button.LEFT)
	          	//	setNextState(new MainGameState("test01"));
	          	
	          	break;
	          case KEY_RELEASED:
	        	if(event.asKeyEvent().key==Key.ESCAPE) {
	        		ctx.window.draw(backdrop);
	        		setNextState(playGameState); }
	          case MOUSE_BUTTON_PRESSED: 
	          case MOUSE_MOVED: 
	        	  sliderOne.mouseover(ctx.window, sliderOne.sliderSprite.getGlobalBounds());
	        	  sliderTwo.mouseover(ctx.window, sliderTwo.sliderSprite.getGlobalBounds());
	        	
	        	break;	        	       
	        }
	    }
		
		ctx.window.draw(backdrop);
		
		sliderOne.draw(ctx.window);
		sliderTwo.draw(ctx.window);
		
	}
	
}
