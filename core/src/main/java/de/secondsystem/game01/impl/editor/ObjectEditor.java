package de.secondsystem.game01.impl.editor;

import de.secondsystem.game01.impl.gui.AttributesDataTable;
import de.secondsystem.game01.impl.gui.AttributesDataTable.AttributesSource;
import de.secondsystem.game01.impl.gui.ElementContainer;
import de.secondsystem.game01.impl.gui.Panel;

public class ObjectEditor extends ElementContainer {

	public static final float WIDTH = 450.f;
	
	private Panel panel;
	private AttributesDataTable attributeTable;
	
	public ObjectEditor(float x, float y, float width, float height, ElementContainer owner) {
		super(x, y, width, height, owner);
		
		panel = new Panel(width-WIDTH, 0, WIDTH, height, this);
	}
	
	public void onObjectSelection(AttributesSource attributesSource) {	
		panel.removeElement(attributeTable);
		attributeTable = new AttributesDataTable(0, 0, WIDTH-5, attributesSource, panel);
	}

}
