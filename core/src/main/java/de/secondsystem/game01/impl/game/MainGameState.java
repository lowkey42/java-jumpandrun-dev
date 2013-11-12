package de.secondsystem.game01.impl.game;

import org.jsfml.window.Keyboard.Key;
import org.jsfml.window.event.Event;

import de.secondsystem.game01.impl.GameContext;
import de.secondsystem.game01.impl.GameState;
import de.secondsystem.game01.impl.editor.EditorGameState;
import de.secondsystem.game01.impl.map.GameMap;
import de.secondsystem.game01.impl.map.IGameMapSerializer;
import de.secondsystem.game01.impl.map.JsonGameMapSerializer;
import de.secondsystem.game01.impl.map.Tileset;

public class MainGameState extends GameState {

	private final GameMap map;
	
	public MainGameState( String mapId ) {
		IGameMapSerializer mapSerializer = new JsonGameMapSerializer();
		
		map = /*new GameMap("test01", new Tileset("test01"));//*/mapSerializer.deserialize(mapId, true, false);
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
	        	if( event.asKeyEvent().key==Key.TAB ) {
					map.switchWorlds();
	        	}
	        }
	    }
	}

}
