package de.secondsystem.game01.impl.intro;

import java.io.IOException;

import org.jsfml.graphics.ConstFont;
import org.jsfml.graphics.ConstTexture;
import org.jsfml.graphics.FloatRect;
import org.jsfml.graphics.IntRect;
import org.jsfml.graphics.RenderTarget;
import org.jsfml.graphics.Sprite;
import org.jsfml.graphics.Text;
import org.jsfml.window.Mouse;
import org.jsfml.window.Window;

import de.secondsystem.game01.impl.ResourceManager;
import de.secondsystem.game01.impl.intro.MenuButton.IOnClickListener;
	
	/**
	 * This class provides a slideable button with values between 0 and 100
	 * @author Sebastian
	 * 
	 */
public final class SlideButton {

	// Attributes
	String text;
	short value;
	int pos_x, pos_y;
	
	final int width;
	final int height;
	//final Text myText;
	final Sprite sliderSprite;
	
	IOnClickListener clickListener;
	
	
	// Constructors
	
	SlideButton(String text, String file, String fonttype, int pos_x, int pos_y, IOnClickListener clickListener) {
		this.pos_x = pos_x;
		this.pos_y = pos_y;
		this.clickListener = clickListener;
	
		try {
		// Loading standard Font for MenuButtons
		ConstFont myFont = ResourceManager.font.get(fonttype);
		
		// Loading Standard Texture for MenuButtons
		ConstTexture slideButton = ResourceManager.texture_gui.get(file);
		
		height = slideButton.getSize().y / 2;
		value = (short)((slideButton.getSize().x) - 20 / 100); 
		width = slideButton.getSize().x;
	
		// Button Sprite generation and positioning
		sliderSprite = new Sprite(slideButton);
		sliderSprite.setPosition(pos_x, pos_y);
		changeTextureClip(0);
		
		} catch ( IOException e ) {
			throw new Error(e.getMessage(), e);
		}
	}
		
	
	SlideButton(String text, int pos_x, int pos_y, IOnClickListener clickListener) {
		this(text, "VolumeButton.png", "FreeSansBold.otf", pos_x, pos_y, clickListener);
	}
	
	
	SlideButton(String text, int pos_x, int pos_y) {
		this(text, "VolumeButton.png", "FreeSansBold.otf", pos_x, pos_y, new IOnClickListener(){@Override public void onClick(){System.out.println("pressed");}});
	}
	
	
	void mouseover(Window window, FloatRect gbounds){
		// TODO --> test if there is an alternative:    if(gbounds.contains(Mouse.getPosition(window).x, Mouse.getPosition(window).y)){
		if(Mouse.getPosition(window).x < this.sliderSprite.getPosition().x + width && Mouse.getPosition(window).x > this.sliderSprite.getPosition().x){
			this.sliderSprite.setTextureRect(new IntRect(0, height, (int)((Mouse.getPosition().x) - (this.sliderSprite.getPosition().x)), height));
			System.out.println("inside " + this.sliderSprite);
		}
		
		
	}
	
	
	
	void draw(RenderTarget rt) {
		rt.draw(sliderSprite);		
	}
	
	
	private void changeTextureClip(int pos) {
		sliderSprite.setTextureRect(new IntRect(0,height*pos,width,height));
	}
	
	
}