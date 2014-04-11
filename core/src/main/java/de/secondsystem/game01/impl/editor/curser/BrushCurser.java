package de.secondsystem.game01.impl.editor.curser;

import org.jsfml.graphics.Color;
import org.jsfml.system.Vector2f;

import de.secondsystem.game01.impl.editor.curser.BrushPalette.IBrush;
import de.secondsystem.game01.impl.map.ILayerObject;
import de.secondsystem.game01.impl.map.IMapProvider;
import de.secondsystem.game01.model.Attributes;

class BrushCurser extends AbstractCurser {
	
	protected final IBrush brush;
	
	public BrushCurser(IMapProvider mapProvider, IBrush brush) {
		super(mapProvider, Color.BLUE);
		this.brush = brush;
	}

	@Override
	protected ILayerObject getLayerObject() {
		return brush.getObject();
	}

	@Override
	public void setAttributes(Attributes attributes) {
		brush.setAttributes(attributes);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		brush.onDestroy();
	}

	
	@Override
	public void onMouseMoved(Vector2f point) {
		getLayerObject().setPosition(point);
	}
}
