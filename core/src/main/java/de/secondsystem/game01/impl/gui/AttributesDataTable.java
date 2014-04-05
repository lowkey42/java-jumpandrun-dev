package de.secondsystem.game01.impl.gui;

import java.util.Arrays;

import de.secondsystem.game01.model.Attributes;
import de.secondsystem.game01.model.Attributes.Attribute;

/**
 * 
 * @author lowkey
 * _________________________________________
 * | Key       | Type | Value      |DEL|ADD|
 * |-----------|------|------------|---|---|
 * | x         | ↓NUM | 56.78      | X | + |
 * | y         | ↓NUM | -799.88    | X | + |
 * | onTOUCHED | ↓OBJ |            | X | + |
 * | →factory  | ↓STR | .PPEHF     | X | + |
 * | →out      | ↓STR | UNLOCKED   | X | + |
 * | onUSED    | ↓SEQ |            | X | + |
 * | →1        | ↓OBJ |            | X | + |
 * |  →factory | ↓STR | .PPEHF     | X | + |
 * |  →out     | ↓STR | UNLOCKED   | X | + |
 * | →2        | ↓OBJ |            | X | + |
 * |  →factory | ↓STR | .ScriptEHF | X | + |
 * |  →body    | ↓STR | alert('Y') | X | + |
 * |---------------------------------------|
 * | APPLY                         | +     |
 * -----------------------------------------
 * 
 */
public final class AttributesDataTable extends DataTable<Attribute> {

	public interface AttributesSource {
		Attributes getAttributes();
	}

	public AttributesDataTable(float x, float y, float width, AttributesSource attributesSource, ElementContainer owner) {
		super(x, y, width, owner);
		init(null, Arrays.<ColumnDef<Attribute>>asList(new KeyColumn(), new TypeColumn(), new ValueColumn(), new ActionsColumn()));
	}
	
	public Attributes getModifiedAttributes() {
		return null; // TODO
	}
	
	@Override
	public LayoutElementContainer addRow(Attribute rowData) {
		// TODO Auto-generated method stub
		return super.addRow(rowData);
	}
	
	private final class KeyColumn extends AbstractColumnDef<Attribute> {

		public KeyColumn() {
			super("Key", 0.3f);
		}

		@Override
		public Element createValueElement(float width, Attribute data, LayoutElementContainer row) {
			return row.createInputField(width, data.key);
		}
	}
	
	private final class TypeColumn extends AbstractColumnDef<Attribute> {

		public TypeColumn() {
			super("Type", 0.2f);
		}

		@Override
		public Element createValueElement(float width, Attribute data, LayoutElementContainer row) {
			return row.createInputField(width, data.key);
		}
	}
	
	private final class KeyRef implements RwValueRef<String> {
		
		private Attributes attributes;
		private Attribute data;

		@Override
		public String getValue() {
			return null;
		}

		@Override
		public void setValue(String value) {
			
		}
	}
	
	
	private final class ValueColumn extends AbstractColumnDef<Attribute> {

		public ValueColumn() {
			super("Value", 0.4f);
		}

		@Override
		public Element createValueElement(float width, Attribute data, LayoutElementContainer row) {
			return row.createInputField(width, data.value.toString());
		}
		
	}
	
	private final class ActionsColumn extends AbstractColumnDef<Attribute> {

		public ActionsColumn() {
			super("Actions", 0.1f);
		}

		@Override
		public Element createValueElement(float width, Attribute data, LayoutElementContainer row) {
			return row.createLabel("[+] [-]", width, 1);
		}
	}
}
