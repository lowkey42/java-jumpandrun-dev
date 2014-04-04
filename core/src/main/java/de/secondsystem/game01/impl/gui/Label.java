package de.secondsystem.game01.impl.gui;

import java.io.IOException;

import org.jsfml.graphics.ConstFont;
import org.jsfml.graphics.RenderTarget;
import org.jsfml.graphics.Text;
import org.jsfml.system.Vector2f;

import de.secondsystem.game01.impl.ResourceManager;

public class Label extends GUIElement {

	protected Text text;
	
	public Label(float x, float y, String text, GUIElement parent) {
		super(x, y, 0.f, 0.f, parent);
		
		width  = this.text.getGlobalBounds().width;
		height = this.text.getGlobalBounds().height;
		// Loading standard Font (12.5 pixel width & 21 pixel height per char --> Monospace VeraMono)
		this.text = new Text(text, loadFont("VeraMono.ttf"), (int) (height/4.f));
		this.text.setOrigin(width / 2.f, height / 2.f);
		this.text.setPosition(x, y);
	}
	
	public Label(float x, float y, GUIElement parent) {
		this(x, y, "label", parent);
	}
	
	public void setFont(ConstFont font) {
		text.setFont(font);
	}
	
	public void setFont(String filename) {
		text.setFont(loadFont(filename));
	}
	
	private ConstFont loadFont(String filename) {
		try {
			ConstFont font = ResourceManager.font.get(filename);
			return font;
		} catch( IOException e ) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	@Override
	public void draw(RenderTarget renderTarget) {
		super.draw(renderTarget);
		
		renderTarget.draw(text);
	}

	@Override
	public boolean inside(Vector2f point) {
		return text.getGlobalBounds().contains(point);
	}

	@Override
	public void refresh() {
		text.setScale(width/text.getGlobalBounds().width, height/text.getGlobalBounds().height/height);
		text.setPosition(pos);
		text.setRotation(rotation);
	}

}
