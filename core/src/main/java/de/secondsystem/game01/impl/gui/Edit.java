/**
 * 
 */
package de.secondsystem.game01.impl.gui;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.RectangleShape;
import org.jsfml.graphics.RenderTarget;
import org.jsfml.graphics.Text;
import org.jsfml.system.Vector2f;

/**
 * This class provides an InputText Box with 1 input line
 * @author Sebastian
 *
 */
public class Edit extends Element implements TextElement {

	private static final char CURSER_CHAR_1 = '┊';
	private static final char CURSER_CHAR_2 = '┋';
	private static final int CURSER_BLINK_DELAY = 500; 
	private static final int CHARACTER_CLIP_LIMIT = 5;
	private static final int BORDER_SIZE = 2;
	private static final int TEXT_X_OFFSET = 4;
	
	protected final RwValueRef<String> textRef;
	
	private final StringBuilder buffer;
	
	protected final Text text;
	
	private final RectangleShape box;
	
	private int curserPosition = -1;
	
	private boolean curserBlinkState;
	
	private int curserBlinkDelayAcc;

	Edit(float x, float y, float width, String text, ElementContainer owner){
		this(x, y, width, new SimpleRwValueRef<>(text), owner);
	}
	Edit(float x, float y, float width, RwValueRef<String> textRef, ElementContainer owner){
		super(x, y, width, 1, owner);
		this.textRef = textRef;
		
		text = new Text(""+CURSER_CHAR_2, getStyle().textFont, getStyle().textFontSize);
		text.setOrigin(0, this.text.getLocalBounds().height / 2);
		setDimensions(width, this.text.getLocalBounds().height*2+10);
		text.setString(textRef!=null && textRef.getValue()!=null ? textRef.getValue() : "");
		
		buffer = new StringBuilder(textRef!=null && textRef.getValue()!=null ? textRef.getValue() : "");
		box = new RectangleShape(new Vector2f(width, height));
		box.setFillColor(new Color(0, 0, 0, 0));
		box.setOutlineColor(Color.WHITE);
		box.setOutlineThickness(BORDER_SIZE);
	}

	public String getText() {
		return buffer.toString();
	}
	public void setText(String text) {
		buffer.setLength(0);
		buffer.append(text);
		updateText(true);
	}
	
	@Override
	protected void drawImpl(RenderTarget renderTarget) {
		text.setPosition( Vector2f.add(getPosition(), new Vector2f(TEXT_X_OFFSET, (this.text.getLocalBounds().height+10) / 2)) );
		box.setPosition(getPosition());
		
		renderTarget.draw(box);
		renderTarget.draw(text);
	}
	
	protected void updateText(boolean modified) {
		if( modified )
			textRef.setValue(getText());
		else if( !buffer.toString().equals(textRef.getValue()) ) {
			buffer.setLength(0);
			buffer.append(textRef.getValue());
		}

		curserPosition = Math.min(curserPosition, buffer.length());	
		
		if( curserPosition>=0 )
			text.setString(buffer.toString().substring(0, curserPosition)
					+ (curserBlinkState ? CURSER_CHAR_1 : CURSER_CHAR_2)
					+ buffer.toString().substring(curserPosition));
		else
			text.setString(buffer.toString());
		
		// clip text to border
		int leftToCurser = buffer.length()-1 - curserPosition;
		while(text.getLocalBounds().width>getWidth()-TEXT_X_OFFSET && leftToCurser>CHARACTER_CLIP_LIMIT ) {
			leftToCurser--;
			text.setString(text.getString().substring(0, text.getString().length()-1));
		}
		
		leftToCurser = curserPosition;
		while(text.getLocalBounds().width>getWidth()-TEXT_X_OFFSET && leftToCurser>CHARACTER_CLIP_LIMIT ) {
			leftToCurser--;
			text.setString(text.getString().substring(1));
		}
	}
	
	@Override
	protected void onFocus(Vector2f mp) {
		box.setOutlineColor(Color.RED);
		
		boolean curserSet = false;
		for( int i=0; i<text.getString().length(); ++i ) {
			if(	text.findCharacterPos(i).x>mp.x ) {
				curserPosition = Math.max(curserPosition>=i ? i-2 : i-1, 0);
				curserSet = true;
				break;
			}
		}
		if( !curserSet )
			curserPosition = buffer.length();
		
		updateText(false);
	}
	@Override
	protected void onUnFocus() {
		box.setOutlineColor(Color.WHITE);
		curserPosition = -1;
		updateText(false);
	}
	@Override
	protected void onKeyPressed(KeyType type) {
		switch (type) {
			case BACKSPACE:
				if( curserPosition>=getCharCount(curserPosition-1) )
					curserPosition-=getCharCount(curserPosition-1);
					// fallthrough
				else
					break;
				
			case DEL:
				if( buffer.length()>0 ) {
					buffer.delete(curserPosition, curserPosition+getCharCount(curserPosition));
					updateText(true);
				}
				break;
				
			case LEFT:
				curserPosition = Math.max(curserPosition-getCharCount(curserPosition-1), 0);
				break;
				
			case RIGHT:
				curserPosition = Math.min(curserPosition+getCharCount(curserPosition-1), buffer.length());
				break;
				
			default:
				break;
		}
	}
	
	private int getCharCount(int pos) {
		return pos>=0 && pos<buffer.length() ? Character.charCount(buffer.codePointAt(pos)) : 1;
	}
	
	@Override
	protected void onTextInput(int character) {
		if( !Character.isISOControl(character) && Character.isDefined(character) ) {
			buffer.insert(curserPosition, Character.toChars(character));
			curserPosition+=Character.charCount(character);
			updateText(true);
		}
	}

	@Override
	public void update(long frameTimeMs) {
		curserBlinkDelayAcc+=frameTimeMs;
		while( curserBlinkDelayAcc>=CURSER_BLINK_DELAY ) {
			curserBlinkState=!curserBlinkState;
			curserBlinkDelayAcc-=CURSER_BLINK_DELAY;
			updateText(false);
		}
	}
	
}
