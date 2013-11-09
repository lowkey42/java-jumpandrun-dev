package de.secondsystem.game01.impl.map;

import java.util.ArrayList;
import java.util.List;

import org.jsfml.graphics.RenderTarget;
import org.jsfml.graphics.Sprite;
import org.jsfml.system.Vector2f;

public final class Layer {
	
	List<LayerObject> objects = new ArrayList<>();
	boolean show = true;
	
	public void draw(RenderTarget rt) {
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