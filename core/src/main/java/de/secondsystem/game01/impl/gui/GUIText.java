/**
 * 
 */
package de.secondsystem.game01.impl.gui;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.RectangleShape;
import org.jsfml.graphics.RenderTarget;
import org.jsfml.system.Vector2f;

import de.secondsystem.game01.impl.gui.listeners.IOnClickListener;

/**
 * @author Sebastian
 *
 */
public abstract class GUIText extends GUIElement{

	
	// shared Attributes
	
	protected RectangleShape myBox;
	
	protected boolean isActive = false;
	
	
	// Constructors
	
	GUIText(float x, float y, float width, float height, String text, GUIElement owner, IOnClickListener clickListener){
		super(x, y, width, height, text, owner, clickListener);
		
		this.caption.setPosition(pos.x + 5, pos.y);
		myBox = new RectangleShape(new Vector2f(width, height));
		myBox.setPosition(pos); myBox.setFillColor(new Color(0, 0, 0, 0)); myBox.setOutlineThickness(2);
	}
	

	
	
	GUIText(float x, float y, float width, float height, String text, GUIElement owner){
		this(x, y, width, height, text, owner, new IOnClickListener() {
			
			@Override
			public void onClick() {
			}
		});
	}
		
	
	// shared Methods
	
	public void draw(RenderTarget rt){
		rt.draw(myBox); 
		rt.draw(caption);
	}
	
	public void setActive(){
		this.isActive = true;
	}
	
	public void setInactive(){
		this.isActive = false;
	}
		
}