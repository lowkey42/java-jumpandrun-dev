package de.secondsystem.game01.impl.game.entities.events;

import de.secondsystem.game01.impl.game.entities.IGameEntity;
import de.secondsystem.game01.impl.map.IGameMap;
import de.secondsystem.game01.model.Attributes;
import de.secondsystem.game01.model.IAnimated;
import de.secondsystem.game01.model.Attributes.Attribute;
import de.secondsystem.game01.model.Attributes.AttributeIfNotNull;
import de.secondsystem.game01.model.IAnimated.AnimationType;

public class AnimateEventHandler implements IEventHandler {
	
	private final AnimationType animation;
	
	private final Float speed;
	
	private final Boolean repeated;
	
	public AnimateEventHandler(AnimationType animation, Float speed, Boolean repeated) {
		this.animation = animation;
		this.speed = speed;
		this.repeated = repeated;
	}

	public AnimateEventHandler(IGameMap map, Attributes attributes) {
		this(	AnimationType.valueOf(attributes.getString("anim")), 
				attributes.getFloat("speed"), 
				attributes.getBoolean("repeated") );
	}

	@Override
	public Object handle(Object... args) {System.out.println("animEH");
		final IGameEntity owner = (IGameEntity) args[0];
		final IAnimated animated = (IAnimated) owner.getRepresentation();
		
		animated.play(animation, speed!=null ? speed : 1, repeated!=null && repeated);
		
		return true;
	}
	
	@Override
	public Attributes serialize() {
		return new Attributes(
				new Attribute(EventUtils.FACTORY, EventUtils.normalizeHandlerFactory(AnimateEHF.class.getName())), 
				new Attribute("anim", animation.name()), 
				new AttributeIfNotNull("speed", speed), 
				new AttributeIfNotNull("repeated", repeated) );
	}
	
}

final class AnimateEHF implements IEventHandlerFactory {
	@Override
	public AnimateEventHandler create(IGameMap map, Attributes attributes) {
		return new AnimateEventHandler(map, attributes);
	}
}