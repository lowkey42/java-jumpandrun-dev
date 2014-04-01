/**
 * 
 */
package de.secondsystem.game01.impl.gui;

import org.jsfml.system.Vector2f;
import org.jsfml.window.event.Event;

/**
 * This class provides an InputText Box with 1 input line
 * @author Sebastian
 *
 */
public class InputText extends GUIText {

	// Attributes

	private String prevString = ""; 
	private StringBuffer newString = new StringBuffer("");
		
	// Constructors
	
	InputText(float x, float y, float width, float height, String text, GUIElement owner){
		super(x, y, width, height, text, owner);
	}
	
	
	// Methods	
	
	public void newKey(Event event){
		if(this.isActive){
			this.caption.setString(this.caption.getString() + event.asTextEvent().character);
			
			// Checking if the text inside the box exceeds width (12.5 pixel per char --> Monospace VeraMono) 
			if(caption.getString().length() > (this.width/12.5)){
				prevString += caption.getString().charAt(0);
				for(int i = 1; i <= (this.width/12.5); i++)
					newString.append(caption.getString().charAt(i));
				caption.setString(newString.toString());
				// setting newString free for next text interaction
				newString.delete(0, newString.length());
			}
		}
	}
	
	
	public void removeKey(){
		if(this.isActive){
			// Checking if Textbox contains text BUT prevString is empty 
			if(caption.getString().length() > 0 && prevString.length() == 0){
				for(int i = 0; i < caption.getString().length() - 1; i++)
					newString.append(caption.getString().charAt(i));
				caption.setString(newString.toString());
				newString.delete(0, newString.length());;
				// Checking if Textbox contains text AND prevString contains something
			} else if(caption.getString().length() > 0 && prevString.length() > 0){
				for(int i = 0; i < caption.getString().length() - 1; i++)
					newString.append(caption.getString().charAt(i));
				caption.setString((prevString.charAt(prevString.length()-1)) + newString.toString());
				newString.delete(0, newString.length());;
				for(int i = 0; i < prevString.length()-1; i++)
					newString.append(prevString.charAt(i));
				prevString = newString.toString();
				newString.delete(0, newString.length());;
			}
		}
	}
	
	
	public String finalizeInput(){
		if(this.isActive){
			String toSend = prevString + caption.getString();
			prevString = ""; caption.setString("");
			return toSend;	
		}
		return "";
	}

	
	public String getText(){
		return prevString + caption.getString();
	}


	@Override
	public boolean inside(Vector2f point) {
		// TODO Auto-generated method stub
		return false;
	}
}