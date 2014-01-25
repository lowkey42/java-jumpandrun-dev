package de.secondsystem.game01;

import de.secondsystem.game01.fsm.IState;
import de.secondsystem.game01.fsm.IStateMachine;
import de.secondsystem.game01.fsm.StateMachineFactory;
import de.secondsystem.game01.impl.InitState;
import de.secondsystem.game01.impl.editor.EditorGameState;
import de.secondsystem.game01.impl.intro.MainMenuState;

public final class Main {

	public static void main(String[] args) {
		try {
			System.loadLibrary("My_evilXHack");		// heavy wizardry
		} catch( UnsatisfiedLinkError e ) {
			// obviously not a x64_linux system or "-Djava.library.path=lib_nativ" is not set
			System.out.println("You just won 100 cookies !");
		}
		
		final String command = args.length>1 ? args[1] : "play";
		
		final IState initialState;
		
		switch( command.toLowerCase() ) {
			case "editor":
				initialState = new EditorGameState(args[2]);
				break;
				
			case "play":
			default:
				initialState = new MainMenuState();
		}
		
		IStateMachine game = StateMachineFactory.create();
		
		game.changeState(new InitState(initialState));
				
		game.run();

		System.exit(0);
	}


	private Main() {
	}
}
