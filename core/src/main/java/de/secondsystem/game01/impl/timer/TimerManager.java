package de.secondsystem.game01.impl.timer;

import java.util.PriorityQueue;

import de.secondsystem.game01.impl.game.entities.IGameEntity;
import de.secondsystem.game01.impl.scripting.ScriptEnvironment;
import de.secondsystem.game01.model.IUpdateable;

public class TimerManager implements IUpdateable{
	private PriorityQueue<Timer> timers = new PriorityQueue<Timer>();
	private final ScriptEnvironment env;
	
	public TimerManager(ScriptEnvironment scriptEnv) {
		env = scriptEnv;
	}
	
	public void createTimer(long intervalMs, boolean repeated, String funcName, IGameEntity target, Object... args) {
		timers.add(new Timer(env, intervalMs, repeated, funcName, target, args));
	}
	
	public void createTimer(long intervalMs, boolean repeated, String funcName, Object... args) {
		timers.add(new Timer(env, intervalMs, repeated, funcName, null, args));
	}
	
	@Override
	public void update( long frameTimeMs ) {
		Timer t;
		while( !timers.isEmpty() && System.currentTimeMillis() >= (t = timers.peek()).startAt())
			if( t.onTick() )
				t.reset();
			else
				timers.poll();
	}
}
