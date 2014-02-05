package de.secondsystem.game01.impl.game;

import org.jsfml.graphics.BlendMode;
import org.jsfml.graphics.Color;
import org.jsfml.graphics.ConstView;
import org.jsfml.graphics.RenderStates;
import org.jsfml.graphics.RenderTarget;
import org.jsfml.graphics.RenderTexture;
import org.jsfml.graphics.Sprite;
import org.jsfml.graphics.TextureCreationException;
import org.jsfml.graphics.View;
import org.jsfml.system.Vector2f;

import de.secondsystem.game01.impl.GameContext;
import de.secondsystem.game01.impl.map.IGameMap;
import de.secondsystem.game01.impl.map.IGameMap.IWorldSwitchListener;
import de.secondsystem.game01.impl.map.IGameMap.WorldId;
import de.secondsystem.game01.model.GameException;
import de.secondsystem.game01.model.IDrawable;
import de.secondsystem.game01.model.IUpdateable;

public final class EffectMapRenderer implements IDrawable, IUpdateable, IWorldSwitchListener {
	
	private static final int FADE_TIME = 1000;

	private final IGameMap map;

	private final RenderTexture effectBuffer = new RenderTexture();

	private final RenderTexture fadeBuffer = new RenderTexture();
	
	private int fadeTimeLeft;
	
	private boolean fadeEnabled = true;
	
	
	public EffectMapRenderer(GameContext ctx, IGameMap map) {
		this.map = map;

		try {
			fadeBuffer.create(ctx.getViewWidth(), ctx.getViewHeight());
			effectBuffer.create(ctx.getViewWidth(), ctx.getViewHeight());
			
		} catch (TextureCreationException e) {
			throw new GameException("Unable to create buffer: "+e.getMessage(), e);
		}
		
		map.registerWorldSwitchListener(this);
	}

	private void displayBuffer(RenderTexture buffer, RenderTarget renderTarget, RenderStates rs, float alpha) {
		displayBuffer(buffer, renderTarget, rs, alpha, 1);
	}
	private void displayBuffer(RenderTexture buffer, RenderTarget renderTarget, RenderStates rs, float alpha, float scale) {
		final ConstView cView = renderTarget.getView();
		
		Sprite fadeSprite = new Sprite(buffer.getTexture());
		fadeSprite.setColor(new Color(255, 255, 255, (int) alpha));
		fadeSprite.setScale(scale, scale);

		renderTarget.setView(new View(Vector2f.div(cView.getSize(), 2), cView.getSize()));
		renderTarget.draw(fadeSprite, rs);

		renderTarget.setView(cView);
	}

	@Override
	public void draw(RenderTarget renderTarget) {
		final ConstView cView = renderTarget.getView();
				
		//effectBuffer.setView(cView);
		map.draw(renderTarget);
		//effectBuffer.display();
		
		//displayBuffer(effectBuffer, renderTarget, new RenderStates(BlendMode.ALPHA), 255);

		// TODO: add effects (e.g. Bloom)
		
		if( fadeTimeLeft>0 && fadeEnabled ) {
			fadeBuffer.setView(cView);
			map.drawInactiveWorld(fadeBuffer);
			fadeBuffer.display();
			
			displayBuffer(fadeBuffer, renderTarget, new RenderStates(BlendMode.ALPHA), ((float)fadeTimeLeft)/FADE_TIME*200.f);
		}

		renderTarget.setView(cView);
	}

	@Override
	public void onWorldSwitch(WorldId newWorldId) {
		fadeTimeLeft=FADE_TIME;
	}

	@Override
	public void update(long frameTimeMs) {
		map.update(frameTimeMs);
		
		fadeTimeLeft-=frameTimeMs;
	}

}
