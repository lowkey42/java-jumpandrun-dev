package de.secondsystem.game01.impl.scripting.timer;

import java.lang.ref.WeakReference;

import de.secondsystem.game01.impl.game.entities.IGameEntity;
import de.secondsystem.game01.impl.game.entities.events.IEventHandler;

public class EventTimer extends Timer {
	
	private WeakReference<IGameEntity> sender;
	
	private IEventHandler handler;
	
	private Object[] args;

	public EventTimer(long intervalMs,
			boolean repeated, IGameEntity sender, IEventHandler handler, Object[] args) {
		super(null, intervalMs, repeated);
		this.sender = new WeakReference<IGameEntity>(sender);
		this.handler = handler;
		this.args = args;
	}

	@Override
	protected void call() {
		handler.handle(args);
	}

	@Override
	public boolean onTick() {
		IGameEntity entity = sender.get();
		return entity==null || entity.isDestroyed() ? false : super.onTick();
	}

}
