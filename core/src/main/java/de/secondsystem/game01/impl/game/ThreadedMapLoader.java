package de.secondsystem.game01.impl.game;

import java.util.concurrent.atomic.AtomicReference;

import org.jsfml.graphics.RenderTarget;
import org.jsfml.system.Vector2f;

import de.secondsystem.game01.impl.GameContext;
import de.secondsystem.game01.impl.ResourceManager;
import de.secondsystem.game01.impl.graphic.AnimatedSprite;
import de.secondsystem.game01.model.GameException;
import de.secondsystem.game01.model.IDrawable;
import de.secondsystem.game01.model.IUpdateable;

public class ThreadedMapLoader implements IDrawable, IUpdateable {

	private final String mapId;
	
	private final GameContext ctx;
	
	private final Thread thread;
	
	private final AtomicReference<MainGameState> loaded = new AtomicReference<>();
	
	private AnimatedSprite loadingSprite;
	
	public ThreadedMapLoader(String mapId, GameContext ctx) {
		this.mapId = mapId;
		this.ctx = ctx;
		thread = new Thread(main);
		thread.setDaemon(true);
		thread.setPriority(Thread.MIN_PRIORITY);
		thread.start();
		
		try {
			loadingSprite = new AnimatedSprite(ResourceManager.animation.get("loading.anim"), 100, 100);
			loadingSprite.setPosition(new Vector2f(ctx.getViewWidth()-loadingSprite.getWidth()-10, ctx.getViewHeight()-loadingSprite.getHeight()-10));
			
		} catch (GameException e) {
			loadingSprite = null;
		}
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
			loaded.set( new MainGameState(mapId, ctx, null) );
		}
	};

	
	@Override
	public void draw(RenderTarget renderTarget) {
		if( !isFinished() && loadingSprite!=null ) {
			loadingSprite.draw(ctx.window);
		}
	}

	@Override
	public void update(long frameTime) {
		if( !isFinished() && loadingSprite!=null ) {
			loadingSprite.update(frameTime);
		}
	}
	
}
