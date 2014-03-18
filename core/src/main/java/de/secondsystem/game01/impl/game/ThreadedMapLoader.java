package de.secondsystem.game01.impl.game;

import java.util.concurrent.atomic.AtomicReference;

import de.secondsystem.game01.impl.GameContext;

public class ThreadedMapLoader {

	private final String mapId;
	
	private final GameContext ctx;
	
	private final Thread thread;
	
	private final AtomicReference<MainGameState> loaded = new AtomicReference<>();
	
	public ThreadedMapLoader(String mapId, GameContext ctx) {
		this.mapId = mapId;
		this.ctx = ctx;
		thread = new Thread(main);
		thread.setDaemon(true);
		thread.setPriority(Thread.MIN_PRIORITY);
		thread.start();
	}
	
	public boolean isFinished() {
		return loaded.get()!=null;
	}
	public MainGameState getLoadedMap() {
		return loaded.get();
	}

	private final Runnable main = new Runnable() {
		
		@Override
		public void run() {
			loaded.set( new MainGameState(mapId, ctx) );
		}
	};
	
}
