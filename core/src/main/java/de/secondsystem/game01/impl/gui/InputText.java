/**
 * 
 */
package de.secondsystem.game01.impl.gui;

import java.io.IOException;

import org.jsfml.graphics.ConstFont;
import org.jsfml.graphics.RectangleShape;
import org.jsfml.graphics.RenderTarget;
import org.jsfml.graphics.Text;
import org.jsfml.system.Vector2f;
import org.jsfml.window.event.Event;

import de.secondsystem.game01.impl.ResourceManager;
import de.secondsystem.game01.model.IDrawable;
import de.secondsystem.game01.model.IUpdateable;

/**
 * This class provides an InputText Box with 1 input line
 * @author Sebastian
 *
 */
public class InputText extends GUIText{

	// Attributes

	private String prevString = ""; 
	private StringBuffer newString = new StringBuffer("");
		
	// Constructors
	
	InputText(int pos_x, int pos_y, int width){
		super(pos_x, pos_y, width);
	}
	
	
	// Methods	
	
	public void newKey(Event event){
		if(this.isActive){
			this.myText.setString(this.myText.getString() + event.asTextEvent().character);
			
			// Checking if the text inside the box exceeds width (12.5 pixel per char --> Monospace VeraMono) 
			if(myText.getString().length() > (this.width/12.5)){
				prevString += myText.getString().charAt(0);
				for(int i = 1; i <= (this.width/12.5); i++)
					newString.append(myText.getString().charAt(i));
				myText.setString(newString.toString());
				// setting newString free for next text interaction
				newString.delete(0, newString.length());
			}
		}
	}
	
	
	public void removeKey(){
		if(this.isActive){
			// Checking if Textbox contains text BUT prevString is empty 
			if(myText.getString().length() > 0 && prevString.length() == 0){
				for(int i = 0; i < myText.getString().length() - 1; i++)
					newString.append(myText.getString().charAt(i));
				myText.setString(newString.toString());
				newString.delete(0, newString.length());;
				// Checking if Textbox contains text AND prevString contains something
			} else if(myText.getString().length() > 0 && prevString.length() > 0){
				for(int i = 0; i < myText.getString().length() - 1; i++)
					newString.append(myText.getString().charAt(i));
				myText.setString((prevString.charAt(prevString.length()-1)) + newString.toString());
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
			String toSend = prevString + myText.getString();
			prevString = ""; myText.setString("");
			return toSend;	
		}
		return "";
	}

	
	public String getText(){
		return prevString + myText.getString();
	}
	
	
	@Override
	public void update(long frameTimeMs) {
		
	}
}