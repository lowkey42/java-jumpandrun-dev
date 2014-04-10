package de.secondsystem.game01.impl.editor;

import org.jsfml.graphics.Color;

import de.secondsystem.game01.impl.editor.CurserManager.ISelectionChangedListener;
import de.secondsystem.game01.impl.gui.AttributesDataTable;
import de.secondsystem.game01.impl.gui.AttributesDataTable.AttributesSource;
import de.secondsystem.game01.impl.gui.ElementContainer;
import de.secondsystem.game01.impl.gui.LayoutElementContainer;
import de.secondsystem.game01.impl.gui.Panel;
import de.secondsystem.game01.impl.map.IGameMap;
import de.secondsystem.game01.impl.map.ILayerObject;
import de.secondsystem.game01.model.Attributes;

public class ObjectPanel extends Panel implements AttributesSource, ISelectionChangedListener {

	private static final int TABLE_WIDTH = 440;
	
	private static final int SPACING = 5;

	public static final int WIDTH = TABLE_WIDTH+SPACING*2;
	
	private IEditorCurser curser;
	
	private final IMapProvider mapProvider;
	
	private final Panel objectSelection;
	
	private final AttributesDataTable attributeTable;
	
	public ObjectPanel(float x, float y, float height, ElementContainer owner, IMapProvider mapProvider) {
		super(x, y, WIDTH, height, new Layout(LayoutDirection.VERTICAL, SPACING), owner);
		this.mapProvider = mapProvider;
		setLayoutOffset(SPACING, SPACING);
		setFillColor(new Color(0, 0, 0, 200));
		
		objectSelection = createPanel(TABLE_WIDTH, TABLE_WIDTH);
		attributeTable = createAttributesDataTable(TABLE_WIDTH, this);
	}
	
	@Override
	public void onSelectionChanged(IEditorCurser newSelection) {
		curser = newSelection;
		attributeTable.reset();
	}

	@Override
	public Attributes getAttributes() {
		return curser.getAttributes();
	}

	@Override
	public void applyAttributes(Attributes newAttributes) {
		curser.setAttributes(newAttributes);
	}

	public void scrollObjectTemplates(boolean up) {
		// TODO
	}
	
}
