package de.secondsystem.game01.impl.gui;

/*import org.jsfml.audio.Sound;
import org.jsfml.audio.SoundBuffer;*/
import org.jsfml.graphics.RenderTarget;
import org.jsfml.system.Vector2f;
import org.jsfml.window.Window;

import de.secondsystem.game01.impl.gui.listeners.IOnClickListener;

/**
 * This class provides a functional universal button with push ability
 * @author Sebastian
 *
 */
public final class MenuButton extends GUIButton {

	// Sound buttonOver;
	
	// Constructors
	public MenuButton(float x, float y, String caption, GUIElement owner, String file, String fonttype, IOnClickListener clickListener) {
		super(x, y, 250, 100, caption, owner, clickListener);
	}
	
	
	public MenuButton(float x, float y, String text, GUIElement owner, IOnClickListener clickListener) {
		this(x, y, text, owner, "MainMenuButton.png", "FreeSansBold.otf", clickListener);
	}
	
	
	public MenuButton(float x, float y, String caption, GUIElement owner) {
		this(x, y, caption, owner, new IOnClickListener(){@Override public void onClick(){System.out.println("pressed");}});
	}


	// Methods

	public double getSize(){
		return this.width;
	}
	
	@Override
	public void draw(RenderTarget rt){
		if( rt instanceof Window )
			mouseover( (Window) rt );
		sprite.draw(rt);
		rt.draw(caption);
	}
}