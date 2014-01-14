package de.secondsystem.game01.impl.map;

import java.util.HashSet;
import java.util.Set;

import org.jsfml.graphics.BlendMode;
import org.jsfml.graphics.Color;
import org.jsfml.graphics.ConstView;
import org.jsfml.graphics.FloatRect;
import org.jsfml.graphics.RenderStates;
import org.jsfml.graphics.RenderTarget;
import org.jsfml.graphics.RenderTexture;
import org.jsfml.graphics.Sprite;
import org.jsfml.graphics.TextureCreationException;
import org.jsfml.graphics.View;
import org.jsfml.system.Vector2f;

import de.secondsystem.game01.impl.GameContext;
import de.secondsystem.game01.impl.game.controller.ControllerManager;
import de.secondsystem.game01.impl.game.entities.GameEntityManager;
import de.secondsystem.game01.impl.game.entities.IGameEntity;
import de.secondsystem.game01.impl.game.entities.IGameEntityManager;
import de.secondsystem.game01.impl.game.entities.events.EventManager;
import de.secondsystem.game01.impl.game.entities.events.impl.SequenceManager;
import de.secondsystem.game01.impl.graphic.LightMap;
import de.secondsystem.game01.impl.map.objects.EntityLayer;
import de.secondsystem.game01.impl.map.objects.LightLayer;
import de.secondsystem.game01.impl.map.objects.SimpleLayer;
import de.secondsystem.game01.impl.map.physics.Box2dPhysicalWorld;
import de.secondsystem.game01.impl.map.physics.IPhysicsWorld;
import de.secondsystem.game01.impl.scripting.ScriptEnvironment;
import de.secondsystem.game01.impl.scripting.ScriptEnvironment.ScriptType;
import de.secondsystem.game01.impl.timer.TimerManager;
import de.secondsystem.game01.model.Attributes.Attribute;

public class GameMap implements IGameMap {
	
	public static final class GameWorld {
		final ILayer[] graphicLayer;
		Color backgroundColor;
		
		GameWorld( WorldId worldId, IGameEntityManager entityManager, LightMap lightMap ) {
			graphicLayer = new ILayer[LayerType.LAYER_COUNT];
			for( LayerType l : LayerType.values() )
				switch( l ) {
					case OBJECTS:
						graphicLayer[l.layerIndex] = new EntityLayer(l, entityManager, worldId);
						break;
						
					case LIGHTS:
						graphicLayer[l.layerIndex] = new LightLayer(l, lightMap);
						break;
						
					default:
						graphicLayer[l.layerIndex] = new SimpleLayer(l);
				}
			
			backgroundColor = Color.BLACK;
		}
		void addNode( LayerType layer, ILayerObject sprite ) {
			addNode(layer.layerIndex, sprite);
		}
		void addNode( int layerIndex, ILayerObject sprite ) {
			graphicLayer[layerIndex].addNode(sprite);
		}
	}
	
	private String mapId;
	
	final GameWorld gameWorld[] = new GameWorld[2];
	
	private Tileset tileset;
	
	private WorldId activeWorldId;
	
	public final boolean editable;
	
	public final boolean playable;
	
	private final IPhysicsWorld physicalWorld;
	
	private final IGameEntityManager entityManager;
	
	private final SequenceManager sequenceManager = new SequenceManager();
	
	private final ControllerManager controllerManager = new ControllerManager();
	
	private final EventManager eventManager = new EventManager();
	
	private final Set<IWorldSwitchListener> worldSwitchListeners = new HashSet<>();
	
	private final LightMap lightMap;
	
	final ScriptEnvironment scripts; 
	
	private final TimerManager timerManager;
	
	public GameMap(GameContext ctx, String mapId, Tileset tileset, Color ambientLight) {
		this(ctx, mapId, tileset, ambientLight, true, true);
	}
	
	GameMap(GameContext ctx, String mapId, Tileset tileset, Color ambientLight, boolean playable, boolean editable) {
		this.mapId = mapId;
		this.tileset = tileset;
		this.editable = editable;
		this.playable = playable;
		this.activeWorldId = WorldId.MAIN;

		lightMap = ctx!=null && ctx.settings.dynamicLight ? new LightMap(ctx.getViewWidth(), ctx.getViewHeight(), ambientLight) : null;
		
		if( playable ) {
			physicalWorld = new Box2dPhysicalWorld();
			physicalWorld.init(new Vector2f(0, 15.f));
		} else
			physicalWorld = null;
		
		entityManager = new GameEntityManager(this);	

		for( WorldId wId : WorldId.values() )
			gameWorld[wId.arrayIndex] = new GameWorld(wId, entityManager, lightMap);
		
		
		scripts = new ScriptEnvironment(ScriptType.JAVA_SCRIPT, new Attribute("mapId", mapId), 
				new Attribute("map", this), new Attribute("entities", entityManager), new Attribute("events", eventManager));
		
		timerManager = new TimerManager(scripts);
		
		eventManager.setScriptEnvironment(scripts);	
		
		try {
			renderBuffer.create(1920, 1080);
			fadeBuffer.create(1920, 1080);
		} catch (TextureCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
		
	/* (non-Javadoc)
	 * @see de.secondsystem.game01.impl.map.IGameMap#getMapId()
	 */
	@Override
	public String getMapId() {
		return mapId;
	}
	/* (non-Javadoc)
	 * @see de.secondsystem.game01.impl.map.IGameMap#setMapId(java.lang.String)
	 */
	@Override
	public void setMapId(String mapId) {
		this.mapId = mapId;
	}
	/* (non-Javadoc)
	 * @see de.secondsystem.game01.impl.map.IGameMap#setTileset(de.secondsystem.game01.impl.map.Tileset)
	 */
	@Override
	public void setTileset(Tileset tileset) {
		this.tileset = tileset;
	}
	/* (non-Javadoc)
	 * @see de.secondsystem.game01.impl.map.IGameMap#getTileset()
	 */
	@Override
	public Tileset getTileset() {
		return tileset;
	}
	
	// objectLayer
	// collisionLayer
	// triggerLayer
	// particleLayer
	
	
	/* (non-Javadoc)
	 * @see de.secondsystem.game01.impl.map.IGameMap#switchWorlds()
	 */
	@Override
	public void switchWorlds() {
		activeWorldId = activeWorldId==WorldId.MAIN ? WorldId.OTHER : WorldId.MAIN;
		
		for( IWorldSwitchListener listener : worldSwitchListeners )
			listener.onWorldSwitch(activeWorldId);
		
		fadeTimeLeft=FADE_TIME;
	}
	

	private final RenderTexture renderBuffer = new RenderTexture();

	private final RenderTexture fadeBuffer = new RenderTexture();
	
	private int fadeTimeLeft;
	
	private boolean fadeEnabled = true;
	
	private static final int FADE_TIME = 1000;
	
	/* (non-Javadoc)
	 * @see de.secondsystem.game01.impl.map.IGameMap#draw(org.jsfml.graphics.RenderTarget)
	 */
	@Override
	public void draw(RenderTarget rt) {
		final ConstView cView = rt.getView();
		lightMap.setView(cView);
		lightMap.clear();
		
		renderBuffer.clear(gameWorld[activeWorldId.arrayIndex].backgroundColor);
		
		for( LayerType l : LayerType.values() ) {
			ILayer layer = gameWorld[activeWorldId.arrayIndex].graphicLayer[l.layerIndex];
			
			if( layer.isVisible() ) {
				if( l.parallax!=1.f )
					renderBuffer.setView( new View(Vector2f .mul(cView.getCenter(), l.parallax), cView.getSize()) );
				else
					renderBuffer.setView(cView);
				 
				layer.draw(renderBuffer);
			}
		}

		if( fadeTimeLeft>0 && fadeEnabled ) {
			fadeBuffer.clear(Color.TRANSPARENT);
			
			final WorldId inactiveWorld = activeWorldId==WorldId.MAIN ? WorldId.OTHER : WorldId.MAIN;
			for( LayerType l : LayerType.values() ) {
				ILayer layer = gameWorld[inactiveWorld.arrayIndex].graphicLayer[l.layerIndex];
				
				if( layer.isVisible() && l.fade ) {
					if( l.parallax!=1.f )
						fadeBuffer.setView( new View(Vector2f .mul(cView.getCenter(), l.parallax), cView.getSize()) );
					else
						fadeBuffer.setView(cView);
					 
					layer.draw(fadeBuffer);
				}
			}
			
			fadeBuffer.display();
			
			Sprite fadeSprite = new Sprite(fadeBuffer.getTexture());
			fadeSprite.setColor(new Color(255, 255, 255, (int) (((float)fadeTimeLeft)/FADE_TIME*255.f) ));

			renderBuffer.setView(new View(Vector2f.div(cView.getSize(), 2), cView.getSize()));
			renderBuffer.draw(fadeSprite, new RenderStates(BlendMode.ALPHA));
			renderBuffer.setView(cView);
		}

		lightMap.draw(renderBuffer);
		
		renderBuffer.display();
		
		rt.setView(new View(Vector2f.div(cView.getSize(), 2), cView.getSize()));
		rt.draw(new Sprite(renderBuffer.getTexture()));
		rt.setView(cView);
	}
	/* (non-Javadoc)
	 * @see de.secondsystem.game01.impl.map.IGameMap#update(long)
	 */
	@Override
	public void update(long frameTimeMs) {
		for( LayerType l : LayerType.values() )
			if( l.updated )
				for( GameWorld world : gameWorld )
					world.graphicLayer[l.layerIndex].update(frameTimeMs);
		
		entityManager.update(frameTimeMs);
		
		long ps = System.currentTimeMillis();
		
		if( physicalWorld !=null )
			physicalWorld.update(frameTimeMs);
		
		if( System.currentTimeMillis()-ps > 10 ) {
			System.out.println("pTime-Peak: "+(System.currentTimeMillis()-ps));
		}
			
		timerManager.update(frameTimeMs);
		
		fadeTimeLeft-=frameTimeMs;
	}
	
	
	/* (non-Javadoc)
	 * @see de.secondsystem.game01.impl.map.IGameMap#getActiveWorldId()
	 */
	@Override
	public WorldId getActiveWorldId() {
		return activeWorldId;
	}

	@Override
	public void setActiveWorldId(WorldId worldId) {
		if( activeWorldId!=worldId )
			switchWorlds();
	}

	/* (non-Javadoc)
	 * @see de.secondsystem.game01.impl.map.IGameMap#addNode(int, de.secondsystem.game01.impl.map.LayerType, de.secondsystem.game01.impl.map.LayerObject)
	 */
	@Override
	public void addNode( WorldId worldId, LayerType layer, ILayerObject sprite ) {
		gameWorld[worldId.arrayIndex].graphicLayer[layer.layerIndex].addNode(sprite);
	}
	/* (non-Javadoc)
	 * @see de.secondsystem.game01.impl.map.IGameMap#addNode(de.secondsystem.game01.impl.map.LayerType, de.secondsystem.game01.impl.map.LayerObject)
	 */
	@Override
	public void addNode( LayerType layer, ILayerObject sprite ) {
		addNode(activeWorldId, layer, sprite);
	}
	/* (non-Javadoc)
	 * @see de.secondsystem.game01.impl.map.IGameMap#findNode(de.secondsystem.game01.impl.map.LayerType, org.jsfml.system.Vector2f)
	 */
	@Override
	public ILayerObject findNode( LayerType layer, Vector2f point ) {
		return gameWorld[activeWorldId.arrayIndex].graphicLayer[layer.layerIndex].findNode(point);
	}
	/* (non-Javadoc)
	 * @see de.secondsystem.game01.impl.map.IGameMap#remove(de.secondsystem.game01.impl.map.LayerType, de.secondsystem.game01.impl.map.LayerObject)
	 */
	@Override
	public void remove( LayerType layer, ILayerObject s ) {
		gameWorld[activeWorldId.arrayIndex].graphicLayer[layer.layerIndex].remove(s);
	}
		
	/* (non-Javadoc)
	 * @see de.secondsystem.game01.impl.map.IGameMap#flipShowLayer(de.secondsystem.game01.impl.map.LayerType)
	 */
	@Override
	public boolean flipShowLayer( LayerType layer ) {
		return gameWorld[activeWorldId.arrayIndex].graphicLayer[layer.layerIndex].setVisible( !gameWorld[activeWorldId.arrayIndex].graphicLayer[layer.layerIndex].isVisible() );
	}
	/* (non-Javadoc)
	 * @see de.secondsystem.game01.impl.map.IGameMap#getShownLayer()
	 */
	@Override
	public boolean[] getShownLayer() {
		boolean[] s = new boolean[LayerType.LAYER_COUNT];
		for( int i=0; i<LayerType.LAYER_COUNT; ++i )
			s[i] = gameWorld[activeWorldId.arrayIndex].graphicLayer[i].isVisible();
		
		return s;
	}

	/* (non-Javadoc)
	 * @see de.secondsystem.game01.impl.map.IGameMap#getPhysicalWorld()
	 */
	@Override
	public IPhysicsWorld getPhysicalWorld() {
		return physicalWorld;
	}

	@Override
	public void registerWorldSwitchListener(IWorldSwitchListener listener) {
		worldSwitchListeners.add(listener);
	}

	@Override
	public void deregisterWorldSwitchListener(IWorldSwitchListener listener) {
		worldSwitchListeners.remove(listener);
	}

	@Override
	public boolean isEditable() {
		return editable;
	}

	@Override
	public IGameEntityManager getEntityManager() {
		return entityManager;
	}

	@Override
	public ScriptEnvironment getScriptEnv() {
		return scripts;
	}

	@Override
	public TimerManager getTimerManager() {
		return timerManager;
	}

	@Override
	public SequenceManager getSequenceManager() {
		return sequenceManager;
	}

	@Override
	public ControllerManager getControllerManager() {
		return controllerManager;
	}

	@Override
	public EventManager getEventManager() {
		return eventManager;
	}

	@Override
	public IGameEntity findEntity(Vector2f pos) {
		IGameEntity entity = entityManager.findEntity(pos);
		if( entity != null && entity.isInWorld(activeWorldId))
			return entity;
		
		return null;
	}

	@Override
	public void removeEntity(IGameEntity entity) {
		if( entity.isInWorld(activeWorldId) )
			entityManager.destroy(entity.uuid());
	}

	@Override
	public LightMap getLightMap() {
		return lightMap;
	}

	@Override
	public void setFade(boolean enable) {
		fadeEnabled = enable;
	}
	
}
