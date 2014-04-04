package de.secondsystem.game01.impl.gui;

import de.secondsystem.game01.model.Attributes;

public class AttributesDataTable extends DataTable {

	public interface AttributesSource {
		Attributes getAttributes();
	}

	public AttributesDataTable(float x, float y, float width, float height, AttributesSource attributesSource,
			ElementContainer owner) {
		super(x, y, width, height, owner);
	}
	
	public Attributes getModifiedAttributes() {
		return null; // TODO
	}
	
}
