package de.secondsystem.game01.impl.gui;

import org.jsfml.graphics.RenderTarget;
import org.jsfml.system.Vector2f;

import de.secondsystem.game01.impl.gui.listeners.IOnClickListener;

public class Label extends GUIElement {

	public Label(float x, float y, float width, float height, GUIElement owner, IOnClickListener clickListener) {
		super(x, y, width, height, owner, clickListener);
	}

	@Override
	public void draw(RenderTarget renderTarget) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean inside(Vector2f point) {
		// TODO Auto-generated method stub
		return false;
	}

}
