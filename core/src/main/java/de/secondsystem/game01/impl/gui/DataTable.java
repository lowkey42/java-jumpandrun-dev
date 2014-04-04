package de.secondsystem.game01.impl.gui;

import java.util.List;

public class DataTable<T> extends ElementContainer {

	public interface ColumnDef<T> {
		String getName();
		float getWidthPercentage();
		Element createValueElement(T data);
	}
	
	public DataTable(float x, float y, float width, ElementContainer owner, Iterable<T> data, List<ColumnDef<T>> columns) {
		super(x, y, width, 1, owner);
	}

	//TODO: ganz viel code
	
	private static final class Row extends ElementContainer {

		public Row(float x, float y, Element... elements) {
			super(x, y, 1, 1);
			// TODO
		}
		
	}
}
