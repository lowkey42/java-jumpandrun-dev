/**
 * 
 */
package de.secondsystem.game01.impl.gui;

import org.jsfml.graphics.RenderTarget;
import org.jsfml.system.Vector2f;

import de.secondsystem.game01.impl.gui.MenuButton.IOnClickListener;
import de.secondsystem.game01.model.IDrawable;
import de.secondsystem.game01.model.IUpdateable;

/**
 * @author Sebastian
 *
 */
public abstract class GUIElement implements IDrawable, IUpdateable{

	// shared Attributes
	
	final protected int width, height;
	final protected String myText;
	protected Vector2f myPos;
	
	IOnClickListener clickListener;
	
	// Constructors
	
	public GUIElement(int pos_x, int pos_y, int width, int height, String myText){
		this.width = width;
		this.height = height;
		this.myText = myText;
		this.myPos = new Vector2f(pos_x, pos_y);
	}
	
	public GUIElement(int pos_x, int pos_y, int width, int height){
		this.width = width;
		this.height = height;
		this.myText = "";
		this.myPos = new Vector2f(pos_x, pos_y);
	}
	
	
	// shared Methods
	
	public abstract void draw(RenderTarget rt);
	public abstract void update(long frameTimeMs);
	
	public Vector2f getPos(){
		return this.myPos;
	}
	public void setPos(Vector2f pos){
		this.myPos = pos;
	}		
	
}