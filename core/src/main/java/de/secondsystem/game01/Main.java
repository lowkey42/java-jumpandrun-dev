package de.secondsystem.game01;

import de.secondsystem.game01.fsm.IState;
import de.secondsystem.game01.fsm.IStateMachine;
import de.secondsystem.game01.fsm.StateMachineFactory;
import de.secondsystem.game01.impl.FuncGameState;
import de.secondsystem.game01.impl.GameContext;
import de.secondsystem.game01.impl.InitState;
import de.secondsystem.game01.impl.editor.EditorGameState;
import de.secondsystem.game01.impl.intro.MainMenuState;
import de.secondsystem.game01.impl.map.GameMap;
import de.secondsystem.game01.impl.map.IGameMapSerializer;
import de.secondsystem.game01.impl.map.JsonGameMapSerializer;
import de.secondsystem.game01.impl.map.Tileset;

public final class Main {

	public static void main(final String[] args) {
		try {
			System.loadLibrary("My_evilXHack");		// heavy wizardry
		} catch( UnsatisfiedLinkError e ) {
			// obviously not a x64_linux system or "-Djava.library.path=lib_nativ" is not set
			System.out.println("You just won 100 cookies !");
		}
		
		final String command = args.length>1 ? args[0] : "play";
		
		final IState initialState;
		
		switch( command.toLowerCase() ) {
			case "createmap":
				initialState = new FuncGameState() {
					@Override
					protected void onFrame(GameContext ctx, long frameTime) {
						IGameMapSerializer mapSerializer = new JsonGameMapSerializer();
						GameMap map = new GameMap(ctx, args[1], new Tileset(args[2]));
						mapSerializer.serialize(map);
						
						super.onFrame(ctx, frameTime);
					}
				};
				break;
		
			case "editor":
				initialState = new EditorGameState(args[1]);
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
