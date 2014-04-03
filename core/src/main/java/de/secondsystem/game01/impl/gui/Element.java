/**
 * 
 */
package de.secondsystem.game01.impl.gui;

import org.jsfml.graphics.RenderTarget;
import org.jsfml.system.Vector2f;

import de.secondsystem.game01.model.GameException;
import de.secondsystem.game01.model.IDimensioned;
import de.secondsystem.game01.model.IDrawable;
import de.secondsystem.game01.model.IInsideCheck;
import de.secondsystem.game01.model.IMoveable;
import de.secondsystem.game01.model.IScalable;
import de.secondsystem.game01.model.IUpdateable;

/**
 * @author Sebastian
 *
 */
public abstract class Element implements IDrawable, IDimensioned, IScalable, IMoveable, IInsideCheck, IUpdateable {

	public static enum KeyType {
		UP, DOWN, LEFT, RIGHT, ENTER, EXIT,
		BACKSPACE, DEL, TAB
	}
	
	// shared Attributes
	
	protected float width, height;
	protected Vector2f pos;
	protected boolean visible = true;
	protected final ElementContainer owner;
	
	public Element(float x, float y, float width, float height, ElementContainer owner) {
		this.owner = owner;
		this.width = width;
		this.height = height;
		this.pos = new Vector2f(x, y);
		
		if( owner!=null ) {
			owner.addElement(this);
		}
	}
	
	protected void onFocus(Vector2f mp){}
	protected void onUnFocus(){}
	
	protected void onMouseOver(Vector2f mp){}
	protected void onMouseOut(){}
	
	protected void onTextInput(int character){}
	protected void onKeyPressed(KeyType type){}
	protected void onKeyReleased(KeyType type){}
	
	protected abstract void drawImpl(RenderTarget renderTarget);

	protected Style getStyle() {
		return getParentStyle(owner);
	}
	protected static Style getParentStyle(ElementContainer owner) {
		if( owner!=null )
			return owner.getStyle();
		
		throw new GameException("No GUI-Style found (rechead top-level)");
	}
	
	@Override
	public final void draw(RenderTarget renderTarget) {
		if(visible)
			drawImpl(renderTarget);
	}
	
	public boolean isVisible() {
		return visible;
	}
	
	public void setVisible(boolean visible) {
		this.visible = visible;
	}
	
	@Override
	public boolean inside(Vector2f point) {
		return point.x>=pos.x && point.x<=pos.x+width 
			&& point.y>=pos.y && point.y<=pos.y+height;
	}

	@Override
	public void setPosition(Vector2f pos) {
		this.pos = pos;
	}


	@Override
	public void setRotation(float degree) {
	}


	@Override
	public float getRotation() {
		return 0;
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