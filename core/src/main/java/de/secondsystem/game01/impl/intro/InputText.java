/**
 * 
 */
package de.secondsystem.game01.impl.intro;

import java.io.IOException;

import org.jsfml.graphics.ConstFont;
import org.jsfml.graphics.RectangleShape;
import org.jsfml.graphics.RenderTarget;
import org.jsfml.graphics.Text;
import org.jsfml.system.Vector2f;
import org.jsfml.window.event.Event;

import de.secondsystem.game01.impl.ResourceManager;

/**
 * @author Sebastian
 *
 */
public class InputText {

	// Attributes
	private int width, height = 25, pos_x, pos_y;
	
	private final RectangleShape linie_x1, linie_x2, linie_y1, linie_y2;
	
	private final Vector2f myPos;
	
	String prevString = "";
	String newString = "";
	
	String fonttype = "VeraMono.ttf";
	
	private Text myText;
	
	
	// Constructors
	InputText(int pos_x, int pos_y, int width, String inhalt){
		this.width = width;
		this.pos_x = pos_x;
		this.pos_y = pos_y;		
		
		Vector2f myVec_x = new Vector2f(width, 1);
		Vector2f myVec_y = new Vector2f(1, height);
		
		linie_x1 = new RectangleShape(myVec_x); linie_x2 = new RectangleShape(myVec_x);
		linie_y1 = new RectangleShape(myVec_y); linie_y2 = new RectangleShape(myVec_y);
		
		linie_x1.setPosition(pos_x, pos_y); linie_x2.setPosition(pos_x, linie_x1.getPosition().y + height);
		linie_y1.setPosition(pos_x, pos_y); linie_y2.setPosition(linie_y1.getPosition().x + width, pos_y);
		
		myPos = new Vector2f(pos_x, pos_y);
		
			
		try {
			// Loading standard Font
			ConstFont myFont = ResourceManager.font.get(fonttype);
			myText = new Text(inhalt, myFont, (height - 5));
			myText.setPosition(myPos.x + 5, myPos.y);
			} catch( IOException e ) {
				throw new Error(e.getMessage(), e);
			}
		
		
	}
	
	
	// Methods
	public void draw(RenderTarget rt){
		rt.draw(this.linie_x1); rt.draw(this.linie_x2); rt.draw(this.linie_y1); rt.draw(this.linie_y2); rt.draw(myText);		
	}
	
	public void newKey(Event event){
		this.myText.setString(this.myText.getString() + event.asTextEvent().character);
		
		if(myText.getString().length() > 16){
			prevString += myText.getString().charAt(0);
			for(int i = 1; i < 17; i++)
				newString += myText.getString().charAt(i);
			myText.setString(newString);
			newString = "";
		}
		
		System.out.println("Previous input String contains: " + prevString);
		
		
		
	}
	
	public void removeKey(Event event){
		
		if(myText.getString().length() > 0 && prevString.length() == 0){
			for(int i = 0; i < myText.getString().length() - 1; i++)
				newString += myText.getString().charAt(i);
			myText.setString(newString);
			System.out.println("removed!");
			newString = "";
		} else if(myText.getString().length() > 0 && prevString.length() > 0){
			for(int i = 0; i < myText.getString().length() - 1; i++)
				newString += myText.getString().charAt(i);
			myText.setString((prevString.charAt(prevString.length()-1)) + newString);
			newString = "";
			for(int i = 0; i < prevString.length()-1; i++)
				newString += prevString.charAt(i);
			prevString = newString;
			newString = "";
		}

		
	}
	
}