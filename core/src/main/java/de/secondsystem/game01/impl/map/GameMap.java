package de.secondsystem.game01.impl.map;

import org.jsfml.graphics.ConstView;
import org.jsfml.graphics.RenderTarget;
import org.jsfml.graphics.Sprite;
import org.jsfml.graphics.View;
import org.jsfml.system.Vector2f;

public class GameMap {

	private Layer[] graphicLayer = new Layer[LayerType.LAYER_COUNT];
	
	public final Tileset tileset;
	
	public GameMap(String tilesetName) {
		for( LayerType l : LayerType.values() ) {
			graphicLayer[l.layerIndex] = new Layer();
		}
		
		tileset = new Tileset(tilesetName);
	}
	
	// objectLayer
	// collisionLayer
	// triggerLayer
	// particleLayer
	
	public void draw(RenderTarget rt) {
		final ConstView cView = rt.getView();
		
		for( LayerType l : LayerType.values() ) {
			Layer layer = graphicLayer[l.layerIndex];
			
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
		graphicLayer[layer.layerIndex].addNode(sprite);
	}
	public LayerObject findNode( LayerType layer, Vector2f point ) {
		return graphicLayer[layer.layerIndex].findNode(point);
	}
	public void remove( LayerType layer, LayerObject s ) {
		graphicLayer[layer.layerIndex].remove(s);
	}
		
	public boolean flipShowLayer( LayerType layer ) {
		return graphicLayer[layer.layerIndex].show = !graphicLayer[layer.layerIndex].show;
	}
	public boolean[] getShownLayer() {
		boolean[] s = new boolean[LayerType.LAYER_COUNT];
		for( int i=0; i<LayerType.LAYER_COUNT; ++i )
			s[i] = graphicLayer[i].show;
		
		return s;
	}
	
}
