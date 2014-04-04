/**
 * 
 */
package de.secondsystem.game01.impl.gui;

import java.io.IOException;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.ConstFont;
import org.jsfml.graphics.RectangleShape;
import org.jsfml.graphics.RenderTarget;
import org.jsfml.graphics.Text;
import org.jsfml.system.Vector2f;

import de.secondsystem.game01.impl.ResourceManager;
import de.secondsystem.game01.impl.gui.listeners.IOnClickListener;

/**
 * @author Sebastian
 *
 */
public abstract class GUIText extends GUIElement{

	
	// shared Attributes
	
	protected RectangleShape myBox;
	
	protected boolean isActive = false;
	protected Text text;
	
	// Constructors
	
	GUIText(float x, float y, float width, float height, String text, GUIElement owner){
		super(x, y, width, height, owner);
		
		// Loading standard Font (12.5 pixel width & 21 pixel height per char --> Monospace VeraMono)
		ConstFont font;
		try {
			font = ResourceManager.font.get("VeraMono.ttf");
			this.text = new Text(text, font, (int) (height - 5));
			this.text.setOrigin(this.text.getGlobalBounds().width / 2.f, this.text.getGlobalBounds().height / 2.f);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		this.text.setPosition(pos.x + 5, pos.y);
		myBox = new RectangleShape(new Vector2f(width, height));
		myBox.setPosition(pos); myBox.setFillColor(new Color(0, 0, 0, 0)); myBox.setOutlineThickness(2);
	}
		
	
	// shared Methods
	
	public void draw(RenderTarget rt){
		rt.draw(myBox); 
		rt.draw(text);
	}
	
	public void setActive(){
		this.isActive = true;
	}
	
	public void setInactive(){
		this.isActive = false;
	}
		
}