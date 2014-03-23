package de.secondsystem.game01.impl.game;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.jsfml.audio.Sound;
import org.jsfml.graphics.Color;
import org.jsfml.graphics.ConstView;
import org.jsfml.graphics.Sprite;
import org.jsfml.system.Vector2f;
import org.jsfml.window.Keyboard.Key;
import org.jsfml.window.event.Event;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

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
import de.secondsystem.game01.impl.game.entities.events.AttackEventHandler;
import de.secondsystem.game01.impl.game.entities.events.EventType;
import de.secondsystem.game01.impl.game.entities.events.IEventHandler;
import de.secondsystem.game01.impl.game.entities.events.KillEventHandler;
import de.secondsystem.game01.impl.game.entities.events.PingPongEventHandler;
import de.secondsystem.game01.impl.intro.MainMenuState;
import de.secondsystem.game01.impl.map.GameMap;
import de.secondsystem.game01.impl.map.IGameMap;
import de.secondsystem.game01.impl.map.IGameMapSerializer;
import de.secondsystem.game01.impl.map.JsonGameMapSerializer;
import de.secondsystem.game01.impl.map.IGameMap.WorldId;
import de.secondsystem.game01.impl.sound.MonologueTextBox;
import de.secondsystem.game01.impl.sound.MusicWrapper;
import de.secondsystem.game01.model.Attributes;
import de.secondsystem.game01.model.Attributes.Attribute;
import de.secondsystem.game01.model.GameException;

public class MainGameState extends GameState {

	private final String mapId;
	
	private MonologueTextBox monologueTextBox;
	
	private MusicWrapper backgroundMusic;
	
	private DevConsole console = new DevConsole();
	
	private GameMap map;
	
	private EffectMapRenderer mapRenderer;
	
	private Camera camera;
	
	private IControllableGameEntity player;
	
	private KeyboardController controller;
	
	private final UUID PLAYER_UUID = UUID.nameUUIDFromBytes("player".getBytes());
	
	public MainGameState( String mapId ) {
		this.mapId = mapId;
	}
	public MainGameState( String mapId, GameContext ctx ) {
		this.mapId = mapId;
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
	private IEventHandler possessedEntityDamagedHandler = new IEventHandler() {
		@Override public Attributes serialize() {
			return null;
		}
		
		@Override public Object handle(Object... args) {
			unpossess();
			return null;
		}
	};
	
	private void possess(IControllableGameEntity player, IControllableGameEntity target) {
		ignoreDamageEntity = target.uuid();
		ignoreDamageTimer = Long.MAX_VALUE;
		
		possessedEntity = target;

		possessedEntityController = possessedEntity.getController();
		controller.addGE(possessedEntity);
		controller.removeGE(player);
		player.setWorldMask(0);
		camera.setController(possessedEntity);
		possessedEntity.addEventHandler(EventType.DAMAGED, possessedEntityDamagedHandler);
	}
	private boolean unpossess() {
		if( possessedEntity!=null ) {
			ignoreDamageTimer = System.currentTimeMillis() + 5000;
			
			possessedEntity.removeEventHandler(EventType.DAMAGED, possessedEntityDamagedHandler);
			if( possessedEntity.isLiftingSomething() )
				possessedEntity.liftOrThrowObject(2);

			if( player.setWorld(WorldId.MAIN) ) {
				player.setPosition(possessedEntity.getPosition());
				player.setRotation(possessedEntity.getRotation());
				controller.addGE(player);
				controller.removeGE(possessedEntity);
				possessedEntity.setController(possessedEntityController);
	
				camera.setController(player);
				possessedEntity = null;
				
			} else
				return false;
			
			return true;
		}
		
		return false;
	}
	
	private final class PlayerAttackEventHandler extends AttackEventHandler {
		
		@Override
		protected boolean attack(IGameEntity owner, IGameEntity target,
				float force) {
			if( owner.isInWorld(WorldId.MAIN) && target instanceof IControllableGameEntity && ((IControllableGameEntity) target).isPossessable() ) {
				possess((IControllableGameEntity) owner, (IControllableGameEntity) target);
				return true;
				
			} else
				return super.attack(owner, target, force);
		}
		
	}
	
	private ThreadedMapLoader activeMapLoader;
	public final Set<Sprite> sprites = new HashSet<>();
	public class ScriptApi {
		private final GameContext ctx;
		private JSONObject storedValues;
		private final JSONParser parser = new JSONParser();
		
		public ScriptApi(GameContext ctx) {
			this.ctx = ctx;
			try ( Reader reader = Files.newBufferedReader(Paths.get("save.json"), StandardCharsets.UTF_8) ){
				storedValues = (JSONObject) parser.parse(reader);
				
			} catch (IOException | ParseException e) {
				storedValues = new JSONObject();
			}
		}
		
		public void loadMap(String mapId) {
			if( activeMapLoader==null )
				activeMapLoader = new ThreadedMapLoader(mapId, ctx);
		}
		
		public void playMonologue(String name) {
			monologueTextBox.play(name);
		}
		
		@SuppressWarnings("unchecked")
		public void store(String key, Object val) {
			storedValues.put(key, val);
			
			try ( Writer writer = Files.newBufferedWriter(Paths.get("save.json"), StandardCharsets.UTF_8) ){
				storedValues.writeJSONString(writer);
				
			} catch (IOException e) {
				System.err.println("Unable to store value: "+e.getMessage());
			}
		}
		public Object load(String key) {
			return storedValues.get(key);
		}
		public Sound playSound(String name) {
			try {
				Sound s = new Sound(ResourceManager.sound.get(name));
				s.setRelativeToListener(false);
				s.play();
				return s;
				
			} catch (IOException e) {
				System.err.println(e.getMessage());
				e.printStackTrace();
				return null;
			}
		}
		public Sprite createSprite(String texture, float x, float y) {
			try {
				Sprite s = new Sprite(ResourceManager.texture_gui.get(texture));
				s.setPosition(x, y);
				sprites.add(s);
				return s;
				
			} catch (IOException e) {
				System.err.println(e.getMessage());
				e.printStackTrace();
				return null;
			}
		}
		public void deleteSprite(Sprite sprite) {
			sprites.remove(sprite);
		}
		public void updateSpriteTex(Sprite sprite, String texture) {
			try {
				sprite.setTexture(ResourceManager.texture_gui.get(texture));
				
			} catch (IOException e) {
				System.err.println(e.getMessage());
				e.printStackTrace();
			}
		}
		public void updateSpriteColor(Sprite sprite, int r, int g, int b, int a) {
			sprite.setColor(new Color(r, g, b, a));
		}
	}
	
	protected Object createScriptApi(GameContext ctx) {
		return new ScriptApi(ctx);
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
			
		} catch (IOException e) {
			throw new GameException("Unable to load font for MonologueTextBox: "+e.getMessage(), e);
		}
		
		IGameMapSerializer mapSerializer = new JsonGameMapSerializer();
		map = mapSerializer.deserialize(ctx, mapId, true, true);
		
		map.getEntityManager().create("lever", new Attributes(new Attribute("x",300), new Attribute("y",500), new Attribute("worldId",3)) )
		.setEventHandler(EventType.USED, new PingPongEventHandler(EventType.DAMAGED));

		map.getScriptEnv().bind("API", createScriptApi(ctx));
		
		mapRenderer = new EffectMapRenderer(ctx, map);

		// lets kick the JIT in his ass
		for(int i=0; i<5*60; i++) {
			mapRenderer.update(18);
		}
		
		player = (IControllableGameEntity) map.getEntityManager().get(PLAYER_UUID);
		if( player == null )
			player = (IControllableGameEntity) map.getEntityManager().create(PLAYER_UUID, "player", new Attributes(new Attribute("x",300), new Attribute("y",100)) );

		player.addEventHandler(EventType.DAMAGED, new PlayerDeathEventHandler(map) );
		player.addEventHandler(EventType.ATTACK, new PlayerAttackEventHandler());

		camera = new Camera(player);

		controller = new KeyboardController(ctx.settings.keyMapping, new IWorldSwitchInterceptor() {
			@Override public boolean doWorldSwitch() {
				return !unpossess();
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
		if( activeMapLoader!=null && activeMapLoader.isFinished() ) {
			setNextState(activeMapLoader.getLoadedMap());
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
		
		if( System.currentTimeMillis()>=ignoreDamageTimer ) {
			ignoreDamageEntity=null;
		}
	}
	
	@Override
	protected void processEvent(GameContext ctx, Event event) {
		if( event.type==Event.Type.KEY_RELEASED ) {
        	if( event.asKeyEvent().key==Key.F12 ) {
        		setNextState(new EditorGameState(this, map));
        	}
        	if( event.asKeyEvent().key==Key.ESCAPE ) {
        		setNextState(new MainMenuState(this));
        	}
        }
        
        controller.processEvents(event);
	}

}
