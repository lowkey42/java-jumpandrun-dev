package de.secondsystem.game01.impl.graphic;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import org.jsfml.graphics.BlendMode;
import org.jsfml.graphics.Color;
import org.jsfml.graphics.ConstShader;
import org.jsfml.graphics.ConstTexture;
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
	
	private final AtomicBoolean enabled = new AtomicBoolean(true);
	
	public LightMap( int width, int height ) {
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
		
	}
	
	public void setAmbientLight(Color ambientLight) {
		sprite.setColor(ambientLight);
	}
	
	public void setView( ConstView view ) {
		lightMap.setView(view);
	}

	public void draw( RenderTarget target, ConstTexture frameBufferTexture ) {
		final ConstView orgView = target.getView();
		target.setView(new View(Vector2f.div(orgView.getSize(), 2), orgView.getSize()));
		
		lightMap.display();
		
		((Shader)shader).setParameter("fb", frameBufferTexture);
		
		target.draw(sprite, new RenderStates(shader));
		
		target.setView(orgView);
	}
	
	public void draw( RenderTarget target ) {
		((RenderTexture)target).display();
		draw(target, ((RenderTexture)target).getTexture());
	}
	
	public void drawLight( Light light ) {
		if( enabled.get() )
			lightMap.draw(light.getDrawable(), new RenderStates(BlendMode.ADD));
	}
	
	public void clear() {
		lightMap.clear();
	}
	
	public void disable() {
		enabled.set(false);
	}
	public void enable() {
		enabled.set(true);
	}

	@Override
	public void draw(RenderTarget target, RenderStates states) {
		draw(target);
	}
}
