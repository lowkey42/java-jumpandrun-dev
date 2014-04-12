package de.secondsystem.game01.impl.editor;

import java.util.List;

import org.jsfml.graphics.Color;

import de.secondsystem.game01.impl.editor.curser.CurserManager.ISelectionChangedListener;
import de.secondsystem.game01.impl.editor.curser.CurserManager;
import de.secondsystem.game01.impl.editor.curser.IEditorCurser;
import de.secondsystem.game01.impl.gui.AttributesDataTable;
import de.secondsystem.game01.impl.gui.AttributesDataTable.AttributesSource;
import de.secondsystem.game01.impl.gui.ElementContainer;
import de.secondsystem.game01.impl.gui.Panel;
import de.secondsystem.game01.impl.gui.ThumbnailButton;
import de.secondsystem.game01.impl.gui.ThumbnailButton.ThumbnailData;
import de.secondsystem.game01.impl.gui.listeners.IOnClickListener;
import de.secondsystem.game01.impl.gui.VScrollPanel;
import de.secondsystem.game01.model.Attributes;

public class ObjectPanel extends Panel implements AttributesSource, ISelectionChangedListener {

	private static final int TABLE_WIDTH = 440;
	
	private static final int SPACING = 5;

	public static final int WIDTH = TABLE_WIDTH+SPACING*2;
	
	private CurserManager curserManager;
	
	private IEditorCurser curser;
	
	private final VScrollPanel objectSelection;
	
	private final AttributesDataTable attributeTable;
	
	public ObjectPanel(float x, float y, float height, ElementContainer owner, CurserManager curserManager) {
		super(x, y, WIDTH, height, new Layout(LayoutDirection.VERTICAL, SPACING), owner);
		setLayoutOffset(SPACING, SPACING);
		setFillColor(new Color(0, 0, 0, 200));
		
		this.curserManager = curserManager;
		
		objectSelection = createScrollPanel(TABLE_WIDTH, TABLE_WIDTH/2, new Layout(10, TABLE_WIDTH-100));
		attributeTable = createScrollPanel(TABLE_WIDTH, height-TABLE_WIDTH/2, new Layout(LayoutDirection.VERTICAL, 0))
				.createAttributesDataTable(TABLE_WIDTH-VScrollPanel.WIDTH, this);
		attributeTable.setVisible(false);
		
		curserManager.addListerner(this);
	}
	
	private void generateThumbnails() {
		objectSelection.clear();
		
		List<ThumbnailButton.ThumbnailData> td = curserManager.generateBrushThumbnail();
		for( int i=0; i<td.size(); ++i  ) {
			ThumbnailData t = td.get(i);
			final int index = i;
			objectSelection.updateOffset( new ThumbnailButton(objectSelection.getXOffset(), objectSelection.getYOffset(), 100, 100, t, objectSelection, 
					new IOnClickListener() {
						@Override public void onClick() {
							curserManager.setBrush(index);
						}
			}) );
		}
	}
	
	@Override
	public void onSelectionChanged(IEditorCurser newSelection) {
		if( curser!=newSelection )
			generateThumbnails();
		
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
