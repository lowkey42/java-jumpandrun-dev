package de.secondsystem.game01.impl.map.objects;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import de.secondsystem.game01.impl.map.IGameMap;
import de.secondsystem.game01.impl.map.LayerObject;

public enum LayerObjectType {

	SPRITE		("sp", SpriteLayerObject.class),
	COLLISION	("cl", CollisionObject.class),
	TEST_CHARACTER ("tc", TestCharacter.class);
	
	private static final String CONSTRUCT_METHOD_NAME = "create";
	
	/** Shortened id of this type (must be unique) */
	public final String shortId;
	
	/** Reference to the concrete class, implementing this LO-Type */
	private final Class<? extends LayerObject> clazz;
	
	/** Reference to the create-Method of the implementation */
	private final Method constructMethod;
	
	private LayerObjectType(String shortId, Class<? extends LayerObject> clazz) {
		this.shortId = shortId;
		this.clazz = clazz;
		
		try {
			constructMethod = clazz.getMethod(CONSTRUCT_METHOD_NAME, IGameMap.class, int.class,  Map.class);
			
		} catch (NoSuchMethodException | SecurityException e) {
			throw new Error("CONTRACT_VIOLATION: no static "+CONSTRUCT_METHOD_NAME+"-Method in LayerObjectClass "+clazz, e);
		}
	}
	
	public static LayerObjectType getByShortId(String shortId) {
		for( LayerObjectType lot : values() )
			if( lot.shortId.equals(shortId) )
				return lot;
		
		return null;
	}
	
	public static LayerObjectType getByType(Class<? extends LayerObject> clazz) {
		for( LayerObjectType lot : values() )
			if( lot.clazz.equals(clazz) )
				return lot;
		
		return null;
	}
	
	public LayerObject create( IGameMap map, int world, Map<String, Object> attributes ) {
		try {
			return (LayerObject) constructMethod.invoke(null, map, world, attributes);
			
		} catch (IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			throw new Error("CONTRACT_VIOLATION: exception calling static "+CONSTRUCT_METHOD_NAME+"-Method in LayerObjectClass "+clazz, e);
		}
	}
	
}
