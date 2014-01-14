package de.secondsystem.game01.util;

import org.jsfml.graphics.Color;

public final class SerializationUtil {


	public static String encodeColor(Color color) {
		return "#"+encodeColorComponent(color.r)+encodeColorComponent(color.g)+encodeColorComponent(color.b)+encodeColorComponent(color.a);
	}
	private static String encodeColorComponent(int c) {
		String s = Integer.toHexString(c);
		assert( s.length()==2 || s.length()==1 );
		
		return s.length()==2 ? s : "0"+s;
	}

	public static Color decodeColor(String str) {
		if( str==null )
			return null;
		
		assert( str.startsWith("#") );
		return new Color(
				decodeColorComponent(str.substring(1, 3)),
				decodeColorComponent(str.substring(3, 5)),
				decodeColorComponent(str.substring(5, 7)),
				decodeColorComponent(str.substring(7, 9)) );
	}
	private static int decodeColorComponent(String str) {
		return Integer.parseInt(str, 16);
	}
	
	private SerializationUtil() {
	}

}
