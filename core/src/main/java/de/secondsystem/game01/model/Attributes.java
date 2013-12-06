package de.secondsystem.game01.model;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("serial")
public class Attributes extends HashMap<String, Object> {

	public static final class Attribute {
		public final String key;
		public final Object value;
		public Attribute(String k, Object v) {
			this.key = k;
			this.value = v;
		}
	}
	
	public Attributes(){}
	
	public Attributes(Attribute... entries) {
		for( Attribute e : entries )
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
	
	@Override
	public Attributes clone() {
		return new Attributes((Map<?,?>) super.clone());
	}
}
