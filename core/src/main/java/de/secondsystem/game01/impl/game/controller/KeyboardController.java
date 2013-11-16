package de.secondsystem.game01.impl.game.controller;

import org.jsfml.window.Keyboard;
import org.jsfml.window.Keyboard.Key;

import de.secondsystem.game01.impl.game.entities.AbstractGameEntityController;
import de.secondsystem.game01.impl.game.entities.IControllable.HDirection;
import de.secondsystem.game01.impl.game.entities.IControllable.VDirection;

public final class KeyboardController extends AbstractGameEntityController {

	public void process() {
		if( Keyboard.isKeyPressed(Key.A) )
			proxy.moveHorizontally(HDirection.LEFT);
		else if( Keyboard.isKeyPressed(Key.D) )
			proxy.moveHorizontally(HDirection.RIGHT);

		if( Keyboard.isKeyPressed(Key.W) )
			proxy.moveVertically(VDirection.UP);
		else if( Keyboard.isKeyPressed(Key.S) )
			proxy.moveVertically(VDirection.DOWN);

		if( Keyboard.isKeyPressed(Key.SPACE) )
			proxy.jump();
	}
	
}
