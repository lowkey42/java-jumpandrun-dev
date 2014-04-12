/**
 * 
 */
package de.secondsystem.game01.impl.gui;

import java.util.HashSet;
import java.util.Set;

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
	
	public interface Overlay extends IDrawable, IInsideCheck {
		void onFocus(Vector2f mp);
		void setOffsetPosition(Vector2f pos);
	}
	
	// shared Attributes
	
	protected float width, height;
	protected Vector2f pos;
	protected boolean visible = true;
	protected final ElementContainer owner;
	protected final Set<Overlay> overlays;
	
	public Element(float x, float y, float width, float height, ElementContainer owner) {
		this.owner = owner;
		this.width = width;
		this.height = height;
		this.pos = new Vector2f(x, y);
		
		if( owner!=null ) {
			owner.addElement(this);
			overlays = null;
		} else
			overlays = new HashSet<>();
	}
	
	protected void onFocus(Vector2f mp){}
	protected void onUnFocus(){}
	
	protected void onMouseOver(Vector2f mp){}
	protected void onMouseOut(){}
	
	protected void onScroll(int offset) {}
	
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
	
	protected void registerOverlay( Overlay overlay ) {
		if( overlays!=null )
			overlays.add(overlay);
		else
			owner.registerOverlay(overlay);
	}
	protected void unregisterOverlay( Overlay overlay ) {
		if( overlays!=null )
			overlays.remove(overlay);
		else
			owner.unregisterOverlay(overlay);
	}
	
	@Override
	public final void draw(RenderTarget renderTarget) {
		if(visible)
			drawImpl(renderTarget);
		
		if( overlays!=null )
			for( Overlay o : overlays )
				o.draw(renderTarget);
	}
	
	public boolean isVisible() {
		return visible;
	}
	
	public void setVisible(boolean visible) {
		this.visible = visible;
	}
	
	@Override
	public boolean inside(Vector2f point) {
		return point.x>=getPosition().x && point.x<=getPosition().x+width 
			&& point.y>=getPosition().y && point.y<=getPosition().y+height;
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
		return owner!=null ? Vector2f.add(pos, owner.getPosition()) : pos;
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