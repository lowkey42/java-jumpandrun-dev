package de.secondsystem.game01.impl.map;

import java.util.Arrays;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.ConstView;
import org.jsfml.graphics.RenderTarget;
import org.jsfml.graphics.Sprite;
import org.jsfml.graphics.View;
import org.jsfml.system.Vector2f;

import de.secondsystem.game01.impl.map.GameMap.World;

public class GameMap {

	public static final class World {
		final Layer[] graphicLayer;
		Tileset tileset;
		Color backgroundColor;
		
		World(String tilesetName) {
			graphicLayer = new Layer[LayerType.LAYER_COUNT];
			for( LayerType l : LayerType.values() )
				graphicLayer[l.layerIndex] = new Layer(l);
			
			tileset = new Tileset(tilesetName);
			backgroundColor = Color.BLACK;
		}
		World(Color bColor, String tilesetName) {
			graphicLayer = new Layer[LayerType.LAYER_COUNT];
			for( LayerType l : LayerType.values() )
				graphicLayer[l.layerIndex] = new Layer(l);
			
			tileset = new Tileset(tilesetName);
			backgroundColor = bColor;
		}
		void setTileset(String tilesetName) {
			tileset = new Tileset(tilesetName);
		}
	}
	
	final World world[] = new World[2];
	
	int activeWorld;
	
	public GameMap(String tilesetName1, String tilesetName2) {
		world[0] = new World(tilesetName1);
		world[1] = new World(tilesetName2);
		
		activeWorld = 0;
	}
	GameMap(World world0, World world1) {
		world[0] = world0;
		world[0] = world1;
		activeWorld = 0;
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
	
	public void addNode( LayerType layer, LayerObject sprite ) {
		world[activeWorld].graphicLayer[layer.layerIndex].addNode(sprite);
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
