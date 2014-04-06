package de.secondsystem.game01.impl.gui;

import java.util.Arrays;

import org.jsfml.system.Vector2f;

import de.secondsystem.game01.impl.gui.AttributeDataCollection.AttributeVal;
import de.secondsystem.game01.impl.gui.AttributeDataCollection.ColumnType;
import de.secondsystem.game01.impl.gui.listeners.IOnClickListener;
import de.secondsystem.game01.model.Attributes;

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
public final class AttributesDataTable extends DataTable<AttributeVal> {

	public interface AttributesSource {
		Attributes getAttributes();
	}
	
	private final AttributesSource attributesSource;
	
	private AttributeDataCollection attributeMap;
	
	private Panel buttonPanel;
	
	public AttributesDataTable(float x, float y, float width, AttributesSource attributesSource, ElementContainer owner) {
		super(x, y, width, owner);
		this.attributesSource = attributesSource;
		attributeMap = new AttributeDataCollection(attributesSource.getAttributes());
		init(attributeMap, Arrays.<ColumnDef<AttributeVal>>asList(new KeyColumn(), new TypeColumn(), new ValueColumn(), new ActionsColumn()));
		createButtonPanel();
	}
	
	protected void createButtonPanel() {
		buttonPanel = createPanel(getWidth(), getParentStyle(owner).buttonTexture.getDefault().frameHeight*2+5, 
				new Layout(LayoutDirection.VERTICAL, 0));
		buttonPanel.createButton("Apply", new IOnClickListener() {
			@Override public void onClick() {
				System.out.println("TODO: ADT-Apply");
			}
		});
		buttonPanel.createButton("Reset", new IOnClickListener() {
			@Override public void onClick() {
				recreateDataRows(attributeMap = new AttributeDataCollection(attributesSource.getAttributes()));
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
	
	public Attributes patchAttributes( Attributes orgAttributes ) {
		return orgAttributes; // TODO
	}
	
	private final class KeyColumn extends AbstractColumnDef<AttributeVal> {

		public KeyColumn() {
			super("Key", 0.3f);
		}

		@Override
		public Element createValueElement(float width, AttributeVal data, LayoutElementContainer row) {
			if( data.depth>0 )
				row.createLabel("", data.depth*5, 5);
			
			if( data.parent==null || data.parent.type!=ColumnType.SEQ )
				return row.createInputField(width - data.depth*5, data.new KeyRef());
			else
				return row.createPanel(width - data.depth*5, 50);
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
				Panel p = row.createPanel(width, 40);
				p.createCheckbox(new ToBooleanRwValueRef(data.new ValueRef()));
				return p;
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
			super("Actions", 0.1f);
		}

		@Override
		public Element createValueElement(float width, AttributeVal data, LayoutElementContainer row) {
			return row.createLabel("[X]", width, 1);
		}
	}
}
