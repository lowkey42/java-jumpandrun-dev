/**
 * 
 */
package de.secondsystem.game01.impl.gui;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.RectangleShape;
import org.jsfml.graphics.RenderTarget;
import org.jsfml.graphics.Text;
import org.jsfml.system.Vector2f;

/**
 * @author Sebastian
 *
 */
public abstract class GUIText extends GUIElement{

	
	// shared Attributes
	
	protected Text myText;
	
	protected RectangleShape myBox;
	
	protected boolean isActive = false;
	
	
	// Constructors
	
	GUIText(int pos_x, int pos_y, int width, int height, String myText){
		super(pos_x, pos_y, width, height, myText);
		myBox = new RectangleShape(new Vector2f(width, height));
		myBox.setPosition(myPos); myBox.setFillColor(new Color(0, 0, 0, 0)); myBox.setOutlineThickness(1);
	}
	
	GUIText(int pos_x, int pos_y, int width, String myText){
		super(pos_x, pos_y, width, 25, myText);
		myBox = new RectangleShape(new Vector2f(width, height));
		myBox.setPosition(myPos); myBox.setFillColor(new Color(0, 0, 0, 0)); myBox.setOutlineThickness(1);
	}
	
	GUIText(int pos_x, int pos_y, int width){
		super(pos_x, pos_y, width, 25, "");
		myBox = new RectangleShape(new Vector2f(width, height));
		myBox.setPosition(myPos); myBox.setFillColor(new Color(0, 0, 0, 0)); myBox.setOutlineThickness(1);
	}
		
	
	// shared Methods
	
	public void draw(RenderTarget rt){
		rt.draw(myBox); rt.draw(myText);
	}
	
	public void setActive(){
		this.isActive = true;
	}
	
	public void setInactive(){
		this.isActive = false;
	}
		
}
