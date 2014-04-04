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
	public Label(float x, float y, String text, ElementContainer parent, Element forElem) {
		super(x, y, 1, 1, parent);
		this.forElem = forElem;

		this.text = new Text(text, getStyle().textFont, getStyle().textFontSize);
		this.text.setOrigin(0, this.text.getGlobalBounds().height / 2.f);
		this.text.setPosition(x, y + height / 2);
		setDimensions(this.text.getGlobalBounds().width, this.text.getGlobalBounds().height);
	}
	
	public void setFor(Element forElem) {
		this.forElem = forElem;
	}

	@Override protected void onFocus(Vector2f mp){forElem.onFocus(mp);}
	@Override protected void onUnFocus(){forElem.onUnFocus();}
	
	@Override protected void onMouseOver(Vector2f mp){forElem.onMouseOver(mp);}
	@Override protected void onMouseOut(){forElem.onMouseOut();}
	
	@Override protected void onTextInput(int character){forElem.onTextInput(character);}
	@Override protected void onKeyPressed(KeyType type){forElem.onKeyPressed(type);}
	@Override protected void onKeyReleased(KeyType type){forElem.onKeyReleased(type);}
	
	@Override
	public void update(long frameTimeMs) {
	}

	@Override
	protected void drawImpl(RenderTarget renderTarget) {
		renderTarget.draw(text);
	}

}
