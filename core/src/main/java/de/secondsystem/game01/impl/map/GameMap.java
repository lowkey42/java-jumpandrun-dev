package de.secondsystem.game01.impl.map;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.ConstView;
import org.jsfml.graphics.RenderTarget;
import org.jsfml.graphics.View;
import org.jsfml.system.Vector2f;

public class GameMap {

	public static final class World {
		final Layer[] graphicLayer;
		Color backgroundColor;
		
		World() {
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
	
	final World world[] = new World[2];
	
	private Tileset tileset;
	
	private int activeWorld;
	
	final boolean editable;
	
	final boolean playable;
	
	public GameMap(String mapId, Tileset tileset) {
		this.mapId = mapId;
		world[0] = new World();
		world[1] = new World();

		this.tileset = tileset;
		this.editable = true;
		this.playable = true;
	}
	GameMap(String mapId, Tileset tileset, boolean playable, boolean editable) {
		this.mapId = mapId;
		world[0] = new World();
		world[1] = new World();
		
		this.tileset = tileset;
		this.editable = editable;
		this.playable = playable;
		this.activeWorld = 0;
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
		activeWorld = activeWorld==0 ? 1 : 0;
	}
	
	public void draw(RenderTarget rt) {
		final ConstView cView = rt.getView();
		
		rt.clear(world[activeWorld].backgroundColor);
		
		for( LayerType l : LayerType.values() ) {
			Layer layer = world[activeWorld].graphicLayer[l.layerIndex];
			
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

	public void addNode( int worldId, LayerType layer, LayerObject sprite ) {
		world[worldId].graphicLayer[layer.layerIndex].addNode(sprite);
	}
	public void addNode( LayerType layer, LayerObject sprite ) {
		addNode(activeWorld, layer, sprite);
	}
	public LayerObject findNode( LayerType layer, Vector2f point ) {
		return world[activeWorld].graphicLayer[layer.layerIndex].findNode(point);
	}
	public void remove( LayerType layer, LayerObject s ) {
		world[activeWorld].graphicLayer[layer.layerIndex].remove(s);
	}
		
	public boolean flipShowLayer( LayerType layer ) {
		return world[activeWorld].graphicLayer[layer.layerIndex].show = !world[activeWorld].graphicLayer[layer.layerIndex].show;
	}
	public boolean[] getShownLayer() {
		boolean[] s = new boolean[LayerType.LAYER_COUNT];
		for( int i=0; i<LayerType.LAYER_COUNT; ++i )
			s[i] = world[activeWorld].graphicLayer[i].show;
		
		return s;
	}
	
}
