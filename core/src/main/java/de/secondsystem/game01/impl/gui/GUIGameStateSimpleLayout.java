package de.secondsystem.game01.impl.gui;

import de.secondsystem.game01.impl.gui.listeners.IOnClickListener;

public abstract class GUIGameStateSimpleLayout extends GUIGameState {

	protected abstract int getElementSpacing();
	
	private int offset;
	

	// factory-methods
	protected final Slider createSlider() {
		Slider e = createSlider(0, offset);
		offset+=e.getHeight() + getElementSpacing();
		
		return e;
	}
	protected final Label createLabel(String text) {
		Label e = createLabel(0, offset, text);
		offset+=e.getHeight() + getElementSpacing();
		
		return e;
	}
	protected final Label createLabel(String text, Element forElem) {
		Label e = createLabel(0, offset, text, forElem);
		offset+=e.getHeight() + getElementSpacing();
		
		return e;
	}
	protected final Button createButton(String caption, IOnClickListener clickListener) {
		Button e = createButton(0, offset, caption, clickListener);
		offset+=e.getHeight() + getElementSpacing();
		
		return e;
	}
	protected final Edit createInputField(float width, String text) {
		Edit e = createInputField(0, offset, width, text);
		offset+=e.getHeight() + getElementSpacing();
		
		return e;
	}

}
