package de.secondsystem.game01.impl.game.controller;

import org.jsfml.window.Keyboard;
import org.jsfml.window.Keyboard.Key;

import de.secondsystem.game01.impl.game.entities.AbstractGameEntityController;
import de.secondsystem.game01.impl.game.entities.IControllable.Direction;

public final class KeyboardController extends AbstractGameEntityController {

	public void process() {
		if( Keyboard.isKeyPressed(Key.LEFT) )
			proxy.move(Direction.RIGHT);
		else if( Keyboard.isKeyPressed(Key.LEFT) )
			proxy.move(Direction.LEFT);

		if( Keyboard.isKeyPressed(Key.UP) )
			proxy.move(Direction.UP);
		else if( Keyboard.isKeyPressed(Key.DOWN) )
			proxy.move(Direction.DOWN);

		if( Keyboard.isKeyPressed(Key.SPACE) )
			proxy.jump();
		else
			proxy.stopJump();
	}
	
}
