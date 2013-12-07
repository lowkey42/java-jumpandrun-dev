package de.secondsystem.game01.impl.gui;

/*import org.jsfml.audio.Sound;
import org.jsfml.audio.SoundBuffer;*/
import org.jsfml.graphics.RenderTarget;
import org.jsfml.window.Window;

/**
 * This class provides a functional universal button with push ability
 * @author Sebastian
 *
 */
public final class MenuButton extends GUIButton {

	
	// Sound buttonOver;
	
	// Constructors
	public MenuButton(int pos_x, int pos_y, String content, String file, String fonttype, IOnClickListener clickListener) {
		super(pos_x, pos_y, content, clickListener);
	}
	
	
	public MenuButton(int pos_x, int pos_y, String text, IOnClickListener clickListener) {
		this(pos_x, pos_y, text, "MainMenuButton.png", "FreeSansBold.otf", clickListener);
	}
	
	
	public MenuButton(int pos_x, int pos_y, String text) {
		this(pos_x, pos_y, text, new IOnClickListener(){@Override public void onClick(){System.out.println("pressed");}});
	}


	// Methods

	public double getSize(){
		return this.width;
	}
	
	@Override
	public void draw(RenderTarget rt){
		if( rt instanceof Window )
			mouseover( (Window) rt );
		rt.draw(mySprite);
		rt.draw(myText);
	}
}