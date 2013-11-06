package de.secondsystem.game01.impl.into;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.TextureCreationException;
import org.jsfml.system.Vector2f;
import org.jsfml.window.event.Event;

import de.secondsystem.game01.impl.GameContext;
import de.secondsystem.game01.impl.GameState;
import de.secondsystem.game01.impl.graphic.Light;
import de.secondsystem.game01.impl.graphic.LightMap;

/**
 * Zustand für Intro (erster Zustand nach Initialisierung)
 * Beispiel für weitere Zustände
 * TODO: remove
 * @author lowkey
 *
 */
public final class IntroState extends GameState {

	LightMap lm;
	Light l;
	Light l2;
	
	@Override
	protected void onStart(GameContext ctx) {
		try {
			lm = new LightMap(800, 600);
			l = new Light(new Vector2f(400, 300), new Color(255,0,0), 150.f, (float) Math.PI/4,  (float) Math.PI);
			l2 = new Light(new Vector2f(350, 300), new Color(255,255,150), 300.f, (float) Math.PI/2,  (float) Math.PI-.2f);
			
			lm.clear(1.f);
			lm.drawLight(l);
			lm.drawLight(l2);
			
		} catch (TextureCreationException e) {
			throw new RuntimeException(e);
		}
		// TODO
	}

	@Override
	protected void onStop(GameContext ctx) {
		// TODO
	}

	@Override
	protected void onFrame(GameContext ctx) {
		// TODO
		for(Event event : ctx.window.pollEvents()) {
	        if(event.type == Event.Type.CLOSED) {
	            //The user pressed the close button
	            ctx.window.close();
	        }
	    }
		
		ctx.window.draw(lm);
	}

}
