package de.secondsystem.game01.impl.gui;

import java.util.ArrayList;

public class StringGrid extends Panel {

	private final ArrayList<ArrayList<Edit>> cells = new ArrayList<ArrayList<Edit>>();
	
	private int colCount;
	private int rowCount;
	private float cellWidth;
	private float cellHeight; // 44.f
	
	public StringGrid(float x, float y, int rowCount, int colCount, float cellWidth, float cellHeight, ElementContainer owner) {
		super(x, y, cellWidth*colCount, cellHeight*rowCount, owner);
		
		this.colCount   = colCount;
		this.cellWidth  = cellWidth;
		this.cellHeight = cellHeight;
		
		for(int i=0; i<rowCount; i++) {
			addRow();
		}
	}
	
	public void addRow() {
		ArrayList<Edit> row = new ArrayList<>();
		
		for(int i=0; i<colCount; i++) {
			row.add(new Edit(i*cellWidth, rowCount*cellHeight, cellWidth, "", this));
		}
		
		rowCount++;
	}
	
	public void addCol() {
		for(int i=0; i<rowCount; i++) {
			cells.get(i).add(new Edit(colCount*cellWidth, i*cellHeight, cellWidth, "", this));
		}
		
		colCount++;
	}
}
