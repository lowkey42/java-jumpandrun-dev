package de.secondsystem.game01.impl.gui;

import java.util.ArrayList;
import java.util.List;

import org.jsfml.graphics.RenderTarget;
import org.jsfml.system.Vector2f;

import de.secondsystem.game01.impl.gui.AttributesDataTable.AttributesSource;
import de.secondsystem.game01.impl.gui.DataTable.ColumnDef;
import de.secondsystem.game01.impl.gui.LayoutElementContainer.Layout;
import de.secondsystem.game01.impl.gui.listeners.IOnClickListener;

public class ElementContainer extends Element {
	
	private final List<Element> children = new ArrayList<>();

	private Element mouseOver;
	
	private Element focus;
	
	private final Style style;
	
	public ElementContainer(float x, float y, float width, float height) {
		super(x, y, width, height, null);
		this.style = null;
	}
	
	public ElementContainer(float x, float y, float width, float height, Style style) {
		super(x, y, width, height, null);
		this.style = style;
	}

	public ElementContainer(float x, float y, float width, float height, ElementContainer owner) {
		super(x, y, width, height, owner);
		this.style = null;
	}
	
	@Override
	protected Style getStyle() {
		return style!=null ? style : super.getStyle();
	}

	void addElement( Element element ) {
		children.add(element);
	}
	void removeElement( Element element ) {
		children.remove(element);
	}
	
	protected Element getByPos(Vector2f mp) {
		for( Element c : children ) {
			if( c.inside(mp) ) {
				return c;
			}
		}
		
		return getStyle().autoFocus && !children.isEmpty() ? children.get(0) : null;
	}
	
	@Override
	public void onMouseOver(Vector2f mp) {
		Element e = getByPos(mp);
		
		if( mouseOver!=null && e!=mouseOver )
			mouseOver.onMouseOut();
		
		mouseOver = e;
		if( mouseOver!=null )
			mouseOver.onMouseOver(mp);
	}
	
	@Override
	protected void onMouseOut() {
		if( mouseOver!=null )
			mouseOver.onMouseOut();
	}
	
	@Override
	public void onFocus(Vector2f mp) {
		if( overlays!=null )
			for( Overlay o : overlays )
				if( o.inside(mp) ) {
					o.onFocus(mp);
					return;
				}
		
		Element e = getByPos(mp);

		if( focus!=null && e!=focus )
			focus.onUnFocus();
		
		focus = e;
		if( focus!=null )
			focus.onFocus(mp);
	}
	
	@Override
	protected void onUnFocus() {
		if( focus!=null )
			focus.onUnFocus();
	}
	
	@Override
	public void onKeyPressed(KeyType type) {
		if( focus!=null )
			focus.onKeyPressed(type);
	}
	@Override
	public void onKeyReleased(KeyType type) {
		if( focus!=null )
			focus.onKeyReleased(type);
	}
	@Override
	protected void onTextInput(int character) {
		if( focus!=null )
			focus.onTextInput(character);
	}
	
	@Override
	protected void drawImpl(RenderTarget renderTarget) {
		for( Element c : children )
			c.draw(renderTarget);
	}

	@Override
	public void update(long frameTimeMs) {
		for( Element c : children )
			c.update(frameTimeMs);
	}


	public final <T> DataTable<T> createDataTable(float x, float y, float width, Iterable<T> rowData, 
			List<ColumnDef<T>> columns) {
		return new DataTable<>(x, y, width, this, rowData, columns);
	}
	public final AttributesDataTable createAttributesDataTable(float x, float y, float width, AttributesSource attributesSource) {
		return new AttributesDataTable(x, y, width, attributesSource, this);
	}
	public final Panel createPanel(float x, float y, float width, float height) {
		return new Panel(x, y, width, height, this);
	}
	public final Panel createPanel(float x, float y, float width, float height, Layout layout) {
		return new Panel(x, y, width, height, layout, this);
	}
	public final Slider createSlider(float x, float y) {
		return new Slider(x, y, this);
	}
	public final Label createLabel(float x, float y, String text, Element forElem) {
		return new Label(x, y, text, this, forElem);
	}
	public final Label createLabel(float x, float y, String text) {
		return new Label(x, y, text, this);
	}
	public final Label createLabel(float x, float y, String text, float width, float height, Element forElem) {
		return new Label(x, y, text, width, height, this, forElem);
	}
	public final Label createLabel(float x, float y, String text, float width, float height) {
		return new Label(x, y, text, width, height, this);
	}
	public final Button createButton(float x, float y, String caption, IOnClickListener clickListener) {
		return new Button(x, y, caption, this, clickListener);
	}
	public final Edit createInputField(float x, float y, float width, String text) {
		return new Edit(x, y, width, text, this);
	}
	public final Edit createInputField(float x, float y, float width, RwValueRef<String> text) {
		return new Edit(x, y, width, text, this);
	}
	public final StringGrid createStringGrid(float x, float y, int rowCount, int colCount, float cellWidth, float cellHeight) {
		return new StringGrid(x, y, rowCount, colCount, cellWidth, cellHeight, this);
	}
	public final CheckBox createCheckbox(float x, float y, RwValueRef<Boolean> state) {
		return new CheckBox(x, y, state, "", this);
	}
	public final <T extends Enum<T>> DropDownField<T> createDropDown(float x, float y, float width, Class<T> valueEnum, RwValueRef<T> value) {
		return new DropDownField<>(x, y, width, valueEnum, value, this);
	}
	
}
