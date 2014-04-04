/**
 * 
 */
package de.secondsystem.game01.impl.gui;

import org.jsfml.graphics.FloatRect;
import org.jsfml.graphics.RenderTarget;
import org.jsfml.graphics.Text;
import org.jsfml.system.Vector2f;

import de.secondsystem.game01.impl.graphic.AnimatedSprite;
import de.secondsystem.game01.impl.gui.listeners.IOnClickListener;
import de.secondsystem.game01.model.IAnimated.AnimationType;

/**
 * @author Sebastian
 *
 */
public class Button extends Element {

	protected final AnimatedSprite sprite;
	
	protected final Text caption;
	
	protected final IOnClickListener clickListener;
	
	
	public Button(float x, float y, String caption, ElementContainer owner, IOnClickListener clickListener){
		super(x, y, getParentStyle(owner).buttonTexture.getDefault().frameWidth, getParentStyle(owner).buttonTexture.getDefault().frameHeight, owner);
		
		this.clickListener = clickListener;
		
		// Button Sprite generation and positioning
		sprite = new AnimatedSprite(getStyle().buttonTexture);
		sprite.setPosition(new Vector2f(x+getWidth() / 2, y + getHeight() / 2));
		
		// Button inner text positioning and calibration
		this.caption = new Text(caption, getStyle().buttonFont, getStyle().buttonFontSize);
		FloatRect textRect = this.caption.getLocalBounds();
		this.caption.setOrigin(textRect.width / 2.f, textRect.height );
		this.caption.setPosition(x + getWidth() / 2, y + getHeight() / 2);
	}
	
	@Override
	protected void drawImpl(RenderTarget rt) {
		sprite.draw(rt);
		rt.draw(caption);
	}
	
	@Override
	protected void onMouseOver(Vector2f mp) {
		sprite.play(AnimationType.MOUSE_OVER, 1, true);
	}
	@Override
	protected void onMouseOut() {
		sprite.play(AnimationType.IDLE, 1, true);
	}
	@Override
	protected void onUnFocus() {
		sprite.play(AnimationType.IDLE, 1, true);
	}
	@Override
	protected void onFocus(Vector2f mp) {
		sprite.play(AnimationType.MOUSE_OVER, 1, true);
	}
	
	@Override
	protected void onKeyPressed(KeyType type) {
		if( type==KeyType.ENTER )
			sprite.play(AnimationType.CLICKED, 1, true);
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
