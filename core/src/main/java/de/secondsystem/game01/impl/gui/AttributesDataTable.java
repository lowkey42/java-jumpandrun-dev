package de.secondsystem.game01.impl.gui;

import java.io.IOException;
import java.util.Arrays;

import org.jsfml.graphics.Color;
import org.jsfml.system.Vector2f;

import de.secondsystem.game01.impl.ResourceManager;
import de.secondsystem.game01.impl.game.entities.events.EventType;
import de.secondsystem.game01.impl.gui.AttributeDataCollection.AttributeVal;
import de.secondsystem.game01.impl.gui.AttributeDataCollection.ColumnType;
import de.secondsystem.game01.impl.gui.AttributeDataCollection.IRedrawable;
import de.secondsystem.game01.impl.gui.listeners.IOnClickListener;
import de.secondsystem.game01.model.Attributes;
import de.secondsystem.game01.model.GameException;

/**
 * 
 * @author lowkey
 * _________________________________________
 * | Key       | Type | Value      |Del|
 * |-----------|------|------------|---|
 * | x         | ↓NUM | 56.78      | X |
 * | y         | ↓NUM | -799.88    | X |
 * | onTOUCHED | ↓OBJ |            | X |
 * | →factory  | ↓STR | .PPEHF     | X |
 * | →out      | ↓STR | UNLOCKED   | X |
 * | →         | ↓STR |            |   |
 * | onUSED    | ↓SEQ |            | X |
 * | →1        | ↓OBJ |            | X |
 * |  →factory | ↓STR | .PPEHF     | X |
 * |  →out     | ↓STR | UNLOCKED   | X |
 * |  →        | ↓STR |            |   |
 * | →2        | ↓OBJ |            | X |
 * |  →factory | ↓STR | .ScriptEHF | X |
 * |  →body    | ↓STR | alert('Y') | X |
 * |  →        | ↓STR |            |   |
 * | →         | ↓STR |            |   |
 * |           | ↓STR |            |   |
 * |-----------------------------------|
 * | APPLY              RESET          |
 * -----------------------------------------
 * 
 */
public final class AttributesDataTable extends DataTable<AttributeVal> implements IRedrawable {

	public interface AttributesSource {
		Attributes getAttributes();
		void applyAttributes(Attributes newAttributes);
	}
	
	private final AttributesSource attributesSource;
	
	private AttributeDataCollection attributeMap;
	
	private Panel buttonPanel;
	
	public AttributesDataTable(float x, float y, float width, AttributesSource attributesSource, ElementContainer owner) {
		super(x, y, width, owner);
		this.attributesSource = attributesSource;
		attributeMap = new AttributeDataCollection(attributesSource.getAttributes(), this);
		init(attributeMap, Arrays.<ColumnDef<AttributeVal>>asList(new KeyColumn(), new TypeColumn(), new ValueColumn(), new ActionsColumn()));
		createButtonPanel();
	}
	
	@Override
	public void redraw() {
		recreateDataRows(attributeMap);
	}
	public void reset() {
		recreateDataRows(attributeMap = new AttributeDataCollection(attributesSource.getAttributes(), AttributesDataTable.this));
	}
	
	protected void createButtonPanel() {
		buttonPanel = createPanel(getWidth(), getParentStyle(owner).buttonTexture.getDefault().frameHeight*2+5, 
				new Layout(LayoutDirection.VERTICAL, 0));
		buttonPanel.createButton("Apply", new IOnClickListener() {
			@Override public void onClick() {
				attributesSource.applyAttributes(attributeMap.getAttributes());
				reset();
			}
		});
		buttonPanel.createButton("Reset", new IOnClickListener() {
			@Override public void onClick() {
				reset();
			}
		});

		setDimensions(getWidth(), getYOffset());
	}
	
	@Override
	protected void recreateDataRows(Iterable<AttributeVal> rowData) {
		super.recreateDataRows(rowData);
		removeElement(buttonPanel);
		createButtonPanel();
	}
	
	@Override
	public boolean deleteRow(LayoutElementContainer row) {

		buttonPanel.setPosition(Vector2f.sub(Vector2f.sub(buttonPanel.getPosition(),getPosition()), new Vector2f(0, row.height+ROW_SPACING)));
		
		return super.deleteRow(row);
	}
	
	@Override
	public LayoutElementContainer addRow(AttributeVal rowData) {
		LayoutElementContainer row = super.addRow(rowData);
		rowData.row = row;
		return row;
	}
	
	@Override
	protected Color getRowBackgroundColorBase(AttributeVal data) {
		return data.modified ? new Color(140, 140, 0, 150) : super.getRowBackgroundColorBase(data);
	}
	
	private final class KeyColumn extends AbstractColumnDef<AttributeVal> {

		public KeyColumn() {
			super("Key", 0.3f);
		}

		@Override
		public Element createValueElement(float width, AttributeVal data, LayoutElementContainer row) {
			if( data.depth>0 )
				row.createLabel("", data.depth*10, 5);
			
			if( data.parent==null || data.parent.type!=ColumnType.SEQ )
				return row.createInputField(width - data.depth*10, data.new KeyRef());
			else
				return row.createPanel(width - data.depth*10, 50);
		}
	}

	private final class TypeColumn extends AbstractColumnDef<AttributeVal> {
		
		public TypeColumn() {
			super("Type", 0.2f);
		}

		@Override
		public Element createValueElement(float width, AttributeVal data, LayoutElementContainer row) {
			return row.createDropDown(width, ColumnType.class, data.new TypeRef());
		}
	}
	
	private final class ValueColumn extends AbstractColumnDef<AttributeVal> {

		public ValueColumn() {
			super("Value", 0.4f);
		}

		@Override
		public Element createValueElement(float width, AttributeVal data, LayoutElementContainer row) {
			if( data.type==ColumnType.BOOL ) {
				Panel p = row.createPanel(width, 50);
				p.createCheckbox(new ToBooleanRwValueRef(data.new ValueRef()));
				p.setFillColor(Color.TRANSPARENT);
				return p;
			} else if( data.type==ColumnType.EVENT ) {
				return row.createDropDown(width, EventType.class, data.new ValueEventRef());
			
			} else
				return row.createInputField(width, data.new ValueRef());
		}
		
	}
	private static final class ToBooleanRwValueRef implements RwValueRef<Boolean> {
		private final RwValueRef<String> ref;
		public ToBooleanRwValueRef(RwValueRef<String> ref) {
			this.ref = ref;
		}
		@Override
		public Boolean getValue() {
			return Boolean.valueOf(ref.getValue());
		}
		@Override
		public void setValue(Boolean value) {
			ref.setValue(value.toString());
		}
	}
	
	private final class ActionsColumn extends AbstractColumnDef<AttributeVal> {

		public ActionsColumn() {
			super("Del", 0.1f);
		}
		
		@Override
		public Element createValueElement(float width, final AttributeVal data, LayoutElementContainer row) {
			Panel e;
			
			row.updateOffset(e=new Panel(row.getXOffset(), row.getYOffset(), width, 50, row) {
				@Override
				protected Style getStyle() {
					try {
						return super.getStyle().setButtonTexture(ResourceManager.animation.get("smallButton.anim"));
					} catch (IOException e) {
						throw new GameException(e.getMessage(),e);
					}
				}
			});
			
			if( data.type!=null )
				e.createButton("X", new IOnClickListener() {
					
					@Override
					public void onClick() {
						attributeMap.deleteAttribute(data);
						redraw();
					}
				});
			
			return  e;
		}
	}
}
