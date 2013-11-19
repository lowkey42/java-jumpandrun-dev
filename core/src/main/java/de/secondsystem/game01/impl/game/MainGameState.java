package de.secondsystem.game01.impl.game;

import org.jsfml.graphics.ConstView;
import org.jsfml.graphics.View;
import org.jsfml.system.Vector2f;
import org.jsfml.window.Keyboard.Key;
import org.jsfml.window.event.Event;

import de.secondsystem.game01.impl.GameContext;
import de.secondsystem.game01.impl.GameState;
import de.secondsystem.game01.impl.editor.EditorGameState;
import de.secondsystem.game01.impl.game.controller.KeyboardController;
import de.secondsystem.game01.impl.game.entities.IControllableGameEntity;
import de.secondsystem.game01.impl.intro.MainMenuState;
import de.secondsystem.game01.impl.map.GameMap;
import de.secondsystem.game01.impl.map.ICameraController;
import de.secondsystem.game01.impl.map.IGameMapSerializer;
import de.secondsystem.game01.impl.map.JsonGameMapSerializer;
import de.secondsystem.game01.model.Attributes;
import de.secondsystem.game01.model.Attributes.Attribute;

public class MainGameState extends GameState {

	private final GameMap map;
	
	private ICameraController cameraController;
	
	private final IControllableGameEntity player;
	
	private KeyboardController controller;
	
	
	public MainGameState( String mapId ) {
		IGameMapSerializer mapSerializer = new JsonGameMapSerializer();
		
		map = /*new GameMap("test01", new Tileset("test01"));//*/mapSerializer.deserialize(mapId, true, true);
		
		player = map.getEntityManager().createControllable( "player", new Attributes(new Attribute("x",300), new Attribute("y",100)) );
		
		cameraController = player;
	}
	
	@Override
	protected void onStart(GameContext ctx) {
		controller = new KeyboardController(ctx.settings.keyMapping);
		controller.addGE(player);
	}

	@Override
	protected void onStop(GameContext ctx) {
		// TODO: free resources
	}

	@Override
	protected void onFrame(GameContext ctx, long frameTime) {
		controller.process();
		
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
	        	if( event.asKeyEvent().key==Key.ESCAPE ) {
	        		setNextState(new MainMenuState(this));
	        	}
	        }
	    }
	}

}
