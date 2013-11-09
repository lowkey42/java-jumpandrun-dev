package de.secondsystem.game01.impl.game;

import java.io.IOException;
import java.nio.file.Paths;

import org.jsfml.graphics.Sprite;
import org.jsfml.graphics.Texture;
import org.jsfml.window.Keyboard.Key;
import org.jsfml.window.event.Event;

import de.secondsystem.game01.impl.GameContext;
import de.secondsystem.game01.impl.GameState;
import de.secondsystem.game01.impl.editor.EditorGameState;
import de.secondsystem.game01.impl.map.GameMap;
import de.secondsystem.game01.impl.map.GraphicLayer.GraphicLayerType;

public class MainGameState extends GameState {

	private final GameMap map;
	
	public MainGameState( String mapId ) {
		// TODO: load
		map = new GameMap();
		Texture t1 = new Texture();
		try {
			t1.loadFromFile(Paths.get("assets/tiles/test01.png"));
		} catch (IOException e) {
			throw new Error(e);
		}
		Sprite s1 = new Sprite(t1);
		s1.setPosition(200, 200);
		map.addNode(GraphicLayerType.FOREGROUND_0, s1);
	}
	
	@Override
	protected void onStart(GameContext ctx) {
		// TODO: load map, save game, etc.
	}

	@Override
	protected void onStop(GameContext ctx) {
		// TODO: free resources
	}

	@Override
	protected void onFrame(GameContext ctx) {
		map.draw(ctx.window);
		for(Event event : ctx.window.pollEvents()) {
	        if(event.type == Event.Type.CLOSED) {
	            //The user pressed the close button
	            ctx.window.close();
	            
	        } else if( event.type==Event.Type.KEY_RELEASED ) {
	        	if( event.asKeyEvent().key==Key.F12 ) {
	        		setNextState(new EditorGameState(this, map));
	        	}
	        }
	    }
	}

}
