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
	
	protected RectangleShape myBox;
	
	protected boolean isActive = false;
	
	
	// Constructors
	
	GUIText(int pos_x, int pos_y, int width, int height, String content, IOnClickListener clickListener){
		super(pos_x, pos_y, width, height, content, clickListener);
		myText.setPosition(getPos().x + 5, getPos().y);
		myBox = new RectangleShape(new Vector2f(width, height));
		myBox.setPosition(myPos); myBox.setFillColor(new Color(0, 0, 0, 0)); myBox.setOutlineThickness(1);
	}
	
	
	GUIText(int pos_x, int pos_y, int width, int height, Text myText, IOnClickListener clickListener){
		super(pos_x, pos_y, width, height, myText, clickListener);
		myText.setPosition(getPos().x + 5, getPos().y);
		myBox = new RectangleShape(new Vector2f(width, height));
		myBox.setPosition(myPos); myBox.setFillColor(new Color(0, 0, 0, 0)); myBox.setOutlineThickness(1);
	}
	
	
	GUIText(int pos_x, int pos_y, int width, int height){
		this(pos_x, pos_y, width, height, "", new IOnClickListener() {
			
			@Override
			public void onClick() {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	
	GUIText(int pos_x, int pos_y, int width, Text myText){
		this(pos_x, pos_y, width, 25, myText, new IOnClickListener() {
			
			@Override
			public void onClick() {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	
	GUIText(int pos_x, int pos_y, int width){
		this(pos_x, pos_y, width, 25, "", new IOnClickListener() {
			
			@Override
			public void onClick() {
				// TODO Auto-generated method stub
				
			}
		});
	}
		
	
	// shared Methods
	
	public void draw(RenderTarget rt){
		rt.draw(myBox); 
		rt.draw(myText);
	}
	
	public void setActive(){
		this.isActive = true;
	}
	
	public void setInactive(){
		this.isActive = false;
	}
		
}