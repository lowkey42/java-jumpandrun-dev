package de.secondsystem.game01.impl.gui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.Stack;

import de.secondsystem.game01.model.Attributes;

class AttributeDataCollection implements Iterable<AttributeDataCollection.AttributeVal> {

	public static enum ColumnType {
		NUM, STR, BOOL, SEQ, OBJ;
	}
	
	public class AttributeVal {
		int depth;
		String key;
		ColumnType type;
		AttributeVal parent;
		Object val;
		LayoutElementContainer row;
		
		public class KeyRef implements RwValueRef<String> {
			@Override
			public String getValue() {
				return key;
			}

			@SuppressWarnings("unchecked")
			@Override
			public void setValue(String nkey) {
				if( !key.equals(nkey) && parent!=null ) {
					if( parent.val instanceof Map ) {
						((Map<String, AttributeVal>) parent.val).remove(key);
						((Map<String, AttributeVal>) parent.val).put(nkey, AttributeVal.this);
					}
					
					key = nkey;
				}
			}
		}
		
		public class ValueRef implements RwValueRef<String> {

			@Override
			public String getValue() {
				if( type==null )
					return "NEW";
				
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
				if( type==null )
					return;
					
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
		
		public class TypeRef implements RwValueRef<ColumnType> {
			@Override
			public ColumnType getValue() {
				return type;
			}
			@Override
			public void setValue(ColumnType ntype) {
				if( type!=ntype ) {
					
//					if( type==ColumnType.SEQ || type==ColumnType.OBJ )
//						deleteAllSubElements(AttributeVal.this);
					
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
							val = new LinkedHashMap<>();
							
						case SEQ:
							val = new ArrayList<>();
							break;
					}
					
					type = ntype;
				// TODO:	recreateDataRows(attributeMap.values());
				}
			}
		}
	}
	
	private long uniqueIdSource = 1;
	
	private final Set<AttributeVal> roots = new LinkedHashSet<>();

	
	public AttributeDataCollection(Attributes attributes) {
		roots.addAll(addToAttributeMap(attributes, null, 0).values());
	}

	private Map<String, AttributeVal> addToAttributeMap(Map<String, Object> obj, AttributeVal parent, int depth) {
		Map<String, AttributeVal> values = new LinkedHashMap<>(obj.size());
		
		for( Entry<String, Object> e : obj.entrySet() ) {
			AttributeVal val = new AttributeVal();
			
			values.put(e.getKey(), val);
			
			initAttributeVal(val, e.getKey(), e.getValue(), parent, depth);
		}
		
		AttributeVal nullVal = new AttributeVal();
		nullVal.depth = depth;
		nullVal.key = "+";
		nullVal.parent = parent;
		nullVal.type = null;
		
		values.put("+", nullVal);
		
		return values;
	}
	private List<AttributeVal> addToAttributeMap(Collection<Object> seq, AttributeVal parent, int depth) {
		List<AttributeVal> values = new ArrayList<>(seq.size());
		
		for( Object o : seq ) {
			String key = Long.toString(uniqueIdSource++);
			AttributeVal val = new AttributeVal();
			
			values.add(val);
					
			initAttributeVal(val, key, o, parent, depth);
		}
		
		AttributeVal nullVal = new AttributeVal();
		nullVal.depth = depth;
		nullVal.key = Long.toString(uniqueIdSource++);
		nullVal.parent = parent;
		nullVal.type = null;
		
		values.add(nullVal);
		
		return values;
	}
	@SuppressWarnings("unchecked")
	private void initAttributeVal(AttributeVal val, String key, Object value, AttributeVal parent, int depth) {
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
			val.val = addToAttributeMap((Collection<Object>) value, val, depth+1);
			
		} else if( value instanceof Map ) {
			val.type = ColumnType.OBJ;
			val.val = addToAttributeMap((Map<String, Object>) value, val, depth+1);
			
		} else if( value instanceof Collection ) {
			val.type = ColumnType.SEQ;
			val.val = addToAttributeMap((Collection<Object>) value, val, depth+1);
			
		} else {
			val.type = ColumnType.STR;
			val.val = value.toString();
		}
	}
	
	@SuppressWarnings("unchecked")
	public void deleteAttribute(AttributeVal val) {
		deleteAllSubElements(val);
		
		if( val.parent!=null ) {
			if( val.parent.val instanceof Map )
				((Map<String, AttributeVal>) val.parent.val).remove(val.key);
			else if( val.parent.val instanceof Collection )
				((Collection<AttributeVal>) val.parent.val).remove(val);
				
		} else {
			roots.remove(val);
		}
	}
	
	@SuppressWarnings("unchecked")
	protected void deleteAllSubElements(AttributeVal val) {
		switch( val.type ) {
			case OBJ:
				for( AttributeVal e : ((Map<String, AttributeVal>)val.val).values() )
					deleteAttribute(e);
				break;
				
			case SEQ:
				for( AttributeVal e : ((List<AttributeVal>)val.val) )
					deleteAttribute(e);
				break;
				
			default:
				break;
		}
	}

	@Override
	public Iterator<AttributeVal> iterator() {
		return new AttributeIterator();
	}
	
	private final class AttributeIterator implements Iterator<AttributeVal> {
		
		final Stack<Iterator<AttributeVal>> iterators;
		
		AttributeIterator() {
			iterators = new Stack<>();
			iterators.push(roots.iterator());
		}
		
		@Override
		public boolean hasNext() {
			while( !iterators.isEmpty() && !iterators.peek().hasNext() )
				iterators.pop();
			
			return !iterators.empty();
		}

		@SuppressWarnings("unchecked")
		@Override
		public AttributeVal next() {
			Iterator<AttributeVal> iter = iterators.peek();
			AttributeVal val = iter.next();
			
			if( val.val instanceof Collection )
				iterators.push(((Collection<AttributeVal>) val.val).iterator());
			
			else if( val.val instanceof Map )
				iterators.push(((Map<String, AttributeVal>) val.val).values().iterator());
			
			return val;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
		
	}
}
