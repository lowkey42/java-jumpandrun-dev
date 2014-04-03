/**
 * 
 */
package de.secondsystem.game01.impl.gui;

import java.io.IOException;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.ConstFont;
import org.jsfml.graphics.RenderTarget;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.graphics.Text;
import org.jsfml.system.Vector2f;
import org.jsfml.window.Mouse;
import org.jsfml.window.Window;

import de.secondsystem.game01.impl.ResourceManager;
import de.secondsystem.game01.impl.graphic.AnimatedSprite;
import de.secondsystem.game01.impl.gui.listeners.IOnClickListener;

/**
 * @author Sebastian
 *
 */
public class GUIButton extends GUIElement {

	// shared Attributes
	
	protected int textureWidth, textureHeight;
	
	protected AnimatedSprite sprite;
	protected Text caption;
	
	// Constructors
	
	GUIButton(float x, float y, float width, float height, String caption, GUIElement owner, IOnClickListener clickListener){
		super(x, y, width, height, owner, clickListener);
		
		try {

			// Loading standard Font (12.5 pixel width & 21 pixel height per char --> Monospace VeraMono)
			ConstFont font = ResourceManager.font.get("VeraMono.ttf");
			this.caption = new Text(caption, font, (int) (height/4.f));
			this.caption.setOrigin(this.caption.getGlobalBounds().width / 2.f, this.caption.getGlobalBounds().height / 2.f);

			sprite = new AnimatedSprite(ResourceManager.animation.get("MainMenuButton.anim"), width, height);
			sprite.setPosition(new Vector2f(x, y));
			
			this.caption.setPosition(sprite.getPosition().x, sprite.getPosition().y - this.caption.getGlobalBounds().height / 3.f);

			} catch( IOException e ) {
				throw new Error(e.getMessage(), e);
			}
	
	}
		
	
	// shared Methods
	
	public void draw(RenderTarget rt){
		sprite.draw(rt);
		rt.draw(caption);
	}
	
	protected void changeTextureClip(int pos) {
//		sprite.setTextureRect(new IntRect(0,textureHeight*pos,textureWidth,textureHeight));
	}
	
	
	public void mouseover(Window window){
		Vector2f mouse = ((RenderWindow) window).mapPixelToCoords(Mouse.getPosition(window));
		if( inside(mouse) ){
			changeTextureClip(Mouse.isButtonPressed(org.jsfml.window.Mouse.Button.LEFT) ? 2 : 1); caption.setColor(Color.RED);
			//System.out.println("  OVER  ");
			//buttonOver.play();
		} else {
			changeTextureClip(0); caption.setColor(Color.WHITE);
		}
	}
	
	public boolean onButtonReleased(float x, float y) {
		if( inside(new Vector2f(x, y)) ) {
			clickListener.onClick();
			return true;
		}
		return false;
	}


	@Override
	public boolean inside(Vector2f point) {
		return sprite.inside(point);
	}
	
}