package de.secondsystem.game01.impl.editor;

import org.jsfml.graphics.Color;

import de.secondsystem.game01.impl.gui.ElementContainer;
import de.secondsystem.game01.impl.gui.Label;
import de.secondsystem.game01.impl.gui.Panel;
import de.secondsystem.game01.impl.gui.Style;
import de.secondsystem.game01.impl.map.IGameMap;
import de.secondsystem.game01.impl.map.LayerType;

public class EditorGUI extends ElementContainer {
	
	private Panel hintPanel;
	private Label layerHint;
	
	public EditorGUI(float x, float y, float width, float height) {
		super(x, y, width, height, Style.createDefaultStyle());
		
		hintPanel = new Panel(0,0, width, 40, this);
		hintPanel.setFillColor(Color.TRANSPARENT);
		
		layerHint = new Label(5, 15, "", hintPanel);
	}
	
	
	public void setLayerHint(IGameMap map, LayerType currentLayer) {
		boolean[] s = map.getShownLayer();

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
	
}
