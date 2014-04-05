package de.secondsystem.game01.impl.gui;

import org.jsfml.graphics.RenderTarget;
import org.jsfml.graphics.Text;
import org.jsfml.system.Vector2f;

public class Label extends Element {

	private Text text;
	
	private Element labeledElem;

	public Label(float x, float y, String text, ElementContainer parent) {
		this(x, y, text, parent, null);
	}
	
	public Label(float x, float y, String text, float width, float height, ElementContainer parent) {
		this(x, y, text, width, height, parent, null);
	}
	
	public Label(float x, float y, String text, ElementContainer parent, Element labeledElem) {
		this(x, y, text, 1, 1, parent, labeledElem);
	}
	
	public Label(float x, float y, String text, float width, float height, ElementContainer parent, Element labeledElem) {
		super(x, y, width, height, parent);
		
		if( parent == labeledElem )
			throw new IllegalArgumentException("Labeled element can't be the owner/parent of the label due to a potential stack overflow.");
		
		this.labeledElem = labeledElem;

		setText(text);
		
		setDimensions(Math.max(width, this.text.getGlobalBounds().width), Math.max(height, this.text.getGlobalBounds().height));
	}
	
	public void setText(String text) {
		this.text = new Text(text, getStyle().textFont, getStyle().textFontSize);
		this.text.setOrigin(0, this.text.getGlobalBounds().height / 2.f);
	}
	
	public void setFor(Element forElem) {
		this.labeledElem = forElem;
	}

	@Override protected void onFocus(Vector2f mp) {
		if(labeledElem!=null) 
			labeledElem.onFocus(mp);
	}
	
	@Override protected void onUnFocus() {
		if(labeledElem!=null) 
			labeledElem.onUnFocus();
	}
	
	@Override protected void onMouseOver(Vector2f mp) {
		if(labeledElem!=null) 
			labeledElem.onMouseOver(mp);
	}
	
	@Override protected void onMouseOut() {
		if(labeledElem!=null) 
			labeledElem.onMouseOut();
	}
	
	@Override protected void onTextInput(int character) {
		if(labeledElem!=null) 
			labeledElem.onTextInput(character);
	}
	
	@Override protected void onKeyPressed(KeyType type) {
		if(labeledElem!=null) 
			labeledElem.onKeyPressed(type);
	}
	
	@Override protected void onKeyReleased(KeyType type) {
		if(labeledElem!=null) 
			labeledElem.onKeyReleased(type);
	}
	
	@Override
	public void update(long frameTimeMs) {
	}

	@Override
	protected void drawImpl(RenderTarget renderTarget) {
		text.setPosition(getPosition());
		
		renderTarget.draw(text);
	}

}
