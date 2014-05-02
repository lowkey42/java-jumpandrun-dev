package de.secondsystem.game01.model.collections;

import java.util.LinkedHashMap;

@SuppressWarnings("serial")
public class LruCache<K,V> extends LinkedHashMap<K, V> {

	public interface Loader<K,V> {
		V load(K key);
	}
	
	private final int maxSize;
	
	private final Loader<K,V> loader;
	
	public LruCache(int maxSize, Loader<K,V> loader) {
		this.maxSize = maxSize;
		this.loader = loader;
	}
	
	@Override
	protected boolean removeEldestEntry(java.util.Map.Entry<K, V> eldest) {
		return size() > maxSize;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public synchronized V get(Object key) {
		V v = super.get(key);
		if( v==null ) {
			v = loader.load((K)key);
			if( v!=null )
				put((K) key, v);
		}
		
		return v;
	}
	
}
