package de.secondsystem.game01.impl.intro;

import java.io.IOException;





/*import org.jsfml.audio.Sound;
import org.jsfml.audio.SoundBuffer;*/
import org.jsfml.graphics.Color;
import org.jsfml.graphics.ConstFont;
import org.jsfml.graphics.ConstTexture;
import org.jsfml.graphics.FloatRect;
import org.jsfml.graphics.IntRect;
import org.jsfml.graphics.RenderTarget;
import org.jsfml.graphics.Sprite;
import org.jsfml.graphics.Text;
import org.jsfml.window.Mouse;
import org.jsfml.window.Window;

import de.secondsystem.game01.impl.ResourceManager;
import de.secondsystem.game01.impl.game.MainGameState;
import de.secondsystem.game01.model.IDrawable;
import de.secondsystem.game01.model.IUpdateable;

/**
 * This class provides a functional universal button with push ability
 * @author Sebastian
 *
 */
public final class MenuButton implements IDrawable, IUpdateable {

	public interface IOnClickListener {
		void onClick();
	}

	private final IOnClickListener clickListener;
	
	private final Text myText;
	private final Sprite newsprite;
	private final int height;
	private final int width;
	
	// Sound buttonOver;
	
	
	// Constructors
	public MenuButton(String text, String file, String fonttype, int pos_x, int pos_y, IOnClickListener clickListener) {
		this.clickListener = clickListener;
		
		try {
		// Loading standard Font for MenuButtons
		ConstFont myFont = ResourceManager.font.get(fonttype);
		
		// Loading Standard Texture for MenuButtons
		ConstTexture newButton = ResourceManager.texture_gui.get(file);
		
		height = newButton.getSize().y / 3;
		width = newButton.getSize().x;
					
		// Button Sprite generation and positioning
		newsprite = new Sprite(newButton);
		newsprite.setPosition(pos_x, pos_y);
		changeTextureClip(0);
		
		// Button inner text generation, positioning and calibration
		myText = new Text(text, myFont, 26);
		FloatRect textRect = myText.getGlobalBounds();
		myText.setOrigin(textRect.width / 2, textRect.height / 1.5f);
		myText.setPosition(newsprite.getPosition().x + width / 2, newsprite.getPosition().y + height / 2);

		/* TODO --> stabile sound implementations
		ConstSoundBuffer buttonOverBuffer = ResourceManager.sound.get("Button_over.wav");
		
		Sound buttonOver = new Sound(buttonOverBuffer);
		buttonOver.play();*/
		
		} catch( IOException e ) {
			throw new Error(e.getMessage(), e);
		}
	}
	
	
	public MenuButton(String text, int pos_x, int pos_y, IOnClickListener clickListener) {
		this(text, "MainMenuButton.png", "FreeSansBold.otf", pos_x, pos_y, clickListener);
	}
	
	
	public MenuButton(String text, int pos_x, int pos_y) {
		this(text, "MainMenuButton.png", "FreeSansBold.otf", pos_x, pos_y, new IOnClickListener(){@Override public void onClick(){System.out.println("pressed");}});
	}
	


	// Methods
	// Draw a sprite
	@Override
	public void draw(RenderTarget rt){
		if( rt instanceof Window )
			mouseover( (Window) rt );
		
		// TODO --> Decide if myText.setPosition should be done inside draw method
		// myText.setPosition(newsprite.getPosition().x + width / 2, newsprite.getPosition().y + height / 2);
		rt.draw(newsprite);
		rt.draw(myText);
	}
	
	private void mouseover(Window window){
		if(this.newsprite.getGlobalBounds().contains(Mouse.getPosition(window).x, Mouse.getPosition(window).y)){
			changeTextureClip(Mouse.isButtonPressed(org.jsfml.window.Mouse.Button.LEFT) ? 2 : 1); myText.setColor(Color.RED);
			//System.out.println("  OVER  ");
			//buttonOver.play();
		} else {
			changeTextureClip(0); myText.setColor(Color.WHITE);
		}
	}
	
		
	private void changeTextureClip(int pos) {
		newsprite.setTextureRect(new IntRect(0,height*pos,width,height));
	}


	@Override
	public void update(long frameTimeMs) {
		
	}
	
	public void onButtonReleased(float x, float y) {
		if( newsprite.getGlobalBounds().contains(x, y) ) {
			clickListener.onClick();
		}
	}
	
}
