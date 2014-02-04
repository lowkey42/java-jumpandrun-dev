package de.secondsystem.game01.impl.game.controller;

import org.jsfml.window.Joystick;
import org.jsfml.window.Joystick.Axis;
import org.jsfml.window.Keyboard;
import org.jsfml.window.Keyboard.Key;
import org.jsfml.window.event.Event;

import de.secondsystem.game01.impl.game.entities.AbstractGameEntityController;
import de.secondsystem.game01.impl.game.entities.IControllable.HDirection;
import de.secondsystem.game01.impl.game.entities.IControllable.VDirection;
import de.secondsystem.game01.model.Settings.KeyMapping;

public final class KeyboardController extends AbstractGameEntityController {

	private static final float THROW_FORCE_INC_PER_MS = 5/1000.f; 
	
	private final KeyMapping mapping;
	
	public KeyboardController(KeyMapping mapping) {
		this.mapping = mapping;
	}
	
	float throwForce=0;
	boolean switchW_cooldown = false;
	private static final int JOYSTICK_DZONE = 20;
	private static final int JOYSTICK_AMAX = 100-JOYSTICK_DZONE;
	
	public void processEvents(Event event) {
    	if( event.type==Event.Type.KEY_RELEASED ) {
        	if( event.asKeyEvent().key==Key.TAB ) {
        		proxy.switchWorlds();
        	}
        	
        	if( event.asKeyEvent().key==mapping.lift ) {
        		proxy.liftOrThrowObject(throwForce);
				proxy.attack(2.5f+ throwForce*2);
        		throwForce = 0;
        		
        	} else if( event.asKeyEvent().key==mapping.use )
        		proxy.use();
        }
	}

	public void update(long frameTimeMs) {
		if( Joystick.isConnected(0) ) {
			float x = Joystick.getAxisPosition(0, Axis.X);
			float y = Joystick.getAxisPosition(0, Axis.Y);
			
			if( x<-JOYSTICK_DZONE )
				proxy.moveHorizontally(HDirection.LEFT, (-x-JOYSTICK_DZONE)/JOYSTICK_AMAX);
			else if( x>JOYSTICK_DZONE )
				proxy.moveHorizontally(HDirection.RIGHT, (x-JOYSTICK_DZONE)/JOYSTICK_AMAX);
			
			if( y<-JOYSTICK_DZONE )
				proxy.moveVertically(VDirection.UP, (-y-JOYSTICK_DZONE)/JOYSTICK_AMAX);
			else if( y>JOYSTICK_DZONE )
				proxy.moveVertically(VDirection.DOWN, (y-JOYSTICK_DZONE)/JOYSTICK_AMAX);
			
			if( Joystick.isButtonPressed(0, 0) )
				proxy.jump();

			if( Joystick.isButtonPressed(0, 1) )
				proxy.use();
			
			if( Joystick.isButtonPressed(0, 2) ) {
				throwForce+=THROW_FORCE_INC_PER_MS*frameTimeMs;
				
			} else if( throwForce>0 ) {
				proxy.liftOrThrowObject(throwForce);
				proxy.attack(5+ throwForce*2);
				throwForce=0;
			}

			if( Joystick.getAxisPosition(0, Axis.Z)>=50 ) {
				if( !switchW_cooldown )
					proxy.switchWorlds();
				
				switchW_cooldown = true;
				
			} else
				switchW_cooldown = false;
		}
		
		if( Keyboard.isKeyPressed(mapping.moveLeft) )
			proxy.moveHorizontally(HDirection.LEFT, 1);
		else if( Keyboard.isKeyPressed(mapping.moveRight) )
			proxy.moveHorizontally(HDirection.RIGHT, 1);

		if( Keyboard.isKeyPressed(mapping.moveUp) ) 
			proxy.moveVertically(VDirection.UP, 1);

		else if( Keyboard.isKeyPressed(mapping.moveDown) )
			proxy.moveVertically(VDirection.DOWN, 1);
		
		if( Keyboard.isKeyPressed(mapping.lift) ) 
			throwForce+=THROW_FORCE_INC_PER_MS*frameTimeMs;
		
		if( Keyboard.isKeyPressed(mapping.jump) )
    		proxy.jump();
	}
}
