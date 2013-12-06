/**
 * 
 */
package de.secondsystem.game01.impl.gui;

import org.jsfml.graphics.RenderTarget;
import org.jsfml.graphics.Text;

import de.secondsystem.game01.impl.gui.MenuButton.IOnClickListener;

/**
 * @author Sebastian
 *
 */
public abstract class GUIButton extends GUIElement {

	// shared Attributes
	
	
	
	// Constructors
	
	GUIButton(int pos_x, int pos_y, int width, int height, Text myText, IOnClickListener clickListener){
		super(pos_x, pos_y, width, height, myText);
		this.clickListener = clickListener;
	}
	
	GUIButton(int pos_x, int pos_y, int width, int height, Text myText){
		super(pos_x, pos_y, width, height, myText);
	}
	
	GUIButton(int pos_x, int pos_y, int width, int height, String content){
		super(pos_x, pos_y, width, height, content);
	}
	
	GUIButton(int pos_x, int pos_y, String content, IOnClickListener clickListener){
		super(pos_x, pos_y, 0, 0, content);
		this.clickListener = clickListener;
	}
	
	GUIButton(int pos_x, int pos_y, String content){
		super(pos_x, pos_y, 0, 0, content);
	}
	
	GUIButton(int pos_x, int pos_y){
		super(pos_x, pos_y, 0, 0, "UNNAMED");
	}
	
	
	// shared Methods
	
	public abstract void draw(RenderTarget rt);
	public abstract void update(long frameTimeMs);
	
	
	
	
	
	
	
	
	
	
	
	
}
