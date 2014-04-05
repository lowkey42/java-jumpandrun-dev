package de.secondsystem.game01.impl.gui;

import java.util.ArrayList;
import java.util.List;

import org.jsfml.graphics.Color;
import org.jsfml.system.Vector2f;

public class DataTable<T> extends LayoutElementContainer {

	private static final int COLUMN_PADDING = 0;
	private static final int ROW_SPACING = 0;
	private static final int ROW_PADDING = 0;
	
	public interface ColumnDef<T> {
		String getName();
		float getWidthPercentage();
		Element createValueElement(float width, T data, LayoutElementContainer row);
	}
	
	private final List<ColumnDef<T>> columnDefs;
	
	private final List<DataRow> dataRows = new ArrayList<>();
	
	public DataTable(float x, float y, float width, ElementContainer owner, Iterable<T> rowData, List<ColumnDef<T>> columns) {
		super(x, y, width, 1, owner, new Layout(LayoutDirection.VERTICAL, ROW_SPACING));
		columnDefs = columns;

		updateOffset(new HeadRow(getXOffset(), getYOffset(), width, columns));
		
		for( T row : rowData )
			dataRows.add(updateOffset(new DataRow(getXOffset(), getYOffset(), width, row, columns)));
		
		setDimensions(width, getYOffset());
	}
	
	public LayoutElementContainer addRow( T rowData ) {
		final DataRow row;
		dataRows.add(row = updateOffset(new DataRow(getXOffset(), getYOffset(), getWidth(), rowData, columnDefs)));
		return row;
	}
	public boolean deleteRow(LayoutElementContainer row) {
		int index = dataRows.indexOf(row);
		if( index<0 )
			return false;
		
		dataRows.remove(index);
		removeElement(row);
		
		for( int i=index; i<dataRows.size(); ++i ) {
			dataRows.get(i).setPosition(Vector2f.sub(dataRows.get(i).getPosition(), new Vector2f(0, row.height+ROW_SPACING)));
			dataRows.get(i).setFillColor(getRowBackgroundColor(dataRows.get(i).data));
			dataRows.get(i).setOutlineColor(getRowOutlineColor(dataRows.get(i).data));
		}
		
		return true;
	}

	//TODO: ganz viel code

	protected Color getHeadOutlineColor() {
		return Color.WHITE;
	}
	protected Color getHeadBackgroundColor() {
		return Color.BLACK;
	}
	
	protected Color getRowOutlineColor(T data) {
		return Color.WHITE;
	}
	protected Color getRowBackgroundColor(T data) {
		return dataRows.size()%2==0 ? Color.BLACK : new Color(50, 50, 50);
	}
	
	private final class HeadRow extends Panel {
		public HeadRow(float x, float y, float width, List<ColumnDef<T>> columns) {
			super(x, y, width, 1, new Layout(LayoutDirection.HORIZONTAL, COLUMN_PADDING), DataTable.this);
			setFillColor(getHeadBackgroundColor());
			setOutlineColor(getHeadOutlineColor());
			
			final float widthLeft = width - COLUMN_PADDING*(columns.size()+1);
			float maxHeight = 1;
			
			for( ColumnDef<T> c : columns )
				maxHeight = Math.max(maxHeight, createLabel(c.getName(), widthLeft*c.getWidthPercentage(), 1).getHeight());
			
			setDimensions(width, maxHeight+ROW_PADDING);
		}
	}
	
	private final class DataRow extends Panel {
		final T data;

		public DataRow(float x, float y, float width, T data, List<ColumnDef<T>> columns) {
			super(x, y, width, 50, new Layout(LayoutDirection.HORIZONTAL, COLUMN_PADDING), DataTable.this);
			setFillColor(getRowBackgroundColor(data));
			setOutlineColor(getRowOutlineColor(data));
			this.data = data;
			
			final float widthLeft = width - COLUMN_PADDING*(columns.size()+1);
			float maxHeight = 1;
			
			for( ColumnDef<T> c : columns )
				maxHeight = Math.max(maxHeight, c.createValueElement( widthLeft*c.getWidthPercentage(), data, DataRow.this).getHeight());
			
			setDimensions(width, maxHeight+ROW_PADDING);
		}
	}
}
