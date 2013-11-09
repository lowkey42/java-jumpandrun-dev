package de.secondsystem.game01.impl.map;

import org.jsfml.graphics.ConstView;
import org.jsfml.graphics.RenderTarget;
import org.jsfml.graphics.Sprite;
import org.jsfml.graphics.View;
import org.jsfml.system.Vector2f;

import de.secondsystem.game01.impl.map.GraphicLayer.GraphicLayerType;

public class GameMap {

	private GraphicLayer[] graphicLayer = new GraphicLayer[GraphicLayer.LAYER_COUNT];
		
	public GameMap() {
		for( GraphicLayerType l : GraphicLayerType.values() ) {
			graphicLayer[l.layerIndex] = new GraphicLayer();
		}
	}
	
	// objectLayer
	// collisionLayer
	// triggerLayer
	// particleLayer
	
	public void draw(RenderTarget rt) {
		final ConstView cView = rt.getView();
		
		for( GraphicLayerType l : GraphicLayerType.values() ) {
			GraphicLayer layer = graphicLayer[l.layerIndex];
			
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
	
	public void addNode( GraphicLayerType layer, Sprite sprite ) {
		graphicLayer[layer.layerIndex].addNode(sprite);
	}
	public Sprite findNode( GraphicLayerType layer, Vector2f point ) {
		return graphicLayer[layer.layerIndex].findNode(point);
	}
	public void remove( GraphicLayerType layer, Sprite s ) {
		graphicLayer[layer.layerIndex].remove(s);
	}
		
	public boolean flipShowLayer( GraphicLayerType layer ) {
		return graphicLayer[layer.layerIndex].show = !graphicLayer[layer.layerIndex].show;
	}
	public boolean[] getShownLayer() {
		boolean[] s = new boolean[GraphicLayer.LAYER_COUNT];
		for( int i=0; i<GraphicLayer.LAYER_COUNT; ++i )
			s[i] = graphicLayer[i].show;
		
		return s;
	}
	
}
