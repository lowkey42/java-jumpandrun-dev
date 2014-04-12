package de.secondsystem.game01.impl.gui;

import org.jsfml.graphics.RenderTarget;
import org.jsfml.system.Vector2f;

import de.secondsystem.game01.impl.graphic.AnimatedSprite;
import de.secondsystem.game01.impl.graphic.AnimationTexture;
import de.secondsystem.game01.model.IAnimated.AnimationType;

public class CheckBox extends Element {

	protected final AnimatedSprite sprite;
	
	protected RwValueRef<Boolean> checked;

	public CheckBox(float x, float y, ElementContainer owner) {
		this(x, y, new SimpleRwValueRef<>(false), owner);
	}
	public CheckBox(float x, float y, boolean checked, ElementContainer owner) {
		this(x, y, new SimpleRwValueRef<>(checked), owner);
	}
	public CheckBox(float x, float y, RwValueRef<Boolean> checked, ElementContainer owner) {
		this(x, y, checked, getParentStyle(owner).checkBoxTexture, owner);
	}
	protected CheckBox(float x, float y, RwValueRef<Boolean> checked, AnimationTexture tex, ElementContainer owner) {
		super(x, y, tex.getDefault().frameWidth, tex.getDefault().frameHeight, owner);
		this.checked = checked;
		
		sprite = new AnimatedSprite(tex);
		if( checked.getValue() )
			sprite.play(AnimationType.CLICKED, 1, true);
		else
			sprite.play(AnimationType.IDLE, 1, true);
	}

	@Override
	public void update(long frameTimeMs) {
		sprite.setPosition(Vector2f.add(getPosition(), new Vector2f(getWidth() / 2, getHeight() / 2)));
		if( checked.getValue() )
			sprite.play(AnimationType.CLICKED, 1, true);
		else
			sprite.play(AnimationType.IDLE, 1, true);
	}

	@Override
	protected void drawImpl(RenderTarget renderTarget) {
		sprite.draw(renderTarget);
	}
	
	@Override
	public void onKeyReleased(KeyType type) {
		if( type==KeyType.ENTER )
			checked.setValue(!checked.getValue());
	}

	public boolean checked() {
		return checked.getValue();
	}
	
}
