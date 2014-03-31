package de.secondsystem.game01.impl.gui;

import java.io.IOException;

import org.jsfml.graphics.ConstFont;
import org.jsfml.graphics.ConstTexture;
import org.jsfml.graphics.IntRect;
import org.jsfml.graphics.RenderTarget;
import org.jsfml.graphics.Sprite;
import org.jsfml.window.Mouse;
import org.jsfml.window.event.Event;
import de.secondsystem.game01.impl.GameContext;
import de.secondsystem.game01.impl.ResourceManager;
import de.secondsystem.game01.impl.gui.IOnClickListener;
	
	/**
	 * This class provides a slideable button with values between 0 and 100
	 * @author Sebastian
	 * 
	 */
public final class SlideButton {

	// Attributes
	String myText;
	short value = 0;
	int pos_x, pos_y;
	
	final int width;
	final int height;
	//final Text myText;
	
	final Sprite foregroundSprite;
	final Sprite backgroundSprite;
	
	IOnClickListener clickListener;
	
	
	// Constructors
	
	public SlideButton(String text, String file, String fonttype, int pos_x, int pos_y, IOnClickListener clickListener) {
		this.pos_x = pos_x;
		this.pos_y = pos_y;
		this.myText = text;
		this.clickListener = clickListener;
	
		try {
		// Loading standard Font for MenuButtons
		ConstFont myFont = ResourceManager.font.get(fonttype);
		
		// Loading Standard Texture for MenuButtons
		ConstTexture slideButton = ResourceManager.texture_gui.get(file);
		
		height = slideButton.getSize().y / 2;
		width = slideButton.getSize().x;
	
		// Button Sprite generation and positioning
		foregroundSprite = new Sprite(slideButton);
		backgroundSprite = new Sprite(slideButton);
		foregroundSprite.setPosition(pos_x, pos_y);
		backgroundSprite.setPosition(pos_x, pos_y);
		changeTextureClip();
		
		} catch ( IOException e ) {
			throw new Error(e.getMessage(), e);
		}
	}
		
	
	public SlideButton(String text, int pos_x, int pos_y, IOnClickListener clickListener) {
		this(text, "VolumeButton.png", "FreeSansBold.otf", pos_x, pos_y, clickListener);
	}
	
	
	public SlideButton(String text, int pos_x, int pos_y) {
		this(text, "VolumeButton.png", "FreeSansBold.otf", pos_x, pos_y, new IOnClickListener(){@Override public void onClick(){System.out.println("pressed");}});
	}
	
	
	public void mouseover(GameContext ctx, Event event){
		
		// TODO --> test if there is an alternative:    
		//if(foregroundSprite.getGlobalBounds().contains(Mouse.getPosition(window).x, Mouse.getPosition(window).y)){
		if(ctx.getMousePosition().x < this.foregroundSprite.getPosition().x + width - 10 && ctx.getMousePosition().x > this.foregroundSprite.getPosition().x + 10
		&& ctx.getMousePosition().y < this.foregroundSprite.getPosition().y + height - 10 && ctx.getMousePosition().y > this.foregroundSprite.getPosition().y + 10){
			if(Mouse.isButtonPressed(org.jsfml.window.Mouse.Button.LEFT)){
			this.foregroundSprite.setTextureRect(new IntRect(0, height, (int)((ctx.getMousePosition().x) - (this.foregroundSprite.getPosition().x)), height));
			// Transforming Coordinates into a value (MousePosX - LeftUpCornerSprite - 10 pixels for Border / 4.8 (--> (500pixel - 20) / 100 (max))
			value = (short)((ctx.getMousePosition().x - this.foregroundSprite.getPosition().x - 10)/4.8);
			System.out.println(this.myText + ": current value: " + value);
			}
		}
	}
	
	
	void convertValue(){
		
	}
	
	public void draw(RenderTarget rt) {
		rt.draw(backgroundSprite);
		rt.draw(foregroundSprite);				
	}
	
	
	private void changeTextureClip() {
		foregroundSprite.setTextureRect(new IntRect(0,height*1,width,height));
		backgroundSprite.setTextureRect(new IntRect(0,height*0,width,height));
	}
}