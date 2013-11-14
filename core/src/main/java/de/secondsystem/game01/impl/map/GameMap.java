package de.secondsystem.game01.impl.map;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.ConstView;
import org.jsfml.graphics.RenderTarget;
import org.jsfml.graphics.View;
import org.jsfml.system.Vector2f;

import de.secondsystem.game01.impl.map.physics.Box2dPhysicalWorld;
import de.secondsystem.game01.impl.map.physics.IPhysicalWorld;

public class GameMap {
	
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
	
	private int activeGameWorldId;
	
	public final boolean editable;
	
	public final boolean playable;
	
	private final IPhysicalWorld physicalWorld;
	
	public GameMap(String mapId, Tileset tileset) {
		this.mapId = mapId;
		gameWorld[0] = new GameWorld();
		gameWorld[1] = new GameWorld();

		this.tileset = tileset;
		this.editable = true;
		this.playable = true;

		if( playable ) {
			physicalWorld = new Box2dPhysicalWorld();
			physicalWorld.init(new Vector2f(0, 11.f));
		} else
			physicalWorld = null;
	}
	
	GameMap(String mapId, Tileset tileset, boolean playable, boolean editable) {
		this.mapId = mapId;
		gameWorld[0] = new GameWorld();
		gameWorld[1] = new GameWorld();
		
		this.tileset = tileset;
		this.editable = editable;
		this.playable = playable;
		this.activeGameWorldId = 0;

		if( playable ) {
			physicalWorld = new Box2dPhysicalWorld();
			physicalWorld.init(new Vector2f(0, 11.f));
		} else
			physicalWorld = null;
	}
	
	public String getMapId() {
		return mapId;
	}
	public void setMapId(String mapId) {
		this.mapId = mapId;
	}
	public void setTileset(Tileset tileset) {
		this.tileset = tileset;
	}
	public Tileset getTileset() {
		return tileset;
	}
	
	// objectLayer
	// collisionLayer
	// triggerLayer
	// particleLayer
	
	
	public void switchWorlds() {
		activeGameWorldId = activeGameWorldId==0 ? 1 : 0;
		
		for( LayerType l : LayerType.values() ) 
			gameWorld[activeGameWorldId].graphicLayer[l.layerIndex].onGameWorldSwitch(activeGameWorldId);
	}
	
	public void draw(RenderTarget rt) {
		final ConstView cView = rt.getView();
		
		rt.clear(gameWorld[activeGameWorldId].backgroundColor);
		
		for( LayerType l : LayerType.values() ) {
			Layer layer = gameWorld[activeGameWorldId].graphicLayer[l.layerIndex];
			
			// parallax explanation can be found in the class "LayerType"
			if( layer.show ) {
				if( l.parallax!=1.f )
					rt.setView( new View(Vector2f .mul(cView.getCenter(), l.parallax), cView.getSize()) );
				else
					rt.setView(cView);
				
				layer.draw(rt);
			}
		}
		
		rt.setView(cView);
	}
	public void update(long frameTimeMs) {
		for( LayerType l : LayerType.values() )
			if( l.updated )
				for( GameWorld world : gameWorld )
					world.graphicLayer[l.layerIndex].update(frameTimeMs);
		
		if( physicalWorld !=null )
			physicalWorld.update(frameTimeMs);
	}
	
	public int getActiveGameWorldId() {
		return activeGameWorldId;
	}

	public void addNode( int worldId, LayerType layer, LayerObject sprite ) {
		gameWorld[worldId].graphicLayer[layer.layerIndex].addNode(sprite);
	}
	public void addNode( LayerType layer, LayerObject sprite ) {
		addNode(activeGameWorldId, layer, sprite);
	}
	public LayerObject findNode( LayerType layer, Vector2f point ) {
		return gameWorld[activeGameWorldId].graphicLayer[layer.layerIndex].findNode(point);
	}
	public void remove( LayerType layer, LayerObject s ) {
		gameWorld[activeGameWorldId].graphicLayer[layer.layerIndex].remove(s);
	}
		
	public boolean flipShowLayer( LayerType layer ) {
		return gameWorld[activeGameWorldId].graphicLayer[layer.layerIndex].show = !gameWorld[activeGameWorldId].graphicLayer[layer.layerIndex].show;
	}
	public boolean[] getShownLayer() {
		boolean[] s = new boolean[LayerType.LAYER_COUNT];
		for( int i=0; i<LayerType.LAYER_COUNT; ++i )
			s[i] = gameWorld[activeGameWorldId].graphicLayer[i].show;
		
		return s;
	}

	public IPhysicalWorld getPhysicalWorld() {
		return physicalWorld;
	}
	
}
