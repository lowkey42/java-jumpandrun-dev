package de.secondsystem.game01.impl.game.entities.events;

import de.secondsystem.game01.impl.game.entities.IGameEntity;
import de.secondsystem.game01.model.Attributes;
import de.secondsystem.game01.model.IAnimated;
import de.secondsystem.game01.model.IAnimated.AnimationType;

public class AnimateEventHandler implements IEventHandler {

	public static enum Action {
		PLAY, PAUSE, RESUME, STOP, REVERSE;
	}
	
	private final AnimationType animation;
	
	private final Action action;
	
	private final float speed;
	
	private final boolean repeated;
	
	public AnimateEventHandler(Action action, AnimationType animation, Float speed, Boolean repeated) {
		this.action = action;
		this.animation = animation;
		this.speed = speed!=null ? speed : 1;
		this.repeated = repeated!=null ? repeated : true;
	}

	@Override
	public Object handle(Object... args) {
		final IGameEntity owner = (IGameEntity) args[0];
		final IAnimated animated = (IAnimated) owner.getRepresentation();
		
		switch( action ) {
			case PLAY:
				animated.play(animation, speed, repeated, true, false);
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
		// TODO Auto-generated method stub
		return null;
	}

}
