package de.secondsystem.game01.impl.gui;

import org.jsfml.graphics.RenderTarget;
import org.jsfml.system.Vector2f;

import de.secondsystem.game01.impl.graphic.AnimatedSprite;
import de.secondsystem.game01.model.IAnimated.AnimationType;

public class CheckBox extends ElementContainer {

	protected final AnimatedSprite sprite;
	
	protected final Label caption;
	
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
		
		this.caption = new Label(width -5, 0, caption, this, null); 
		// TODO: eventuell nicht die günstigste Lösung, das Label als Unterelement der Checkbox zu behandeln
		//  - weniger flexibel (eine Checkbox muss immer ein Label an der selben Position haben?)
		//  - labeledElem-Funktionalität funktioniert so nicht mehr, d.h. ein Klick auf das Label ändert nicht die Checkbox
		//  - Alignment innerhalb der Layouts ist damit nur unnötig kompliziert (z.b. links Labels und rechts checkboxen, buttons, slider, etc.)
		//
		// d.h. ich würde das eher, wie bei den anderen Elementen, komplett rauslassen und wenn nötig außerhalb der Checkbox erstellen.
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
