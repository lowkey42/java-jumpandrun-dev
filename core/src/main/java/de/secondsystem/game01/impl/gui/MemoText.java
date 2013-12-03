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

/**
 * This class provides a multiline Text Box
 * @author Sebastian
 *
 *
 */
public final class MemoText {

	// Attributes
	int pos_x, pos_y, width, height;
	
	private int maxLines, maxChars, linePointer = 0;
	private final Vector2f myPos;
	private final RectangleShape linie_x1, linie_x2, linie_y1, linie_y2;
	
	private Text myText[];
	private String newString = "", prevString = "", content;
	
	private boolean isActive = false;
	
	// Constructors
	MemoText(int pos_x, int pos_y, int width, int height, String content){
		this.pos_x = pos_x;
		this.pos_y = pos_y;
		this.width = width;
		this.height = height;
		this.content = content;
		
		maxLines = (int)(this.height / 21);
		maxChars = (int)(this.width / 13);
		
		myPos = new Vector2f(pos_x, pos_y);
						
		Vector2f myVec_x = new Vector2f(width, 1);
		Vector2f myVec_y = new Vector2f(1, height);
		
		// Creating the surrounding MEMO Container
		linie_x1 = new RectangleShape(myVec_x); linie_x2 = new RectangleShape(myVec_x);
		linie_y1 = new RectangleShape(myVec_y); linie_y2 = new RectangleShape(myVec_y);		
		// Position the MEMO Container
		linie_x1.setPosition(pos_x, pos_y); linie_x2.setPosition(pos_x, pos_y + height);
		linie_y1.setPosition(pos_x, pos_y); linie_y2.setPosition(pos_x + width, pos_y);
		
		try {
			// Loading standard Font (12.5 pixel width & 21 pixel height per char --> Monospace VeraMono)
			ConstFont myFont = ResourceManager.font.get("VeraMono.ttf");
			// Creating Text array (array maximum = maxLines)
			myText = new Text[maxLines];
				for(int i = 0; i < maxLines; i++){
					myText[i] = new Text("", myFont, 20);
					myText[i].setPosition(pos_x + 5, pos_y + i*21);
					myText[i].setString("TEST_TEST_TEST_TEST");
					System.out.println(myText[i].getString());
				}
			} catch( IOException e ) {
				throw new Error(e.getMessage(), e);
			}
		
		
		System.out.println("MEMO --> Possible Lines with standard fonts " + maxLines);
		System.out.println("MEMO --> Possible Chars in each line with std font: " + maxChars);
		
	}
	
	
	
	// Methods
	
	public void draw(RenderTarget rt){
		rt.draw(linie_x1); rt.draw(linie_x2); rt.draw(linie_y1); rt.draw(linie_y2);
		for(int i = 0; i < myText.length; i++){
			rt.draw(myText[i]);
		}
	}
	
	
	public void newKey(Event event){
		if(this.isActive){
			// Enoug space for characters left --> just write them into linePointer marked arrays
			if(this.myText[linePointer].getString().length() <= maxChars){
				this.myText[linePointer].setString(this.myText[linePointer].getString() + event.asTextEvent().character);
			// End of line is reached --> set linePointer to the next Text array field
			} else if(linePointer < maxLines - 1){
				linePointer += 1;
				this.newKey(event);
			// End of all lines reached --> all Text lines are filled with characters
			} else {			
				// Constructing PreString containing left outshifted text
				prevString += myText[0].getString().charAt(0);
				
				// Shift all Arrays except the last one (Last array's new key has to be the user input)
				for(int i = 0; i < myText.length - 1; i++){
					newString = "";
					for(int j = 1; j < myText[i].getString().length(); j++){
						newString += myText[i].getString().charAt(j);						
					}
					newString += myText[i+1].getString().charAt(0);
					myText[i].setString(newString);					
				}				
				// Refreshing last array's content with user input
				newString = "";
				for(int i = 1; i < myText[myText.length-1].getString().length(); i++)
					newString += myText[myText.length-1].getString().charAt(i);
				newString += event.asTextEvent().character; 
				myText[myText.length-1].setString(newString);	
			}
			
		}
		
	}

	
	public void removeKey(){
		if(this.isActive){
			if(this.myText[linePointer].getString().length()-1 < 0 && linePointer != 0){
				linePointer -= 1;
			}
			newString = "";
			for(int i = 0; i < myText[linePointer].getString().length()-1; i++){			
				newString += myText[linePointer].getString().charAt(i);
			}
			this.myText[linePointer].setString(newString);
		}
	}
	
	public void setActive(){
		this.isActive = true;
	}
	
	public void setInactive(){
		this.isActive = false;
	}
	
}
