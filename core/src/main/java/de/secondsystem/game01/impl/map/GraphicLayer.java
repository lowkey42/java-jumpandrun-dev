package de.secondsystem.game01.impl.map;

import java.util.ArrayList;
import java.util.List;

import org.jsfml.graphics.RenderTarget;
import org.jsfml.graphics.Sprite;
import org.jsfml.system.Vector2f;

public final class GraphicLayer {
	public static final int LAYER_COUNT = 5;
	
	List<Sprite> objects = new ArrayList<>();
	boolean show = true;
	
	public void draw(RenderTarget rt) {
		for( Sprite s : objects )
			rt.draw(s);
	}
	public void addNode( Sprite sprite ) {
		objects.add(sprite);
	}
	public Sprite findNode( Vector2f point ) {
		for( Sprite s : objects )
			if( s.getGlobalBounds().contains(point) )
				return s;
		
		return null;
	}
	public void remove( Sprite s ) {
		objects.remove(s);
	}
		
	public static enum GraphicLayerType {
		BACKGROUND_2(0, .8f),
		BACKGROUND_1(1, .95f),
		BACKGROUND_0(2, 1.f),
		FOREGROUND_0(3, 1.f),
		FOREGROUND_1(4, 1.f);

		public final int layerIndex;
		public final float parallax;
		private GraphicLayerType(int _layerIndex, float _parallax) {
			layerIndex = _layerIndex;
			parallax = _parallax;
		}
	}
}