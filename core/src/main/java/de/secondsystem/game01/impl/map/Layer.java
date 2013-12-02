package de.secondsystem.game01.impl.map;

import java.util.ArrayList;
import java.util.List;

import org.jsfml.graphics.RenderTarget;
import org.jsfml.system.Vector2f;

import de.secondsystem.game01.model.IUpdateable;

final class Layer {
	
	List<ILayerObject> objects = new ArrayList<>();
	final LayerType type;
	boolean show;
	
	public Layer( LayerType type ) {
		this.type = type;
		show = type.visible;
	}
	
	public void draw(RenderTarget rt) {
		if( show )
			for( ILayerObject s : objects )
				s.draw(rt);
	}
	public void addNode( ILayerObject obj ) {
		objects.add(obj);
	}
	public ILayerObject findNode( Vector2f point ) {
		for( ILayerObject o : objects )
			if( o.inside(point) )
				return o;
		
		return null;
	}
	public void remove( ILayerObject s ) {
		objects.remove(s);
	}
	
	public void update(long frameTimeMs) {
		for( ILayerObject s : objects )
			if( s instanceof IUpdateable )
				((IUpdateable)s).update(frameTimeMs);
	}
	
}