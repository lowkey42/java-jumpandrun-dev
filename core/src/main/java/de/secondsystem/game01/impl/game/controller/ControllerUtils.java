package de.secondsystem.game01.impl.game.controller;

import de.secondsystem.game01.impl.game.entities.IControllableGameEntity;
import de.secondsystem.game01.impl.game.entities.IGameEntityController;
import de.secondsystem.game01.impl.map.IGameMap;
import de.secondsystem.game01.model.Attributes;

public final class ControllerUtils {

	static String FACTORY = "factory";
	
	private static String PACKAGE_NAME = ControllerUtils.class.getPackage().getName();
	
	private static String PACKAGE_SHORTCUT = ".";
	
	static String normalizeControllerFactory(String classname) {
		return classname.startsWith(PACKAGE_NAME) ? PACKAGE_SHORTCUT+classname.substring(PACKAGE_NAME.length()+1) : classname;
	}
	static String denormalizeControllerFactory(String classname) {
		return classname.startsWith(PACKAGE_SHORTCUT) ? PACKAGE_NAME+"."+ classname.substring(1) : classname;
	}
	
	public static IGameEntityController createController(IControllableGameEntity entity, IGameMap map, Attributes attributes) {
		final String factoryName = attributes.getString(ControllerUtils.FACTORY);
		
		try {
			@SuppressWarnings("unchecked")
			Class<? extends IControllerFactory> clazz = (Class<? extends IControllerFactory>) ControllerUtils.class.getClassLoader().loadClass(denormalizeControllerFactory(factoryName));
			
			return clazz.newInstance().create(entity, map, attributes);
			
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
			throw new RuntimeException("Unable to create EventHandler by '"+factoryName+"'.",e);
		}
	}
	
	private ControllerUtils() {
	}

}
