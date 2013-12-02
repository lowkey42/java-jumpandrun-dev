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
public final class MemoText {

	// Attributes
	int width, height;
	int pos_x, pos_y;
	
	private boolean isActive = false;
	
	private int currentLine, maxLines, maxChars, linePointer = 0;
	private final Vector2f myPos;
	private final RectangleShape linie_x1, linie_x2, linie_y1, linie_y2;
	
	private Text myText[];
	private String newString = "", prevString = "", content;
	
	// Constructors
	MemoText(int pos_x, int pos_y, int width, int height, String content){
		this.pos_x = pos_x;
		this.pos_y = pos_y;
		this.width = width;
		this.height = height;
		this.content = content;
		
		myPos = new Vector2f(pos_x, pos_y);
		
		int a = (int)(this.width/12.5);
		
		
		Vector2f myVec_x = new Vector2f(width, 1);
		Vector2f myVec_y = new Vector2f(1, height);
		
		linie_x1 = new RectangleShape(myVec_x); linie_x2 = new RectangleShape(myVec_x);
		linie_y1 = new RectangleShape(myVec_y); linie_y2 = new RectangleShape(myVec_y);		
		
		linie_x1.setPosition(pos_x, pos_y); linie_x2.setPosition(pos_x, pos_y + height);
		linie_y1.setPosition(pos_x, pos_y); linie_y2.setPosition(pos_x + width, pos_y);
		
		try {
			// Loading standard Font (12.5 pixel width & 21 pixel height per char --> Monospace VeraMono
			ConstFont myFont = ResourceManager.font.get("VeraMono.ttf");
			myText = new Text[((int)(this.height/21))];
				for(int i = 0; i < ((int)(this.height/21)); i++){
					myText[i] = new Text("", myFont, 20);
					myText[i].setPosition(pos_x, pos_y + i*21);
					System.out.println(myText[i].getString());
				}
			} catch( IOException e ) {
				throw new Error(e.getMessage(), e);
			}
		
		System.out.println("MEMO --> Possible Lines with standard fonts " + this.height / 21);
		System.out.println("MEMO --> Possible Chars in each line with std font: " + this.width / 12.5);
		
	}
	
	
	
	
	// Methods
	
	public void draw(RenderTarget rt){
		rt.draw(linie_x1); rt.draw(linie_x2); rt.draw(linie_y1); rt.draw(linie_y2);
		for(int i = 0; i < myText.length; i++){
			rt.draw(myText[i]);
		}
	}
	
	
	public void newKey(Event event){
		if(this.isActive)
			if(this.myText[linePointer].getString().length() <= this.width / 12.5){
				this.myText[linePointer].setString(this.myText[linePointer].getString() + event.asTextEvent().character);
			} else {
				linePointer += 1;
				this.myText[linePointer].setString(this.myText[linePointer].getString() + event.asTextEvent().character);
			}
		
	}
	
	
	public void setActive(){
		this.isActive = true;
	}
	
	public void setInactive(){
		this.isActive = false;
	}
	
}
