package de.secondsystem.game01.impl.editor.curser;

import java.util.List;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.RectangleShape;
import org.jsfml.system.Vector2f;

import de.secondsystem.game01.impl.map.ILayerObject;
import de.secondsystem.game01.impl.map.IMapProvider;
import de.secondsystem.game01.model.Attributes;

class SelectionCurser extends AbstractCurser {
	
	private List<ILayerObject> layerObjects;
	
	private int index = 0;
	
	private final RectangleShape resizeMarkers[] = new RectangleShape[] {	// TODO: migrate mouse-scale code from EditorLayerObject
			new RectangleShape(),
			new RectangleShape(),
			new RectangleShape(),
			new RectangleShape()
	};
	
	public SelectionCurser( IMapProvider mapProvider, List<ILayerObject> layerObject ) {
		super(mapProvider, Color.RED);
		this.layerObjects = layerObject;
		
		for(RectangleShape shape : resizeMarkers) {
			shape.setFillColor(Color.RED);
			shape.setOutlineThickness(0);
		}
	}

	@Override
	protected ILayerObject getLayerObject() {
		return layerObjects.get(index);
	}

	@Override
	public void setAttributes(Attributes attributes) {
		layerObjects.set(index, mapProvider.getMap().updateNode(layerObjects.get(index), attributes));
	}

	@Override
	public void onDragged(Vector2f point) {
		if( inside(point) ) {
			super.onDragged(point);
		}
	}

	@Override
	public void cirlce(boolean up) {
		index += up ? 1 : -1;
		
		if( index>layerObjects.size()-1 )
			index = 0;
		else if( index<0 )
			index = layerObjects.size()-1;
	}

	@Override
	public int getCurrentBrushIndex() {
		return index;
	}

	@Override
	public int getBrushCount() {
		return layerObjects.size()-1;
	}
	
}
