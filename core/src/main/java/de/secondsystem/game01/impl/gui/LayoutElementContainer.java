package de.secondsystem.game01.impl.gui;

import java.util.List;

import de.secondsystem.game01.impl.gui.AttributesDataTable.AttributesSource;
import de.secondsystem.game01.impl.gui.DataTable.ColumnDef;
import de.secondsystem.game01.impl.gui.listeners.IOnClickListener;

public class LayoutElementContainer extends ElementContainer {
	
	public static enum LayoutDirection {
		HORIZONTAL,
		VERTICAL
	}
	public static final class Layout {
		public final int spacing;
		public final LayoutDirection direction;
		public Layout(LayoutDirection direction, int spacing) {
			this.direction = direction;
			this.spacing = spacing;
		}
	}
	
	private final Layout layout;
	
	private float xOffset = 0;
	
	private float yOffset = 0;
	
	private float offset;
	
	private float storedOffset;

	public LayoutElementContainer(float x, float y, float width, float height, Layout layout) {
		super(x, y, width, height);
		this.layout = layout;
	}
	
	public LayoutElementContainer(float x, float y, float width, float height, Style style, Layout layout) {
		super(x, y, width, height, style);
		this.layout = layout;
	}

	public LayoutElementContainer(float x, float y, float width, float height, ElementContainer owner, Layout layout) {
		super(x, y, width, height, owner);
		this.layout = layout;
	}
	
	protected void setLayoutOffset(float x, float y) {
		xOffset=x;
		yOffset=y;
	}
	
	protected void storeOffset() {
		storedOffset = offset;
	}
	protected void restoreOffset() {
		offset = storedOffset;
	}
	
	public float getXOffset() {
		return xOffset+ (layout.direction==LayoutDirection.HORIZONTAL ? offset : 0);
	}
	public float getYOffset() {
		return yOffset + (layout.direction==LayoutDirection.VERTICAL ? offset : 0);
	}
	public <E extends Element> E updateOffset(E e) {
		offset+= layout.spacing + (layout.direction==LayoutDirection.HORIZONTAL ? e.getWidth() : e.getHeight());
		return e;
	}

	// factory-methods
	public final <T> DataTable<T> createDataTable(float width, Iterable<T> rowData, 
			List<ColumnDef<T>> columns) {
		return updateOffset( createDataTable(getXOffset(), getYOffset(), width, rowData, columns) );
	}
	public final AttributesDataTable createAttributesDataTable(float width, AttributesSource attributesSource) {
		return updateOffset( createAttributesDataTable(getXOffset(), getYOffset(), width, attributesSource) );
	}
	public final Panel createPanel(float width, float height) {
		return updateOffset( createPanel(getXOffset(), getYOffset(), width, height) );
	}
	public final Panel createPanel(float width, float height, Layout layout) {
		return updateOffset( createPanel(getXOffset(), getYOffset(), width, height, layout) );
	}
	public final Slider createSlider() {
		return updateOffset( createSlider(getXOffset(), getYOffset()) );
	}
	public final Label createLabel(String text, Element forElem) {
		return updateOffset( createLabel(getXOffset(), getYOffset(), text, forElem) );
	}
	public final Label createLabel(String text) {
		return updateOffset( createLabel(getXOffset(), getYOffset(), text) );
	}
	public final Label createLabel(String text, float width, float height, Element forElem) {
		return updateOffset( createLabel(getXOffset(), getYOffset(), text, width, height, forElem) );
	}
	public final Label createLabel(String text, float width, float height) {
		return updateOffset( createLabel(getXOffset(), getYOffset(), text, width, height) );
	}
	public final Button createButton(String caption, IOnClickListener clickListener) {
		return updateOffset( createButton(getXOffset(), getYOffset(), caption, clickListener) );
	}
	public final Edit createInputField(float width, String text) {
		return updateOffset( createInputField(getXOffset(), getYOffset(), width, text) );
	}
	public final Edit createInputField(float width, RwValueRef<String> text) {
		return updateOffset( createInputField(getXOffset(), getYOffset(), width, text) );
	}
	public final StringGrid createStringGrid(int rowCount, int colCount, float cellWidth, float cellHeight) {
		return updateOffset( createStringGrid(getXOffset(), getYOffset(), rowCount, colCount, cellWidth, cellHeight) );
	}
	public final CheckBox createCheckbox(RwValueRef<Boolean> state) {
		return updateOffset(createCheckbox(getXOffset(), getYOffset(), state));
	}
	public final <T extends Enum<T>> DropDownField<T> createDropDown(float width, Class<T> valueEnum, RwValueRef<T> value) {
		return updateOffset(createDropDown(getXOffset(), getYOffset(), width, valueEnum, value));
	}
	public final VScrollPanel createScrollPanel(float width, float height, Layout layout) {
		return updateOffset(createScrollPanel(getXOffset(), getYOffset(), width, height, layout));
	}
}
