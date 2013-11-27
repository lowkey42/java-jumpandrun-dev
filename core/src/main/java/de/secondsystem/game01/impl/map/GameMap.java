package de.secondsystem.game01.impl.map;

import java.util.HashSet;
import java.util.Set;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.ConstView;
import org.jsfml.graphics.RenderTarget;
import org.jsfml.graphics.View;
import org.jsfml.system.Vector2f;

import de.secondsystem.game01.impl.game.entities.GameEntityManager;
import de.secondsystem.game01.impl.game.entities.IGameEntityManager;
import de.secondsystem.game01.impl.map.physics.Box2dPhysicalWorld;
import de.secondsystem.game01.impl.map.physics.IPhysicalWorld;
import de.secondsystem.game01.impl.scripting.ScriptEnvironment;
import de.secondsystem.game01.impl.scripting.ScriptEnvironment.ScriptType;
import de.secondsystem.game01.model.Attributes.Attribute;

public class GameMap implements IGameMap {
	
	public static final class GameWorld {
		final Layer[] graphicLayer;
		Color backgroundColor;
		
		GameWorld() {
			graphicLayer = new Layer[LayerType.LAYER_COUNT];
			for( LayerType l : LayerType.values() )
				graphicLayer[l.layerIndex] = new Layer(l);
			
			backgroundColor = Color.BLACK;
		}
		void addNode( LayerType layer, LayerObject sprite ) {
			addNode(layer.layerIndex, sprite);
		}
		void addNode( int layerIndex, LayerObject sprite ) {
			graphicLayer[layerIndex].addNode(sprite);
		}
	}
	
	private String mapId;
	
	final GameWorld gameWorld[] = new GameWorld[2];
	
	private Tileset tileset;
	
	private int activeWorldId;
	
	public final boolean editable;
	
	public final boolean playable;
	
	private final IPhysicalWorld physicalWorld;
	
	private final IGameEntityManager entityManager;
	
	private final Set<IWorldSwitchListener> worldSwitchListeners = new HashSet<>();
	
	final ScriptEnvironment scripts; 
	
	
	public GameMap(String mapId, Tileset tileset) {
		this(mapId, tileset, true, true);
	}
	
	GameMap(String mapId, Tileset tileset, boolean playable, boolean editable) {
		this.mapId = mapId;
		gameWorld[0] = new GameWorld();
		gameWorld[1] = new GameWorld();
		
		this.tileset = tileset;
		this.editable = editable;
		this.playable = playable;
		this.activeWorldId = 0;

		if( playable ) {
			physicalWorld = new Box2dPhysicalWorld();
			physicalWorld.init(new Vector2f(0, 11.f));
		} else
			physicalWorld = null;
		
		entityManager = new GameEntityManager(this);

		scripts = new ScriptEnvironment(ScriptType.JAVA_SCRIPT, new Attribute("mapId", mapId), new Attribute("map", this), new Attribute("entities", entityManager) );
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
		activeWorldId = activeWorldId==0 ? 1 : 0;
		
		for( IWorldSwitchListener listener : worldSwitchListeners )
			listener.onWorldSwitch(activeWorldId);
	}
	
	/* (non-Javadoc)
	 * @see de.secondsystem.game01.impl.map.IGameMap#draw(org.jsfml.graphics.RenderTarget)
	 */
	@Override
	public void draw(RenderTarget rt) {
		final ConstView cView = rt.getView();
		
		rt.clear(gameWorld[activeWorldId].backgroundColor);
		
		for( LayerType l : LayerType.values() ) {
			Layer layer = gameWorld[activeWorldId].graphicLayer[l.layerIndex];
			
			if( layer.show ) {
				if( l.parallax!=1.f )
					rt.setView( new View(Vector2f .mul(cView.getCenter(), l.parallax), cView.getSize()) );
				else
					rt.setView(cView);
				
				layer.draw(rt);
			}
			
			if( l==LayerType.OBJECTS )
				entityManager.draw(rt);
		}
		
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
			
	}
	
	
	/* (non-Javadoc)
	 * @see de.secondsystem.game01.impl.map.IGameMap#getActiveWorldId()
	 */
	@Override
	public int getActiveWorldId() {
		return activeWorldId;
	}

	@Override
	public void setActiveWorldId(int worldId) {
		this.activeWorldId = worldId;
	}

	/* (non-Javadoc)
	 * @see de.secondsystem.game01.impl.map.IGameMap#addNode(int, de.secondsystem.game01.impl.map.LayerType, de.secondsystem.game01.impl.map.LayerObject)
	 */
	@Override
	public void addNode( int worldId, LayerType layer, LayerObject sprite ) {
		gameWorld[worldId].graphicLayer[layer.layerIndex].addNode(sprite);
	}
	/* (non-Javadoc)
	 * @see de.secondsystem.game01.impl.map.IGameMap#addNode(de.secondsystem.game01.impl.map.LayerType, de.secondsystem.game01.impl.map.LayerObject)
	 */
	@Override
	public void addNode( LayerType layer, LayerObject sprite ) {
		addNode(activeWorldId, layer, sprite);
	}
	/* (non-Javadoc)
	 * @see de.secondsystem.game01.impl.map.IGameMap#findNode(de.secondsystem.game01.impl.map.LayerType, org.jsfml.system.Vector2f)
	 */
	@Override
	public LayerObject findNode( LayerType layer, Vector2f point ) {
		return gameWorld[activeWorldId].graphicLayer[layer.layerIndex].findNode(point);
	}
	/* (non-Javadoc)
	 * @see de.secondsystem.game01.impl.map.IGameMap#remove(de.secondsystem.game01.impl.map.LayerType, de.secondsystem.game01.impl.map.LayerObject)
	 */
	@Override
	public void remove( LayerType layer, LayerObject s ) {
		gameWorld[activeWorldId].graphicLayer[layer.layerIndex].remove(s);
	}
		
	/* (non-Javadoc)
	 * @see de.secondsystem.game01.impl.map.IGameMap#flipShowLayer(de.secondsystem.game01.impl.map.LayerType)
	 */
	@Override
	public boolean flipShowLayer( LayerType layer ) {
		return gameWorld[activeWorldId].graphicLayer[layer.layerIndex].show = !gameWorld[activeWorldId].graphicLayer[layer.layerIndex].show;
	}
	/* (non-Javadoc)
	 * @see de.secondsystem.game01.impl.map.IGameMap#getShownLayer()
	 */
	@Override
	public boolean[] getShownLayer() {
		boolean[] s = new boolean[LayerType.LAYER_COUNT];
		for( int i=0; i<LayerType.LAYER_COUNT; ++i )
			s[i] = gameWorld[activeWorldId].graphicLayer[i].show;
		
		return s;
	}

	/* (non-Javadoc)
	 * @see de.secondsystem.game01.impl.map.IGameMap#getPhysicalWorld()
	 */
	@Override
	public IPhysicalWorld getPhysicalWorld() {
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
	
}
