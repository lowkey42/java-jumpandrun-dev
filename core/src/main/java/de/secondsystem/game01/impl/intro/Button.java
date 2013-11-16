package de.secondsystem.game01.impl.intro;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;

import org.jsfml.graphics.RenderTarget;
import org.jsfml.graphics.Texture;
import org.jsfml.graphics.Sprite;
import org.jsfml.graphics.IntRect;
import org.jsfml.window.Mouse;
import org.jsfml.window.Window;

/**
 * This class provides a functional button with different attributes
 * @author Sebastian
 *
 */
public final class Button {

	Path file;
	String title, text;
	int pos_x, pos_y;
	
	final Sprite newsprite;
	final int height;
	final int width;
		
	// Constructors
	Button(String text, Path file, int pos_x, int pos_y){
		this.file = file;
		this.pos_x = pos_x;
		this.pos_y = pos_y;
		this.text = text;
		
		Texture newButton = new Texture();
				
		try {
			// Try to load the texture file
			newButton.loadFromFile(file);
		
			System.out.println("DATEI ERFOLGREICH EINGEBUNDEN!");
		} catch(IOException ex) {
			//Ouch! something went wrong
			System.out.println("DATEI KONNTE NICHT GELADEN WERDEN!");
			ex.printStackTrace();
		}
		
		height = newButton.getSize().y / 3;
		width = newButton.getSize().x;
		
		newsprite = new Sprite(newButton);
		newsprite.setPosition(pos_x, pos_y);
		changeTextureClip(0);

	}
	
	Button(String text, int pos_x, int pos_y){
		this(text, Paths.get("assets", "gui", "buttons", "ButtonClass.png"), pos_x, pos_y);
	}
	
	// Methods	
	
	// Draw a sprite
	void draw(RenderTarget rt){
		rt.draw(newsprite);
	}
	
	void mouseover(Window window){
		if(this.newsprite.getGlobalBounds().contains(Mouse.getPosition(window).x, Mouse.getPosition(window).y)){
			changeTextureClip(Mouse.isButtonPressed(org.jsfml.window.Mouse.Button.LEFT) ? 2 : 1);
		} else {
			changeTextureClip(0);
		}
	}
	
	private void changeTextureClip(int pos) {
		newsprite.setTextureRect(new IntRect(0,height*pos,width,height));
	}
	
}
