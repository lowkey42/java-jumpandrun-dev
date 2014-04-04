package de.secondsystem.game01.impl.gui;

import de.secondsystem.game01.impl.gui.listeners.IOnClickListener;

public abstract class GUIGameStateSimpleLayout extends GUIGameState {

	protected abstract int getElementSpacing();
	
	protected abstract int getXPosition();
	
	protected abstract int getYPosition();
	
	private int offset;
	

	// factory-methods
	protected final Slider createSlider() {
		Slider e = createSlider(getXPosition(), getYPosition()+offset);
		offset+=e.getHeight() + getElementSpacing();
		
		return e;
	}
	protected final Label createLabel(String text) {
		Label e = createLabel(getXPosition(), getYPosition()+offset, text);
		offset+=e.getHeight() + getElementSpacing();
		
		return e;
	}
	protected final Label createLabel(String text, Element forElem) {
		Label e = createLabel(getXPosition(), getYPosition()+offset, text, forElem);
		offset+=e.getHeight() + getElementSpacing();
		
		return e;
	}
	protected final Button createButton(String caption, IOnClickListener clickListener) {
		Button e = createButton(getXPosition(), getYPosition()+offset, caption, clickListener);
		offset+=e.getHeight() + getElementSpacing();
		
		return e;
	}
	protected final Edit createInputField(float width, String text) {
		Edit e = createInputField(getXPosition(), getYPosition()+offset, width, text);
		offset+=e.getHeight() + getElementSpacing();
		
		return e;
	}

}
