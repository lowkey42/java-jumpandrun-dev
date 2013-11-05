package de.secondsystem.game01.fsm;

/**
 * Erstellt eine neue Instanz der Standard-StateMachine
 * @author lowkey
 *
 */
public final class StateMachineFactory {

	/**
	 * Erstellt eine neue Instanz der Standard-StateMachine
	 * @return
	 */
	public static IStateMachine create() {
		return new StateMachineImpl();
	}
	
	private StateMachineFactory(){}
}
