package de.secondsystem.game01.impl.game;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.ConstView;
import org.jsfml.graphics.Sprite;
import org.jsfml.system.Vector2f;
import org.jsfml.window.Keyboard.Key;
import org.jsfml.window.event.Event;

import de.secondsystem.game01.impl.DevConsole;
import de.secondsystem.game01.impl.GameContext;
import de.secondsystem.game01.impl.GameState;
import de.secondsystem.game01.impl.ResourceManager;
import de.secondsystem.game01.impl.editor.EditorGameState;
import de.secondsystem.game01.impl.game.controller.KeyboardController;
import de.secondsystem.game01.impl.game.controller.KeyboardController.IWorldSwitchInterceptor;
import de.secondsystem.game01.impl.game.entities.IControllableGameEntity;
import de.secondsystem.game01.impl.game.entities.IGameEntity;
import de.secondsystem.game01.impl.game.entities.IGameEntityController;
import de.secondsystem.game01.impl.game.entities.effects.GEGlowEffect;
import de.secondsystem.game01.impl.game.entities.effects.IGameEntityEffect;
import de.secondsystem.game01.impl.game.entities.events.AttackEventHandler;
import de.secondsystem.game01.impl.game.entities.events.EventType;
import de.secondsystem.game01.impl.game.entities.events.IEventHandler;
import de.secondsystem.game01.impl.game.entities.events.KillEventHandler;
import de.secondsystem.game01.impl.game.entities.events.PingPongEventHandler;
import de.secondsystem.game01.impl.graphic.AnimatedSprite;
import de.secondsystem.game01.impl.intro.MainMenuState;
import de.secondsystem.game01.impl.map.GameMap;
import de.secondsystem.game01.impl.map.IGameMap;
import de.secondsystem.game01.impl.map.IGameMap.WorldId;
import de.secondsystem.game01.impl.map.IGameMapSerializer;
import de.secondsystem.game01.impl.map.JsonGameMapSerializer;
import de.secondsystem.game01.impl.scripting.IScriptApi;
import de.secondsystem.game01.impl.sound.MonologueTextBox;
import de.secondsystem.game01.impl.sound.MusicWrapper;
import de.secondsystem.game01.model.Attributes;
import de.secondsystem.game01.model.Attributes.Attribute;
import de.secondsystem.game01.model.GameException;

public class MainGameState extends GameState {

	private final String mapId;
	
	private final GameState parentState;
	
	MonologueTextBox monologueTextBox;
	
	private MusicWrapper backgroundMusic;
	
	private AnimatedSprite loadingSprite;
	
	private DevConsole console = new DevConsole();
	
	private GameMap map;
	
	private EffectMapRenderer mapRenderer;
	
	private Camera camera;
	
	private IControllableGameEntity player;
	
	private KeyboardController controller;

	public MainGameState( String mapId, GameState parentState ) {
		this.mapId = mapId;
		this.parentState = parentState;
		try {
			loadingSprite = new AnimatedSprite(ResourceManager.animation.get("loading.anim"), 100, 100);
			
		} catch (GameException e) {
			loadingSprite = null;
		}
	}
	public MainGameState( String mapId, GameContext ctx, GameState parentState ) {
		this(mapId, parentState);
		init(ctx);
	}
	
	private final class PlayerDeathEventHandler extends KillEventHandler {

		public PlayerDeathEventHandler(IGameMap map) {
			super(map, new Attributes());
		}

		@Override
		public Object handle(Object... args) {
			if( ((IGameEntity)args[1]).uuid().equals(ignoreDamageEntity) )
				return null;
			
			return super.handle(args);
		}
		
		@Override
		protected void killEntity(IGameEntity entity) {
			setNextState(new GameOverGameState());
			System.out.println("killed entity");
		}
	}
	
	private UUID ignoreDamageEntity;
	private long ignoreDamageTimer=0;

	private IControllableGameEntity possessedEntity;
	private IGameEntityController possessedEntityController;
	private IGameEntityEffect possessionEffect;
	private Vector2f fixedPlayerPos;
	private IEventHandler possessedEntityDamagedHandler = new IEventHandler() {
		@Override public Attributes serialize() {
			return null;
		}
		
		@Override public Object handle(Object... args) {
			unpossess();
			return null;
		}
	};
	
	private void possess(IControllableGameEntity player, IControllableGameEntity target, int possessTime) {
		ignoreDamageEntity = target.uuid();
		ignoreDamageTimer = System.currentTimeMillis() + possessTime;
		
		possessedEntity = target;

		possessedEntityController = possessedEntity.getController();
		controller.addGE(possessedEntity);
		controller.removeGE(player);
		player.setWorldMask(0);
		camera.setController(possessedEntity);
		possessedEntity.addEventHandler(EventType.DAMAGED, possessedEntityDamagedHandler);

		possessedEntity.addEffect(possessionEffect=new GEGlowEffect(map, possessedEntity.getRepresentation(), 
				new Color(80, 30, 200, 255), new Color(200, 200, 255, 100), 15, 30, 15/(possessTime/1000.f) ));
	}
	private boolean unpossess() {
		if( possessedEntity!=null ) {
			ignoreDamageEntity = possessedEntity.uuid();
			ignoreDamageTimer = System.currentTimeMillis() + 5000;
			
			possessedEntity.removeEventHandler(EventType.DAMAGED, possessedEntityDamagedHandler);
			if( possessedEntity.isLiftingSomething() )
				possessedEntity.liftOrThrowObject(2);

			player.forceWorld(WorldId.MAIN);
			player.setPosition(fixedPlayerPos=possessedEntity.getPosition());
			player.setRotation(possessedEntity.getRotation());
			controller.addGE(player);
			controller.removeGE(possessedEntity);
			possessedEntity.setController(possessedEntityController);
			possessedEntity.removeEffect(possessionEffect);
			possessedEntity.addEffect(possessionEffect=new GEGlowEffect(map, possessedEntity.getRepresentation(), 
					new Color(50, 10, 100, 255), new Color(160, 100, 180, 100), 20, 30, 18/5.f), 5000);

			camera.setController(player);
			possessedEntity = null;
			
			return true;
		}
		
		return false;
	}
	
	private final class PlayerAttackEventHandler extends AttackEventHandler {
		
		@Override
		protected boolean attack(IGameEntity owner, IGameEntity target,
				float force) {
			if( owner.isInWorld(WorldId.MAIN) && target instanceof IControllableGameEntity && ((IControllableGameEntity) target).getPossessableTime()>0 ) {
				possess((IControllableGameEntity) owner, (IControllableGameEntity) target, ((IControllableGameEntity) target).getPossessableTime());
				return true;
				
			} else
				return super.attack(owner, target, force);
		}
		
	}
	
	ThreadedMapLoader activeMapLoader;
	final Set<Sprite> sprites = new HashSet<>();
	protected IScriptApi createScriptApi(GameContext ctx) {
		return new ScriptApiImpl(this, ctx);
	}
	
	private boolean initCalled=false;
	private void init(GameContext ctx) {
		if( initCalled )
			return;
		else
			initCalled = true;
		
		backgroundMusic = new MusicWrapper((short) (ctx.settings.volume*0.5));
		
		try {
			monologueTextBox = new MonologueTextBox(ResourceManager.font.get("FreeSans.otf"), 30, new Vector2f(ctx.getViewWidth()/2, ctx.getViewHeight()-100));
			
		} catch (GameException e) {
			throw new GameException("Unable to load font for MonologueTextBox: "+e.getMessage(), e);
		}
		
		IGameMapSerializer mapSerializer = new JsonGameMapSerializer();
		map = mapSerializer.deserialize(ctx, mapId, createScriptApi(ctx), true, true);
		
		map.getEntityManager().create("lever", new Attributes(new Attribute("x",300), new Attribute("y",500), new Attribute("worldId",3)) )
		.setEventHandler(EventType.USED, new PingPongEventHandler(EventType.DAMAGED));
		
		mapRenderer = new EffectMapRenderer(ctx, map);

		// lets kick the JIT in his ass
		for(int i=0; i<30; i++) {
			mapRenderer.update(8);
		}
		
		Set<IGameEntity> playerEntities = map.getEntityManager().listByGroup("player");
		
		player = !playerEntities.isEmpty() ? 
				(IControllableGameEntity) playerEntities.iterator().next() :
				map.getEntityManager().createControllable("player", new Attributes(new Attribute("x",300), new Attribute("y",100)) );

		player.addEventHandler(EventType.DAMAGED, new PlayerDeathEventHandler(map) );
		player.addEventHandler(EventType.ATTACK, new PlayerAttackEventHandler());

		camera = new Camera(player);

		controller = new KeyboardController(ctx.settings.keyMapping, new IWorldSwitchInterceptor() {
			@Override public boolean doWorldSwitch() {
				if( unpossess() )
					return false;
				
				mapRenderer.onWorldSwitch(map.getActiveWorldId());
				return true;
			}
		});
		controller.addGE(player);
		
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
		if( fixedPlayerPos!=null ) {
			player.setPosition(fixedPlayerPos);
			fixedPlayerPos = null;
		}
		
		console.update(frameTime);
		
		controller.update(frameTime);
		
		// update worlds
		mapRenderer.update(frameTime);

		camera.update(frameTime);

		backgroundMusic.fade(map.getDefaultBgMusic(camera.getWorldId()), 5000);
		backgroundMusic.update(frameTime);
		
		monologueTextBox.update(frameTime);
		
		final ConstView cView = ctx.window.getView();
		ctx.window.setView(camera.createView(cView));
		map.setActiveWorldId(camera.getWorldId());
		
		// drawing
		mapRenderer.draw(ctx.window);
		
		ctx.window.setView(cView);
		
		monologueTextBox.draw(ctx.window);
		
		for( Sprite s : sprites )
			ctx.window.draw(s);
		

		if( activeMapLoader!=null ) {
			if( activeMapLoader.isFinished() )
				setNextState(activeMapLoader.getLoadedMap());
			
			else if( loadingSprite!=null ) {
				loadingSprite.setPosition(new Vector2f(ctx.getViewWidth()-loadingSprite.getWidth()-10, ctx.getViewHeight()-loadingSprite.getHeight()-10));
				loadingSprite.update(frameTime);
				loadingSprite.draw(ctx.window);
			}
		}
		
		if( System.currentTimeMillis()>=ignoreDamageTimer ) {
			ignoreDamageEntity=null;
			unpossess();
		}
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

}
