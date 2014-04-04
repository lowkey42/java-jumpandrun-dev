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
import de.secondsystem.game01.model.IAnimated.AnimationType;

/**
 * @author Sebastian
 *
 */
public class GUIButton extends GUIElement {

	// shared Attributes
	
	protected AnimatedSprite sprite;
	protected Text caption;
	
	// Constructors
	
	public GUIButton(float x, float y, float width, float height, String caption, GUIElement parent, IOnClickListener clickListener){
		super(x, y, width, height, parent);
		
		this.clickListener = clickListener;
		
		try {

			// Loading standard Font (12.5 pixel width & 21 pixel height per char --> Monospace VeraMono)
			ConstFont font = ResourceManager.font.get("VeraMono.ttf");
			this.caption = new Text(caption, font, (int) (height/4.f));
			this.caption.setOrigin(this.caption.getGlobalBounds().width / 2.f, this.caption.getGlobalBounds().height / 2.f);

			sprite = new AnimatedSprite(ResourceManager.animation.get("MainMenuButton.anim"), width, height);
			sprite.setPosition(new Vector2f(x, y));
			
			this.caption.setPosition(sprite.getPosition().x, sprite.getPosition().y - this.caption.getGlobalBounds().height / 3.f);

			} catch( IOException e ) {
				throw new RuntimeException(e.getMessage(), e);
			}
	
	}
	
	public GUIButton(float x, float y, String caption, GUIElement parent, IOnClickListener clickListener) {
		this(x, y, 250, 100, caption, parent, clickListener);
	}
	
	public GUIButton(float x, float y, GUIElement parent, IOnClickListener clickListener) {
		this(x, y, "button", parent, clickListener);
	}
	
	public GUIButton(float x, float y, String caption, GUIElement parent) {
		this(x, y, caption, parent, null);
	}
	
	public GUIButton(float x, float y, GUIElement parent) {
		this(x, y, parent, null);
	}	
	
	// shared Methods
	
	public void draw(RenderTarget rt){
		if( rt instanceof Window )
			mouseover( (Window) rt );
		sprite.draw(rt);
		rt.draw(caption);
		
		super.draw(rt);
	}
	
	public void mouseover(Window window){
		Vector2f mouse = ((RenderWindow) window).mapPixelToCoords(Mouse.getPosition(window));
		if( inside(mouse) ){
			caption.setColor(Color.RED);
		} else {
			caption.setColor(Color.WHITE);
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

	@Override
	public void refresh() {
		sprite.setDimensions(width, height);
		sprite.setRotation(rotation);
		sprite.setPosition(pos);
	}
	
	@Override
	public void onMouseLeave() {
		super.onMouseLeave();
		
		sprite.play(AnimationType.IDLE, 1.f, true);
	}

	@Override
	public void onMouseEnter() {
		super.onMouseEnter();
		
		sprite.play(AnimationType.MOUSE_OVER, 1.f, true);
	}
	
	@Override
	public void onClick() {
		super.onClick();
		
		sprite.play(AnimationType.CLICKED, 1.f, true);
	}
}