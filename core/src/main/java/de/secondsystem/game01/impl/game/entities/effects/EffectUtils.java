package de.secondsystem.game01.impl.game.entities.effects;

import org.jsfml.system.Vector2f;

import com.google.common.base.Function;

import de.secondsystem.game01.impl.map.IGameMap;
import de.secondsystem.game01.model.Attributes;

public final class EffectUtils {

	public static String FACTORY = "factory";
	
	private static String PACKAGE_NAME = EffectUtils.class.getPackage().getName();
	
	private static String PACKAGE_SHORTCUT = ".";
	
	public static String normalizeHandlerFactory(String classname) {
		return classname.startsWith(PACKAGE_NAME) ? PACKAGE_SHORTCUT+classname.substring(PACKAGE_NAME.length()+1) : classname;
	}
	public static String denormalizeHandlerFactory(String classname) {
		return classname.startsWith(PACKAGE_SHORTCUT) ? PACKAGE_NAME+"."+ classname.substring(1) : classname;
	}
	
	public static IGameEntityEffect createEventHandler(IGameMap map, Attributes attributes, int worldMask, Vector2f position, float rotation) {
		final String factoryName = attributes.getString(EffectUtils.FACTORY);
		
		if( factoryName==null )
			throw new Error("No factory defined for event-handler: "+attributes);
		
		try {
			@SuppressWarnings("unchecked")
			Class<? extends IGameEntityEffectFactory> clazz = (Class<? extends IGameEntityEffectFactory>) EffectUtils.class.getClassLoader().loadClass(denormalizeHandlerFactory(factoryName));
			
			return clazz.newInstance().create(map, attributes, worldMask, position, rotation);
			
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
			throw new RuntimeException("Unable to create EventHandler by '"+factoryName+"'.",e);
		}
	}
	

	
	public static final Function<IGameEntityEffect, Attributes> HANDLER_SERIALIZER = new Function<IGameEntityEffect, Attributes>() {
		@Override
		public Attributes apply(IGameEntityEffect input) {
			return input.serialize();
		}
	};

	
	private EffectUtils() {
	}

}
