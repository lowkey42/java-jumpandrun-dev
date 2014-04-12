package de.secondsystem.game01.impl.gui;

import java.util.HashSet;
import java.util.Set;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.FloatRect;
import org.jsfml.graphics.RectangleShape;
import org.jsfml.graphics.RenderTarget;
import org.jsfml.graphics.View;
import org.jsfml.system.Vector2f;

import de.secondsystem.game01.util.Tools;

public final class VScrollPanel extends LayoutElementContainer {

	private static final float SCROLL_SPEED = 50;
	public static final int WIDTH = 10;
	private static final int LENGTH = 100;

	private final RectangleShape border;
	
	private final RectangleShape scrollBar;
	private final RectangleShape slider;
	
	private final Set<Overlay> subOverlays = new HashSet<>();
	
	private Vector2f lastMousePos = Vector2f.ZERO;
	private boolean draggingSlider = false;
	
	private float scrollPos;
	private float innerHeight;
	
	public VScrollPanel(float x, float y, float width, float height,
			Layout layout, ElementContainer parent) {
		super(x, y, width, height, parent, layout);

		border = new RectangleShape(new Vector2f(width, height));
		border.setOutlineThickness(2.f);
		border.setOutlineColor(Color.WHITE);
		border.setFillColor(Color.BLACK);
		
		scrollBar = new RectangleShape(new Vector2f(WIDTH, height));
		slider = new RectangleShape(new Vector2f(WIDTH, LENGTH));
		scrollBar.setPosition(getPosition().x+ width-WIDTH, getPosition().y);
		scrollBar.setFillColor(Color.BLACK);
		scrollBar.setOutlineThickness(1);
		scrollBar.setOutlineColor(Color.WHITE);
		slider.setPosition(getPosition().x+ width-WIDTH, getPosition().y);
		slider.setFillColor(Color.WHITE);
		slider.setOutlineThickness(0);
	}
	
	@Override
	protected void onScroll(int offset) {
		scrollPos-=offset*SCROLL_SPEED;
	}
	
	@Override
	protected void registerOverlay(Overlay overlay) {
		super.registerOverlay(overlay);
		subOverlays.add(overlay);
	}
	@Override
	protected void unregisterOverlay(Overlay overlay) {
		super.unregisterOverlay(overlay);
		subOverlays.remove(overlay);
	}
	
	@Override
	public void onMouseOver(Vector2f mp) {
		if( draggingSlider ) {
			float yDiff = mp.y-lastMousePos.y;
			scrollPos += (yDiff/(getHeight()-LENGTH)) * Math.max(0, innerHeight-getHeight());
		}
		
		lastMousePos = mp;
		
		super.onMouseOver(new Vector2f(mp.x, mp.y+scrollPos));
	}
	
	@Override
	public void onFocus(Vector2f mp) {
		super.onFocus(new Vector2f(mp.x, mp.y+scrollPos));
	}
	
	@Override
	public void onKeyPressed(KeyType type) {
		if( type==KeyType.ENTER && Tools.isInside(slider, lastMousePos) ) {
			draggingSlider = true;
		} else
			super.onKeyPressed(type);
	}
	@Override
	public void onKeyReleased(KeyType type) {
		draggingSlider = false;
		super.onKeyReleased(type);
	}
	
	@Override
	protected void drawImpl(RenderTarget renderTarget) {
		innerHeight = 0;
		for( Element e : children )
			innerHeight = Math.max(innerHeight, e.getPosition().y+e.getHeight());
		
		innerHeight-=getPosition().y;
		
		scrollPos = Math.max(0, Math.min(scrollPos, Math.max(0, innerHeight-getHeight())));
		
		border.setPosition(getPosition());
		renderTarget.draw(border);
		
		View view = (View) renderTarget.getView();
		FloatRect panelRect = new FloatRect(getPosition().x / view.getSize().x,
		(getPosition().y) / view.getSize().y,
		(getWidth()) / view.getSize().x,
		(getHeight()) / view.getSize().y);
		
		View v = new View(new FloatRect(getPosition().x, getPosition().y+scrollPos, getWidth(), getHeight()));
		v.setViewport(panelRect);
		renderTarget.setView(v);
		
		super.drawImpl(renderTarget);
		
		renderTarget.setView(view);

		slider.setPosition(getPosition().x+ getWidth()-WIDTH, getPosition().y+ (scrollPos/Math.max(0, innerHeight-getHeight()))*(getHeight()-LENGTH) );
		renderTarget.draw(scrollBar);
		renderTarget.draw(slider);
		
		for( Overlay o : subOverlays ) {
			o.setOffsetPosition(new Vector2f(0, -scrollPos));
		}
	}

	public void setFillColor(Color color) {
		border.setFillColor(color);
	}
	
	public void setOutlineColor(Color color) {
		border.setOutlineColor(color);
	}

}
