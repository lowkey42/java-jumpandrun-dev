package de.secondsystem.game01.impl.map;

import java.util.ArrayList;
import java.util.List;

import org.jsfml.graphics.RenderTarget;
import org.jsfml.system.Vector2f;

final class Layer {
	
	List<LayerObject> objects = new ArrayList<>();
	final LayerType type;
	boolean show;
	
	public Layer( LayerType type ) {
		this.type = type;
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
	
	public void update(long frameTimeMs) {
		for( LayerObject s : objects )
			if( s instanceof IUpdateable )
				((IUpdateable)s).update(frameTimeMs);
	}
	
}