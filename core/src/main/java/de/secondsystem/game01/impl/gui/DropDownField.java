package de.secondsystem.game01.impl.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.RectangleShape;
import org.jsfml.graphics.RenderTarget;
import org.jsfml.graphics.Text;
import org.jsfml.system.Vector2f;

import de.secondsystem.game01.model.IDrawable;
import de.secondsystem.game01.model.IInsideCheck;
import de.secondsystem.game01.util.Tools;

public class DropDownField<T extends Enum<T>> extends Edit {
	
	private final RwValueRef<T> valueRef;
	
	private final List<Suggestion> suggestions;
	
	private boolean active = false;

	public DropDownField(float x, float y, float width, Class<T> valueEnum, RwValueRef<T> value, ElementContainer owner) {
		super(x, y, width, new ToStringValueRef<>(valueEnum, value), owner);
		valueRef = value;
		text.setColor(Color.GREEN);
		
		suggestions = Collections.unmodifiableList(createSuggestions(valueEnum.getEnumConstants()));
	}
	
	private List<Suggestion> createSuggestions(@SuppressWarnings("unchecked") T... options) {
		List<Suggestion> s = new ArrayList<>(options.length);
		
		int index = 0;
		for( T o : options )
			s.add(new Suggestion(getHeight() + (index++)*getHeight()/2, o, getWidth(), getHeight()));
		
		return s;
	}
	
	@Override
	protected void onFocus(Vector2f mp) {
		active = true;
		super.onFocus(mp);
		for( Suggestion s : suggestions )
			if( s.inside(mp) ) {
				setText(s.value.name());
				break;
			}
	}
	
	@Override
	protected void onUnFocus() {
		active = false;
		super.onUnFocus();
	}
	
	@Override
	public boolean inside(Vector2f point) {
		return point.x>=getPosition().x && point.x<=getPosition().x+width 
				&& point.y>=getPosition().y && point.y<=getPosition().y+height+(active ? suggestions.size()*height/2 : 0);
	}
	
	@Override
	protected void updateText(boolean modified) {
		super.updateText(modified);
		
		if( !active )
			text.setColor(Color.WHITE);
		else if( valueRef.getValue()!=null && valueRef.getValue().toString().equals(textRef.getValue()) )
			text.setColor(Color.GREEN);
		else
			text.setColor(Color.RED);
	}
	
	@Override
	public void update(long frameTimeMs) {
		super.update(frameTimeMs);
	}

	@Override
	protected void drawImpl(RenderTarget renderTarget) {
		super.drawImpl(renderTarget);
	}
	
	@Override
	protected void drawOverlaysImpl(RenderTarget renderTarget) {
		if( active )
			for( Suggestion s : suggestions )
				s.draw(renderTarget);
	}

	private static final class ToStringValueRef<T extends Enum<T>> implements RwValueRef<String> {
		private final Class<T> valueEnum;
		private final RwValueRef<T> ref;
		private T prevValue;
		private String strVal = "";
		public ToStringValueRef(Class<T> valueEnum, RwValueRef<T> ref) {
			this.valueEnum = valueEnum;
			this.ref = ref;
		}
		@Override
		public String getValue() {
			if( prevValue!=ref.getValue() )
				return (prevValue=ref.getValue()).name();
			
			return strVal;
		}
		@Override
		public void setValue(String value) {
			strVal = value;
			value = value.replace(' ', '_');
			T val;
			try {
				val = Enum.valueOf(valueEnum, value);
				
			} catch( IllegalArgumentException e ) {
				try {
					val = Enum.valueOf(valueEnum, value.toUpperCase(Locale.ENGLISH));
				} catch( IllegalArgumentException e1 ) {
					return;
				}
			}
			
			if( val!=null ) {
				ref.setValue(val);
				strVal = val.name();
			}
		}
	}
	
	private final class Suggestion implements IInsideCheck, IDrawable {
		private final Text text;
		private final RectangleShape box;
		private final float yPos;
		public final T value;
		public Suggestion(float yPos, T value, float width, float height) {
			this.yPos = yPos;
			this.value = value;
			
			text = new Text(value.name(), getStyle().textFont, getStyle().dropDownSuggestionsFontSize);
			text.setOrigin(0, this.text.getLocalBounds().height / 2);
			
			box = new RectangleShape(new Vector2f(width, height/2));
			box.setFillColor(new Color(150, 150, 150, 100));
			box.setOutlineColor(Color.WHITE);
			box.setOutlineThickness(1);
		}
		
		@Override
		public boolean inside(Vector2f point) {
			return Tools.isInside(box, point);
		}

		@Override
		public void draw(RenderTarget rt) {
			box.setPosition(Vector2f.add(new Vector2f(0,yPos), getPosition()));
			text.setPosition(Vector2f.add(new Vector2f(0,yPos+box.getSize().y/2), getPosition()));
			
			rt.draw(box);
			rt.draw(text);
		}
	}
	
}
