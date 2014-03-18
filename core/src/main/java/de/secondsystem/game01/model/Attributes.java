package de.secondsystem.game01.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

@SuppressWarnings("serial")
public class Attributes extends HashMap<String, Object> {

	public static class Attribute {
		public final String key;
		public final Object value;
		public Attribute(String k, Number v) {this(k, (Object) v);}
		public Attribute(String k, Boolean v) {this(k, (Object) v);}
		public Attribute(String k, String v) {this(k, (Object) v);}
		public Attribute(String k, Map<?, ?> v) {this(k, (Object) v);}
		public Attribute(String k, Collection<?> v) {this(k, (Object) (v instanceof List ? v : new ArrayList<>(v)));}
		public Attribute(String k, Object v) {
			this.key = k;
			this.value = v;
		}
	}
	public static class AttributeIf extends Attribute {
		public final boolean condition;

		public AttributeIf(boolean condition, String k, Collection<?> v) {this(condition, k, (Object) (v instanceof List ? v : new ArrayList<>(v)));}
		public AttributeIf(boolean condition, String k, Object v) {
			super(k, v);
			this.condition = condition;
		}
	}
	public static final class AttributeIfNotNull extends AttributeIf {
		public AttributeIfNotNull(String k, Object v) {
			super(k!=null && v!=null, k, v);
		}
	}
	
	public Attributes(){}
	
	public Attributes(Attribute... entries) {
		for( Attribute e : entries )
			if( !(e instanceof AttributeIf) || ((AttributeIf)e).condition )
				put(e.key, e.value);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Attributes(Map... maps) {
		for( Map<String, Object> map : maps ) {
			if( map!=null )
				putAll(map);
		}
	}

	public Boolean getBoolean(String key) {
		Object val = get(key);
		return val!=null ? Boolean.valueOf(val.toString()) : null;
	}
	public boolean getBoolean(String key, boolean def) {
		Object val = get(key);
		return val!=null ? Boolean.valueOf(val.toString()) : def;
	}
	
	public Float getFloat(String key) {
		Object val = get(key);
		return val!=null ? ((Number)val).floatValue() : null;
	}
	public float getFloat(String key, float defaultValue) {
		Object val = get(key);
		return val!=null ? ((Number)val).floatValue() : defaultValue;
	}

	public Double getDouble(String key) {
		Object val = get(key);
		return val!=null ? ((Number)val).doubleValue() : null;
	}
	public double getDouble(String key, double defaultValue) {
		Object val = get(key);
		return val!=null ? ((Number)val).doubleValue() : defaultValue;
	}

	public Integer getInteger(String key) {
		Object val = get(key);
		return val!=null ? ((Number)val).intValue() : null;
	}
	public int getInteger(String key, int defaultValue) {
		Object val = get(key);
		return val!=null ? ((Number)val).intValue() : defaultValue;
	}

	public String getString(String key) {
		Object val = get(key);
		return val!=null ? val.toString() : null;
	}
	public String getString(String key, String defaultValue) {
		String val = getString(key);
		return val!=null ? val : defaultValue;
	}
	
	@SuppressWarnings("unchecked")
	public Attributes getObject(String key) {
		Object val = get(key);
		return val!=null ? new Attributes((Map<String, Object>)val ) : null;
	}
	
	@SuppressWarnings("unchecked")
	public <T> List<T> getList(String key) {
		Object val = get(key);
		if( val==null )
			return null;
		
		return val!=null ? (List<T>) val : null;
	}
	
	public List<Attributes> getObjectList(String key) {
		List<Map<String, ?>> wrappedList = this.<Map<String, ?>>getList(key);
		return wrappedList!=null ? new ListWrapper(wrappedList) : null;
	}
	
	@Override
	public Attributes clone() {
		return new Attributes((Map<?,?>) super.clone());
	}
}

class ListWrapper implements List<Attributes> {

	private final List<Map<String, ?>> wrappedList;
	
	public ListWrapper(List<Map<String, ?>> wrappedList) {
		this.wrappedList = wrappedList;
	}

	@Override
	public int size() {
		return wrappedList.size();
	}

	@Override
	public boolean isEmpty() {
		return wrappedList.isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		return wrappedList.contains(o);
	}

	@Override
	public Iterator<Attributes> iterator() {
		return new IteratorWrapper();
	}

	private class IteratorWrapper implements Iterator<Attributes> {
		
		private final Iterator<Map<String, ?>> iter = wrappedList.iterator();
		
		@Override
		public boolean hasNext() {
			return iter.hasNext();
		}

		@Override
		public Attributes next() {
			return new Attributes(iter.next());
		}

		@Override
		public void remove() {
			iter.remove();
		}
		
	}
	
	@Override
	public Object[] toArray() {
        throw new UnsupportedOperationException("toArray() is not supported by this wrapper");
	}

	@Override
	public <T> T[] toArray(T[] a) {
        throw new UnsupportedOperationException("toArray(T[] a) is not supported by this wrapper");
	}

	@Override
	public boolean add(Attributes e) {
		return wrappedList.add(e);
	}

	@Override
	public boolean remove(Object o) {
		return wrappedList.remove(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return wrappedList.containsAll(c);
	}

	@Override
	public boolean addAll(Collection<? extends Attributes> c) {
		return wrappedList.addAll(c);
	}

	@Override
	public boolean addAll(int index, Collection<? extends Attributes> c) {
		return wrappedList.addAll(c);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		return wrappedList.removeAll(c);
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return wrappedList.retainAll(c);
	}

	@Override
	public void clear() {
		wrappedList.clear();
	}

	@Override
	public Attributes get(int index) {
		return new Attributes(wrappedList.get(index));
	}

	@Override
	public Attributes set(int index, Attributes element) {
		return new Attributes(wrappedList.set(index, element));
	}

	@Override
	public void add(int index, Attributes element) {
		wrappedList.add(index, element);
	}

	@Override
	public Attributes remove(int index) {
		return new Attributes(wrappedList.remove(index));
	}

	@Override
	public int indexOf(Object o) {
		return wrappedList.indexOf(o);
	}

	@Override
	public int lastIndexOf(Object o) {
		return wrappedList.lastIndexOf(o);
	}

	@Override
	public ListIterator<Attributes> listIterator() {
		return new ListIteratorWrapper(wrappedList.listIterator());
	}

	private static class ListIteratorWrapper implements ListIterator<Attributes> {
		
		private final ListIterator<Map<String, ?>> iter;
		
		public ListIteratorWrapper(ListIterator<Map<String, ?>> iter) {
			this.iter = iter;
		}
		
		@Override
		public boolean hasNext() {
			return iter.hasNext();
		}

		@Override
		public Attributes next() {
			return new Attributes(iter.next());
		}

		@Override
		public void remove() {
			iter.remove();
		}

		@Override
		public boolean hasPrevious() {
			return iter.hasPrevious();
		}

		@Override
		public Attributes previous() {
			return new Attributes(iter.previous());
		}

		@Override
		public int nextIndex() {
			return iter.nextIndex();
		}

		@Override
		public int previousIndex() {
			return iter.previousIndex();
		}

		@Override
		public void set(Attributes e) {
			iter.set(e);
		}

		@Override
		public void add(Attributes e) {
			iter.add(e);
		}
	}

	@Override
	public ListIterator<Attributes> listIterator(int index) {
		return new ListIteratorWrapper(wrappedList.listIterator(index));
	}

	@Override
	public List<Attributes> subList(int fromIndex, int toIndex) {
		return new ListWrapper(wrappedList.subList(fromIndex, toIndex));
	}
	
}