package de.secondsystem.game01.impl.graphic;

import org.jsfml.graphics.BlendMode;
import org.jsfml.graphics.Drawable;
import org.jsfml.graphics.RenderStates;
import org.jsfml.graphics.RenderTarget;
import org.jsfml.graphics.RenderTexture;
import org.jsfml.graphics.Sprite;
import org.jsfml.graphics.TextureCreationException;

public class LightMap implements Drawable {

	private final RenderTexture lightMap;
	
	public LightMap( int width, int height ) throws TextureCreationException {
		lightMap = new RenderTexture();
		lightMap.create(width, height);
	}
	
	
	public void draw( RenderTarget target ) {
		lightMap.display();
		Sprite s = new Sprite(lightMap.getTexture());
	//	s.setPosition(lightMap.getSize().x/2, lightMap.getSize().y/2);
		target.draw(s, new RenderStates(BlendMode.ADD));
	}
	
	public void drawLight( Light light ) {
		lightMap.draw(light.getDrawable(), new RenderStates(BlendMode.ADD));
	}
	
	public void clear( float percentage ) {
		lightMap.clear();
	}

	@Override
	public void draw(RenderTarget target, RenderStates states) {
		draw(target);
	}
}
