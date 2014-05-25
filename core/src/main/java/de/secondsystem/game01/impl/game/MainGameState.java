package de.secondsystem.game01.impl.game;

import org.jsfml.window.Keyboard.Key;
import org.jsfml.window.event.Event;

import de.secondsystem.game01.fsm.IState;
import de.secondsystem.game01.impl.DevConsole;
import de.secondsystem.game01.impl.GameContext;
import de.secondsystem.game01.impl.GameState;
import de.secondsystem.game01.impl.editor.EditorGameState;
import de.secondsystem.game01.impl.game.controller.KeyboardController;
import de.secondsystem.game01.impl.game.controller.KeyboardController.IWorldSwitchInterceptor;
import de.secondsystem.game01.impl.intro.MainMenuState;
import de.secondsystem.game01.impl.map.GameMap;
import de.secondsystem.game01.impl.map.JsonGameMapSerializer;
import de.secondsystem.game01.impl.sound.MusicWrapper;

public class MainGameState extends GameState {
	
	private final String mapId;
	
	private final GameState parentState;
	
	private MusicWrapper backgroundMusic;
	
	private DevConsole console = new DevConsole();
	
	private GameMap map;
	
	private EffectMapRenderer mapRenderer;
	
	private final Camera camera = new Camera();
	
	private PlayerComponent playerComponent;
	
	private KeyboardController controller;
	
	private ScriptApiImpl extApi;

	public MainGameState( String mapId, GameState parentState ) {
		this.mapId = mapId;
		this.parentState = parentState;
	}
	public MainGameState( String mapId, GameContext ctx, GameState parentState ) {
		this(mapId, parentState);
		init(ctx);
	}
	
	private boolean initCalled=false;
	private void init(GameContext ctx) {
		if( initCalled )
			return;
		else
			initCalled = true;
		
		backgroundMusic = new MusicWrapper((short) (ctx.settings.volume*0.5));
		
		extApi = new ScriptApiImpl(this, ctx);
		
		map = new JsonGameMapSerializer().deserialize(ctx, mapId, extApi, true, true);
		
		mapRenderer = new EffectMapRenderer(ctx, map);

		// lets kick the JIT in his ass
		for(int i=0; i<30; i++) {
			mapRenderer.update(8);
		}

		controller = new KeyboardController(ctx.settings.keyMapping, new IWorldSwitchInterceptor() {
			@Override public boolean doWorldSwitch() {
				mapRenderer.onWorldSwitch(map.getActiveWorldId());
				return true;
			}
		});
		playerComponent = new PlayerComponent(map, camera, controller);
		
		console.setScriptEnvironment(map.getScriptEnv());
		
		map.getScriptEnv().exec("init");
	}
	
	@Override
	protected void onStart(GameContext ctx) {
		init(ctx);
		
		backgroundMusic.play();
		
		map.getScriptEnv().exec("onStart");
	}
	
	@Override
	protected void onStop(GameContext ctx) {
		backgroundMusic.pause();
		
		map.getScriptEnv().exec("onStop");
	}

	@Override
	protected void onFrame(GameContext ctx, long frameTime) {
		console.update(frameTime);
		
		controller.update(frameTime);
		
		playerComponent.update(frameTime);
		
		// update worlds
		mapRenderer.update(frameTime);

		camera.update(frameTime);

		backgroundMusic.fade(map.getDefaultBgMusic(camera.getWorldId()), 5000);
		backgroundMusic.update(frameTime);
		
		camera.drawInView(ctx.window, map, mapRenderer);
		
		extApi.update(frameTime);
		extApi.draw(ctx.window);
	}
	
	@Override
	protected void processEvent(GameContext ctx, Event event) {
		if( event.type==Event.Type.KEY_RELEASED ) {
        	if( event.asKeyEvent().key==Key.F12 ) {
        		setNextState(EditorGameState.createLive(this, map));
        	}
        	if( event.asKeyEvent().key==Key.ESCAPE ) {
        		setNextState(parentState!=null ? parentState : new MainMenuState(this));
        	}
        }
        
        controller.processEvents(event);
	}

	void requestStateSwitch(IState state) {
		setNextState(state);
	}
}
