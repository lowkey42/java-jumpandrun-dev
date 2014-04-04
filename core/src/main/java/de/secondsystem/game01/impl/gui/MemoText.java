/**
 * 
 */
package de.secondsystem.game01.impl.gui;

import java.io.IOException;

import org.jsfml.graphics.ConstFont;
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
//	// Attributes
//	
//	private int maxLines, maxChars, linePointer = 0;
//	private Text myText[];
//	
//	private String prevString = "", fullString;
//	private StringBuffer newString = new StringBuffer("");
//	
//	// Constructors
//	
//	MemoText(float x, float y, float width, float height, String text, Element owner){
//		super(x, y, width, height, text, owner);
//		
//		maxLines = (int)(this.height / 21);
//		maxChars = (int)(this.width / 12.6);
//		
//		try {
//			// Loading standard Font (12.5 pixel width & 21 pixel height per char --> Monospace VeraMono)
//			ConstFont myFont = ResourceManager.font.get("VeraMono.ttf");
//			// Creating Text array (array maximum = maxLines)
//			myText = new Text[maxLines];
//				for(int i = 0; i < maxLines; i++){
//					myText[i] = new Text("", myFont, 20);
//					myText[i].setPosition(x + 5, y + i*21);
//					myText[i].setString("");
//					System.out.println(myText[i].getString());
//				}
//			} catch( IOException e ) {
//				throw new Error(e.getMessage(), e);
//			}
//		System.out.println("MEMO --> Possible Lines with standard fonts " + maxLines);
//		System.out.println("MEMO --> Possible Chars in each line with std font: " + maxChars);
//	}
//	
//	
//	// Methods
//	
//	@Override
//	public void draw(RenderTarget rt){
//		rt.draw(myBox);
//		for(int i = 0; i < myText.length; i++){
//			rt.draw(myText[i]);
//		}
//	}
//	
//	
//	public void newKey(Event event){
//		if(this.isActive){
//			// Enoug space for characters left --> just write them into linePointer marked arrays
//			if(this.myText[linePointer].getString().length() <= maxChars){
//				this.myText[linePointer].setString(this.myText[linePointer].getString() + event.asTextEvent().character);
//			// End of line is reached --> set linePointer to the next Text array field
//			} else if(linePointer < maxLines - 1){
//				linePointer += 1;
//				this.newKey(event);
//			// End of all lines reached --> all Text lines are filled with characters
//			} else {			
//				// Constructing PreString containing left outshifted text
//				prevString += myText[0].getString().charAt(0);
//				
//				// Shift all Arrays backward except the last one (Last array's new key has to be the user input)
//				for(int i = 0; i < myText.length - 1; i++){
//					newString.delete(0, newString.length());
//					for(int j = 1; j < myText[i].getString().length(); j++){
//						newString.append(myText[i].getString().charAt(j));						
//					}
//					newString.append(myText[i+1].getString().charAt(0));
//					myText[i].setString(newString.toString());					
//				}				
//				// Refreshing last array's content with user input
//				newString.delete(0, newString.length());
//				for(int i = 1; i < myText[myText.length-1].getString().length(); i++)
//					newString.append(myText[myText.length-1].getString().charAt(i));
//				newString.append(event.asTextEvent().character); 
//				myText[myText.length-1].setString(newString.toString());		
//			}
//		}
//	}
//
//	
//	public void removeKey(){
//		if(this.isActive){
//			// Shift all Arrays forward except the first one where the last Char of prevString has to be placed in
//			if(prevString != "" && prevString.length() - 1 > 0){
//				for(int i = myText.length - 1; i > 0; i--){
//					newString.delete(0, newString.length());
//					// Adding last Char of previous String to the first position of the StringBuffer newString
//					newString.append(myText[i-1].getString().charAt(myText[i-i].getString().length()-1));
//					for(int j = 0; j < myText[i].getString().length() - 1; j++){
//						newString.append(myText[i].getString().charAt(j));						
//					}
//					myText[i].setString(newString.toString());					
//				}	
//				// Refreshing first array's content with last char of the prevString
//				newString.delete(0, newString.length());
//				newString.append(prevString.charAt(prevString.length()-1));
//				for(int i = 0; i < myText[0].getString().length() - 1; i++){
//					newString.append(myText[0].getString().charAt(i));
//				}
//				prevString = prevString.substring(0, prevString.length()-1);
//				myText[0].setString(newString.toString());
//				
//			} else if(this.myText[linePointer].getString().length()-1 < 0 && linePointer != 0){
//				linePointer -= 1;
//			} else {
//				newString.delete(0, newString.length());
//				for(int i = 0; i < myText[linePointer].getString().length()-1; i++){			
//					newString.append(myText[linePointer].getString().charAt(i));
//				}
//			this.myText[linePointer].setString(newString.toString());
//			}
//		}
//	}
//	
//	
//	public String finalizeInput(){
//		if(this.isActive){
//			fullString = prevString;
//			for(int i = 0; i < myText.length-1; i++)
//				fullString += myText[i].getString();
//			prevString = "";
//			for(int i = 0; i < myText.length; i++)
//				myText[i].setString("");
//			linePointer = 0;
//			return prevString + fullString;
//		}
//		return "";
//	}
//	
//	
//	public void scrollText(Event event){
//		
//		System.out.println("Mouse wheel moved " + (event.asMouseWheelEvent().delta == 1 ? "up" : "down"));
//		if(event.asMouseWheelEvent().delta == 1 && prevString.length()/(maxChars+1) >= 1){
//			
//			System.out.println("Mindestens eine ganze Line im prev!");
//			System.out.println(prevString.length()/(maxChars+1));
//			System.out.println("MaxChars per Line: " + maxChars + "  Length of prevString: " + prevString.length());
//			
//		}
//	}
//
//
//	@Override
//	public boolean inside(Vector2f point) {
//		// TODO Auto-generated method stub
//		return false;
//	}
}
