/**
 * 
 */
package de.secondsystem.game01.impl.gui;

import java.io.IOException;

import org.jsfml.graphics.ConstFont;
import org.jsfml.graphics.Font;
import org.jsfml.graphics.Text;
import org.jsfml.system.Vector2f;

import de.secondsystem.game01.impl.ResourceManager;
import de.secondsystem.game01.model.IDimensioned;
import de.secondsystem.game01.model.IDrawable;
import de.secondsystem.game01.model.IInsideCheck;
import de.secondsystem.game01.model.IMoveable;
import de.secondsystem.game01.model.IScalable;

/**
 * @author Sebastian
 *
 */
public abstract class GUIElement implements IDrawable, IDimensioned, IScalable, IMoveable, IInsideCheck {

	// shared Attributes
	
	protected float width, height;
	protected Text text;
	protected Font font;
	protected Vector2f pos;
	protected float rotation;
	
	protected IOnClickListener clickListener;
	
	
	// Constructors
	
	public GUIElement(float x, float y, int width, int height, Text text, IOnClickListener clickListener){
		this.width = width;
		this.height = height;
		this.text = text;
		this.pos = new Vector2f(x, y);
		this.clickListener = clickListener;
	}
		
		
	public GUIElement(float x, float y, int width, int height, String content, IOnClickListener clickListener){
		this.width = width;
		this.height = height;
		this.pos = new Vector2f(x, y);
		this.clickListener = clickListener;		
			
		try {
			// Loading standard Font (12.5 pixel width & 21 pixel height per char --> Monospace VeraMono)
			font = (Font) ResourceManager.font.get("VeraMono.ttf");
			text = new Text(content, font, (height - 5));
			
		} catch( IOException e ) {
			throw new Error(e.getMessage(), e);
		}
	}
	
	
	// shared Methods
	
	public ConstFont getFont(){
		return this.font;
	}

	@Override
	public void setPosition(Vector2f pos) {
		this.pos = pos;
	}


	@Override
	public void setRotation(float degree) {
		rotation = degree;
	}


	@Override
	public float getRotation() {
		return rotation;
	}


	@Override
	public Vector2f getPosition() {
		return pos;
	}


	@Override
	public void setDimensions(float width, float height) {
		this.width  = width;
		this.height = height;
	}


	@Override
	public float getHeight() {
		return height;
	}


	@Override
	public float getWidth() {
		return width;
	}
	
}