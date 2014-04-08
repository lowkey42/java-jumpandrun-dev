package de.secondsystem.game01.impl.gui;

import java.util.ArrayList;
import java.util.List;

import org.jsfml.graphics.Color;
import org.jsfml.system.Vector2f;

public class DataTable<T> extends LayoutElementContainer {

	protected static final int COLUMN_PADDING = 0;
	protected static final int ROW_SPACING = 0;
	protected static final int ROW_PADDING = 0;
	
	public interface ColumnDef<T> {
		String getName();
		float getWidthPercentage();
		Element createValueElement(float width, T data, LayoutElementContainer row);
	}
	public static abstract class AbstractColumnDef<T> implements ColumnDef<T> {
		private final String name;
		private final float widthPercentage;
		public AbstractColumnDef(String name, float widthPercentage) {
			this.name = name;
			this.widthPercentage = widthPercentage;
		}
		
		public String getName() {
			return name;
		}
		public float getWidthPercentage() {
			return widthPercentage;
		}
	}
	
	private List<ColumnDef<T>> columnDefs;
	
	private final List<DataRow> dataRows = new ArrayList<>();

	protected DataTable(float x, float y, float width, ElementContainer owner) {
		super(x, y, width, 1, owner, new Layout(LayoutDirection.VERTICAL, ROW_SPACING));
	}
	public DataTable(float x, float y, float width, ElementContainer owner, Iterable<T> rowData, List<ColumnDef<T>> columns) {
		super(x, y, width, 1, owner, new Layout(LayoutDirection.VERTICAL, ROW_SPACING));
		init(rowData, columns);
	}
	protected void init(Iterable<T> rowData, List<ColumnDef<T>> columns) {
		columnDefs = columns;

		updateOffset(new HeadRow(getXOffset(), getYOffset(), width, columns));
		
		storeOffset();
		
		for( T row : rowData )
			addRow(row);
	}
	protected void recreateDataRows(Iterable<T> rowData) {
		for( DataRow d : dataRows )
			removeElement(d);
		
		dataRows.clear();
			
		restoreOffset();
		
		for( T row : rowData )
			addRow(row);
	}
	
	public LayoutElementContainer addRow( T rowData ) {
		final DataRow row;
		dataRows.add(row = updateOffset(new DataRow(getXOffset(), getYOffset(), getWidth(), rowData, columnDefs)));
		
		setDimensions(width, getYOffset());
		return row;
	}
	public boolean deleteRow(LayoutElementContainer row) {
		int index = dataRows.indexOf(row);
		if( index<0 )
			return false;
		
		dataRows.remove(index);
		removeElement(row);
		
		for( int i=index; i<dataRows.size(); ++i ) {
			dataRows.get(i).setPosition(Vector2f.sub(Vector2f.sub(dataRows.get(i).getPosition(),getPosition()), new Vector2f(0, row.height+ROW_SPACING)));
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
		Color c = getRowBackgroundColorBase(data);
		return dataRows.size()%2==0 ? c : new Color(c.r+50, c.g+50, c.b+50);
	}
	protected Color getRowBackgroundColorBase(T data) {
		return Color.BLACK;
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
