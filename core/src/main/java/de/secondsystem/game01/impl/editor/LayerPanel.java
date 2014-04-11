package de.secondsystem.game01.impl.editor;

import java.util.HashSet;
import java.util.Set;

import org.jsfml.graphics.Color;
import org.jsfml.window.event.KeyEvent;

import de.secondsystem.game01.impl.gui.ElementContainer;
import de.secondsystem.game01.impl.gui.Label;
import de.secondsystem.game01.impl.gui.Panel;
import de.secondsystem.game01.impl.map.IMapProvider;
import de.secondsystem.game01.impl.map.LayerType;

public class LayerPanel extends Panel {	
	
	public static interface IOnLayerChangedListener {
		void onLayerChanged(LayerType layer);
	}
	
	private final Set<IOnLayerChangedListener> listeners = new HashSet<>();
	
	private final IMapProvider mapProvider;
	
	private Label layerHint;	// TODO: create a nice panel with toggleButtons, etc.
	
	private LayerType currentLayer = LayerType.FOREGROUND_0;
	
	public LayerPanel(float x, float y, float width, ElementContainer owner, IMapProvider mapProvider) {
		super(x, y, width, 50, owner);
		this.mapProvider = mapProvider;
		
		setLayoutOffset(10, 10);
		
		setFillColor(new Color(0, 0, 0, 200));
		
		layerHint = createLabel("");	
		updateLayerHint();
	}
	
	public void setLayer(LayerType layer) {
		if( layer!=currentLayer ) {
			currentLayer = layer;
			updateLayerHint();
			for(IOnLayerChangedListener l : listeners)
				l.onLayerChanged(layer);
		}
	}

	public LayerType getLayer() {
		return currentLayer;
	}
	
	public void addListener( IOnLayerChangedListener listener ) {
		listeners.add(listener);
		listener.onLayerChanged(currentLayer);
	}
	
	protected void updateLayerHint() {
		boolean[] s = mapProvider.getMap().getShownLayer();

		StringBuilder str = new StringBuilder();

		for (LayerType l : LayerType.values()) {
			if (currentLayer == l)
				str.append("=").append(l.name).append("=");
			else
				str.append(l.name);

			str.append(s[l.layerIndex] ? "[X]" : "[ ]");

			str.append("\t");
		}

		layerHint.setText(str.toString());
	}
	
	public boolean handleKeyCommands(KeyEvent event) {

		switch (event.key) {
			case NUM1:
				if (event.control) // toggle background 2 visibility
					mapProvider.getMap().flipShowLayer(LayerType.BACKGROUND_2);
				else // select background 2
					setLayer(LayerType.BACKGROUND_2);
				return true;
				
			case NUM2:
				if (event.control) // toggle background 1 visibility
					mapProvider.getMap().flipShowLayer(LayerType.BACKGROUND_1);
				else // select background 1
					setLayer(LayerType.BACKGROUND_1);
				return true;
				
			case NUM3:
				if (event.control) // toggle background 0 visibility
					mapProvider.getMap().flipShowLayer(LayerType.BACKGROUND_0);
				else // select background 0
					setLayer(LayerType.BACKGROUND_0);
				return true;
	
			case NUM4:
				if (event.control) // toggle foreground 0 visibility
					mapProvider.getMap().flipShowLayer(LayerType.FOREGROUND_0);
				else // select foreground 0
					setLayer(LayerType.FOREGROUND_0);
				return true;
				
			case NUM5:
				if (event.control) // toggle foreground 1 visibility
					mapProvider.getMap().flipShowLayer(LayerType.FOREGROUND_1);
				else // select foreground 1
					setLayer(LayerType.FOREGROUND_1);
				return true;
	
			case P:
				if (event.control) // toggle collision layer visibility
					mapProvider.getMap().flipShowLayer(LayerType.PHYSICS);
				else // select collision layer
					setLayer(LayerType.PHYSICS);
				return true;
	
			case O:
				if (event.control) // toggle object layer visibility
					mapProvider.getMap().flipShowLayer(LayerType.OBJECTS);
				else // select object layer
					setLayer(LayerType.OBJECTS);
				return true;
				
			case L:
				if(event.control) // toggle light layer visibility
					mapProvider.getMap().flipShowLayer(LayerType.LIGHTS);
				else // select light layer
					setLayer(LayerType.LIGHTS);
				return true;
	
			default:
				return false;
		}
	}
	
}
