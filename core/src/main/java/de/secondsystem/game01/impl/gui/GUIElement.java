/**
 * 
 */
package de.secondsystem.game01.impl.gui;

import java.util.ArrayList;

import org.jsfml.graphics.RenderTarget;
import org.jsfml.system.Vector2f;

import de.secondsystem.game01.impl.GameContext;
import de.secondsystem.game01.impl.gui.listeners.IOnClickListener;
import de.secondsystem.game01.impl.gui.listeners.IOnMouseEnterListener;
import de.secondsystem.game01.impl.gui.listeners.IOnMouseLeaveListener;
import de.secondsystem.game01.impl.gui.listeners.IOnMouseOverListener;
import de.secondsystem.game01.model.IDimensioned;
import de.secondsystem.game01.model.IDrawable;
import de.secondsystem.game01.model.IInsideCheck;
import de.secondsystem.game01.model.IMoveable;
import de.secondsystem.game01.model.IScalable;

/**
 * @author Sebastian
 *
 */
public abstract class GUIElement implements IDrawable, IDimensioned, IScalable, IMoveable, IInsideCheck, 
		IOnMouseOverListener, IOnMouseEnterListener, IOnMouseLeaveListener, IOnClickListener {

	// shared Attributes
	
	protected float width, height;
	// position and rotation are relative to the parent
	protected Vector2f pos;
	protected float rotation;
	protected boolean visible;
	protected GUIElement parent;
	
	protected IOnClickListener clickListener;
	protected ArrayList<GUIElement> children = new ArrayList<>();
	protected boolean mouseOver;
	
	public GUIElement(float x, float y, float width, float height, GUIElement parent) {
		setParent(parent);
		this.width = width;
		this.height = height;
		this.pos = new Vector2f(x, y);	
	}
	
	public void setParent(GUIElement parent) {
		this.parent = parent;
		if( parent != null )
			parent.addChild(this);
	}
	
	public GUIElement getParent() {
		return parent;
	}
	
	protected void addChild(GUIElement child) {
		children.add(child);
	}
	
	protected ArrayList<GUIElement> getChildren() {
		return children;
	}
	
	protected void removeChild(GUIElement child) {
		children.remove(child);
	}
	
	public void setOnClickListener(IOnClickListener clickListener) {
		this.clickListener = clickListener;
	}
	
	public boolean isVisible() {
		return visible;
	}
	
	public void setVisible(boolean visible) {
		this.visible = visible;
	}
	
	public void refresh() {
		if( parent != null ) {
			setPosition(Vector2f.add(parent.getPosition(), pos));
			setRotation(parent.getRotation() + rotation);
			
		}
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
	
	@Override
	public void onMouseOver() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMouseLeave() {
		mouseOver = false;
	}

	@Override
	public void onMouseEnter() {
		mouseOver = true;	
	}
	
	@Override
	public void onClick() {
		if( clickListener != null )
			clickListener.onClick();
	}
	
	@Override
	public void draw(RenderTarget renderTarget) {
		for(GUIElement child : children) {
			child.draw(renderTarget);
		}
	}
	
	public void update(GameContext ctx) {
		if( inside(ctx.getMousePosition()) ) {
			if( !mouseOver ) 
				onMouseEnter();
			
			onMouseOver();
		}
		else {
			if( mouseOver )
				onMouseLeave();
		}
		
		refresh();
	}
}