/**
 * 
 */
package de.secondsystem.game01.impl.gui;

import java.io.IOException;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.ConstTexture;
import org.jsfml.graphics.FloatRect;
import org.jsfml.graphics.IntRect;
import org.jsfml.graphics.RenderTarget;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.graphics.Sprite;
import org.jsfml.graphics.Text;
import org.jsfml.system.Vector2f;
import org.jsfml.window.Mouse;
import org.jsfml.window.Window;

import de.secondsystem.game01.impl.ResourceManager;
import de.secondsystem.game01.impl.gui.IOnClickListener;

/**
 * @author Sebastian
 *
 */
public abstract class GUIButton extends GUIElement {

	// shared Attributes
	
	protected int textureWidth, textureHeight;
	
	protected Sprite mySprite;
	
	// Constructors
	
	GUIButton(int pos_x, int pos_y, Text myText, IOnClickListener clickListener){
		super(pos_x, pos_y, 0, 31, myText, clickListener);
		
		try {
			// Loading Standard Texture for MenuButtons
			ConstTexture myTexture = ResourceManager.texture_gui.get("MainMenuButton.png");
			
			textureHeight = myTexture.getSize().y / 3; textureWidth = myTexture.getSize().x;
						
			// Button Sprite generation and positioning
			mySprite = new Sprite(myTexture);
			mySprite.setPosition(pos_x, pos_y);
			changeTextureClip(0);
			
			// Button inner text positioning and calibration
			FloatRect textRect = myText.getGlobalBounds();
			myText.setOrigin(textRect.width / 2, textRect.height / 1.5f);
			myText.setPosition(mySprite.getPosition().x + mySprite.getGlobalBounds().width / 2, mySprite.getPosition().y + mySprite.getGlobalBounds().height / 2);

			} catch( IOException e ) {
				throw new Error(e.getMessage(), e);
			}
	
	}
	
	GUIButton(int pos_x, int pos_y, String content, IOnClickListener clickListener){
		super(pos_x, pos_y, 0, 31, content, clickListener);
		
		try {
			// Loading Standard Texture for MenuButtons
			ConstTexture myTexture = ResourceManager.texture_gui.get("MainMenuButton.png");
			
			textureHeight = myTexture.getSize().y / 3; textureWidth = myTexture.getSize().x;
						
			// Button Sprite generation and positioning
			mySprite = new Sprite(myTexture);
			mySprite.setPosition(pos_x, pos_y);
			changeTextureClip(0);
			
			// Button inner text positioning and calibration
			FloatRect textRect = myText.getGlobalBounds();
			myText.setOrigin(textRect.width / 2, textRect.height / 1.5f);
			myText.setPosition(mySprite.getPosition().x + mySprite.getGlobalBounds().width / 2, mySprite.getPosition().y + mySprite.getGlobalBounds().height / 2);

			} catch( IOException e ) {
				throw new Error(e.getMessage(), e);
			}
	
	}
	
	GUIButton(int pos_x, int pos_y, Text myText){
		this(pos_x, pos_y, myText, new IOnClickListener(){@Override public void onClick(){System.out.println("pressed");}});
	}
	
	GUIButton(int pos_x, int pos_y, String content){
		this(pos_x, pos_y, content, new IOnClickListener(){@Override public void onClick(){System.out.println("pressed");}});
	}
		
	
	// shared Methods
	
	public void draw(RenderTarget rt){
		rt.draw(mySprite);
		rt.draw(myText);
	}
	
	protected void changeTextureClip(int pos) {
		mySprite.setTextureRect(new IntRect(0,textureHeight*pos,textureWidth,textureHeight));
	}
	
	
	public void mouseover(Window window){
		Vector2f mouse = ((RenderWindow) window).mapPixelToCoords(Mouse.getPosition(window));
		if(this.mySprite.getGlobalBounds().contains(mouse.x, mouse.y)){
			changeTextureClip(Mouse.isButtonPressed(org.jsfml.window.Mouse.Button.LEFT) ? 2 : 1); myText.setColor(Color.RED);
			//System.out.println("  OVER  ");
			//buttonOver.play();
		} else {
			changeTextureClip(0); myText.setColor(Color.WHITE);
		}
	}
	
	public void onButtonReleased(float x, float y) {
		if( mySprite.getGlobalBounds().contains(x, y) ) {
			clickListener.onClick();
		}
	}
	
}