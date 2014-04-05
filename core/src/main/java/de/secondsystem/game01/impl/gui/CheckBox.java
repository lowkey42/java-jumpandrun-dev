package de.secondsystem.game01.impl.gui;

import org.jsfml.graphics.RenderTarget;
import org.jsfml.system.Vector2f;

import de.secondsystem.game01.impl.graphic.AnimatedSprite;
import de.secondsystem.game01.model.IAnimated.AnimationType;

public class CheckBox extends ElementContainer {

	protected final AnimatedSprite sprite;
	
	protected final Label caption;
	
	protected boolean checked = false;
	
	public CheckBox(float x, float y, String caption, ElementContainer owner) {
		super(x, y, getParentStyle(owner).checkBoxTexture.getDefault().frameWidth, getParentStyle(owner).checkBoxTexture.getDefault().frameHeight, owner);
		
		sprite = new AnimatedSprite(getStyle().checkBoxTexture);
		
		this.caption = new Label(width -5, height/2.f -11, caption, this, null); 
	}

	@Override
	public void update(long frameTimeMs) {
		sprite.setPosition(Vector2f.add(getPosition(), new Vector2f(getWidth() / 2, getHeight() / 2)));
		
		super.update(frameTimeMs);
	}

	@Override
	protected void drawImpl(RenderTarget renderTarget) {
		sprite.draw(renderTarget);

		super.drawImpl(renderTarget);
	}
	
	@Override
	public void onKeyReleased(KeyType type) {
		if( type==KeyType.ENTER ) {
			checked = !checked;
			if( checked )
				sprite.play(AnimationType.CLICKED, 1, true);
			else
				sprite.play(AnimationType.IDLE, 1, true);
		}
		
		super.onKeyReleased(type);
	}

	public boolean checked() {
		return checked;
	}

	
}
