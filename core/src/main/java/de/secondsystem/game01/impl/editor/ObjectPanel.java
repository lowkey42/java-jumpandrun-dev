package de.secondsystem.game01.impl.editor;

import org.jsfml.graphics.Color;

import de.secondsystem.game01.impl.editor.curser.CurserManager.ISelectionChangedListener;
import de.secondsystem.game01.impl.editor.curser.IEditorCurser;
import de.secondsystem.game01.impl.gui.AttributesDataTable;
import de.secondsystem.game01.impl.gui.AttributesDataTable.AttributesSource;
import de.secondsystem.game01.impl.gui.ElementContainer;
import de.secondsystem.game01.impl.gui.Panel;
import de.secondsystem.game01.model.Attributes;

public class ObjectPanel extends Panel implements AttributesSource, ISelectionChangedListener {

	private static final int TABLE_WIDTH = 440;
	
	private static final int SPACING = 5;

	public static final int WIDTH = TABLE_WIDTH+SPACING*2;
	
	private IEditorCurser curser;
	
	@SuppressWarnings("unused")
	private final Panel objectSelection;	// TODO: Brush-Selections
	
	private final AttributesDataTable attributeTable;
	
	public ObjectPanel(float x, float y, float height, ElementContainer owner) {
		super(x, y, WIDTH, height, new Layout(LayoutDirection.VERTICAL, SPACING), owner);
		setLayoutOffset(SPACING, SPACING);
		setFillColor(new Color(0, 0, 0, 200));
		
		objectSelection = createPanel(TABLE_WIDTH, TABLE_WIDTH/2);
		attributeTable = createAttributesDataTable(TABLE_WIDTH, this);
		attributeTable.setVisible(false);
	}
	
	@Override
	public void onSelectionChanged(IEditorCurser newSelection) {
		curser = newSelection;
		if( curser!=null )
			attributeTable.reset();
		
		attributeTable.setVisible(curser!=null);
	}

	@Override
	public Attributes getAttributes() {
		return curser!=null ? curser.getAttributes() : new Attributes();
	}

	@Override
	public void applyAttributes(Attributes newAttributes) {
		if( curser!=null )
			curser.setAttributes(newAttributes);
	}
}
