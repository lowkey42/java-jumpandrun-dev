package de.secondsystem.game01.impl.game;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

import org.jsfml.graphics.ConstView;
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
	
	private Camera camera;
	
	private IControllableGameEntity player;
	
	private KeyboardController controller;
	
	private final String PLAYER_UUID = "aa013690-1408-4a13-8329-cbfb1cfa7f6b";
	
	public MainGameState( String mapId ) {
		this.mapId = mapId;
	}
	
	private final class PlayerDeathEventHandler extends KillEventHandler {

		@Override
		protected void killEntity(IGameEntity entity) {
			setNextState(new GameOverGameState());
		}
	}
	
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
			possessedEntity.removeEventHandler(EventType.DAMAGED, possessedEntityDamagedHandler);
			if( possessedEntity.isLiftingSomething() )
				possessedEntity.liftOrThrowObject(2);
				
			player.setPosition(possessedEntity.getPosition());
			player.setRotation(possessedEntity.getRotation());
			player.setWorld(WorldId.MAIN);
			controller.addGE(player);
			controller.removeGE(possessedEntity);
			possessedEntity.setController(possessedEntityController);

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
			if( owner.isInWorld(WorldId.MAIN) && target instanceof IControllableGameEntity && ((IControllableGameEntity) target).isPossessable() ) {
				possess((IControllableGameEntity) owner, (IControllableGameEntity) target);
				return true;
				
			} else
				return super.attack(owner, target, force);
		}
		
	}
	
	public class ScriptApi {
		@SuppressWarnings("unused")
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
			setNextState(new MainGameState(mapId));
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
	}
	
	protected Object createScriptApi(GameContext ctx) {
		return new ScriptApi(ctx);
	}
	
	@Override
	protected void onStart(GameContext ctx) {
		if( backgroundMusic==null )
			backgroundMusic = new MusicWrapper((short) (ctx.settings.volume*0.5));
		else
			backgroundMusic.play();
		
		if( monologueTextBox==null )
			try {
				monologueTextBox = new MonologueTextBox(ResourceManager.font.get("FreeSans.otf"), 30, new Vector2f(ctx.getViewWidth()/2, ctx.getViewHeight()-100));
				
			} catch (IOException e) {
				throw new GameException("Unable to load font for MonologueTextBox: "+e.getMessage(), e);
			}
		
		if( map!=null ) {
			map.setFade(true);
			backgroundMusic.play();
			
		} else {
			IGameMapSerializer mapSerializer = new JsonGameMapSerializer();
			map = mapSerializer.deserialize(ctx, mapId, true, true);
			
			map.getEntityManager().create("lever", new Attributes(new Attribute("x",300), new Attribute("y",500), new Attribute("worldId",3)) )
			.setEventHandler(EventType.USED, new PingPongEventHandler(EventType.DAMAGED));

			map.getScriptEnv().bind("API", createScriptApi(ctx));
		}
		
		
		player = (IControllableGameEntity) map.getEntityManager().get(UUID.fromString(PLAYER_UUID));
		if( player == null )
			player = (IControllableGameEntity) map.getEntityManager().create(UUID.fromString(PLAYER_UUID), "player", new Attributes(new Attribute("x",300), new Attribute("y",100)) );

		player.addEventHandler(EventType.DAMAGED, new PlayerDeathEventHandler() );
		player.addEventHandler(EventType.ATTACK, new PlayerAttackEventHandler());
		
		camera = new Camera(player);
			
//		// something like this will be implemented in the editor
//		IGameEntity entity = map.getEntityManager().create( "lever", new Attributes(new Attribute("x",210), new Attribute("y",270)) );
//		//IGameEntity explosion = map.getEntityManager().create( "explosion", new Attributes(new Attribute("x",50), new Attribute("y",-80)) );
//		IGameEntity fire1 = map.getEntityManager().create( "fire", new Attributes(new Attribute("x",-50), new Attribute("y",200)) );
//		IGameEntity fire2 = map.getEntityManager().create( "fire", new Attributes(new Attribute("x",-50), new Attribute("y",250)) );
//		IGameEntity fire3 = map.getEntityManager().create( "fire", new Attributes(new Attribute("x",-50), new Attribute("y",300)) );
//		
//		CollectionEntityEventHandler eventHandler = map.getEventManager().createCollectionEntityEventHandler();
//		AnimatedSequencedEntity animSequencedEntity = sequenceManager.createAnimatedSequencedEntity(entity);
//		Toggle toggle = sequenceManager.createToggle();
//		//toggle.inputOption.toggleTrigger.put(entity, animSequencedEntity);
//		toggle.addTarget(sequenceManager.createAnimatedSequencedEntity(fire1));
//		toggle.addTarget(sequenceManager.createAnimatedSequencedEntity(fire2));
//		toggle.addTarget(sequenceManager.createAnimatedSequencedEntity(fire3));
//		toggle.addTarget(animSequencedEntity);
//		IControllableGameEntity movingPlatform = map.getEntityManager().createControllable("moving platform", new Attributes(new Attribute("x",150), new Attribute("y",100)) );
//		PatrollingController movingPlatformCon = map.getControllerManager().createPatrollingController(movingPlatform, false);
//		movingPlatformCon.addTargetPoint(300, 100);
//		movingPlatformCon.addTargetPoint(150, 100);
//		movingPlatformCon.addTargetPoint(150, -100);
//		Condition isOwnerKinematic = sequenceManager.createCondition();
//		isOwnerKinematic.inTriggers.put(entity, animSequencedEntity);
//		isOwnerKinematic.add(toggle, isOwnerKinematic.outputOption.isOwnerKinematic, toggle.inputOption.toggleTriggers);
//		toggle.addTarget(sequenceManager.createControllableSequencedEntity(movingPlatformCon));
//		SequencedEntityEventHandler handler = map.getEventManager().createSequencedEntityEventHandler(EntityEventType.USED, isOwnerKinematic);
//		eventHandler.addEntityEventHandler(EntityEventType.USED, handler);	
//		entity.setEventHandler(eventHandler);
		
		
		console.setScriptEnvironment(map.getScriptEnv());
		
		controller = new KeyboardController(ctx.settings.keyMapping, new IWorldSwitchInterceptor() {
			@Override public boolean doWorldSwitch() {
				return !unpossess();
			}
		});
		controller.addGE(player);
	}
	
	@Override
	protected void onStop(GameContext ctx) {
		backgroundMusic.pause();
	}

	@Override
	protected void onFrame(GameContext ctx, long frameTime) {
		console.update(frameTime);
		
		controller.update(frameTime);
		
		// update worlds
		map.update(frameTime);

		camera.update(frameTime);

		backgroundMusic.fade(map.getDefaultBgMusic(camera.getWorldId()), 5000);
		backgroundMusic.update(frameTime);
		
		monologueTextBox.update(frameTime);
		
		final ConstView cView = ctx.window.getView();
		ctx.window.setView(camera.createView(cView));
		map.setActiveWorldId(camera.getWorldId());
		
		// drawing
		map.draw(ctx.window);
		
		ctx.window.setView(cView);
		
		monologueTextBox.draw(ctx.window);
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
