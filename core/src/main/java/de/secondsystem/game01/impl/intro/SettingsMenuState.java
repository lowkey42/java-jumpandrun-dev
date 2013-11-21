package de.secondsystem.game01.impl.intro;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.Sprite;
import org.jsfml.graphics.Texture;
import org.jsfml.graphics.TextureCreationException;
import org.jsfml.window.Mouse;
import org.jsfml.window.Window;
import org.jsfml.window.Keyboard.Key;
import org.jsfml.window.event.Event;

import de.secondsystem.game01.impl.GameContext;
import de.secondsystem.game01.impl.GameState;
import de.secondsystem.game01.impl.game.MainGameState;


public final class SettingsMenuState extends GameState {

	
	GameState playGameState;
	Window oldCtxWin;
	
	MenuButton changeRes = new MenuButton("Change Resolution", 200, 120);
	MenuButton vSync = new MenuButton("VSync", 200, 320);
	MenuButton antiA = new MenuButton("Antialiasing", 200, 520);
	MenuButton back = new MenuButton("BACK", 750, 520);
	
	SlideButton sliderOneBack = new SlideButton("", 620, 100);
	SlideButton sliderOneFront = new SlideButton("Volume", 620, 100);
	SlideButton sliderTwoBack = new SlideButton("", 620, 300);
	SlideButton sliderTwoFront = new SlideButton("Brightness", 620, 300);
	
	Texture backdropBuffer = new Texture();
	Sprite backdrop = new Sprite();
	
	public SettingsMenuState(GameState playGameState, Window oldCtxWin) {
		// Transfering last State into playGameState	
		this.playGameState = playGameState;
		this.oldCtxWin = oldCtxWin;
	}
	
	
	@Override
	protected void onStart(GameContext ctx) {
		System.out.println(">> SettingsMenu erfolgreich aufgerufen <<");
		ctx.window.clear();
		try {
			backdropBuffer.create(ctx.settings.width, ctx.settings.height);
		} catch (TextureCreationException e) {
			e.printStackTrace();
		}
		backdropBuffer.update(oldCtxWin);
		backdrop.setTexture(backdropBuffer);
		ctx.window.display();
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
	        	  if(back.newsprite.getGlobalBounds().contains(Mouse.getPosition(ctx.window).x, (Mouse.getPosition(ctx.window).y)) && event.asMouseButtonEvent().button == org.jsfml.window.Mouse.Button.LEFT){
		          		ctx.window.clear(); ctx.window.display();
		          		setNextState(new MainMenuState(playGameState));}
	          	
	          	break;
	          case KEY_RELEASED:
	        	/*if(event.asKeyEvent().key==Key.ESCAPE) {
	        		ctx.window.draw(backdrop);
	        		setNextState(playGameState); }*/
	          case MOUSE_BUTTON_PRESSED: 
	          case MOUSE_MOVED: 
	        	  changeRes.mouseover(ctx.window);
	        	  vSync.mouseover(ctx.window);
	        	  antiA.mouseover(ctx.window);
	        	  back.mouseover(ctx.window);
	        	  sliderOneFront.mouseover(ctx.window, event);
	        	  sliderTwoFront.mouseover(ctx.window, event);
	        	
	        	break;	        	       
	        }
	    }
		
		ctx.window.draw(backdrop);
		
		sliderOneBack.draw(ctx.window);
		sliderOneFront.draw(ctx.window);
		sliderTwoBack.draw(ctx.window);
		sliderTwoFront.draw(ctx.window);
		
		changeRes.draw(ctx.window);
		vSync.draw(ctx.window);
		antiA.draw(ctx.window);
		back.draw(ctx.window);
		
	}
	
}
