package de.secondsystem.game01.impl.gui;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.RectangleShape;
import org.jsfml.graphics.RenderTarget;
import org.jsfml.system.Vector2f;

import de.secondsystem.game01.util.Tools;

public class Panel extends GUIElement {
	private RectangleShape shape = new RectangleShape();
	
	public Panel(float x, float y, float width, float height, GUIElement parent) {
		super(x, y, width, height, parent);
		
		shape.setOrigin(new Vector2f(width / 2.f, height / 2.f));
		shape.setOutlineThickness(2.f);
		shape.setOutlineColor(Color.WHITE);
		shape.setFillColor(Color.BLACK);
		refresh();
	}

	@Override
	public void draw(RenderTarget renderTarget) {
		renderTarget.draw(shape);
		
		super.draw(renderTarget);
	}

	@Override
	public boolean inside(Vector2f point) {
		return Tools.isInside(shape, point);
	}
	
	public void refresh() {
		super.refresh();
		
		shape.setSize(new Vector2f(width, height));
		shape.setPosition(pos);
	}

}
