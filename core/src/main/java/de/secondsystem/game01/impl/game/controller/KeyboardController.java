package de.secondsystem.game01.impl.game.controller;

import org.jsfml.window.Keyboard;

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
			proxy.moveHorizontally(HDirection.LEFT);
		else if( Keyboard.isKeyPressed(mapping.moveRight) )
			proxy.moveHorizontally(HDirection.RIGHT);

		if( Keyboard.isKeyPressed(mapping.moveUp) )
			proxy.moveVertically(VDirection.UP);
		else if( Keyboard.isKeyPressed(mapping.moveDown) )
			proxy.moveVertically(VDirection.DOWN);

		if( Keyboard.isKeyPressed(mapping.jump) )
			proxy.jump();
		
		if( Keyboard.isKeyPressed(mapping.use) )
			proxy.liftObject(true);
		else
			proxy.liftObject(false);	
	}
	
}
