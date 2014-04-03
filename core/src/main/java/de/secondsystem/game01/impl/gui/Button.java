/**
 * 
 */
package de.secondsystem.game01.impl.gui;

import org.jsfml.graphics.FloatRect;
import org.jsfml.graphics.IntRect;
import org.jsfml.graphics.RenderTarget;
import org.jsfml.graphics.Sprite;
import org.jsfml.graphics.Text;
import org.jsfml.system.Vector2f;

import de.secondsystem.game01.impl.gui.listeners.IOnClickListener;

/**
 * @author Sebastian
 *
 */
public class Button extends Element {

	protected final Sprite sprite;
	
	protected final Text caption;
	
	protected final IOnClickListener clickListener;
	
	
	public Button(float x, float y, String caption, ElementContainer owner, IOnClickListener clickListener){
		super(x, y, getParentStyle(owner).buttonTexture.getSize().x, getParentStyle(owner).buttonTexture.getSize().y/3, owner);
		
		this.clickListener = clickListener;
		
		// Button Sprite generation and positioning
		sprite = new Sprite(getStyle().buttonTexture);
		sprite.setPosition(x, y);
		changeTextureClip(0);
		
		// Button inner text positioning and calibration
		this.caption = new Text(caption, getStyle().buttonFont, getStyle().buttonFontSize);
		FloatRect textRect = this.caption.getLocalBounds();
		this.caption.setOrigin(textRect.width / 2.f, textRect.height );
		this.caption.setPosition(x + getWidth() / 2, y + getHeight() / 2);
	}
	
	@Override
	protected void drawImpl(RenderTarget rt) {
		rt.draw(sprite);
		rt.draw(caption);
	}
	
	protected void changeTextureClip(int pos) {
		sprite.setTextureRect(new IntRect(0, (int) getHeight()*pos, (int) getWidth(), (int) getHeight()));
	}
	
	@Override
	protected void onMouseOver(Vector2f mp) {
		changeTextureClip(1);
	}
	@Override
	protected void onMouseOut() {
		changeTextureClip(0);
	}
	@Override
	protected void onUnFocus() {
		changeTextureClip(0);
	}
	@Override
	protected void onFocus(Vector2f mp) {
		changeTextureClip(1);
	}
	
	@Override
	protected void onKeyPressed(KeyType type) {
		if( type==KeyType.ENTER )
			changeTextureClip(2);
	}
	
	@Override
	protected void onKeyReleased(KeyType type) {
		if( type==KeyType.ENTER && clickListener!=null )
			clickListener.onClick();
	}

	@Override
	public void update(long frameTimeMs) {
	}
	
}