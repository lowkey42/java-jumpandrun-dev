package de.secondsystem.game01.impl.gui;

import org.jsfml.graphics.RenderTarget;
import org.jsfml.graphics.Text;
import org.jsfml.system.Vector2f;

public class Label extends Element {

	private final Text text;
	
	private Element forElem;

	public Label(float x, float y, String text, ElementContainer parent) {
		this(x, y, text, parent, null);
	}
	public Label(float x, float y, String text, float width, float height, ElementContainer parent) {
		this(x, y, text, width, height, parent, null);
	}
	public Label(float x, float y, String text, ElementContainer parent, Element forElem) {
		this(x, y, text, 1, 1, parent, forElem);
	}
	public Label(float x, float y, String text, float width, float height, ElementContainer parent, Element forElem) {
		super(x, y, width, height, parent);
		this.forElem = forElem;

		this.text = new Text(text, getStyle().textFont, getStyle().textFontSize);
		this.text.setOrigin(0, this.text.getGlobalBounds().height / 2.f);
		
		setDimensions(Math.max(width, this.text.getGlobalBounds().width), Math.max(height, this.text.getGlobalBounds().height));
	}
	
	public void setFor(Element forElem) {
		this.forElem = forElem;
	}

	@Override protected void onFocus(Vector2f mp){if(forElem!=null) forElem.onFocus(mp);}
	@Override protected void onUnFocus(){if(forElem!=null) forElem.onUnFocus();}
	
	@Override protected void onMouseOver(Vector2f mp){if(forElem!=null) forElem.onMouseOver(mp);}
	@Override protected void onMouseOut(){if(forElem!=null) forElem.onMouseOut();}
	
	@Override protected void onTextInput(int character){if(forElem!=null) forElem.onTextInput(character);}
	@Override protected void onKeyPressed(KeyType type){if(forElem!=null) forElem.onKeyPressed(type);}
	@Override protected void onKeyReleased(KeyType type){if(forElem!=null) forElem.onKeyReleased(type);}
	
	@Override
	public void update(long frameTimeMs) {
	}

	@Override
	protected void drawImpl(RenderTarget renderTarget) {
		text.setPosition(getPosition());
		
		renderTarget.draw(text);
	}

}
