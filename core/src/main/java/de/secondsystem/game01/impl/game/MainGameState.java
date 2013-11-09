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
import de.secondsystem.game01.impl.map.LayerType;
import de.secondsystem.game01.impl.map.objects.CollisionObject;
import de.secondsystem.game01.impl.map.objects.SpriteLayerObject;

public class MainGameState extends GameState {

	private final GameMap map;
	
	public MainGameState( String mapId ) {
		// TODO: load
		map = new GameMap("test01");

		map.addNode(LayerType.FOREGROUND_0, new SpriteLayerObject(map.tileset, 0, 200, 200, 0, 1));
		map.addNode(LayerType.PHYSICS, new CollisionObject(100, 100, 150, 20, 0));
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
