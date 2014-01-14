package de.secondsystem.game01.impl.graphic;

import java.io.IOException;

import org.jsfml.graphics.BlendMode;
import org.jsfml.graphics.Color;
import org.jsfml.graphics.ConstShader;
import org.jsfml.graphics.ConstView;
import org.jsfml.graphics.Drawable;
import org.jsfml.graphics.RenderStates;
import org.jsfml.graphics.RenderTarget;
import org.jsfml.graphics.RenderTexture;
import org.jsfml.graphics.Shader;
import org.jsfml.graphics.Sprite;
import org.jsfml.graphics.TextureCreationException;
import org.jsfml.graphics.View;
import org.jsfml.system.Vector2f;

import de.secondsystem.game01.impl.ResourceManager;
import de.secondsystem.game01.model.GameException;

public class LightMap implements Drawable {

	private final RenderTexture lightMap;
	
	private final Sprite sprite;
	
	private final ConstShader shader;
	
	public LightMap( int width, int height, Color ambientLight ) {
		lightMap = new RenderTexture();
		try {
			shader = ResourceManager.shader_frag.get("lightmap.frag");
			
		} catch (IOException e1) {
			throw new GameException("Unable to load shader");
		}
		
		try {
			lightMap.create(width, height);
			
		} catch (TextureCreationException e) {
			throw new GameException("Unable to create lightMap");
		}
		
		sprite = new Sprite(lightMap.getTexture());
		sprite.setColor(ambientLight);
		
	}
	
	public void setView( ConstView view ) {
		lightMap.setView(view);
	}

	public void draw( RenderTarget target ) {
		final ConstView orgView = target.getView();
		target.setView(new View(Vector2f.div(orgView.getSize(), 2), orgView.getSize()));
		
		lightMap.display();
		
		((RenderTexture)target).display();
		((Shader)shader).setParameter("fb", ((RenderTexture)target).getTexture());
		
		target.draw(sprite, new RenderStates(shader));
		
		target.setView(orgView);
	}
	
	public void drawLight( Light light ) {
		lightMap.draw(light.getDrawable(), new RenderStates(BlendMode.ADD));
	}
	
	public void clear() {
		lightMap.clear();
	}

	@Override
	public void draw(RenderTarget target, RenderStates states) {
		draw(target);
	}
}
