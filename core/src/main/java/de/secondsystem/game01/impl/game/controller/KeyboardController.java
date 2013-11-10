package de.secondsystem.game01.impl.game.controller;

import org.jsfml.window.Keyboard;
import org.jsfml.window.Keyboard.Key;

import de.secondsystem.game01.impl.game.entities.AbstractGameEntityController;

public final class KeyboardController extends AbstractGameEntityController {

	public void process() {
		if( Keyboard.isKeyPressed(Key.LEFT) )
			proxy.move(false);
		else if( Keyboard.isKeyPressed(Key.LEFT) )
			proxy.move(true);
		else
			proxy.stopMove();
	}
	
}
