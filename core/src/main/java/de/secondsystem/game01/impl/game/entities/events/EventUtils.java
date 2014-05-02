package de.secondsystem.game01.impl.game.entities.events;

import de.secondsystem.game01.impl.map.IGameMap;
import de.secondsystem.game01.model.Attributes;

final class EventUtils {

	public static String FACTORY = "factory";
	
	private static String PACKAGE_NAME = EventUtils.class.getPackage().getName();
	
	private static String PACKAGE_SHORTCUT = ".";
	
	public static String normalizeHandlerFactory(String classname) {
		return classname.startsWith(PACKAGE_NAME) ? PACKAGE_SHORTCUT+classname.substring(PACKAGE_NAME.length()+1) : classname;
	}
	public static String denormalizeHandlerFactory(String classname) {
		return classname.startsWith(PACKAGE_SHORTCUT) ? PACKAGE_NAME+"."+ classname.substring(1) : classname;
	}
	
	public static IEventHandler createEventHandler(IGameMap map, Attributes attributes) {
		final String factoryName = attributes.getString(EventUtils.FACTORY);
		
		if( factoryName==null )
			throw new Error("No factory defined for event-handler: "+attributes);
		
		try {
			@SuppressWarnings("unchecked")
			Class<? extends IEventHandlerFactory> clazz = (Class<? extends IEventHandlerFactory>) EventUtils.class.getClassLoader().loadClass(denormalizeHandlerFactory(factoryName));
			
			return clazz.newInstance().create(map, attributes);
			
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
			throw new RuntimeException("Unable to create EventHandler by '"+factoryName+"'.",e);
		}
	}

	
	private EventUtils() {
	}

}
