/**
 * 
 */
package de.secondsystem.game01.impl.gui;

import java.io.IOException;

import org.jsfml.graphics.ConstFont;
import org.jsfml.graphics.RenderTarget;
import org.jsfml.graphics.Text;
import org.jsfml.system.Vector2f;

import de.secondsystem.game01.impl.ResourceManager;
import de.secondsystem.game01.model.IDrawable;

/**
 * @author Sebastian
 *
 */
public abstract class GUIElement implements IDrawable{

	// shared Attributes
	
	final protected int width, height;
	final protected Text myText;
	private ConstFont myFont;
	protected Vector2f myPos;
	
	protected IOnClickListener clickListener;
	
	// Constructors
	
	
	public GUIElement(int pos_x, int pos_y, int width, int height, Text myText, IOnClickListener clickListener){
		this.width = width;
		this.height = height;
		this.myText = myText;
		this.myPos = new Vector2f(pos_x, pos_y);
		this.clickListener = clickListener;
	}
		
		
	public GUIElement(int pos_x, int pos_y, int width, int height, String content, IOnClickListener clickListener){
		this.width = width;
		this.height = height;
		this.myPos = new Vector2f(pos_x, pos_y);
		this.clickListener = clickListener;
			
			
		try {
			// Loading standard Font (12.5 pixel width & 21 pixel height per char --> Monospace VeraMono)
			myFont = ResourceManager.font.get("VeraMono.ttf");
			myText = new Text(content, myFont, (height - 5));
			
			} catch( IOException e ) {
				throw new Error(e.getMessage(), e);
			}
	}
	
	
	// shared Methods
	
	public abstract void draw(RenderTarget rt);
	
	public ConstFont getFont(){
		return this.myFont;
	}

	public Vector2f getPos(){
		return this.myPos;
	}
	public void setPos(Vector2f pos){
		this.myPos = pos;
	}
}