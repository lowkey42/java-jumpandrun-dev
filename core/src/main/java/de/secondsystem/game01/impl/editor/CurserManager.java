package de.secondsystem.game01.impl.editor;

import java.util.HashSet;
import java.util.Set;

import org.jsfml.system.Vector2f;

import de.secondsystem.game01.impl.editor.LayerPanel.IOnLayerChangedListener;
import de.secondsystem.game01.impl.map.ILayerObject;
import de.secondsystem.game01.impl.map.LayerType;

public final class CurserManager implements IOnLayerChangedListener {
	
	public static interface ISelectionChangedListener {
		void onSelectionChanged(IEditorCurser newSelection);
	}
	
	private final Set<ISelectionChangedListener> listeners = new HashSet<>();
	
	private final IMapProvider mapProvider;
	
	private LayerType layer;
	
	private IEditorCurser curser;
	
	public CurserManager(IMapProvider mapProvider) {
		this.mapProvider = mapProvider;
	}
	
	public void addListerner(ISelectionChangedListener listener) {
		listeners.add(listener);
		listener.onSelectionChanged(get());
	}
	
	public IEditorCurser get() {
		return curser;
	}
	
	@Override
	public void onLayerChanged(LayerType layer) {
		this.layer = layer;
	}
	
	public void deleteSelected() {
		if( curser instanceof SelectionCurser ) {
			mapProvider.getMap().remove(layer, ((SelectionCurser)curser).layerObject);
			setToBrush();
		}
	}

	public void setSelectionFromCurser(Vector2f mouse, LayerType layer) {
		ILayerObject obj = mapProvider.getMap().findNode(layer, mouse);
		
		if( obj!=null )
			curser = new SelectionCurser(mapProvider, obj);
		
		else if( curser instanceof SelectionCurser )
			setToBrush();
	}
	
	public void setToNull() {
		if( curser!=null )
			curser.preDestroy();
		
		curser=null;
	}
	
	public void setToBrush() {
	//TODO:	curser = new BrushCurser(mapProvider, layer);
	}

}
