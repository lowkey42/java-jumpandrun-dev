package de.secondsystem.game01.impl.editor;

import java.util.HashSet;
import java.util.Set;

import org.jsfml.graphics.Color;
import org.jsfml.window.event.KeyEvent;

import de.secondsystem.game01.impl.gui.ElementContainer;
import de.secondsystem.game01.impl.gui.Panel;
import de.secondsystem.game01.impl.gui.RwValueRef;
import de.secondsystem.game01.impl.map.IMapProvider;
import de.secondsystem.game01.impl.map.LayerType;

public class LayerPanel extends Panel implements RwValueRef<LayerType> {	
	
	public static interface IOnLayerChangedListener {
		void onLayerChanged(LayerType layer);
	}
	
	private final Set<IOnLayerChangedListener> listeners = new HashSet<>();
	
	private final IMapProvider mapProvider;
	
	private LayerType currentLayer = LayerType.FOREGROUND_0;
	
	public LayerPanel(float x, float y, float width, ElementContainer owner, IMapProvider mp) {
		super(x, y, width, 60, new Layout(LayoutDirection.HORIZONTAL, 0), owner);
		this.mapProvider = mp;
		
		for( LayerType l : LayerType.values() ) {
			final LayerType layer = l;
			Panel p = createPanel(width/LayerType.values().length, 60, new Layout(LayoutDirection.VERTICAL, 5));
			p.setFillColor(Color.TRANSPARENT);
			p.setOutlineColor(new Color(100, 100, 100));
			
			Panel topPanel = p.createPanel(p.getWidth(), 25, new Layout(LayoutDirection.HORIZONTAL, 20));
			topPanel.setLayoutOffset(50, 0);
			topPanel.setFillColor(Color.TRANSPARENT);
			topPanel.setOutlineColor(Color.TRANSPARENT);
			topPanel.createLabel(layer.name).setFor(			
					topPanel.createCheckbox(new RwValueRef<Boolean>() {
					
					@Override public void setValue(Boolean value) {
						mapProvider.getMap().setShowLayer(layer, value);
					}
					
					@Override public Boolean getValue() {
						return mapProvider.getMap().isLayerShown(layer);
					}
				})
			);
			
			p.createRadiobox(p.getXOffset()+width/LayerType.values().length/2.5f, p.getYOffset(), this, layer);
		}
		
		setFillColor(new Color(0, 0, 0, 200));
	}
	
	public void setLayer(LayerType layer) {
		if( layer!=currentLayer ) {
			currentLayer = layer;
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

	@Override
	public LayerType getValue() {
		return getLayer();
	}

	@Override
	public void setValue(LayerType value) {
		setLayer(value);
	}
	
}
