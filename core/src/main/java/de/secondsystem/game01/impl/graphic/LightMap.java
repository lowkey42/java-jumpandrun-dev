package de.secondsystem.game01.impl.graphic;

import org.jsfml.graphics.BlendMode;
import org.jsfml.graphics.Color;
import org.jsfml.graphics.ConstView;
import org.jsfml.graphics.Drawable;
import org.jsfml.graphics.RectangleShape;
import org.jsfml.graphics.RenderStates;
import org.jsfml.graphics.RenderTarget;
import org.jsfml.graphics.RenderTexture;
import org.jsfml.graphics.Sprite;
import org.jsfml.graphics.TextureCreationException;
import org.jsfml.graphics.View;
import org.jsfml.system.Vector2f;

import de.secondsystem.game01.model.GameException;

public class LightMap implements Drawable {

	private final RenderTexture lightMap;

	private final RectangleShape clearRect;
	
	public LightMap( int width, int height ) {
		lightMap = new RenderTexture();
		
		try {
			lightMap.create(width, height);
			
		} catch (TextureCreationException e) {
			throw new GameException("Unable to create lightMap");
		}
		
		clearRect = new RectangleShape(new Vector2f(width, height));
		clearRect.setPosition(0, 0);
	}
	
	public void setView( ConstView view ) {
		lightMap.setView(view);
	}
	public void draw( RenderTarget target ) {
		final ConstView orgView = target.getView();
		target.setView(new View(target.getDefaultView().getCenter(), orgView.getSize()));
		
		lightMap.display();
		Sprite s = new Sprite(lightMap.getTexture());
		target.draw(s, new RenderStates(BlendMode.ADD));
		
		target.setView(orgView);
	}
	
	public void drawLight( Light light ) {
		lightMap.draw(light.getDrawable(), new RenderStates(BlendMode.ADD));
	}
	
	public void clear( float percentage ) {
		final ConstView orgView = lightMap.getView();
		lightMap.setView(new View(lightMap.getDefaultView().getCenter(), orgView.getSize()));
		
		lightMap.clear();
		//clearRect.setFillColor(new Color(0, 0, 0, (int) percentage*255));
		//lightMap.draw(clearRect, new RenderStates(BlendMode.NONE));
		
		lightMap.setView(orgView);
	}

	@Override
	public void draw(RenderTarget target, RenderStates states) {
		draw(target);
	}
}
