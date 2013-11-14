package de.secondsystem.game01.impl.game;

import org.jsfml.graphics.ConstView;
import org.jsfml.graphics.View;
import org.jsfml.system.Vector2f;
import org.jsfml.window.Keyboard.Key;
import org.jsfml.window.event.Event;

import de.secondsystem.game01.impl.GameContext;
import de.secondsystem.game01.impl.GameState;
import de.secondsystem.game01.impl.editor.EditorGameState;
import de.secondsystem.game01.impl.map.GameMap;
import de.secondsystem.game01.impl.map.ICameraController;
import de.secondsystem.game01.impl.map.IGameMapSerializer;
import de.secondsystem.game01.impl.map.JsonGameMapSerializer;
import de.secondsystem.game01.impl.map.LayerType;
import de.secondsystem.game01.impl.map.objects.TestCharacter;

public class MainGameState extends GameState {

	private final GameMap map;
	
	private ICameraController cameraController;
	
	
	public MainGameState( String mapId ) {
		IGameMapSerializer mapSerializer = new JsonGameMapSerializer();
		
		map = /*new GameMap("test01", new Tileset("test01"));//*/mapSerializer.deserialize(mapId, true, true);
		
		// create character
		TestCharacter testCharacter = new TestCharacter(map, 0, 300.f, 100.f, 50.f, 50.f, 0);
		map.addNode(0, LayerType.OBJECTS, testCharacter);
		map.addNode(1, LayerType.OBJECTS, testCharacter);
		cameraController = testCharacter;
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
	protected void onFrame(GameContext ctx, long frameTime) {
		// update worlds
		map.update(frameTime);

		
		final ConstView cView = ctx.window.getView();
		ctx.window.setView(new View(new Vector2f(cameraController.getPosition().x, cView.getCenter().y), cView.getSize() ));
		
		// drawing
		map.draw(ctx.window);
		
		ctx.window.setView(cView);
		
		
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
