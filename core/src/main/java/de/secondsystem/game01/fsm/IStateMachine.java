package de.secondsystem.game01.fsm;

/**
 * Hält den aktuellen Zustand und Verwaltet den Wechseln zwischen Zuständen
 * @author lowkey
 *
 */
public interface IStateMachine extends Runnable {

	void changeState( IState state );
	
}
