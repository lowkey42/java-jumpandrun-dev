package de.secondsystem.game01;

import de.secondsystem.game01.fsm.IStateMachine;
import de.secondsystem.game01.fsm.StateMachineFactory;
import de.secondsystem.game01.impl.InitState;
import de.secondsystem.game01.impl.into.IntroState;

public final class Main {

	public static void main(String[] args) {
		IStateMachine game = StateMachineFactory.create();
		game.changeState(new InitState(new IntroState()));
		
		game.run();

		System.exit(0);
	}


	private Main() {
	}
}
