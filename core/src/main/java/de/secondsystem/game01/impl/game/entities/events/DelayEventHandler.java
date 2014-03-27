package de.secondsystem.game01.impl.game.entities.events;

import de.secondsystem.game01.impl.game.entities.IGameEntity;
import de.secondsystem.game01.impl.map.IGameMap;
import de.secondsystem.game01.impl.scripting.timer.TimerManager;
import de.secondsystem.game01.model.Attributes;
import de.secondsystem.game01.model.Attributes.Attribute;
import de.secondsystem.game01.model.Attributes.AttributeIfNotNull;

public final class DelayEventHandler implements IEventHandler {

	private final TimerManager timerManager;
	
	private final IEventHandler handler;
	
	private final long delay;
	
	private final boolean repeat;

	public DelayEventHandler(TimerManager timerManager, IEventHandler handler, long delay, boolean repeat) {
		this.timerManager = timerManager;
		this.handler = handler;
		this.delay = delay;
		this.repeat = repeat;
	}
	
	public DelayEventHandler(IGameMap map, Attributes attributes) {
		this(map.getScriptEnv().getTimerManager(), EventUtils.createEventHandler(map, attributes.getObject("sub")),
				attributes.getInteger("delay"), attributes.getBoolean("repeat", false));
	}
	
	@Override
	public Object handle(Object... args) {
		timerManager.createTimer(delay, repeat, (IGameEntity) args[0], handler, args);
		
		return null;
	}
	
	@Override
	public Attributes serialize() {
		return new Attributes(
				new Attribute(EventUtils.FACTORY, EventUtils.normalizeHandlerFactory(DelayEHF.class.getName())), 
				new AttributeIfNotNull("delay", 	delay),
				new AttributeIfNotNull("sub", 		handler==null ? null : handler.serialize()),
				new AttributeIfNotNull("repeat", 	repeat)
		);
	}
	
}

final class DelayEHF implements IEventHandlerFactory {
	@Override
	public DelayEventHandler create(IGameMap map, Attributes attributes) {
		return new DelayEventHandler(map, attributes);
	}
}
