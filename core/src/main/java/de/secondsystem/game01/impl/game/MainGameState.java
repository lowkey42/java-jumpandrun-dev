package de.secondsystem.game01.impl.game;

import org.jsfml.graphics.View;
import org.jsfml.system.Clock;
import org.jsfml.system.Vector2f;
import org.jsfml.window.Keyboard.Key;
import org.jsfml.window.event.Event;

import de.secondsystem.game01.impl.GameContext;
import de.secondsystem.game01.impl.GameState;
import de.secondsystem.game01.impl.editor.EditorGameState;
import de.secondsystem.game01.impl.map.GameMap;
import de.secondsystem.game01.impl.map.IGameMapSerializer;
import de.secondsystem.game01.impl.map.JsonGameMapSerializer;
import de.secondsystem.game01.impl.map.LayerType;
import de.secondsystem.game01.impl.map.objects.TestCharacter;

// ADDED timing // TODO: REMOVE COMMENT
public class MainGameState extends GameState {

	private final GameMap map;
	
	// character
	private TestCharacter testCharacter = null; 
	
	// time variables
	public final Clock frameClock = new Clock();
	
	public MainGameState( String mapId ) {
		IGameMapSerializer mapSerializer = new JsonGameMapSerializer();
		
		map = /*new GameMap("test01", new Tileset("test01"));//*/mapSerializer.deserialize(mapId, true, false);
		
		// create character
		testCharacter = new TestCharacter(0, 300.f, 100.f, 50.f, 50.f, 0);
		map.addNode(0, LayerType.FOREGROUND_1, testCharacter);
		map.addNode(1, LayerType.FOREGROUND_1, testCharacter);
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
		// time
		float dt = frameClock.restart().asSeconds();
		
		// physics
		map.processPhysics(dt);
		
		// drawing
		map.draw(ctx.window);
		
		// character
		testCharacter.update(dt, ctx);
		
		// events
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
