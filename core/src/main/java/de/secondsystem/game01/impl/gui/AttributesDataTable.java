package de.secondsystem.game01.impl.gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import de.secondsystem.game01.model.Attributes;

/**
 * 
 * @author lowkey
 * _________________________________________
 * | Key       | Type | Value      |ACTIONS|
 * |-----------|------|------------|-------|
 * | x         | ↓NUM | 56.78      | X   + |
 * | y         | ↓NUM | -799.88    | X   + |
 * | onTOUCHED | ↓OBJ |            | X   + |
 * | →factory  | ↓STR | .PPEHF     | X   + |
 * | →out      | ↓STR | UNLOCKED   | X   + |
 * | onUSED    | ↓SEQ |            | X   + |
 * | →1        | ↓OBJ |            | X   + |
 * |  →factory | ↓STR | .PPEHF     | X   + |
 * |  →out     | ↓STR | UNLOCKED   | X   + |
 * | →2        | ↓OBJ |            | X   + |
 * |  →factory | ↓STR | .ScriptEHF | X   + |
 * |  →body    | ↓STR | alert('Y') | X   + |
 * |---------------------------------------|
 * | APPLY  RESET                  | +     |
 * -----------------------------------------
 * 
 */
public final class AttributesDataTable extends DataTable<AttributesDataTable.AttributeVal> {

	public interface AttributesSource {
		Attributes getAttributes();
	}

	public static enum ColumnType {
		NUM, STR, BOOL, SEQ, OBJ;
	}
	
	private static class AttributeKey {
		final String key;
		final AttributeVal parent;
		AttributeKey( String key, AttributeVal parent ) {
			this.key = key;
			this.parent = parent;
		}
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((key == null) ? 0 : key.hashCode());
			result = prime * result
					+ ((parent == null) ? 0 : parent.hashCode());
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			AttributeKey other = (AttributeKey) obj;
			if (key == null) {
				if (other.key != null)
					return false;
			} else if (!key.equals(other.key))
				return false;
			if (parent == null) {
				if (other.parent != null)
					return false;
			} else if (parent!=other.parent)
				return false;
			return true;
		}
	}
	public class AttributeVal {
		int depth;
		String key;
		ColumnType type;
		AttributeVal parent;
		Object val;
		
		private class KeyRef implements RwValueRef<String> {
			@Override
			public String getValue() {
				return key;
			}

			@Override
			public void setValue(String value) {
				if( !key.equals(value) ) {
					attributeMap.remove(new AttributeKey(key, parent));
					key = value;
					attributeMap.put(new AttributeKey(key, parent), AttributeVal.this);
				}
			}
		}
		
		private class ValueRef implements RwValueRef<String> {

			@Override
			public String getValue() {
				switch (type) {
					case BOOL:
					case NUM:
					case STR:
						return val.toString();
	
					case OBJ:
					case SEQ:
					default:
					return "-";
				}
			}

			@Override
			public void setValue(String value) {
				switch (type) {
					case BOOL:
						val = Boolean.valueOf(value);
						break;
						
					case NUM:
						val = Double.valueOf(value);
						break;
						
					case STR:
						val = value;
						break;
	
					case OBJ:
					case SEQ:
					default:
						break;
				}
			}
			
		}
		
		private class TypeRef implements RwValueRef<ColumnType> {
			@Override
			public ColumnType getValue() {
				return type;
			}
			@Override
			public void setValue(ColumnType ntype) {
				if( type!=ntype ) {
					switch( ntype ) {
						case STR:
							if( type==ColumnType.NUM || type==ColumnType.BOOL )
								val = val.toString();
							else
								val = "";
							break;
							
						case BOOL:
							if( type==ColumnType.STR )
								val = Boolean.valueOf((String) val);
							else
								val = Boolean.valueOf(true);
							break;
							
						case NUM:
							if( type==ColumnType.STR )
								val = Double.valueOf((String) val);
							else
								val = Double.valueOf(0);
							break;
							
						case OBJ:
							val = new HashMap<>();
							
						case SEQ:
							val = new ArrayList<>();
							break;
					}
					
					type = ntype;
				}
			}
		}
	}
	
	private long uniqueIdSource = 1;
	
	private Map<AttributeKey, AttributeVal> attributeMap;
	
	
	public AttributesDataTable(float x, float y, float width, Attributes attributes, ElementContainer owner) {
		super(x, y, width, owner);
		attributeMap = createAttributeMap(attributes);
		init(attributeMap.values(), Arrays.<ColumnDef<AttributeVal>>asList(new KeyColumn(), new TypeColumn(), new ValueColumn(), new ActionsColumn()));
	}
	
	private Map<AttributeKey, AttributeVal> createAttributeMap(Attributes attributes) {
		Map<AttributeKey, AttributeVal> am = new LinkedHashMap<>();
		
		addToAttributeMap(attributes, null, 0, am);
		
		return am;
	}
	private Map<String, AttributeVal> addToAttributeMap(Map<String, Object> obj, AttributeVal parent, int depth, Map<AttributeKey, AttributeVal> am) {
		Map<String, AttributeVal> values = new HashMap<>(obj.size());
		
		for( Entry<String, Object> e : obj.entrySet() ) {
			AttributeVal val = new AttributeVal();
			
			values.put(e.getKey(), val);
			am.put(new AttributeKey(e.getKey(), parent), val);
			
			initAttributeVal(val, e.getKey(), e.getValue(), parent, depth, am);
		}
		
		return values;
	}
	private List<AttributeVal> addToAttributeMap(Collection<Object> seq, AttributeVal parent, int depth, Map<AttributeKey, AttributeVal> am) {
		List<AttributeVal> values = new ArrayList<>(seq.size());
		
		for( Object o : seq ) {
			String key = Long.toString(uniqueIdSource++);
			AttributeVal val = new AttributeVal();
			
			values.add(val);
			am.put(new AttributeKey(key, parent), val);
					
			initAttributeVal(val, key, o, parent, depth, am);
		}
		
		return values;
	}
	@SuppressWarnings("unchecked")
	private void initAttributeVal(AttributeVal val, String key, Object value, AttributeVal parent, int depth, Map<AttributeKey, AttributeVal> am) {
		val.depth = depth;
		val.key = key;
		val.parent = parent;
		
		if( value instanceof Number ) {
			val.type = ColumnType.NUM;
			val.val = ((Number) value).doubleValue();
			
		} else if( value instanceof Boolean ) {
			val.type = ColumnType.BOOL;
			val.val = (Boolean) value;
			
		} else if( value instanceof Collection ) {
			val.type = ColumnType.SEQ;
			val.val = addToAttributeMap((Collection<Object>) value, val, depth+1, am);
			
		} else if( value instanceof Map ) {
			val.type = ColumnType.OBJ;
			val.val = addToAttributeMap((Map<String, Object>) value, val, depth+1, am);
			
		} else if( value instanceof Collection ) {
			val.type = ColumnType.SEQ;
			val.val = addToAttributeMap((Collection<Object>) value, val, depth+1, am);
			
		} else {
			val.type = ColumnType.STR;
			val.val = value.toString();
		}
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
				return row.createLabel(" ", width - data.depth*5, 0);
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
			return row.createInputField(width, data.new ValueRef());
		}
		
	}
	
	private final class ActionsColumn extends AbstractColumnDef<AttributeVal> {

		public ActionsColumn() {
			super("Actions", 0.1f);
		}

		@Override
		public Element createValueElement(float width, AttributeVal data, LayoutElementContainer row) {
			return row.createLabel("[+] [-]", width, 1);
		}
	}
}
