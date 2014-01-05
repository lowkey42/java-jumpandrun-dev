package de.secondsystem.game01.impl.game.entities.events;

import de.secondsystem.game01.impl.game.entities.IControllableGameEntity;
import de.secondsystem.game01.impl.game.entities.IGameEntity;
import de.secondsystem.game01.impl.map.IGameMap;
import de.secondsystem.game01.model.Attributes;
import de.secondsystem.game01.model.IAnimated;
import de.secondsystem.game01.model.IPlayable;
import de.secondsystem.game01.model.Attributes.Attribute;

public class PlaybackEventHandler implements IEventHandler {

	public static enum Action {
		PLAY, PAUSE, RESUME, STOP, REVERSE;
	}
	
	public static enum Target {
		REPRESENTATION, CONTROLLER;
	}
	
	private final Action action;
	
	private final Target target;
	
	public PlaybackEventHandler(Action action, Target target) {
		this.action = action;
		this.target = target!=null ? target : Target.REPRESENTATION;
	}
	
	public PlaybackEventHandler(IGameMap map, Attributes attributes) {
		this( Action.valueOf(attributes.getString("action")), Target.valueOf(attributes.getString("target")) );
	}

	@Override
	public Object handle(Object... args) {
		final IGameEntity owner = (IGameEntity) args[0];
		final IPlayable playable;
		switch( target ) {
			case CONTROLLER:
				playable = (IPlayable) ((IControllableGameEntity)owner).getController();
				break;
				
			default:
			case REPRESENTATION:
				playable = (IPlayable) owner.getRepresentation();
				break;
		}
		
		switch( action ) {
			case PLAY:
				playable.play();
				break;
				
			case PAUSE:
				playable.pause();
				break;
				
			case RESUME:
				playable.resume();
				break;
				
			case REVERSE:
				playable.reverse();
				break;
				
			case STOP:
				playable.stop();
				break;
		}
		
		return null;
	}
	
	@Override
	public Attributes serialize() {
		return new Attributes(
				new Attribute(EventUtils.FACTORY, EventUtils.normalizeHandlerFactory(PPEHF.class.getName())), 
				new Attribute("action", action.name()), 
				new Attribute("target", target.name()) );
	}
	
}

final class PlaybackEHF implements IEventHandlerFactory {
	@Override
	public PlaybackEventHandler create(IGameMap map, Attributes attributes) {
		return new PlaybackEventHandler(map, attributes);
	}
}