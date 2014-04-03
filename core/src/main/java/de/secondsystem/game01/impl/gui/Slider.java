package de.secondsystem.game01.impl.gui;

import org.jsfml.graphics.IntRect;
import org.jsfml.graphics.RenderTarget;
import org.jsfml.graphics.Sprite;
import org.jsfml.system.Vector2f;
	
	/**
	 * This class provides a slideable button with values between 0 and 100
	 * @author Sebastian
	 * 
	 */
public final class Slider extends Element {

	private static final int BORDER = 5;
	
	private static final float STEP_SIZE = 0.01f / 500;
	
	private float value = 0;
	
	private boolean active;
	
	private final Sprite foregroundSprite;
	
	private final Sprite backgroundSprite;
	
	
	public Slider(float x, float y, ElementContainer owner) {
		super(x, y, getParentStyle(owner).sliderTexture.getSize().x, getParentStyle(owner).sliderTexture.getSize().y, owner);
	
		foregroundSprite = new Sprite(getParentStyle(owner).sliderTexture);
		backgroundSprite = new Sprite(getParentStyle(owner).sliderTexture);
		foregroundSprite.setPosition(x, y);
		backgroundSprite.setPosition(x, y);
		updateTextureClip();
	}
	
	public float getValue() {
		return value;
	}
	public void setValue(float value) {
		this.value = Math.max(0,Math.min(1, value));
		updateTextureClip();
	}
	
	@Override
	protected void onMouseOver(Vector2f mp) {
		if( active ) {
			value = Math.max(0,Math.min(1, (mp.x-getPosition().x+BORDER) / (getWidth()-BORDER*2)));
			updateTextureClip();
			active = false;
		}
	}
	
	@Override
	protected void onKeyPressed(KeyType type) {
		switch (type) {
			case ENTER:
				active = true;
				break;

			case LEFT:
				value-=STEP_SIZE;
				
			case RIGHT:
				value+=STEP_SIZE;
				
			default:
				break;
		}
	}
	
	private void updateTextureClip() {
		foregroundSprite.setTextureRect(new IntRect(0, (int)getHeight()	, (int) getWidth(), 							(int)getHeight()));
		backgroundSprite.setTextureRect(new IntRect(0, 0          		, (int)((getWidth()-BORDER) * value)+BORDER, 	(int)getHeight()));
	}


	@Override
	public void update(long frameTimeMs) {
	}


	@Override
	protected void drawImpl(RenderTarget rt) {
		rt.draw(backgroundSprite);
		rt.draw(foregroundSprite);
	}
}