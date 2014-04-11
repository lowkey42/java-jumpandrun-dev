package de.secondsystem.game01.impl.editor.curser;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.RectangleShape;
import org.jsfml.system.Vector2f;

import de.secondsystem.game01.impl.map.ILayerObject;
import de.secondsystem.game01.impl.map.IMapProvider;
import de.secondsystem.game01.model.Attributes;

class SelectionCurser extends AbstractCurser {
	
	private ILayerObject layerObject;
	
	private final RectangleShape resizeMarkers[] = new RectangleShape[] {	// TODO: migrate mouse-scale code from EditorLayerObject
			new RectangleShape(),
			new RectangleShape(),
			new RectangleShape(),
			new RectangleShape()
	};
	
	public SelectionCurser( IMapProvider mapProvider, ILayerObject layerObject ) {
		super(mapProvider, Color.RED);
		this.layerObject = layerObject;
		
		for(RectangleShape shape : resizeMarkers) {
			shape.setFillColor(Color.RED);
			shape.setOutlineThickness(0);
		}
	}

	@Override
	protected ILayerObject getLayerObject() {
		return layerObject;
	}

	@Override
	public void setAttributes(Attributes attributes) {
		mapProvider.getMap().updateNode(layerObject, attributes);
	}

	@Override
	public void onDragged(Vector2f point) {
		if( inside(point) ) {
			super.onDragged(point);
		}
	}
	
}
