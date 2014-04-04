package de.secondsystem.game01.impl.gui;

import java.util.ArrayList;
import java.util.List;

import org.jsfml.graphics.RenderTarget;
import org.jsfml.system.Vector2f;

public class ElementContainer extends Element {
	
	private final List<Element> children = new ArrayList<>();

	private Element mouseOver;
	
	private Element focus;
	
	private final Style style;
	
	public ElementContainer(float x, float y, float width, float height) {
		this(x, y, width, height, null, null);
	}
	
	public ElementContainer(float x, float y, float width, float height, Style style) {
		this(x, y, width, height, style, null);
	}

	public ElementContainer(float x, float y, float width, float height, Style style,
			ElementContainer owner) {
		super(x, y, width, height, owner);
		this.style = style;
	}
	public ElementContainer(float x, float y, float width, float height,
			ElementContainer owner) {
		this(x, y, width, height, null, owner);
	}
	
	@Override
	protected Style getStyle() {
		return style!=null ? style : super.getStyle();
	}

	public void addElement( Element element ) {
		children.add(element);
	}
	public void removeElement( Element element ) {
		children.remove(element);
	}
	
	protected Element getByPos(Vector2f mp) {
		for( Element c : children ) {
			if( c.inside(mp) ) {
				return c;
			}
		}
		
		return getStyle().autoFocus && !children.isEmpty() ? children.get(0) : null;
	}
	
	@Override
	public void onMouseOver(Vector2f mp) {
		Element e = getByPos(mp);
		
		if( mouseOver!=null && e!=mouseOver )
			mouseOver.onMouseOut();
		
		mouseOver = e;
		if( mouseOver!=null )
			mouseOver.onMouseOver(mp);
	}
	
	@Override
	protected void onMouseOut() {
		if( mouseOver!=null )
			mouseOver.onMouseOut();
	}
	
	@Override
	public void onFocus(Vector2f mp) {
		Element e = getByPos(mp);
		
		if( e==null || e!=focus ) {
			if( focus!=null )
				focus.onUnFocus();
			
			focus = e;
			if( focus!=null )
				focus.onFocus(mp);
		}
	}
	
	@Override
	protected void onUnFocus() {
		if( focus!=null )
			focus.onUnFocus();
	}
	
	@Override
	public void onKeyPressed(KeyType type) {
		if( focus!=null )
			focus.onKeyPressed(type);
	}
	@Override
	public void onKeyReleased(KeyType type) {
		if( focus!=null )
			focus.onKeyReleased(type);
	}
	@Override
	protected void onTextInput(int character) {
		if( focus!=null )
			focus.onTextInput(character);
	}
	
	@Override
	protected void drawImpl(RenderTarget renderTarget) {
		for( Element c : children )
			c.draw(renderTarget);
	}

	@Override
	public void update(long frameTimeMs) {
		for( Element c : children )
			c.update(frameTimeMs);
	}

}
