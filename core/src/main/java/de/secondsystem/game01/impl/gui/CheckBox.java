package de.secondsystem.game01.impl.gui;

import org.jsfml.graphics.RenderTarget;
import org.jsfml.system.Vector2f;

import de.secondsystem.game01.impl.graphic.AnimatedSprite;
import de.secondsystem.game01.model.IAnimated.AnimationType;

public class CheckBox extends Element {

	protected final AnimatedSprite sprite;
	
	protected RwValueRef<Boolean> checked;

	public CheckBox(float x, float y, String caption, ElementContainer owner) {
		this(x, y, new SimpleRwValueRef<>(false), caption, owner);
	}
	public CheckBox(float x, float y, boolean checked, String caption, ElementContainer owner) {
		this(x, y, new SimpleRwValueRef<>(checked), caption, owner);
	}
	public CheckBox(float x, float y, RwValueRef<Boolean> checked, String caption, ElementContainer owner) {
		super(x, y, getParentStyle(owner).checkBoxTexture.getDefault().frameWidth, getParentStyle(owner).checkBoxTexture.getDefault().frameHeight, owner);
		this.checked = checked;
		
		sprite = new AnimatedSprite(getStyle().checkBoxTexture);
		if( checked.getValue() )
			sprite.play(AnimationType.CLICKED, 1, true);
		else
			sprite.play(AnimationType.IDLE, 1, true);
	}

	@Override
	public void update(long frameTimeMs) {
		sprite.setPosition(Vector2f.add(getPosition(), new Vector2f(getWidth() / 2, getHeight() / 2)));
	}

	@Override
	protected void drawImpl(RenderTarget renderTarget) {
		sprite.draw(renderTarget);
	}
	
	@Override
	public void onKeyReleased(KeyType type) {
		if( type==KeyType.ENTER ) {
			checked.setValue(!checked.getValue());
			if( checked.getValue() )
				sprite.play(AnimationType.CLICKED, 1, true);
			else
				sprite.play(AnimationType.IDLE, 1, true);
		}
		
		super.onKeyReleased(type);
	}

	public boolean checked() {
		return checked.getValue();
	}

	
}
