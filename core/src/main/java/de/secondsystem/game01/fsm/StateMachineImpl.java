package de.secondsystem.game01.fsm;

/**
 * Standard-Implementierung einer StateMachine
 * @author lowkey
 *
 */
final class StateMachineImpl implements IStateMachine {
	
	private IState state;

	@Override
	public void run() {
		while( state!=null )
			changeState( state.update() );
	}

	@Override
	public void changeState(IState newState) {
		if( state==newState )
			return;
		
		IContext ctx = null;
		
		if( state!=null )
			ctx = state.exit();

		state = newState;
		
		if( newState!=null )
			newState.enter(ctx);
	}
	
}
