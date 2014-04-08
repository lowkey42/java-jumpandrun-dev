package de.secondsystem.game01.impl.gui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Stack;

import de.secondsystem.game01.impl.game.entities.events.EventType;
import de.secondsystem.game01.model.Attributes;

final class AttributeDataCollection implements Iterable<AttributeDataCollection.AttributeVal> {

	public static interface IRedrawable {
		void redraw();
	}
	
	public static enum ColumnType {
		STR, BOOL, FLOAT, INT, EVENT, SEQ, OBJ;
	}
	
	public class AttributeVal {
		int depth;
		String key;
		ColumnType type;
		AttributeVal parent;
		Object val;
		LayoutElementContainer row;
		boolean modified = false;
		
		public class KeyRef implements RwValueRef<String> {
			@Override
			public String getValue() {
				return key!=null ? key : "";
			}

			@Override
			public void setValue(String nkey) {
				if( !nkey.equals(key) )
					modified = true;
				
				key = nkey;
			}
		}

		public class ValueEventRef implements RwValueRef<EventType> {

			@Override
			public EventType getValue() {
				return (EventType) val;
			}

			@Override
			public void setValue(EventType value) {
				if( !modified && value!=val ) {
					modified = true;
					redrawable.redraw();
				}
				
				val = value;
			}
			
		}
		public class ValueRef implements RwValueRef<String> {

			@Override
			public String getValue() {
				if( type==null )
					return "NEW";
				
				switch (type) {
					case BOOL:
					case STR:
					case FLOAT:
					case INT:
						return val.toString();

					case EVENT:
						throw new UnsupportedOperationException();
						
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
				
				Object orgVal = val;
				
				switch (type) {
					case BOOL:
						val = Boolean.valueOf(value);
						break;
						
					case FLOAT:
						try {
							val = Double.valueOf(value);
						} catch( NumberFormatException e ){}
						break;
						
					case INT:
						try {
							val = Long.valueOf(value);
						} catch( NumberFormatException e ){}
						break;

					case EVENT:
						throw new UnsupportedOperationException();
						
					case STR:
						val = value;
						break;
	
					case OBJ:
					case SEQ:
					default:
						break;
				}
				
				if( !modified && val!=null && !val.equals(orgVal) ) {
					modified = true;
					redrawable.redraw();
				}
			}
			
		}
		
		public class TypeRef implements RwValueRef<ColumnType> {
			@Override
			public ColumnType getValue() {
				return type;
			}
			@SuppressWarnings("unchecked")
			@Override
			public void setValue(ColumnType ntype) {
				if( type!=ntype ) {
					modified = true;
					
					if( type==null ) {
						if( parent!=null ) {
							parent.modified = true;
							if( parent.val instanceof Collection )
								((Collection<AttributeVal>) parent.val).add(createPlaceholderValue(parent, depth));
						}else
							roots.add(createPlaceholderValue(parent, depth));
						
						key="NEW";
					}
					
					if( val==null )
						val = "";
					
					switch( ntype ) {
						case EVENT:
							try {
								val = EventType.valueOf(val.toString());
								
							} catch( IllegalArgumentException e ) {val=null;}
							break;
							
						case STR:
							if( val!=null && (type==ColumnType.FLOAT || type==ColumnType.INT || type==ColumnType.EVENT || type==ColumnType.BOOL) )
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
							
						case FLOAT:
							try {
								val = Double.valueOf(val.toString());
								break;
							} catch( NumberFormatException e ) {}
						
							val = Double.valueOf(0);
							break;
							
						case INT:
							if( type==ColumnType.STR )
								try {
									val = Long.valueOf((String) val);
									break;
								} catch( NumberFormatException e ) {}
							
							else if( type==ColumnType.FLOAT ) {
								val = ((Number)val).intValue();
								break;
							}
							
							val = Long.valueOf(0);
							break;

						case SEQ:
						case OBJ:
							val = new ArrayList<>();
							((Collection<AttributeVal>) val).add(createPlaceholderValue(AttributeVal.this, depth+1));
							break;
					}
					
					type = ntype;
					redrawable.redraw();
				}
			}
		}
	}
	
	private final Set<AttributeVal> roots = new LinkedHashSet<>();

	private final IRedrawable redrawable;
	
	public AttributeDataCollection(Attributes attributes, IRedrawable redrawable) {
		this.redrawable = redrawable;
		roots.addAll(addToAttributeMap(attributes, null, 0));
	}
	
	private List<AttributeVal> addToAttributeMap(Map<String, Object> obj, AttributeVal parent, int depth) {
		List<AttributeVal> values = new ArrayList<>(obj.size());
		
		for( Entry<String, Object> e : obj.entrySet() ) {
			AttributeVal val = new AttributeVal();
			
			values.add(val);
			
			initAttributeVal(val, e.getKey(), e.getValue(), parent, depth);
		}
		
		values.add(createPlaceholderValue(parent, depth));
		
		return values;
	}
	private List<AttributeVal> addToAttributeMap(Collection<Object> seq, AttributeVal parent, int depth) {
		List<AttributeVal> values = new ArrayList<>(seq.size());
		
		for( Object o : seq ) {
			AttributeVal val = new AttributeVal();
			
			values.add(val);
					
			initAttributeVal(val, "", o, parent, depth);
		}
		
		values.add(createPlaceholderValue(parent, depth));
		
		return values;
	}
	private AttributeVal createPlaceholderValue(AttributeVal parent, int depth) {
		AttributeVal nullVal = new AttributeVal();
		nullVal.depth = depth;
		nullVal.key = "";
		nullVal.parent = parent;
		nullVal.type = null;
		
		return nullVal;
	}
	@SuppressWarnings("unchecked")
	private void initAttributeVal(AttributeVal val, String key, Object value, AttributeVal parent, int depth) {
		val.depth = depth;
		val.key = key;
		val.parent = parent;
		
		if( value instanceof Float || value instanceof Double ) {
			val.type = ColumnType.FLOAT;
			val.val = ((Number) value).doubleValue();
			
		} else if( value instanceof Integer || value instanceof Long ) {
			val.type = ColumnType.INT;
			val.val = ((Number) value).longValue();
			
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
			
			try {
				val.val = EventType.valueOf((String) val.val);
				val.type = ColumnType.EVENT;
				
			} catch( IllegalArgumentException e ) {}
		}
	}
	
	@SuppressWarnings("unchecked")
	public void deleteAttribute(AttributeVal val) {
		if( val.parent!=null ) {
			val.parent.modified = true;
			((Collection<AttributeVal>) val.parent.val).remove(val);
				
		} else {
			roots.remove(val);
		}
	}
	
	public Attributes getAttributes() {
		return createAttributesMap(roots);
	}
	private static Attributes createAttributesMap(Collection<AttributeVal> c) {
		Attributes attributes = new Attributes();
		

		for( AttributeVal v : c ) {
			Object a = getAttributeValue(v);
			
			if( a!=null )
				attributes.put(v.key, a);
		}
		
		return attributes;
	}
	private static List<?> createAttributesList(Collection<AttributeVal> c) {
		List<Object> values = new ArrayList<>(c.size());

		for( AttributeVal v : c ) {
			Object a = getAttributeValue(v);
			
			if( a!=null )
				values.add(a);
		}
		
		return values;
	}
	@SuppressWarnings("unchecked")
	private static Object getAttributeValue( AttributeVal v ) {
		if( v.type==null || v.val==null || v.key==null )
			return null;

		switch (v.type) {
			case BOOL:
			case FLOAT:
			case INT:
			case STR:
				return v.val;
				
			case EVENT:
				return v.val.toString();
				
			case OBJ:
				return createAttributesMap((Collection<AttributeVal>) v.val);
				
			case SEQ:
				return createAttributesList((Collection<AttributeVal>) v.val);
		}
		
		return null;
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
			
			return val;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
		
	}
}
