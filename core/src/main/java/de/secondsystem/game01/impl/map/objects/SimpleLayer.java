package de.secondsystem.game01.impl.map.objects;

import java.util.ArrayList;
import java.util.List;

import org.jsfml.graphics.RenderTarget;
import org.jsfml.system.Vector2f;

import de.secondsystem.game01.impl.map.ILayer;
import de.secondsystem.game01.impl.map.ILayerObject;
import de.secondsystem.game01.impl.map.LayerType;
import de.secondsystem.game01.impl.map.IGameMap.WorldId;
import de.secondsystem.game01.model.IUpdateable;

public class SimpleLayer implements ILayer {
	
	protected final List<ILayerObject> objects = new ArrayList<>();
	protected final LayerType type;
	protected final WorldId worldId;
	protected boolean show;
	
	public SimpleLayer( LayerType type, WorldId worldId ) {
		this.worldId = worldId;
		this.type = type;
		show = type.visible;
	}
	
	/* (non-Javadoc)
	 * @see de.secondsystem.game01.impl.map.ILayer#draw(org.jsfml.graphics.RenderTarget)
	 */
	@Override
	public void draw(RenderTarget rt) {
		if( show )
			for( ILayerObject s : objects )
				s.draw(rt);
	}
	/* (non-Javadoc)
	 * @see de.secondsystem.game01.impl.map.ILayer#addNode(de.secondsystem.game01.impl.map.ILayerObject)
	 */
	@Override
	public final void addNode( ILayerObject obj ) {
		obj.setWorld(worldId, true);
		obj.setLayerType(type);
		objects.add(obj);
	}
	
	@Override
	public void replaceNode(ILayerObject obj, ILayerObject nObj) {
		int index = objects.indexOf(obj);
		if( index>=0 ) {
			remove(obj);
			
			nObj.setWorld(worldId, true);
			nObj.setLayerType(type);
			objects.add(index, nObj);
		}
	}
	
	/* (non-Javadoc)
	 * @see de.secondsystem.game01.impl.map.ILayer#findNode(org.jsfml.system.Vector2f)
	 */
	@Override
	public ILayerObject findNode( Vector2f point ) {
		for( ILayerObject o : objects )
			if( o.inside(point) )
				return o;
		
		return null;
	}
	/* (non-Javadoc)
	 * @see de.secondsystem.game01.impl.map.ILayer#remove(de.secondsystem.game01.impl.map.ILayerObject)
	 */
	@Override
	public void remove( ILayerObject obj ) {
		obj.setWorld(worldId, false);
		objects.remove(obj);
	}
	
	/* (non-Javadoc)
	 * @see de.secondsystem.game01.impl.map.ILayer#update(long)
	 */
	@Override
	public void update(long frameTimeMs) {
		for( ILayerObject s : objects )
			if( s instanceof IUpdateable )
				((IUpdateable)s).update(frameTimeMs);
	}

	@Override
	public boolean isVisible() {
		return show;
	}

	@Override
	public boolean setVisible(boolean visible) {
		return show=visible;
	}

	@Override
	public List<ILayerObject> listAll() {
		return objects;
	}
	
}