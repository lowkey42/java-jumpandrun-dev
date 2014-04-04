package de.secondsystem.game01.impl.gui;

import java.util.List;

public class DataTable<T> extends LayoutElementContainer {

	private static final int COLUMN_SPACING = 0;
	private static final int ROW_SPACING = 50;
	
	public interface ColumnDef<T> {
		String getName();
		float getWidthPercentage();
		Element createValueElement(float width, T data, LayoutElementContainer row);
	}
	
	public DataTable(float x, float y, float width, ElementContainer owner, Iterable<T> rowData, List<ColumnDef<T>> columns) {
		super(x, y, width, 1, owner, new Layout(LayoutDirection.VERTICAL, ROW_SPACING));

		updateOffset(new HeadRow(getXOffset(), getYOffset(), width, columns));
		
		for( T row : rowData )
			updateOffset(new DataRow(getXOffset(), getYOffset(), width, row, columns));
		
		setDimensions(width, getYOffset());
	}

	//TODO: ganz viel code
	
	private final class HeadRow extends LayoutElementContainer {
		public HeadRow(float x, float y, float width, List<ColumnDef<T>> columns) {
			super(x, y, width, 1, DataTable.this, new Layout(LayoutDirection.HORIZONTAL, COLUMN_SPACING));
			
			final float widthLeft = width - COLUMN_SPACING*(columns.size()+1);
			float maxHeight = 1;
			
			for( ColumnDef<T> c : columns )
				maxHeight = Math.max(maxHeight, createLabel(c.getName(), widthLeft*c.getWidthPercentage(), 1).getHeight());
			
			setDimensions(width, maxHeight);
		}
	}
	
	private final class DataRow extends LayoutElementContainer {

		public DataRow(float x, float y, float width, T data, List<ColumnDef<T>> columns) {
			super(x, y, width, 50, DataTable.this, new Layout(LayoutDirection.HORIZONTAL, COLUMN_SPACING));
			
			final float widthLeft = width - COLUMN_SPACING*(columns.size()+1);
			float maxHeight = 1;
			
			for( ColumnDef<T> c : columns )
				maxHeight = Math.max(maxHeight, c.createValueElement( widthLeft*c.getWidthPercentage(), data, DataRow.this).getHeight());
			
			setDimensions(width, maxHeight);
		}
	}
}
