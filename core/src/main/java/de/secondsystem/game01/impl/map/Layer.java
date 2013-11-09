package de.secondsystem.game01.impl.map;

import java.util.ArrayList;
import java.util.List;

import org.jsfml.graphics.RenderTarget;
import org.jsfml.system.Vector2f;

final class Layer {
	
	List<LayerObject> objects = new ArrayList<>();
	boolean show;
	
	public Layer( LayerType type ) {
		show = type.visible;
	}
	
	public void draw(RenderTarget rt) {
		if( show )
			for( LayerObject s : objects )
				s.draw(rt);
	}
	public void addNode( LayerObject obj ) {
		objects.add(obj);
	}
	public LayerObject findNode( Vector2f point ) {
		for( LayerObject o : objects )
			if( o.inside(point) )
				return o;
		
		return null;
	}
	public void remove( LayerObject s ) {
		objects.remove(s);
	}
	
}