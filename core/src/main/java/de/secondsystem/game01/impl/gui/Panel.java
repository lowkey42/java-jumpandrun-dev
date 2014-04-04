package de.secondsystem.game01.impl.gui;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.RectangleShape;
import org.jsfml.graphics.RenderTarget;
import org.jsfml.system.Vector2f;

public class Panel extends LayoutElementContainer {
	private final RectangleShape shape;

	public Panel(float x, float y, float width, float height, ElementContainer parent) {
		this(x, y, width, height, new Layout(LayoutDirection.VERTICAL, 0), parent);
	}
	public Panel(float x, float y, float width, float height, Layout layout, ElementContainer parent) {
		super(x, y, width, height, parent, layout);
		
		shape = new RectangleShape(new Vector2f(width, height));
		shape.setOutlineThickness(2.f);
		shape.setOutlineColor(Color.WHITE);
		shape.setFillColor(Color.BLACK);
	}

	@Override
	protected void drawImpl(RenderTarget renderTarget) {
		shape.setPosition(getPosition());
		renderTarget.draw(shape);
		
		super.drawImpl(renderTarget);
	}

}
