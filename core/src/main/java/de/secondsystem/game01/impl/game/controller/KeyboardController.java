package de.secondsystem.game01.impl.game.controller;

import org.jsfml.window.Keyboard;
import org.jsfml.window.Keyboard.Key;
import org.jsfml.window.event.Event;

import de.secondsystem.game01.impl.game.entities.AbstractGameEntityController;
import de.secondsystem.game01.impl.game.entities.IControllable.HDirection;
import de.secondsystem.game01.impl.game.entities.IControllable.VDirection;
import de.secondsystem.game01.model.Settings.KeyMapping;

public final class KeyboardController extends AbstractGameEntityController {

	private final KeyMapping mapping;
	
	public KeyboardController(KeyMapping mapping) {
		this.mapping = mapping;
	}
	
	public void process() {
		if( Keyboard.isKeyPressed(mapping.moveLeft) )
			proxy.moveHorizontally(HDirection.LEFT, 1);
		else if( Keyboard.isKeyPressed(mapping.moveRight) )
			proxy.moveHorizontally(HDirection.RIGHT, 1);

		if( Keyboard.isKeyPressed(mapping.moveUp) ) 
			proxy.moveVertically(VDirection.UP, 1);

		else if( Keyboard.isKeyPressed(mapping.moveDown) )
			proxy.moveVertically(VDirection.DOWN, 1);
		
		if( Keyboard.isKeyPressed(mapping.use) ) 
			proxy.incThrowingPower();		
	}
	
	public void processEvents(Event event) {
    	if( event.type==Event.Type.KEY_RELEASED ) {
        	if( event.asKeyEvent().key==Key.TAB ) {
        		proxy.switchWorlds();
        	}
        	
        	if( event.asKeyEvent().key==mapping.use ) {
        		proxy.use();
        		proxy.liftObject();
        	}
        	
        }
    	else
	    	if( event.type==Event.Type.KEY_PRESSED ) {
	        	if( event.asKeyEvent().key==mapping.jump) {
	        		proxy.jump();
	        	}
	        }
	}
}
