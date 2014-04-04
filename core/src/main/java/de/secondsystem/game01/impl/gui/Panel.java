package de.secondsystem.game01.impl.gui;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.RectangleShape;
import org.jsfml.graphics.RenderTarget;
import org.jsfml.system.Vector2f;

public class Panel extends ElementContainer {
	private final RectangleShape shape;
	
	public Panel(float x, float y, float width, float height, ElementContainer parent) {
		super(x, y, width, height, parent);
		
		shape = new RectangleShape(new Vector2f(width, height));
		shape.setOrigin(new Vector2f(width / 2.f, height / 2.f));
		shape.setPosition(pos);
		shape.setOutlineThickness(2.f);
		shape.setOutlineColor(Color.WHITE);
		shape.setFillColor(Color.BLACK);
	}

	@Override
	protected void drawImpl(RenderTarget renderTarget) {
		renderTarget.draw(shape);
		
		super.drawImpl(renderTarget);
	}

}
