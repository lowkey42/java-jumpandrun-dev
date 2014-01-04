package de.secondsystem.game01.impl.game.entities.events;

import de.secondsystem.game01.impl.game.entities.IGameEntity;
import de.secondsystem.game01.impl.map.IGameMap;
import de.secondsystem.game01.model.Attributes;
import de.secondsystem.game01.model.IAnimated;
import de.secondsystem.game01.model.Attributes.Attribute;

public class PlaybackEventHandler implements IEventHandler {

	public static enum Action {
		PLAY, PAUSE, RESUME, STOP, REVERSE;
	}
	
	private final Action action;
	
	public PlaybackEventHandler(Action action) {
		this.action = action;
	}
	
	public PlaybackEventHandler(IGameMap map, Attributes attributes) {
		this( Action.valueOf(attributes.getString("action")) );
	}

	@Override
	public Object handle(Object... args) {
		final IGameEntity owner = (IGameEntity) args[0];
		final IAnimated animated = (IAnimated) owner.getRepresentation();
		
		switch( action ) {
			case PLAY:
				animated.play();
				break;
				
			case PAUSE:
				animated.pause();
				break;
				
			case RESUME:
				animated.resume();
				break;
				
			case REVERSE:
				animated.reverse();
				break;
				
			case STOP:
				animated.stop();
				break;
		}
		
		return null;
	}
	
	@Override
	public Attributes serialize() {
		return new Attributes(
				new Attribute(EventUtils.FACTORY, EventUtils.normalizeHandlerFactory(PPEHF.class.getName())), 
				new Attribute("action", action.name()));
	}
	
}

final class PlaybackEHF implements IEventHandlerFactory {
	@Override
	public PlaybackEventHandler create(IGameMap map, Attributes attributes) {
		return new PlaybackEventHandler(map, attributes);
	}
}