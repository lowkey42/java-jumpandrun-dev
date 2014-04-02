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
import org.jsfml.system.Vector2f;
import org.jsfml.window.Mouse;
import org.jsfml.window.Window;

import de.secondsystem.game01.impl.ResourceManager;
import de.secondsystem.game01.impl.gui.listeners.IOnClickListener;

/**
 * @author Sebastian
 *
 */
public abstract class GUIButton extends GUIElement {

	// shared Attributes
	
	protected int textureWidth, textureHeight;
	
	protected Sprite sprite;
	
	// Constructors
	
	GUIButton(float x, float y, float width, float height, String caption, GUIElement owner, IOnClickListener clickListener){
		super(x, y, width, height, caption, owner, clickListener);
		
		try {
			// Loading Standard Texture for MenuButtons
			ConstTexture myTexture = ResourceManager.texture_gui.get("MainMenuButton.png");
			
			textureHeight = myTexture.getSize().y / 3; textureWidth = myTexture.getSize().x;
						
			// Button Sprite generation and positioning
			sprite = new Sprite(myTexture);
			sprite.setPosition(x, y);
			changeTextureClip(0);
			
			// Button inner text positioning and calibration
			FloatRect textRect = this.caption.getGlobalBounds();
			this.caption.setOrigin(textRect.width / 2.f, textRect.height / 2.f);
			this.caption.setPosition(sprite.getPosition().x + sprite.getGlobalBounds().width / 2, sprite.getPosition().y + sprite.getGlobalBounds().height / 2);

			} catch( IOException e ) {
				throw new Error(e.getMessage(), e);
			}
	
	}
		
	
	// shared Methods
	
	public void draw(RenderTarget rt){
		rt.draw(sprite);
		rt.draw(caption);
	}
	
	protected void changeTextureClip(int pos) {
		sprite.setTextureRect(new IntRect(0,textureHeight*pos,textureWidth,textureHeight));
	}
	
	
	public void mouseover(Window window){
		Vector2f mouse = ((RenderWindow) window).mapPixelToCoords(Mouse.getPosition(window));
		if(this.sprite.getGlobalBounds().contains(mouse.x, mouse.y)){
			changeTextureClip(Mouse.isButtonPressed(org.jsfml.window.Mouse.Button.LEFT) ? 2 : 1); caption.setColor(Color.RED);
			//System.out.println("  OVER  ");
			//buttonOver.play();
		} else {
			changeTextureClip(0); caption.setColor(Color.WHITE);
		}
	}
	
	public boolean onButtonReleased(float x, float y) {
		if( sprite.getGlobalBounds().contains(x, y) ) {
			clickListener.onClick();
			return true;
		}
		return false;
	}
	
}