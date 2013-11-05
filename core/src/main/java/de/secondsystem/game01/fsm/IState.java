package de.secondsystem.game01.fsm;

/**
 * Zustand der Anwendung
 * @author lowkey
 *
 */
public interface IState {

	void enter(IContext ctx);
	
	IContext exit();
	
	IState update();
	
}
