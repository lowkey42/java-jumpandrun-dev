package de.secondsystem.game01.impl.editor;

import de.secondsystem.game01.impl.editor.BrushMap.Brush;

public class BrushCurser extends SelectionCurser {
	
	private Brush activeBrush;
	
	public BrushCurser(IMapProvider mapProvider, Brush activeBrush) {
		super(mapProvider, null);
	}


	private final BrushMap brushes = new BrushMap();

	@Override
	public void preDestroy() {
		super.preDestroy();
		mapProvider.getMap().remove(layerObject.getLayerType(), layerObject);
	}
	
}
